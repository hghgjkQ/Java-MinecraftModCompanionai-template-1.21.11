package com.companion.draw.particle;

import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

public class purple_point extends BillboardParticle {
    private final SpriteProvider spriteProvider;

    protected purple_point(ClientWorld world, double x, double y, double z,
                              double velocityX, double velocityY, double velocityZ,
                              SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider.getSprite(0, 1));
        this.spriteProvider = spriteProvider;
        this.scale = 0.2f;
        this.maxAge = 1;
        this.setColor(1.0f, 1.0f, 1.0f);
        this.alpha = 0.8f;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSprite(spriteProvider.getSprite(this.age, this.maxAge));
        // 保留死亡检测
        if (this.age >= this.maxAge) {
            this.markDead();
        }
    }

    @Override
    protected RenderType getRenderType() {
        // 使用粒子图集的半透明渲染类型
        return RenderType.PARTICLE_ATLAS_TRANSLUCENT;
    }

    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientWorld world,
                                       double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ,
                                       Random random) {
            return new purple_point(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
        }
    }
}