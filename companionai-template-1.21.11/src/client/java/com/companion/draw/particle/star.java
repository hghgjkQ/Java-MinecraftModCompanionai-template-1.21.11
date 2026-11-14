package com.companion.draw.particle;

import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

public class star extends BillboardParticle {
    private final SpriteProvider spriteProvider;
    private final int halfPeriod;  // 半周期长度（正向或反向一次的时间）

    protected star(ClientWorld world, double x, double y, double z,
                   double velocityX, double velocityY, double velocityZ,
                   SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider.getSprite(0, 1));
        this.spriteProvider = spriteProvider;
        this.scale = 0.1f;
        this.maxAge = 40 + this.random.nextInt(20);
        this.setColor(1.0f, 1.0f, 1.0f);
        this.alpha = 0.8f;

        // 设置半周期长度（这里设为 maxAge，即一次正向或反向与粒子寿命相同）
        this.halfPeriod = this.maxAge;
    }

    @Override
    public void tick() {
        super.tick();

        // --- 实现来回切换动画 ---
        // 计算在当前周期内的位置（0 ~ 2*halfPeriod）
        int t = this.age % (2 * halfPeriod);
        // 映射虚拟年龄：前半段正向，后半段反向
        // 使用虚拟年龄获取精灵，实现来回切换
        this.setSprite(spriteProvider.getSprite((int)(Math.random()*2), this.maxAge));
        // -----------------------

        if (this.age < this.maxAge / 2) {
        } else {
            this.alpha -= 0.02f;
        }

        this.velocityX += (random.nextDouble() - 0.5) * 0.01;
        this.velocityY += (random.nextDouble() - 0.5) * 0.01;
        this.velocityZ += (random.nextDouble() - 0.5) * 0.01;

        if (this.age >= this.maxAge) {
            this.markDead();
        }
    }

    @Override
    protected RenderType getRenderType() {
        // 保持原样，不修改
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
            // 修正：返回正确的类名 star
            return new star(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
        }
    }
}