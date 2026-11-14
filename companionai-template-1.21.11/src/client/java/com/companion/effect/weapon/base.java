package com.companion.effect.weapon;

import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import static com.companion.effect.weapon.tool.*;
import static com.companion.global.*;

public class base {
    public static void sendPos(double x, double y, double z, boolean onGround, float ya, float p)
    {
        // 获取网络处理器并发送仅位置的移动包，携带玩家当前旋转角度
        if (all_client.player != null) {
            all_client.player.networkHandler.sendPacket(
                    new PlayerMoveC2SPacket.Full(
                            new Vec3d(x,y,z),ya,p, onGround,player.horizontalCollision));
        }
    }
    public static void sendPos(double x, double y, double z, boolean onGround)
    {
        // 获取网络处理器并发送仅位置的移动包，携带玩家当前旋转角度
        if (all_client.player != null) {
            all_client.player.networkHandler.sendPacket(
                    new PlayerMoveC2SPacket.PositionAndOnGround(
                            x,y,z, onGround,player.horizontalCollision));
        }
    }

    public static void startCharging() {
        PlayerInteractItemC2SPacket startPacket = new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0,player.getYaw(),player.getPitch());
        player.networkHandler.sendPacket(startPacket);
    }
    // 停止拉弓/发射（发送停止包）
    public static void releaseBow() {
        PlayerActionC2SPacket releasePacket = new PlayerActionC2SPacket(
                PlayerActionC2SPacket.Action.RELEASE_USE_ITEM,
                player.getBlockPos(),
                player.getHorizontalFacing()
        );
        player.networkHandler.sendPacket(releasePacket);
    }

}
