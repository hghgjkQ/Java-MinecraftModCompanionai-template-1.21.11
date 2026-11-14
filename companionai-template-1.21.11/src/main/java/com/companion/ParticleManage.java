package com.companion;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.companion.CompanionAI.MOD_ID;

public class ParticleManage {
    // 原有粒子常量
    public static final SimpleParticleType SPARKLE_PARTICLE =
            FabricParticleTypes.simple();
    public static final SimpleParticleType STAR_PARTICLE =
            FabricParticleTypes.simple();
    public static final SimpleParticleType RED_POINT_PARTICLE =
            FabricParticleTypes.simple();
    public static final SimpleParticleType GREEN_POINT_PARTICLE =
            FabricParticleTypes.simple();

    // 新增颜色粒子常量（对应blue/brown/orange/pink/purple/white/yellow）
    public static final SimpleParticleType BLUE_POINT_PARTICLE =
            FabricParticleTypes.simple();
    public static final SimpleParticleType BROWN_POINT_PARTICLE =
            FabricParticleTypes.simple();
    public static final SimpleParticleType ORANGE_POINT_PARTICLE =
            FabricParticleTypes.simple();
    public static final SimpleParticleType PINK_POINT_PARTICLE =
            FabricParticleTypes.simple();
    public static final SimpleParticleType PURPLE_POINT_PARTICLE =
            FabricParticleTypes.simple();
    public static final SimpleParticleType WHITE_POINT_PARTICLE =
            FabricParticleTypes.simple();
    public static final SimpleParticleType YELLOW_POINT_PARTICLE =
            FabricParticleTypes.simple();



    public static void MAINparticleReg() {
        // 原有粒子注册
        Registry.register(Registries.PARTICLE_TYPE,
                Identifier.of(MOD_ID, "sparkle_particle"),
                SPARKLE_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE,
                Identifier.of(MOD_ID, "star"),
                STAR_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE,
                Identifier.of(MOD_ID, "red_point"),
                RED_POINT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE,
                Identifier.of(MOD_ID, "green_point"),
                GREEN_POINT_PARTICLE);

        // 新增颜色粒子注册（与常量一一对应）
        Registry.register(Registries.PARTICLE_TYPE,
                Identifier.of(MOD_ID, "blue_point"),
                BLUE_POINT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE,
                Identifier.of(MOD_ID, "brown_point"),
                BROWN_POINT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE,
                Identifier.of(MOD_ID, "orange_point"),
                ORANGE_POINT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE,
                Identifier.of(MOD_ID, "pink_point"),
                PINK_POINT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE,
                Identifier.of(MOD_ID, "purple_point"),
                PURPLE_POINT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE,
                Identifier.of(MOD_ID, "white_point"),
                WHITE_POINT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE,
                Identifier.of(MOD_ID, "yellow_point"),
                YELLOW_POINT_PARTICLE);
    }
}