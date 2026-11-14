package com.companion.skills.MiningBot;

import com.companion.ParticleManage;
import com.companion.draw.base;
import com.companion.draw.mix;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashSet;
import java.util.Set;

import static com.companion.effect.tool.miniLeap;
import static com.companion.effect.tool.tipGame;
import static com.companion.global.*;
import static com.companion.skills.MiningBot.FindPos.findNearestAncientDebris;

public class MiningBot {

    public static class ShulkerBoxResult {
        public int rawSlot = -1;
        public int emptySlots = -1;
        public ItemStack boxStack = ItemStack.EMPTY;
        public boolean found() { return rawSlot != -1; }
    }

    private static int countShulkerEmptySlots(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock block)) {
            return -1;
        }
        // 创建临时潜影盒方块实体
        ShulkerBoxBlockEntity be = new ShulkerBoxBlockEntity(
                block.getColor(),
                BlockPos.ORIGIN,
                Blocks.SHULKER_BOX.getDefaultState()
        );
        // 把物品组件数据读取到方块实体中
        //be.readComponents(stack.getComponents());

        // 统计空格（潜影盒固定27格）
        int empty = 0;
        for (int i = 0; i < be.size(); i++) {
            if (be.getStack(i).isEmpty()) empty++;
        }
        return empty;
    }



    private static void draw(){
        if(MiningBotGoalPos!=null) {
            mix.block(MiningBotGoalPos.toCenterPos(),3, ParticleManage.RED_POINT_PARTICLE);
        }
        if(!MiningBotGoalPath.isEmpty()){
            for(BlockPos a:MiningBotGoalPath) {
                base.point(ParticleManage.YELLOW_POINT_PARTICLE,a.toCenterPos());
            }
        }
    }



    public static void TickRun(){
        if(all_client.world==null) return;
        if(MiningBotState){
            ThrowStuff(MiningBotStayItem); //清理背包

            if(!hasEmptyInventorySlot()){
                for (BlockPos p : MiningBotOldPath){
                    if(p.toCenterPos().squaredDistanceTo(player.getEntityPos())<5*5 && all_client.world.getBlockState(p.up()).isAir()){

                    }
                }
            }

            if(MiningBotGoalPos==null){
                MiningBotState=false;
                tipGame("挖矿机器人","未找到任何矿物，被迫终止");
            }else{
                if(MiningBotGoalPath.isEmpty()){
                    MiningBotGoalPath = FindPath.findPath(all_client.world,player.getBlockPos(),MiningBotGoalPos);
                    if(MiningBotGoalPath.isEmpty()){
                        MiningBotBlockBan.add(MiningBotGoalPos);
                    }else{
                        MiningBotGoalPathIndex=0;
                    }
                }else {
                    if (MiningBotGoalPath.size() > MiningBotGoalPathIndex) {
                        if (all_client.world.getBlockState(MiningBotGoalPath.get(MiningBotGoalPathIndex)).isAir()) {
                            if (all_client.world.getBlockState(MiningBotGoalPath.get(MiningBotGoalPathIndex).up()).isAir()) {
                                if (miniLeap(MiningBotGoalPath.get(MiningBotGoalPathIndex).toCenterPos().add(0,-0.5,0), 1)) {
                                    MiningBotOldPath.add(MiningBotGoalPath.get(MiningBotGoalPathIndex).up());
                                    MiningBotOldPath.add(MiningBotGoalPath.get(MiningBotGoalPathIndex));
                                    MiningBotGoalPathIndex++;
                                }
                            } else {
                                dig(MiningBotGoalPath.get(MiningBotGoalPathIndex).up());
                            }
                        } else {
                            dig(MiningBotGoalPath.get(MiningBotGoalPathIndex));
                        }
                    }else{ //走完了
                        if (all_client.world.getBlockState(MiningBotGoalPos).isAir()){
                            MiningBotGoalPath.clear();
                            MiningBotGoalPathIndex=0;
                            MiningBotGoalPos=findNearestAncientDebris(100);
                            MiningBotOldPath.add(MiningBotGoalPos);
                        }else{
                            dig(MiningBotGoalPos);
                        }
                    }
                }
            }
        }
        draw();
    }
    private static void dig(BlockPos pos){
        if (all_client.interactionManager != null) {
            if(!all_client.interactionManager.isBreakingBlock()) {
                all_client.interactionManager.attackBlock(pos, Direction.DOWN);
            }else{
                all_client.interactionManager.updateBlockBreakingProgress(pos, Direction.DOWN);
            }
        }
    }

    private static boolean hasEmptyInventorySlot() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerInteractionManager im = mc.interactionManager;
        PlayerEntity player = mc.player;

        // 空值防护
        if (im == null || player == null) {
            return false;
        }

        PlayerInventory inv = player.getInventory();
        // 遍历主背包+快捷栏 0~35，盔甲副手不计算
        for (int rawSlot = 0; rawSlot < 36; rawSlot++) {
            ItemStack stack = inv.getStack(rawSlot);
            if (stack.isEmpty()) {
                return true; // 找到空格直接返回true
            }
        }
        return false; // 36格全部填满
    }

    private static void ThrowStuff(Set<Item> filter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerInteractionManager im = mc.interactionManager;
        player = mc.player;

        // 空值防护
        if (im == null || player == null || filter == null) {
            return;
        }

        PlayerInventory inv = player.getInventory();

        // 遍历范围：0-35（快捷栏+主背包）
        // 跳过：36-39（盔甲）、40（副手） → 穿盔甲完全不影响
        for (int rawSlot = 0; rawSlot < 36; rawSlot++) {
            ItemStack stack = inv.getStack(rawSlot);
            // 空物品 / 在白名单内 → 跳过
            if (stack.isEmpty() || filter.contains(stack.getItem())) {
                continue;
            }

            // ============== 1.21 标准槽位映射（唯一正确写法） ==============
            int containerSlot;
            if (rawSlot < 9) {
                // 快捷栏 0-8 → 容器槽 36-44
                containerSlot = rawSlot + 36;
            } else {
                // 主背包 9-35 → 容器槽 9-35
                containerSlot = rawSlot;
            }

            // 扔出物品（同步客户端标准逻辑）
            im.clickSlot(0, containerSlot, 1, SlotActionType.THROW, player);
        }
    }
    public static Set<Item> getPlayerInventoryItems() {
        MinecraftClient mc = MinecraftClient.getInstance();
        Set<Item> items = new HashSet<>();
        if (mc.player == null) return items;

        PlayerInventory inv = mc.player.getInventory();

        // 背包+快捷栏
        for (ItemStack stack : inv.getMainStacks())
            if (!stack.isEmpty()) items.add(stack.getItem());
        return items;
    }
}

