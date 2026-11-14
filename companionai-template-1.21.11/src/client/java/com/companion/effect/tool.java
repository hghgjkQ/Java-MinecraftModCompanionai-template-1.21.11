package com.companion.effect;


import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.companion.CompanionAIClient.*;
import static com.companion.global.*;

public class tool {
    public static boolean isPlayerOpenAir() { //检测玩家释放处于露天高度
        if (player == null) return false;

        World world = player.getEntityWorld();
        BlockPos eye = new BlockPos((int) player.getX(), (int) player.getEyeY(), (int) player.getZ());
        int topY = world.getHeight();      // 世界建筑上限
        for (int y = eye.getY(); y <= topY; y++) {
            if (!world.getBlockState(new BlockPos(eye.getX(), y, eye.getZ())).isAir()) {
                return false;                    // 找到固体方块 → 不露天
            }
        }
        return true;                             // 一路都是空气 → 露天
    }

    public static void center() { //校准玩家位置
        Vec3d pos = player.getEntityPos();
        player.setPos(
                ((int) pos.getX()) + 0.5,
                (int) pos.getY(),
                ((int) pos.getZ()) + 0.5
        );
    }

    public static void tp(double x, double y, double z) {
        Vec3d pos = player.getEntityPos();
        player.setPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
        //防止摔伤代码
        player.setVelocity(0, 0, 0); //加速度设为零
        player.setOnGround(true); //告诉服务端“我已落地”
        player.fallDistance = 0.0F;   // 清除摔落高度
    }

    public static void teleport(double distance) {
        //MinecraftClient client = MinecraftClient.getInstance();
        //ClientPlayerEntity player = client.player;
        if (player == null) return;

        Vec3d look = player.getRotationVec(1F);          // 面朝方向单位向量
        Vec3d target = player.getEntityPos().add(look.multiply(distance));
        player.setPos(target.x, target.y, target.z);     // 本地瞬移
        //防止摔伤代码
        player.setVelocity(0, 0, 0); //加速度设为零
        player.setOnGround(true); //告诉服务端“我已落地”
        player.fallDistance = 0.0F;   // 清除摔落高度
    }
    public static void rush(double distance) {
        //MinecraftClient client = MinecraftClient.getInstance();
        //ClientPlayerEntity player = client.player;
        if (player == null) return;

        Vec3d look = player.getRotationVec(1F).multiply(distance);          // 面朝方向单位向量
        player.addVelocity(look);     // 本地瞬移
        //防止摔伤代码
        player.fallDistance = 0.0F;   // 清除摔落高度
        player.setOnGround(false); //告诉服务端“我没落地”
    }

