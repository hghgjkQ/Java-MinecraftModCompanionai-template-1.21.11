package com.companion.effect.weapon;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

import static com.companion.Key.configTool.cfg;
import static com.companion.effect.ability.*;
import static com.companion.effect.weapon.base.*;
import static com.companion.effect.weapon.tool.*;

import static com.companion.global.*;
import static com.companion.global.player;
import static com.companion.skills.Tkill.getEyeEntity;

public class bow {
    public static void bow( ItemStack main,ItemStack main2){
        var player = MinecraftClient.getInstance().player;
        if (cfg.RaidState.get()) {
            if (cfg.BowState.get() && main2.getItem() == Items.BOW) {
                if (player.getItemUseTime() >4) {
                    // 获取玩家当前三维坐标
                    double x = player.getX();
                    double y = player.getY();
                    double z = player.getZ();

                    // 计算调整后的发射威力：配置值归一化后乘以√500（原版威力计算公式适配）
                    double power = 10;
                    double adjustedStrength = power / 10.0 * Math.sqrt(500.0);
                    // 获取玩家视线方向向量，并按调整后的威力缩放
                    Vec3d lookVec = player.getRotationVec(1.0F).multiply(adjustedStrength);


                    // 发送4次原位置、地面状态为true的移动包（欺骗服务端的核心步骤）
                    for (int i = 0; i < 4; ++i)
                        sendPos(x, y, z, false);

                    // 发送视线反方向偏移的位置包，强化欺骗效果
                    sendPos(x - lookVec.x, y - lookVec.y, z - lookVec.z, false);
                    // 发送原位置、地面状态为false的移动包，还原玩家位置状态
                    sendPos(x, y, z, false);
                    releaseBow();

                    LivingEntity e = getEyeEntity(1000);
                    var client = MinecraftClient.getInstance();
                    if (e != null && e.isAlive() && e.getHealth() > 0) {
                        Objects.requireNonNull(client.getNetworkHandler()).sendPacket(
                                new PlayerMoveC2SPacket.Full(
                                        e.getX(), e.getY(), e.getZ(),
                                        client.player.getYaw(), client.player.getPitch(),
                                        false, false
                                )
                        );
                        // 自动切换到该槽位
                        for (int i = 0; i < 4; ++i)
                            sendPos(x, y, z, false);

                        // 发送视线反方向偏移的位置包，强化欺骗效果
                        sendPos(x, y + 20, z, false);
                        // 发送原位置、地面状态为false的移动包，还原玩家位置状态
                        sendPos(x, y, z, false);
                        PlayerInteractEntityC2SPacket attackPacket = PlayerInteractEntityC2SPacket.attack(e, client.player.isSneaking());
                        client.player.networkHandler.sendPacket(attackPacket);
                        // 6. (可选) 播放客户端动画和挥臂效果，提升体验
                        client.player.swingHand(Hand.MAIN_HAND);
                        player.setVelocity(0,0,0);
                    }
                }
            }
        }else{
            if (cfg.BowState.get() && main.getItem() == Items.BOW) {
                if (player.getItemUseTime() >10) {
                    // 获取玩家当前三维坐标
                    double x = player.getX();
                    double y = player.getY();
                    double z = player.getZ();

                    // 计算调整后的发射威力：配置值归一化后乘以√500（原版威力计算公式适配）
                    double power = 10;
                    double adjustedStrength = power / 10.0 * Math.sqrt(500.0);
                    // 获取玩家视线方向向量，并按调整后的威力缩放
                    Vec3d lookVec = player.getRotationVec(1.0F).multiply(adjustedStrength);


                    // 发送4次原位置、地面状态为true的移动包（欺骗服务端的核心步骤）
                    for (int i = 0; i < 4; ++i)
                        sendPos(x, y, z, false);

                    // 发送视线反方向偏移的位置包，强化欺骗效果
                    sendPos(x - lookVec.x, y - lookVec.y, z - lookVec.z, false);
                    // 发送原位置、地面状态为false的移动包，还原玩家位置状态
                    sendPos(x, y, z, false);
                    releaseBow();
                }
            }

        }
    }

}
