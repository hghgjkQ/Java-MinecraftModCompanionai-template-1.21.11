package com.companion.Key;

import com.companion.System.findEndCity;
import com.companion.effect.ability;
import com.companion.effect.weapon.crossbow;
import com.companion.effect.weapon.hook;
import com.companion.effect.weapon.raid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import static com.companion.Key.configTool.cfg;
import static com.companion.global.*;
import static com.companion.effect.tool.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.List;

import static com.companion.effect.weapon.tp.throw_END;
import static com.companion.skills.Tkill.*;

import static com.companion.global.*;
@Environment(EnvType.CLIENT)
public class main {

    // 3. 响应
    public static void start() {
        com.companion.Key.tool.reg();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(com.companion.Key.tool.keyBox[0].wasPressed()) {
                if (client.player != null) client.execute(main::throwA);
            }
            if(com.companion.Key.tool.keyBox[1].wasPressed()) {
                if (client.player != null) client.execute(main::trace);
            }
            if(com.companion.Key.tool.keyBox[2].wasPressed()) {
                if (client.player != null) client.execute(crossbow::setControl);
            }
            if(com.companion.Key.tool.keyBox[3].wasPressed()) {
                if (client.player != null) client.execute(()-> {

                    raid.m();

                });
            }
            if(com.companion.Key.tool.keyBox[4].wasPressed()) {
                if (client.player != null) client.execute(() -> {
                    throw_END(player.getMainHandStack());

                });
            }
            if(com.companion.Key.tool.keyBox[5].wasPressed()) {
                if (client.player != null) client.execute(() -> {
                    client.setScreen(new configTool(client.currentScreen));

                });
            }
            if(com.companion.Key.tool.keyBox[6].wasPressed()) {
                if (client.player != null) client.execute(hook::retractFishingRod);
            }

        });
    }
    private static Vec3d playerPos = new Vec3d(0,0,0); //速度显示
    public static void speed(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> updateSpeedometer());
    }



    private static void updateSpeedometer() {
        if (cfg.enableSpeedShow.get())
            if ((player != null)) {
                double v = playerPos.distanceTo(player.getEntityPos());
                playerPos = player.getEntityPos();
                tip(String.format("当前速度：§6%.2f§fB/T（§6%.2f§fkm/h）", v, v * 72), true);
                if (traceDMGentity != null && !traceDMGentity.isAlive()) {
                    traceDMGentity = null;
                }
            }

    }
    public static void trace(){
        traceDMGentity=getEyeEntity(100);
        if (traceDMGentity != null) {
            TraceEntityHeart=traceDMGentity.getHealth();
        }
    }

    public static void throwA(){
        TarEntity = getNearestPlayer(player);

    }
    // 以指定玩家为原点，获取世界中最近的其他有效玩家（无最大搜索半径，1.21.11专属最简版）
    public static LivingEntity getNearestPlayer(PlayerEntity source) {
        // 仅做核心非空校验，最简判空
        if (source == null) return null;
        World level= source.getEntityWorld();
        List<PlayerEntity> players = List.copyOf(level.getPlayers());
        // 无玩家/仅自身，直接返回null
        if (players.size() <= 1) return null;

        PlayerEntity nearest = null;
        double minDistSq = Double.MAX_VALUE;
        // 遍历+核心有效玩家筛选，无冗余逻辑
        for (PlayerEntity p : players) {
            // 有效玩家：非自身 + 存活 + 已加载（1.21.11有效状态核心判定）
            if (p != source && p.isAlive()) {
                double distSq = source.distanceTo(p); // 原生距离平方，无需手动计算
                if (distSq < minDistSq) {
                    minDistSq = distSq;
                    nearest = p;
                }
            }
        }
        return nearest;
    }

}
