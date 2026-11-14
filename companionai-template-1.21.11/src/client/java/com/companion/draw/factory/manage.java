package com.companion.draw.factory;

import com.companion.CompanionAI;
import com.companion.draw.particle.*;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.companion.CompanionAI.MOD_ID;
import static com.companion.ParticleManage.*;


public class manage {


    public static void particleReg() {
        // 原有粒子工厂注册
        ParticleFactoryRegistry.getInstance().register(
                SPARKLE_PARTICLE,
                SparkleParticle.Factory::new
        );
        ParticleFactoryRegistry.getInstance().register(
                STAR_PARTICLE,
                star.Factory::new
        );
        ParticleFactoryRegistry.getInstance().register(
                RED_POINT_PARTICLE,
                red_point.Factory::new
        );
        ParticleFactoryRegistry.getInstance().register(
                GREEN_POINT_PARTICLE,
                green_point.Factory::new
        );

        // 新增颜色粒子工厂注册（与ParticleManage中的常量一一对应）
        ParticleFactoryRegistry.getInstance().register(
                BLUE_POINT_PARTICLE,
                blue_point.Factory::new
        );
        ParticleFactoryRegistry.getInstance().register(
                BROWN_POINT_PARTICLE,
                brown_point.Factory::new
        );
        ParticleFactoryRegistry.getInstance().register(
                ORANGE_POINT_PARTICLE,
                orange_point.Factory::new
        );
        ParticleFactoryRegistry.getInstance().register(
                PINK_POINT_PARTICLE,
                pink_point.Factory::new
        );
        ParticleFactoryRegistry.getInstance().register(
                PURPLE_POINT_PARTICLE,
                purple_point.Factory::new
        );
        ParticleFactoryRegistry.getInstance().register(
                WHITE_POINT_PARTICLE,
                white_point.Factory::new
        );
        ParticleFactoryRegistry.getInstance().register(
                YELLOW_POINT_PARTICLE,
                yellow_point.Factory::new
        );

    }

}