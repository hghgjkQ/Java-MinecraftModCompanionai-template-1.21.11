package com.companion.skills.bat;

import com.companion.mixin.client.BossBarHudAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.companion.Key.config.setV;
import static com.companion.Key.configTool.cfg;
import static com.companion.effect.tool.error;
import static com.companion.effect.tool.tipGame;
import static com.companion.global.*;

public class AutoRaid {

    private static int drink=0;
    private static Item tar=null;

    /**
     * 是否有不祥之兆 (Bad Omen)
     * 1.21 中通过饮用 Ominous Bottle 获得，进入村庄会转化为 Raid Omen
     */
    public static boolean hasBadOmen(ClientPlayerEntity player) {
        return player.hasStatusEffect(StatusEffects.BAD_OMEN);
    }

    /**
     * 是否有袭击之兆 (Raid Omen) - 1.21 新增
     * Bad Omen 进入村庄后转化而来，持续 30 秒，之后触发袭击
     */
    public static boolean hasRaidOmen(ClientPlayerEntity player) {
        return player.hasStatusEffect(StatusEffects.RAID_OMEN);
    }
    /**
     * 检测客户端是否显示袭击进度条（BossBar）
     * 说明袭击波次正在进行中（Raid Omen 30 秒倒计时已结束）
     */
    public static boolean hasRaidBossBar(MinecraftClient client) {
        if (client.getNetworkHandler() == null) return false;
        if( hasRaidOmen(player) || hasBadOmen(player)) return true;

        InGameHud hud = client.inGameHud;
        BossBarHud bossBarHud = hud.getBossBarHud();

        // 通过 Accessor 获取 bossBars Map
        Map<UUID, ClientBossBar> bossBars = ((BossBarHudAccessor) bossBarHud).getBossBars();

        for (ClientBossBar bossBar : bossBars.values()) {
            // 袭击 BossBar 的名称包含特定翻译键
            String name = bossBar.getName().getString();
            // 或者检查 bossBar 的 ID/UUID 特征（袭击有固定的命名空间）
            // 注意：具体判断逻辑可能需要根据实际环境调整
            if ((name.contains("袭击")) && (!name.contains("胜利"))) {
                return true;
            }
        }
        return false;
    }


    private static boolean sup(PlayerInventory inventory ) {
        Item targetItem=Items.OMINOUS_BOTTLE;
        // 已经在主手就不用动了（用 isOf 比 == 更规范）
        if (player.getMainHandStack().isOf(targetItem)) return true;
        if (all_client.interactionManager == null) return false;

        int hotbarSlot = inventory.getSelectedSlot(); // 0-8，主手在快捷栏中的索引

        // 遍历 0-35（背包 + 快捷栏）
        for (int invSlot = 0; invSlot < 36; invSlot++) {
            ItemStack stack = inventory.getStack(invSlot);
            if (!stack.isOf(targetItem)) continue;

            // ===== 核心修复：将 Inventory 索引转换为 ScreenHandler 槽位 ID =====
            // PlayerScreenHandler 里的 Slot 对象持有它对应的 Inventory 实例和在其中的索引
            int screenSlot = -1;
            for (Slot slot : player.playerScreenHandler.slots) {
                if (slot.inventory == inventory && slot.getIndex() == invSlot) {
                    screenSlot = slot.id; // slot.id 才是 clickSlot 需要的 ScreenHandler 槽位 ID
                    break;
                }
            }
            if (screenSlot == -1) continue; // 兜底

            // 使用 SWAP 一次性把物品换到主手（原子操作，无需模拟两次 PICKUP）
            // 参数说明：button 在 SWAP 模式下代表快捷栏索引 0-8
            all_client.interactionManager.clickSlot(
                    player.playerScreenHandler.syncId,  // 正确的 syncId
                    screenSlot,                          // 正确的源槽位 ID
                    hotbarSlot,                          // 目标：当前选中的快捷栏
                    SlotActionType.SWAP,
                    player
            );
            return true; // 只搬第一组
        }
        return false;
    }

