package com.companion.effect.weapon;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.*;

import static com.companion.Key.configTool.cfg;
import static com.companion.effect.tool.randomR;
import static com.companion.effect.tool.tipGame;
import static com.companion.effect.weapon.base.sendPos;
import static com.companion.effect.weapon.tool.ClickChat;
import static com.companion.global.*;
import static com.companion.skills.Tkill.getEyeEntity;

public class raid {





    private static boolean c=false;
    private static boolean d=false;

    private static void into(MinecraftClient client,ParticleEffect a){
        for (int i=0;i<10;i++) {
            Vec3d offset=randomR(Math.random()*5+1);

            if (client.world != null) {
                client.world.addParticleClient(
                        a,
                        true,
                        true,
                        player.getX() +offset.getX(),
                        player.getY()+Math.random()*5,
                        player.getZ() + offset.getZ(),
                        -offset.getX(),0,-offset.getZ()
                );
            }
        }
    }

    public static void update(boolean a,MinecraftClient client,boolean b){
        if(player.getMainHandStack().getItem()== Items.MACE && cfg.enableMace.get()) {
            if (client==null || client.world == null || client.player==null) return;

            for (int i=0;i<cfg.MacePower/10;i++) {
                Vec3d offset=randomR(1);

                    client.world.addParticleClient(
                            ParticleTypes.ENCHANT,
                            true,
                            true,
                            player.getX() +offset.getX(),
                            player.getY()+0.1,
                            player.getZ() + offset.getZ(),
                            -offset.getX()*cfg.RaidR/10,0,-offset.getZ()*cfg.RaidR/10
                    );
                }


            double radius = cfg.RaidR;

            // 获取范围内所有活体实体，并过滤掉自己
            List<LivingEntity> entities = client.world.getEntitiesByClass(LivingEntity.class,
                    client.player.getBoundingBox().expand(radius),
                    ee -> !filterE(client, ee)
            );


            Vec3d random = new Vec3d(Math.random() * 1 - 0.5, Math.random() * 1 - 0.5, Math.random() * 1 - 0.5);
            for (LivingEntity entity : entities) {
                if(Math.random()>0.3)continue;
                client.world.addParticleClient(
                        ParticleTypes.DAMAGE_INDICATOR,
                        true,
                        true,
                        entity.getX() + random.x,
                        entity.getY() + random.y,
                        entity.getZ() + random.z,
                        random.x,random.y, random.z
                );
                client.world.addParticleClient(
                        ParticleTypes.ENCHANT,
                        true,
                        true,
                        entity.getX() + random.x,
                        entity.getY() + random.y,
                        entity.getZ() + random.z,
                        0,0,0
                );
            }
            if(player.isSneaking()) {
                if (b) {
                    if (d) {
                        LivingEntity tarE = getEyeEntity(128);
                        if (tarE != null) {
                            String name;
                            if (tarE instanceof PlayerEntity) {
                                name=tarE.getName().getString();
                                if (filterPlayer.contains(name)) {
                                    filterPlayer.remove(name);
                                    into(client, ParticleTypes.ANGRY_VILLAGER);
                                    tipGame("MaceFilter", Text.literal("§4玩家§5"+name+"§4不会被过滤了§2").append(ClickChat("[","再次过滤","/companionai macefilter player add "+name,"]")));
                                } else {
                                    filterPlayer.add(name);
                                    into(client, ParticleTypes.HEART);
                                    tipGame("MaceFilter",Text.literal("§a玩家§5"+name+"§a将被过滤§2").append(ClickChat("[","取消过滤","/companionai macefilter player remove "+name,"]")));
                                }
                            } else {
                                name=tarE.getType().getTranslationKey();
                                if (filter.contains(name)) {
                                    filter.remove(name);
                                    into(client, ParticleTypes.ANGRY_VILLAGER);
                                    tipGame("MaceFilter",Text.literal("§4实体§5"+name+"§4不会被过滤§2").append(ClickChat("[","再次过滤","/companionai macefilter entity add "+name,"]")));
                                } else {
                                    filter.add(name);
                                    into(client, ParticleTypes.HEART);
                                    tipGame("MaceFilter",Text.literal("§a实体§5"+name+"§a将被过滤§2").append(ClickChat("[","再次取消过滤","/companionai macefilter entity remove "+name,"]")));
                                }
                            }
                        }

                        d = false;
                    }
                } else if (!d) {
                    d = true;
                }
            }
        }
        if(a && player.getMainHandStack().getItem()== Items.MACE && cfg.enableMace.get()){
            draw();
            if(c) {
                c = false;
                raid.m();
            }
        }else{
            if(!c) {
                c=true;
        }

        }
    }

    private static boolean filterE(MinecraftClient client,LivingEntity ee){
        return ee == client.player || !ee.isAlive() || filter.contains(ee.getType().getTranslationKey())  || client.getCameraEntity()==ee || (ee instanceof net.minecraft.entity.player.PlayerEntity && filterPlayer.contains(ee.getStringifiedName()));
    }

