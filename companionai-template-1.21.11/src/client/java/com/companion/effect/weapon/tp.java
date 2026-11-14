package com.companion.effect.weapon;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import static com.companion.global.*;
import static com.companion.effect.tool.tip;
import static com.companion.effect.weapon.base.sendPos;

public class tp {
    public static void throw_END(ItemStack main){
        tip(111);
        if(true || main.getItem() == Items.ENDER_PEARL ){

            double x = player.getX();
            double y = player.getY();
            double z = player.getZ();

            // 计算调整后的发射威力：配置值归一化后乘以√500（原版威力计算公式适配）
            double power = 50;
            // 获取玩家视线方向向量，并按调整后的威力缩放
            Vec3d lookVec = player.getRotationVec(1.0F).multiply(power);

            // 发送4次原位置、地面状态为true的移动包（欺骗服务端的核心步骤）
            for (int i = 0; i < 4; ++i)
                sendPos(x, y, z, false);

            // 发送视线反方向偏移的位置包，强化欺骗效果
            sendPos(x - lookVec.x, y - lookVec.y, z - lookVec.z, false);
            // 发送原位置、地面状态为false的移动包，还原玩家位置状态
            sendPos(x, y, z, false);

            MinecraftClient.getInstance().interactionManager.interactItem(player, Hand.MAIN_HAND);

        }
    }
}
