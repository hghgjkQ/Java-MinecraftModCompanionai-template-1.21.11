package com.companion.effect;



import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Identifier;

import static com.companion.global.*;



public class show { // 类名首字母大写，符合Java规范
   public static void s(){
       if (TarEntity!=null){
           Vec3d pos=TarEntity.getEyePos();
           line(pos,pos.add(0,10,0),1,ParticleTypes.FLAME);
           drawCircle(ParticleTypes.FLAME,pos.add(0,5,0),3);
        }


   }

    /**
     * 修复版：在起点SP和终点EP之间绘制粒子线
     * @param SP 起点坐标
     * @param EP 终点坐标
     * @param particleDensity 每格距离的粒子数（建议20，值越大线条越平滑）
     */
    public static void line(Vec3d SP, Vec3d EP, int particleDensity,ParticleEffect c) {
        // 1. 基础校验：客户端环境 + 非零距离
        if (all_client == null) return;
        double distance = SP.distanceTo(EP);
        if (distance < 1e-6) { // 距离几乎为0时，只画一个粒子
            point(c, SP);
            return;
        }


        // 2. 计算方向向量并归一化（长度=1，保证每次移动固定距离）
        Vec3d direction = EP.subtract(SP).normalize();
        // 3. 计算步长（每步移动的距离 = 总距离 / 总粒子数）
        int totalParticles = (int) (distance * particleDensity);
        double stepDistance = distance / totalParticles;

        // 4. 循环绘制粒子（包含起点和终点）
        for (int i = 0; i <= totalParticles; i++) {
            // 计算当前粒子位置：起点 + 方向向量 × 步长 × 次数
            Vec3d pos = SP.add(direction.multiply(stepDistance * i));
            point(c, pos);
        }
    }
    public static void line(BlockPos a, BlockPos b, int particleDensity,ParticleEffect c) {
        Vec3d SP = new Vec3d(a.getX(),a.getY(),a.getZ()).add(0.5,0,0.5);
        Vec3d EP = new Vec3d(b.getX(),b.getY(),b.getZ()).add(0.5,0,0.5);
        // 1. 基础校验：客户端环境 + 非零距离
        if (all_client == null) return;
        double distance = SP.distanceTo(EP);
        if (distance < 1e-6) { // 距离几乎为0时，只画一个粒子
            point(c, SP);
            return;
        }


        // 2. 计算方向向量并归一化（长度=1，保证每次移动固定距离）
        Vec3d direction = EP.subtract(SP).normalize();
        // 3. 计算步长（每步移动的距离 = 总距离 / 总粒子数）
        int totalParticles = (int) (distance * particleDensity);
        double stepDistance = distance / totalParticles;

        // 4. 循环绘制粒子（包含起点和终点）
        for (int i = 0; i <= totalParticles; i++) {
            // 计算当前粒子位置：起点 + 方向向量 × 步长 × 次数
            Vec3d pos = SP.add(direction.multiply(stepDistance * i));
            point(c, pos);
        }
    }

   public static void point(ParticleEffect s, Vec3d P){
       if (all_client.world != null) {
           all_client.world.addParticleClient(
                   s,    // 用原版火焰粒子（无需自定义）
                   true,
                   true,
                   P.x,
                   P.y,
                   P.z,
                  0,
                   0,
                   0  );
       }
   }
    /**
     * 绘制圆形粒子轨迹
     * @param s 粒子类型（如 ParticleTypes.REDSTONE）
     * @param center 圆心坐标（Vec3d）
     * @param radius 圆圈半径（double，如 2.0 表示2格）
     */
    public static void drawCircle(ParticleEffect s, Vec3d center, double radius) {
        // 角度步长（越小圆圈越平滑，0.1 是兼顾性能和效果的取值）
        double step = 0.1;
        // 遍历 0 到 2π（完整圆周）
        for (double theta = 0; theta < 2 * Math.PI; theta += step) {
            // 三角函数计算圆周上的点（XZ平面，Y坐标和圆心一致）
            double x = center.x + radius * Math.cos(theta); // 余弦算X轴偏移
            double z = center.z + radius * Math.sin(theta); // 正弦算Z轴偏移
            double y = center.y;                            // Y轴和圆心相同

            // 调用单个粒子方法，绘制圆周上的点
            point(s, new Vec3d(x, y, z));
        }
    }
}