    public static void m(){

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        MinecraftClient client = MinecraftClient.getInstance();
        if (player == null || client.world == null) return;


        LivingEntity e = getNearestEntity(cfg.RaidR);

        if (e != null && e.isAlive() && e.getHealth() > 0) {
            PlayerInteractEntityC2SPacket attackPacket = PlayerInteractEntityC2SPacket.attack(e, client.player.isSneaking());
            double x = player.getX();
            double y = player.getY();
            double z = player.getZ();
            for (int i = 0; i < 4; ++i)
                sendPos(x, y, z, false);
            sendPos(x, y + cfg.MacePower/2, z, false);
            sendPos(x, y, z, false);
            client.player.networkHandler.sendPacket(attackPacket);
            for (int i = 0; i < 4; ++i)
                sendPos(x, y, z, false);
            sendPos(x, y + cfg.MacePower/2, z, false);
            sendPos(x, y, z, false);
            client.player.networkHandler.sendPacket(attackPacket);
            attack(client,e,player);


            //player.setVelocity(0, 0.1, 0);


            /*=========================*/
            double radius = cfg.RaidR;

            Vec3d playerPos = client.player.getEntityPos();
            double minDistSq = radius * radius;

            // 获取范围内所有活体实体，并过滤掉自己
            List<LivingEntity> entities = client.world.getEntitiesByClass(LivingEntity.class,
                    client.player.getBoundingBox().expand(radius),
                    ee -> !filterE(client,ee)
            );

            // 找最近
            for (LivingEntity entity : entities) {
                double distSq = playerPos.squaredDistanceTo(entity.getEntityPos());
                if (distSq < minDistSq) {
                    e=entity;
                    attack(client,e,player);
                }
            }

            client.player.swingHand(Hand.MAIN_HAND);



        }

    }
    private static void attack(MinecraftClient client, Entity e, ClientPlayerEntity player){
        PlayerInteractEntityC2SPacket attackPacket = PlayerInteractEntityC2SPacket.attack(e, client.player.isSneaking());
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        drawKill(e.getEntityPos(), (int) ((LivingEntity) e).getHealth());

        for (int i = 0; i < 4; ++i)
            sendPos(x, y, z, false);
        sendPos(x, y + cfg.MacePower, z, false);
        sendPos(x, y, z, false);
        client.player.networkHandler.sendPacket(attackPacket);

        player.setOnGround(false);
        player.fallDistance = 100;

        for (int i = 0; i < 4; ++i) Objects.requireNonNull(client.getNetworkHandler()).sendPacket(

                new PlayerMoveC2SPacket.Full(
                        e.getX(), e.getY(), e.getZ(),
                        player.getYaw(), 90,
                        false, false
                )
        );
        client.player.networkHandler.sendPacket(attackPacket);
    }
    public static LivingEntity getNearestEntity(double radius) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return null;

        Vec3d playerPos = mc.player.getEntityPos();
        double minDistSq = radius * radius;
        LivingEntity nearest = null;

        // 获取范围内所有活体实体，并过滤掉自己
        List<LivingEntity> entities = mc.world.getEntitiesByClass(LivingEntity.class,
                mc.player.getBoundingBox().expand(radius),
                e -> !filterE(mc,e)
        );

        // 找最近
        for (LivingEntity entity : entities) {
            double distSq = playerPos.squaredDistanceTo(entity.getEntityPos());
            if (distSq < minDistSq) {
                minDistSq = distSq;
                nearest = entity;
            }
        }

        return nearest;
    }

    private static void draw(){
        MinecraftClient client=all_client;

        double radius = cfg.RaidR;

        Vec3d playerPos = client.player.getEntityPos();
        double minDistSq = radius * radius;

        // 获取范围内所有活体实体，并过滤掉自己
        List<LivingEntity> entities = client.world.getEntitiesByClass(LivingEntity.class,
                client.player.getBoundingBox().expand(radius),
                ee -> !filterE(client,ee)
        );

        // 找最近
        for (LivingEntity entity : entities) {
            double distSq = playerPos.squaredDistanceTo(entity.getEntityPos());
            if (distSq < minDistSq) {
                var pos=entity.getEntityPos();
                Vec3d random = new Vec3d(Math.random() * 10 - 5, Math.random() * 10 - 5, Math.random() * 10 - 5);
                all_client.world.addParticleClient(
                        ParticleTypes.SONIC_BOOM,
                        true,
                        true,
                        pos.x + random.x,
                        pos.y + random.y,
                        pos.z + random.z,
               1,1,1
                );
            }
        }
    }

    private static void drawKill(Vec3d pos,int l) {
        if (all_client.world != null) {
            for (int i = 0; i <l ;++i) {
                Vec3d random = new Vec3d(Math.random() * 1 - 0.5, Math.random() * 1 - 0.5, Math.random() * 1 - 0.5);
                all_client.world.addParticleClient(
                        ParticleTypes.SOUL,
                        true,
                        true,
                        pos.x + random.x,
                        pos.y + random.y,
                        pos.z + random.z,
                        0 - random.x,
                        0 - random.y,
                        0 - random.z
                );
                 random = new Vec3d(Math.random() * 1 - 0.5, Math.random() * 1 - 0.5, Math.random() * 1 - 0.5);
                all_client.world.addParticleClient(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        true,
                        true,
                        pos.x + random.x,
                        pos.y + random.y,
                        pos.z + random.z,
                        0 - random.x,
                        0 - random.y,
                        0 - random.z
                );
            }
        }
    }
}
