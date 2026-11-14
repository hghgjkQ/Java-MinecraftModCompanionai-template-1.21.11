package com.companion;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ChestPosStorage {
    private static BlockPos currentChestPos=null;

    public static void setCurrentChestPos(BlockPos pos) {
        currentChestPos = pos;
    }

    public static BlockPos getCurrentChestPos() {
        return currentChestPos;
    }

    public static void clear() {
        currentChestPos = null;
    }
    /**
     * 获取双箱的最小坐标（xyz和最小）
     * @param clickedPos 玩家点击的箱子坐标
     * @param world 客户端世界
     * @return 最小坐标（如果是单箱则返回原坐标）
     */
    public static BlockPos getMinChestPos(BlockPos clickedPos, ClientWorld world) {
        BlockState state = world.getBlockState(clickedPos);

        // 检查是不是箱子
        if (!(state.getBlock() instanceof ChestBlock)) {
            return clickedPos;
        }

        // 获取箱子朝向
        Direction facing = state.get(ChestBlock.FACING);

        // 获取箱子类型（单箱、左半、右半）
        ChestType chestType = state.get(ChestBlock.CHEST_TYPE);

        if (chestType == ChestType.SINGLE) {
            // 单箱，直接返回
            return clickedPos;
        }

        // 计算另一半的坐标
        // 注意：CHEST_TYPE 的 LEFT/RIGHT 是相对于玩家视角的
        // LEFT = 玩家在左半边，RIGHT = 玩家在右半边
        // 但实际上，从方块朝向看：
        // 如果 facing 是北（Z-），左半在西（X-），右半在东（X+）
        // 需要找到与 facing 垂直的方向

        Direction otherHalfDir;
        if (chestType == ChestType.LEFT) {
            // 当前是左半，另一半在右边（顺时针90度）
            otherHalfDir = facing.rotateYClockwise();
        } else {
            // 当前是右半，另一半在左边（逆时针90度）
            otherHalfDir = facing.rotateYCounterclockwise();
        }

        BlockPos otherPos = clickedPos.offset(otherHalfDir);

        // 验证另一半确实是箱子
        BlockState otherState = world.getBlockState(otherPos);
        if (!(otherState.getBlock() instanceof ChestBlock)) {
            // 不是双箱，返回原坐标
            return clickedPos;
        }

        // 比较两个坐标，返回 xyz 和最小的
        return comparePos(clickedPos, otherPos) <= 0 ? clickedPos : otherPos;
    }

    private static int comparePos(BlockPos a, BlockPos b) {
        if (a.getX() != b.getX()) return Integer.compare(a.getX(), b.getX());
        if (a.getY() != b.getY()) return Integer.compare(a.getY(), b.getY());
        return Integer.compare(a.getZ(), b.getZ());
    }
}