    private static void sup(PlayerInventory inventory, Item targetItem) {
        // 已经在主手就不用动了（用 isOf 比 == 更规范）
        if (player.getMainHandStack().isOf(targetItem)) return;
        if (all_client.interactionManager == null) return;

        int hotbarSlot = inventory.getSelectedSlot(); // 0-8，主手在快捷栏中的索引

        // 遍历 0-35（背包 + 快捷栏）
        for (int invSlot = 0; invSlot < 36; invSlot++) {
            ItemStack stack = inventory.getStack(invSlot);
            if (!stack.isOf(targetItem)) continue;

            // ===== 核心修复：将 Inventory 索引转换为 ScreenHandler 槽位 ID =====
            // PlayerScreenHandler 里的 Slot 对象持有它对应的 Inventory 实例和在其中的索引
            int screenSlot = -1;
            for (Slot slot : player.playerScreenHandler.slots) {
                if (slot.inventory == inventory && slot.getIndex() == invSlot) {
                    screenSlot = slot.id; // slot.id 才是 clickSlot 需要的 ScreenHandler 槽位 ID
                    break;
                }
            }
            if (screenSlot == -1) continue; // 兜底

            // 使用 SWAP 一次性把物品换到主手（原子操作，无需模拟两次 PICKUP）
            // 参数说明：button 在 SWAP 模式下代表快捷栏索引 0-8
            all_client.interactionManager.clickSlot(
                    player.playerScreenHandler.syncId,  // 正确的 syncId
                    screenSlot,                          // 正确的源槽位 ID
                    hotbarSlot,                          // 目标：当前选中的快捷栏
                    SlotActionType.SWAP,
                    player
            );
            return; // 只搬第一组
        }
    }
    public static void update(MinecraftClient client,boolean planTime){
        if(cfg.enableAutoRaid.get()){
            client.options.useKey.setPressed(drink > 0);
            if(hasRaidBossBar(client)) { //如果处于袭击
                drink = 0;
                sup(player.getInventory(), tar);
                /*
                if(planTime){
                    // 2. 遍历所有有效容器，逐个拿取
                    RaidStep=0;
                }
                if(RaidStep>-1){
                    BlockPos pos=RaidIn.get(RaidStep);
                    if (player.getInventory().getEmptySlot() <= 0) {
                        // 背包满了，停止掠夺（尽可能多拿的核心）
                        RaidStep=-1;
                    }
                    interactContainer(client, player, pos);
                    if (player.getInventory().getEmptySlot() > 0) {
                        // 背包满了，停止掠夺（尽可能多拿的核心）
                        RaidStep++;
                        if(RaidStep>=RaidIn.size()){
                            RaidStep=-1;
                        }
                    }

                 */

            }else{
                if (tar==null) {
                    tar=player.getMainHandStack().getItem();
                }
                if (sup(player.getInventory())){
                    drink=1;
                }else{
                    tipGame("自动袭击","无不详之瓶，自动停止");
                    sup(player.getInventory(),tar);
                    tar=null;
                    setV(cfg,cfg.enableAutoRaid,false);
                    client.options.useKey.setPressed(false);

                }

            }
        }else{
            if (tar!=null) {
                tar = null;
            }
        }
    }

    /**
     * 自动使用主手物品（相当于长按右键）
     * 调用后客户端会发送交互包到服务端，触发饮用动作
     */
    public static void useMainHandItem() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.interactionManager == null) return;
        // 确保安全使用（不在冷却中、不是创造模式等）
        if (client.player.getItemUseTimeLeft() > 0) return; // 已经在使用物品中

        // 发送使用物品包，触发饮用
        client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
    }




    // 打开容器 + 快速拿走所有物品（Shift+左键核心逻辑）
    private static void interactContainer(MinecraftClient client, ClientPlayerEntity player, BlockPos pos) {
        // 模拟准星指向方块
        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(pos), net.minecraft.util.math.Direction.UP, pos, false);
        client.crosshairTarget = hit;

        // 右键打开容器
        client.interactionManager.interactBlock(player, Hand.MAIN_HAND, hit);

        // 等待容器界面打开（极短延迟，不卡顿游戏）
        client.execute(() -> {
            try {

                Thread.sleep(100);

                // 检查是否已打开箱子界面
                if (client.currentScreen instanceof HandledScreen<?> screen) {
                    if (screen.getScreenHandler() instanceof GenericContainerScreenHandler containerHandler) {
                        // 执行拿取
                        int rows = containerHandler.getRows();

                        for (int i = 0; i < rows * 9; i++) {

                            Slot slot = containerHandler.getSlot(i);
                            if (slot.hasStack() && !slot.getStack().isEmpty()) {
                                if (countEmptySlots(player.getInventory())<=0) break;
                                client.interactionManager.clickSlot(
                                        screen.getScreenHandler().syncId,
                                        i,
                                        0,
                                        SlotActionType.QUICK_MOVE,
                                        client.player
                                );
                            }
                        }
                    }
                }
                MinecraftClient.getInstance().player.closeHandledScreen();
            } catch (InterruptedException e) {
                error(e);
            }

        });
    }

    /**
     * 计算玩家背包中的空槽位数量
     */
    public static int countEmptySlots(PlayerInventory inventory) {
        int emptySlots = 0;
        // inventory.size() 返回背包总槽位数（包括盔甲、副手等），通常为 41
        // 但主物品栏是索引 0-35，盔甲和副手不用于自动存放，可以限制只检查主物品栏
        for (int i = 0; i < 36; i++) {
            if (inventory.getStack(i).isEmpty()) {
                emptySlots++;
            }
        }
        return emptySlots;
    }
}
