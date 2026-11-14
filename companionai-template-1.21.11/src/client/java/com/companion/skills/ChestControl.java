package com.companion.skills;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static com.companion.global.all_client;

public class ChestControl {
    /**
     * 打开指定坐标箱子的核心函数（1.21.11 版本适配）
     * @param chestPos 目标箱子坐标
     */
    public static void openChest(BlockPos chestPos) {
        MinecraftClient client = MinecraftClient.getInstance();

        // 基础空值校验（公开字段/方法）
        if (client.player == null || client.interactionManager == null || client.world == null) {
            return;
        }

        // 构造方块碰撞结果（和游戏右键逻辑完全相同）
        BlockHitResult hitResult = new BlockHitResult(
                Vec3d.ofCenter(chestPos),  // 碰撞位置
                Direction.UP,              // 碰撞方向
                chestPos,                  // 方块坐标
                false                      // 偏移量
        );

        // ==============================================
        // ✅ 核心：调用【公开PUBLIC方法】，自动发送数据包、自动处理sequence
        // ==============================================
        ClientPlayerInteractionManager manager = client.interactionManager;
        ActionResult result = manager.interactBlock(client.player, Hand.MAIN_HAND, hitResult);
    }
    public static void close(){
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.closeHandledScreen();// 直接调用玩家的「关闭容器界面」核心API
        }
    }



    private static final int CHEST_SLOT_MIN = 0;
    private static final int CHEST_SLOT_MAX = 26;
    private static final int PLAYER_INV_MIN = 27;
    private static final int PLAYER_INV_MAX = 62;

    // ==============================================
    // 【放入箱子】玩家背包 → 箱子
    // @param targetItem 要放入的物品(Item类型)
    // @param amount     数量(-1=全部放入)
    // ==============================================
    public static void putItemIntoChest(Item targetItem, int amount) {
        MinecraftClient client = all_client;
        if (!isChestOpen()) return;

        int remaining = amount == -1 ? Integer.MAX_VALUE : amount;
        GenericContainerScreenHandler handler = (GenericContainerScreenHandler) client.player.currentScreenHandler;

        // 遍历玩家背包
        for (int slot = PLAYER_INV_MIN; slot <= PLAYER_INV_MAX && remaining > 0; slot++) {
            Slot invSlot = handler.getSlot(slot);
            ItemStack stack = invSlot.getStack();

            // 【核心修改】仅匹配物品类型，无视NBT/组件
            if (!stack.isEmpty() && stack.isOf(targetItem)) {
                int moveCount = Math.min(stack.getCount(), remaining);
                // SHIFT+点击 快速移动物品
                client.interactionManager.clickSlot(handler.syncId, slot, 0, SlotActionType.QUICK_MOVE, client.player);
                remaining -= moveCount;
            }
        }
    }

    // ==============================================
    // 【取出物品】箱子 → 玩家背包
    // @param targetItem 要取出的物品(Item类型)
    // @param amount     数量(-1=全部取出)
    // ==============================================
    public static void takeItemFromChest(Item targetItem, int amount) {
        MinecraftClient client = all_client;
        if (!isChestOpen()) return;

        int remaining = amount == -1 ? Integer.MAX_VALUE : amount;
        GenericContainerScreenHandler handler = (GenericContainerScreenHandler) client.player.currentScreenHandler;

        // 遍历箱子内部
        for (int slot = CHEST_SLOT_MIN; slot <= CHEST_SLOT_MAX && remaining > 0; slot++) {
            Slot chestSlot = handler.getSlot(slot);
            ItemStack stack = chestSlot.getStack();

            // 【核心修改】仅匹配物品类型，无视NBT/组件
            if (!stack.isEmpty() && stack.isOf(targetItem)) {
                int moveCount = Math.min(stack.getCount(), remaining);
                // SHIFT+点击 快速移动物品
                client.interactionManager.clickSlot(handler.syncId, slot, 0, SlotActionType.QUICK_MOVE, client.player);
                remaining -= moveCount;
            }
        }
    }

    // ==============================================
    // 检查箱子是否已打开
    // ==============================================
    private static boolean isChestOpen() {
        MinecraftClient client = all_client;
        return client.player != null
                && client.interactionManager != null
                && client.player.currentScreenHandler instanceof GenericContainerScreenHandler;
    }
}
