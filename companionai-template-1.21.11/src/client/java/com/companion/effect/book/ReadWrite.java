package com.companion.effect.book;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import java.util.*;

public class ReadWrite {


    /* ========= 覆写：每页≤1024，超100页拒绝 ========= */
    public static boolean write(String a) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return false;
        ItemStack stack = null;
        Hand hand = null;
        for (Hand h : Hand.values()) {
            ItemStack s = mc.player.getStackInHand(h);
            if (s.isOf(Items.WRITABLE_BOOK)) {
                stack = s;
                hand = h;
                break;
            }
        }
        if (stack == null) return false;

        /* 分页：每页≤1024 */
        final int PAGE_MAX = 1024;
        List<RawFilteredPair<String>> pages = new ArrayList<>();
        for (int i = 0; i < a.length(); i += PAGE_MAX) {
            if (pages.size() >= 100) {
                mc.player.sendMessage(Text.literal("§c内容超过100页，已截断！"), false);
                break;
            }
            pages.add(RawFilteredPair.of(a.substring(i, Math.min(i + PAGE_MAX, a.length()))));
        }

        /* 覆写整本书 */
        stack.set(DataComponentTypes.WRITABLE_BOOK_CONTENT,
                new WritableBookContentComponent(pages));
        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(
                new CreativeInventoryActionC2SPacket(36 + hand.ordinal(), stack));
        return true;
    }

    /* ========= 读取：返回每页原文 List<String> ========= */
    public static List<String> readBook() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return Collections.emptyList();

        ItemStack stack = null;
        for (Hand h : Hand.values()) {
            ItemStack s = mc.player.getStackInHand(h);
            if (s.isOf(Items.WRITABLE_BOOK) || s.isOf(Items.WRITTEN_BOOK)) {
                stack = s;
                break;
            }
        }
        if (stack == null) return Collections.emptyList();

        WritableBookContentComponent content =
                stack.get(DataComponentTypes.WRITABLE_BOOK_CONTENT);
        if (content == null) return Collections.emptyList();

        List<String> list = new ArrayList<>();
        for (RawFilteredPair<String> page : content.pages()) {
            list.add(page.raw());   // 取未过滤原文
        }
        return list;
    }
}




