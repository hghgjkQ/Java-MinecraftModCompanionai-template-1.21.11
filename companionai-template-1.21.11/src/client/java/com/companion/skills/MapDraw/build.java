package com.companion.skills.MapDraw;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.BlockTypes;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import static com.companion.global.*;
import java.util.HashMap;

import static com.companion.skills.MapDraw.Workpiece.*;

public class build {

    public static void buildUnit(){

    }

    // 【最简函数】从起点MapDrawPos填充圆石到MapDrawTar
    public static void fillStone() {
        MapDrawTar.clear();
        // 1. 提取起点坐标（X/Z从MapDrawPos取，Y默认填64（海平面），可按需修改）
        int x = (int) MapDrawPos[0];
        int z = (int) MapDrawPos[1];
        int y = MapDrawY;

        // 2. 创建MC原生BlockPos对象（Fabric标准用法）
        BlockPos startPos = new BlockPos(x, y, z);

        // 3. 填充圆石到目标Map（使用MC原生的圆石方块常量）
        MapDrawTar.put(startPos, Blocks.COBBLESTONE);
    }



    public static void throwNonCobble(ClientPlayerEntity player) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerInteractionManager im = mc.interactionManager;
        if (im == null) return;

        PlayerInventory inv = player.getInventory();

        // 遍历所有槽位，直接THROW
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (stack.isEmpty() || stack.isOf(Items.COBBLESTONE)) continue;

            int slotId = i < 9 ? i + 36 : (i == 40 ? 45 : i);
            im.clickSlot(0, slotId, 1, SlotActionType.THROW, player);
        }
    }
}
