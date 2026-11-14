package com.companion.effect.weapon;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static com.companion.Key.configTool.cfg;
import static com.companion.effect.tool.tipGame;

public class Elytra {
    // ==============================================
    // ✅ 新增：记录上一帧各方向键的状态（最小侵入，仅加变量）
    // ==============================================
    private static boolean forwardWasPressed = false;
    private static boolean backWasPressed = false;
    private static boolean leftWasPressed = false;
    private static boolean rightWasPressed = false;
    private static boolean jumpWasPressed = false;
    private static boolean sneakWasPressed = false;

    public static void update(MinecraftClient client){
        ClientPlayerEntity player= client.player;
        if (player == null) return;
        if(!cfg.EasyElytraState.get()) return;

        Vec3d tarPos=player.getEntityPos(); // ✅ 修正：用官方推荐的 getPos()
        float yaw = player.getYaw();
        var options = client.options;

        if(player.isGliding()){
            player.setVelocity(0,0.1,0);
            player.setOnGround(false);
            player.fallDistance=0;

            // ==============================================
            // ✅ 新增：疾跑键模式切换（最小侵入，仅加判断分支）
            // ==============================================
            if (options.sprintKey.isPressed()) {
                // 【疾跑模式】：按一次动一格，长按不算
                tarPos = handleStepMove(player, yaw, options);
            } else {
                // 【原模式】：保留原有逻辑，最小侵入
                tarPos = handleOriginalMove(player, yaw, options, tarPos);
            }

            // ==============================================
            // ✅ 保留原有摄像机判断和位置设置
            // ==============================================
            forwardWasPressed = options.forwardKey.isPressed();
            backWasPressed = options.backKey.isPressed();
            leftWasPressed = options.leftKey.isPressed();
            rightWasPressed = options.rightKey.isPressed();
            jumpWasPressed = options.jumpKey.isPressed();
            sneakWasPressed = options.sneakKey.isPressed();
            if (player.getEntityPos().equals(tarPos) || client.getCameraEntity() != player) {
                player.setPos(player.getBlockX()+0.5, player.getBlockY()+0.5, player.getBlockZ()+0.5);
                return;
            }

            Vec3d realPos = getRealWalkBlock(player, tarPos).toCenterPos();
            player.setPos(realPos.x, realPos.y, realPos.z);


        }
    }

    // ==============================================
    // ✅ 新增：疾跑模式的「一格移动」逻辑（独立方法，最小侵入）
    // ==============================================
    private static Vec3d handleStepMove(ClientPlayerEntity player, float yaw, GameOptions options) {
        Vec3d tarPos = player.getEntityPos();
        double step =1.1; // 固定移动1格

        // 检测「按下瞬间」（上升沿：上一帧没按，这一帧按了）
        if (options.forwardKey.isPressed() && !forwardWasPressed) {
            float radians = (float) Math.toRadians(yaw);
            double x = -MathHelper.sin(radians);
            double z = MathHelper.cos(radians);
            tarPos = tarPos.add(new Vec3d(x, 0, z).multiply(step));
        }
        if (options.leftKey.isPressed() && !leftWasPressed) {
            float radians = (float) Math.toRadians(yaw - 90);
            double x = -MathHelper.sin(radians);
            double z = MathHelper.cos(radians);
            tarPos = tarPos.add(new Vec3d(x, 0, z).multiply(step));
        }
        if (options.rightKey.isPressed() && !rightWasPressed) {
            float radians = (float) Math.toRadians(yaw + 90);
            double x = -MathHelper.sin(radians);
            double z = MathHelper.cos(radians);
            tarPos = tarPos.add(new Vec3d(x, 0, z).multiply(step));
        }
        if (options.backKey.isPressed() && !backWasPressed) {
            float radians = (float) Math.toRadians(yaw + 180);
            double x = -MathHelper.sin(radians);
            double z = MathHelper.cos(radians);
            tarPos = tarPos.add(new Vec3d(x, 0, z).multiply(step));
        }
        if (options.jumpKey.isPressed() && !jumpWasPressed) {
            tarPos = tarPos.add(0, step, 0);
        }
        if (options.sneakKey.isPressed() && !sneakWasPressed) {
            tarPos = tarPos.add(0, -step, 0);
        }

        return tarPos;
    }

    // ==============================================
    // ✅ 提取原有移动逻辑为独立方法（最小侵入，不破坏原代码）
    // ==============================================
    private static Vec3d handleOriginalMove(ClientPlayerEntity player, float yaw, GameOptions options, Vec3d tarPos) {
        if (options.forwardKey.isPressed()) {
            float radians = (float) Math.toRadians(yaw);
            double x = -MathHelper.sin(radians);
            double z = MathHelper.cos(radians);
            tarPos = tarPos.add(new Vec3d(x, 0, z).multiply(cfg.ElytraSpeed));
        }
        if (options.leftKey.isPressed()) {
            float radians = (float) Math.toRadians(yaw - 90);
            double x = -MathHelper.sin(radians);
            double z = MathHelper.cos(radians);
            tarPos = tarPos.add(new Vec3d(x, 0, z).multiply(cfg.ElytraSpeed));
        }
        if (options.rightKey.isPressed()) {
            float radians = (float) Math.toRadians(yaw + 90);
            double x = -MathHelper.sin(radians);
            double z = MathHelper.cos(radians);
            tarPos = tarPos.add(new Vec3d(x, 0, z).multiply(cfg.ElytraSpeed));
        }
        if (options.backKey.isPressed()) {
            float radians = (float) Math.toRadians(yaw + 180);
            double x = -MathHelper.sin(radians);
            double z = MathHelper.cos(radians);
            tarPos = tarPos.add(new Vec3d(x, 0, z).multiply(cfg.ElytraSpeed));
        }
        if (options.jumpKey.isPressed()) {
            tarPos = tarPos.add(0, cfg.ElytraSpeed, 0);
        }
        if (options.sneakKey.isPressed()) {
            tarPos = tarPos.add(0, -cfg.ElytraSpeed, 0);
        }
        return tarPos;
    }

    // ==============================================
    // ✅ 保留原有碰撞检测方法（完全不变）
    // ==============================================
    public static Vec3d getRealWalkPosition(ClientPlayerEntity player, Vec3d targetPos) {
        World world = player.getEntityWorld();
        Vec3d playerPos = player.getEntityPos();

        Vec3d desiredMove = new Vec3d(
                targetPos.x - playerPos.x,
                targetPos.y - playerPos.y,
                targetPos.z - playerPos.z
        );

        Box playerBox = player.getDimensions(player.getPose()).getBoxAt(playerPos);
        double maxExpand = Math.max(Math.abs(desiredMove.x), Math.abs(desiredMove.z));
        Box collisionArea = playerBox.expand(maxExpand);

        Iterable<VoxelShape> shapeIterable = world.getBlockCollisions(player, collisionArea);
        List<VoxelShape> collisions = new ArrayList<>();
        for (VoxelShape shape : shapeIterable) {
            collisions.add(shape);
        }

        Vec3d validMove = Entity.adjustMovementForCollisions(
                player,
                desiredMove,
                playerBox,
                world,
                collisions
        );

        return playerPos.add(validMove);
    }

    public static BlockPos getRealWalkBlock(ClientPlayerEntity player, Vec3d targetPos) {
        return BlockPos.ofFloored(getRealWalkPosition(player, targetPos));
    }
}