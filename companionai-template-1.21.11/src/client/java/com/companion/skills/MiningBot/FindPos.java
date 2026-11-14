package com.companion.skills.MiningBot;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.companion.global.*;
import static com.companion.skills.MiningBot.FindPath.AvoidBlock;

public class FindPos {
    private static final BlockPos[] offsets = {
            BlockPos.ORIGIN,          // 自身
            new BlockPos(0,1,0),      // 上
            new BlockPos(1,0,0),      // 上上
            new BlockPos(0,-1,0),     // 下
            new BlockPos(0,0,1),      // 东
            new BlockPos(-1,0,0),     // 西
            new BlockPos(0,0,-1),      // 南

    };
    /**
     * 搜索最近的下界残骸（方块 + 地上掉落物）
     * @param radius 搜索半径
     * @return 最近的目标坐标
     */
    public static BlockPos findNearestAncientDebris(int radius) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return null;

        World world = mc.world;
        Vec3d playerPos = mc.player.getEntityPos();
        BlockPos playerBlockPos = mc.player.getBlockPos();

        BlockPos nearest = null;
        double minDistSq = Double.MAX_VALUE;

        // ========== 1. 搜索 远古残骸 方块 (1.21.11 标准写法) ==========
        boolean yes;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = MiningBotMinY; y <= MiningBotMaxY; y++) {
                    BlockPos pos = playerBlockPos.add(x, 0, z);
                    pos=new BlockPos(pos.getX(),y,pos.getZ());

                    yes=true;
                    // 判断方块 + 黑名单过滤
                    if (MiningBotGoalBlock .contains(world.getBlockState(pos).getBlock()) && !MiningBotBlockBan.contains(pos)) {
                        for (BlockPos offset : offsets) { //判断这个能不能挖
                            BlockPos checkPos = pos.add(offset);
                            if(AvoidBlock.contains(world.getBlockState(checkPos).getBlock()) && !MiningBotOldPath.contains(checkPos)){ //检测可挖性
                                yes=false;
                                break;
                            }
                        }
                        if (!yes){
                            continue;
                        }
                        double distSq = playerPos.squaredDistanceTo(Vec3d.ofCenter(pos));
                        if (distSq < minDistSq) {
                            minDistSq = distSq;
                            nearest = pos;
                        }
                    }
                }
            }
        }

        // ========== 2. 搜索 地上的远古残骸掉落物 (1.21.11 官方API) ==========
        // 搜索范围盒子
        Box searchBox = new Box(
                playerPos.x - radius, playerPos.y - 20, playerPos.z - radius,
                playerPos.x + radius, playerPos.y + 20, playerPos.z + radius
        );

        // 1.21.11 正确获取物品实体的方式
        for (ItemEntity itemEntity : world.getEntitiesByClass(ItemEntity.class, searchBox, entity -> {
            // 过滤：只留远古残骸掉落物
            return entity != null && MiningBotGoalItems.contains(entity.getStack().getItem());
        })) {
            BlockPos itemPos = itemEntity.getBlockPos();
            double distSq = playerPos.squaredDistanceTo(itemEntity.getEntityPos());

            // 更新最近目标
            if (distSq < minDistSq) {
                minDistSq = distSq;
                nearest = itemPos;
            }
        }

        return nearest;
    }
}