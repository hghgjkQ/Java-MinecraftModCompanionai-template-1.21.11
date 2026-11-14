package com.companion.effect;


import com.companion.draw.mix;
import com.companion.effect.weapon.Elytra;
import com.companion.effect.weapon.crossbow;
import com.companion.effect.weapon.hook;
import com.companion.effect.weapon.raid;
import com.companion.skills.GetNRNBCleanTime;
import com.companion.skills.MiningBot.MiningBot;
import com.companion.skills.bat.AutoRaid;
import com.companion.skills.leap;
import net.minecraft.client.MinecraftClient;

import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.companion.Key.configTool.cfg;
import static com.companion.global.*;
import static com.companion.ParticleManage.*;
import static com.companion.effect.weapon.Spear.spear;
import static com.companion.effect.weapon.base.sendPos;
import static com.companion.effect.weapon.tool.*;


import static com.companion.draw.base.line;

import static com.companion.effect.tool.*;

import static com.companion.effect.weapon.bow.bow;


public class ability {
    private static int planTime=0;
    private static int planTimeMax=100; //5秒一次
    public static void onTick(MinecraftClient client) { //前进
        player = client.player;
        all_client = client;
        if (player == null) return;

        ItemStack main = player.getMainHandStack();
        ItemStack main2 = player.getOffHandStack();
        boolean emptyHand = main.isEmpty(); // 1. 空手
        boolean sprinting = player.isSprinting(); // 2. 疾跑
        boolean leftPressed = GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS; // 3. 左键按下
        boolean rightPressed = GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) == GLFW.GLFW_PRESS;
        planTime++;
        if(planTime>=planTimeMax){
            planTime=0;
        }
        Other(main,leftPressed,sprinting,emptyHand,rightPressed,main2,client,planTime==0);

        //sendPos(0,0,0,false);
        wasPressed = leftPressed;

        //得到遇到过的所有玩家
        AllPlayer.addAll(getAllLoadedPlayerEntities().stream()
                .map(p -> p.getName().getString())
                .toList());

    }
    private static void NoFall(){
        if(cfg.enableNoFall.get()) {
            if (getPlayerHeightAboveGround() < Math.max(player.fallDistance / 5, 2) && player.fallDistance > 2) { //规避摔落伤害
                player.fallDistance = 0.0F;
                player.setOnGround(false);
                player.setVelocity(player.getVelocity().getX(), 0.05, player.getVelocity().getZ()); //加速度设为零
            }
        }
    }
    private static void PathManage(){
        BlockPos posTemp=null;
        if (!BeFoundPath.isEmpty()){

            for(BlockPos pos: BeFoundPath){
                mix.block(pos.toCenterPos(),5,GREEN_POINT_PARTICLE);
                if(posTemp!=null) {
                    line(pos.toCenterPos(), posTemp.toCenterPos(),3, RED_POINT_PARTICLE);
                }
                posTemp=pos;
            }

        }
        if (wasComePath!=-1){
            Vec3d targetPos=BeFoundPath.get(wasComePath).toCenterPos().add(0,-0.5,0);

            if(miniLeap(targetPos,10)){
                wasComePath++;
                if(wasComePath>=BeFoundPath.size()){
                    wasComePath=-1;
                }
            }
        }
    }
    private static void Other(ItemStack main, boolean leftPressed, boolean sprinting, boolean emptyHand, boolean rightPressed, ItemStack main2, MinecraftClient client, boolean b){
        TraceHP();
        bow(main,main2);
        spear(leftPressed,main);
        NoFall();
        Run(emptyHand,sprinting,leftPressed);
        //tip(getBlockScreenVertices(new BlockPos(0,0,0)));
        com.companion.effect.TunnelBulid.main.reg(main,rightPressed,leftPressed);
        PathShow();
        MiningBot.TickRun();
        AutoRaid.update(client,b);
        raid.update(leftPressed,client,rightPressed);
        Elytra.update(client);
        leap.update(client);
        if (player.fishHook != null && player.fishHook.getHookedEntity() != null) {
            hook.retractFishingRod();
        }
        if(rightPressed){
            crossbow.power(player.getMainHandStack());
        }
        PathManage();
        if(cfg.enableAutoAfk.get()) {
            var v=GetNRNBCleanTime.get();
            tip( "§a下次清理倒计时："+v+"秒",true);
            if( v<30) {
                afk();
            }
        }
    }

    private static void afk(){
        // 1. 保存当前服务器信息
        MinecraftClient client=MinecraftClient.getInstance();
        ServerInfo currentServer = client.getCurrentServerEntry();
        if (currentServer == null) {
            tipGame("未在服务器中，无法重连");
            return;
        }

        // 2. 主动断开连接
        client.getNetworkHandler().getConnection().disconnect(Text.literal("检测到清理，断开，60秒后重连"));

        // 3. 延迟60秒后重连
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 主线程执行重连
            client.execute(() -> {
                // 回到服务器列表并连接
                client.send(() -> {
                    ConnectScreen.connect(
                            new MultiplayerScreen(new TitleScreen()),
                            client,
                            ServerAddress.parse(currentServer.address),
                            currentServer,
                            false,
                            null
                    );
                });
            });
        });
    }

    public static List<PlayerEntity> getAllLoadedPlayerEntities() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null) {
            return Collections.emptyList(); // 更安全的空列表
        }

        World world = client.world;
        List<? extends PlayerEntity> rawPlayers = world.getPlayers();

        // 流式转换为List<PlayerEntity>，并过滤null值
        return rawPlayers.stream()
                .filter(Objects::nonNull) // 过滤null玩家，避免空指针
                .map(player -> (PlayerEntity) player)
                .collect(Collectors.toList());
    }



}