    public static void tick(long x) { //等待x tick
        try {
            Thread.sleep(50 * x); // 50 ms = 1 tick
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void tick() { //等待1 tick
        try {
            Thread.sleep(50); // 50 ms = 1 tick
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sleep(long x) {
        try {
            Thread.sleep(x); // 50 ms = 1 tick
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void say(Object x) { //广播信息

        MinecraftClient.getInstance().execute(() -> {
            player.networkHandler.sendChatMessage(String.valueOf(x));
        });
    }

    public static void tip(Object x) { //提示玩家
        MinecraftClient.getInstance().execute(() -> {
            player.sendMessage(Text.literal(String.valueOf(x)), false);
        });
    }
    public static void tipGame(Object x) { //提示玩家
        MinecraftClient.getInstance().execute(() -> {
            player.sendMessage(Text.literal("§a["+ModName+"]"+ x), false);
        });
    }
    public static void tipGame(Object a,Object x) { //提示玩家
        MinecraftClient.getInstance().execute(() -> {
            player.sendMessage(Text.literal("§a["+ModName+"]§b["+a+"]§a"+ x), false);
        });
    }
    public static void tipGame(Object a,Text x) { //提示玩家
        MinecraftClient.getInstance().execute(() -> {
            player.sendMessage(Text.literal("§a["+ModName+"]§b["+a+"]§a").append(x), false);
        });
    }

    public static void tip(Object x,boolean o) { //提示玩家
        MinecraftClient.getInstance().execute(() -> {
            player.sendMessage(Text.literal(String.valueOf(x)), o);
        });
    }

    public static void error(Object x) { //报错玩家
        // 0: getStackTrace, 1: logCaller, 2: 真正的调用者
        StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
        MinecraftClient.getInstance().execute(() -> {
            player.sendMessage(Text.literal("§4["+ModName+"]出现错误：\n"+String.format("调用位置 -> %s.%s(%s:%d)%n", caller.getClassName(), caller.getMethodName(), caller.getFileName(), caller.getLineNumber())+"\n"+x), false);
        });
    }

    public static double sub_sym(double a, double b) {
        double c = a - b;
        if (c == 0) {
            return 0;
        } else {
            return c / Math.abs(c);
        }
    }

    public static int playerMaxOpenY() {
        if (player == null || player.getEntityWorld() == null) { //玩家位置最高露天方块
            return 320; // 确保玩家和世界不为空
        }

        BlockPos playerPos = new BlockPos((int) player.getX(), (int) player.getEyeY(), (int) player.getZ());
        int highestBlockY = Integer.MIN_VALUE; // 初始化为可能的最低高度

        // 从玩家位置向下遍历，直到找到第一个非空气方块
        for (int y = playerPos.getY(); y >= player.getEntityWorld().getBottomY(); y--) {
            BlockPos checkPos = new BlockPos(playerPos.getX(), y, playerPos.getZ());
            if (!player.getEntityWorld().isAir(checkPos)) {
                highestBlockY = y; // 找到非空气方块，记录Y坐标
                break; // 找到非空气方块后停止遍历
            }
        }

        return highestBlockY;
    }
    public static double getPlayerHeightAboveGround() {
        if (player == null || player.getEntityWorld() == null) {
            return 0.0;
        }

        World world = player.getEntityWorld();
        Box playerBoundingBox = player.getBoundingBox();
        double minHeight = Double.MAX_VALUE;
        boolean foundGround = false;

        // 检查玩家碰撞箱四个角以及中心点
        Vec3d[] checkPoints = {
                new Vec3d(playerBoundingBox.minX, playerBoundingBox.minY, playerBoundingBox.minZ),
                new Vec3d(playerBoundingBox.maxX, playerBoundingBox.minY, playerBoundingBox.minZ),
                new Vec3d(playerBoundingBox.minX, playerBoundingBox.minY, playerBoundingBox.maxZ),
                new Vec3d(playerBoundingBox.maxX, playerBoundingBox.minY, playerBoundingBox.maxZ),
                new Vec3d(player.getX(), playerBoundingBox.minY, player.getZ()) // 中心点
        };

        for (Vec3d point : checkPoints) {
            RaycastContext context = new RaycastContext(
                    point,
                    new Vec3d(point.x, world.getBottomY(), point.z),
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    player
            );

            BlockHitResult hit = world.raycast(context);
            if (hit.getType() == HitResult.Type.BLOCK) {
                double height = point.y - hit.getPos().y;
                minHeight = Math.min(minHeight, height);
                foundGround = true;
            }
        }

        if (foundGround) {
            return minHeight;
        }

        // 如果没有击中任何方块，返回玩家到世界底部的最小距离
        return playerBoundingBox.minY - world.getBottomY();
    }

    /**
     * 为了便于进行跃迁的方法
     * @param leap_x 目标x
     * @param leap_y 目标y
     * @param leap_z 目标z
     * @param leap_distance 最大跃迁距离
     */
    public static boolean miniLeap(double leap_x,double leap_y,double leap_z,double leap_distance){

        double subX = leap_x - player.getEntityPos().getX();
        double subY = leap_y - player.getEntityPos().getY();
        double subZ =  leap_z - player.getEntityPos().getZ();
        double d =Math.sqrt(subX * subX + subZ * subZ+subY*subY); //距离
        if(d>leap_distance) {
            double scale = leap_distance / d; // 计算水平距离后归一化放大 10 倍
            tp(
                    subX * scale,
                    subY * scale,
                    subZ * scale
            );
            return false;
        }else {
            tp(
                    subX,
                    subY,
                    subZ
            );
            return true;
        }
    }
    /**
     * 为了便于进行跃迁的方法，最大跃迁距离为10
     * @param leap_x 目标x
     * @param leap_y 目标y
     * @param leap_z 目标z
     */
    public static boolean miniLeap(double leap_x,double leap_y,double leap_z){

        double subX = leap_x - player.getEntityPos().getX();
        double subY = leap_y - player.getEntityPos().getY();
        double subZ =  leap_z - player.getEntityPos().getZ();
        double d =Math.sqrt(subX * subX + subZ * subZ+subY*subY); //距离
        double leap_distance=10;
        if(d>leap_distance) {
            double scale = leap_distance / d; // 计算水平距离后归一化放大 10 倍
            tp(
                    subX * scale,
                    subY * scale,
                    subZ * scale

            );
            return false;
        }else {
            tp(
                    subX,
                    subY,
                    subZ
            );
            return true;
        }
    }
    /**
     * @param pos 目标
     * @param leap_distance 最大跃迁距离
     */
    public static boolean miniLeap(Vec3d pos,double leap_distance){

        double subX = pos.getX() - player.getEntityPos().getX();
        double subY = pos.getY() - player.getEntityPos().getY();
        double subZ =  pos.getZ() - player.getEntityPos().getZ();
        double d =Math.sqrt(subX * subX + subZ * subZ+subY*subY); //距离
        if(d>leap_distance) {
            double scale = leap_distance / d; // 计算水平距离后归一化放大 10 倍
            tp(
                    subX * scale,
                    subY * scale,
                    subZ * scale

            );
            return false;
        }else {
            tp(
                    subX,
                    subY,
                    subZ
            );
            return true;
        }
    }
    public static List<PlayerEntity> getAllLoadedPlayerEntities() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null) {
            return List.of(); // 返回空列表，避免null
        }

        World world = client.world;
        // 核心修复：将List<? extends PlayerEntity>转换为List<PlayerEntity>
        List<? extends PlayerEntity> rawPlayers = world.getPlayers();
        // 流式处理重新收集，显式指定类型为PlayerEntity
        List<PlayerEntity> allPlayers = rawPlayers.stream()
                .map(player -> (PlayerEntity) player) // 强制转换（类型安全，因为都是PlayerEntity子类）
                .collect(Collectors.toList());

        return allPlayers;
    }
    public static Vec3d randomR(double R){
        double angle = ThreadLocalRandom.current().nextDouble(Math.PI * 2);
        return new Vec3d(Math.cos(angle)*R,0,Math.sin(angle)*R);
    }

}
