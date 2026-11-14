package com.companion.effect.TunnelBulid;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static com.companion.global.*;

public class Bulid {
    /**
     * 移动指定物品到主手
     * @param inventory 玩家背包
     * @param targetItem 目标物品
     */
    private static void sup(PlayerInventory inventory, Item targetItem) {
        // 1. 已经在主手就不用动了
        if (player.getMainHandStack().getItem() == targetItem) return;

        // 2. 遍历 0-35（背包 + 快捷栏）
        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (stack.getItem() == targetItem) {
                if (all_client.interactionManager != null) {
                    int hotbarSlot = player.getInventory().getSelectedSlot(); // 0-8
                    all_client.interactionManager.clickSlot(
                            0,
                            slot,
                            0,
                            SlotActionType.PICKUP,
                            player
                    );
                    all_client.interactionManager.clickSlot(
                            0,
                            hotbarSlot,
                            0,
                            SlotActionType.PICKUP,
                            player
                    );
                    return;   // 只搬第一组
                }
            }
        }
    }

    /**
     * 把背包里第一块与主手同类的方块放到任意世界坐标（空气也行）
     *
     * @param target 目标世界坐标
     */
    public static void placeAt(BlockPos target) {
        MinecraftClient.getInstance().execute(() -> {
            ClientPlayerInteractionManager im = all_client.interactionManager;
            if (player == null || im == null) return;
            /* 3. 伪造 BlockHitResult：假装玩家右键了“目标方块”本身 */
            BlockHitResult hit = new BlockHitResult(
                    Vec3d.ofCenter(target), // 命中点设在 target 上方一格的中心
                    Direction.DOWN,              // 表示点击的是 target.up() 的底面
                    target,                 // 被点击的方块坐标
                    false
            );
            /* 4. 发原版交互包 */
            im.interactBlock(player, Hand.MAIN_HAND, hit);
        });
    }
}
