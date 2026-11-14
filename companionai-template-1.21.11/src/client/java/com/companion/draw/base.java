package com.companion.draw;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;

import static com.companion.global.*;

public class base {
    // 角度转弧度核心常量（1° = π/180 弧度），全局复用
    public static final double DEG_TO_RAD = Math.PI / 180D;


    // ===================== 核心：MC专属双轴旋转（仅X/Y轴，贴合游戏视角） =====================
    /**
     * 三维点绕指定中心进行X/Y双轴旋转（适配MC，绕X=俯仰，绕Y=左右）
     * @param originalPoint 原始三维点坐标
     * @param center 旋转中心（图形中心，与绘制pos一致）
     * @param rx 绕X轴旋转角度（角度值，如30=30°，俯仰旋转）
     * @param ry 绕Y轴旋转角度（角度值，左右旋转，适配MC右手坐标系）
     * @return 旋转后的新三维点坐标
     */
    public static Vec3d rotatePoint(Vec3d originalPoint, Vec3d center, double rx, double ry) {
        // 1. 平移点到旋转中心为原点（避免绕世界原点旋转，保证绕图形中心旋转）
        double x = originalPoint.x - center.x;
        double y = originalPoint.y - center.y;
        double z = originalPoint.z - center.z;

        // 角度转弧度，复用全局常量
        double rxRad = rx * DEG_TO_RAD;
        double ryRad = ry * DEG_TO_RAD;

        double newX, newY, newZ;

        // 2. 绕X轴旋转（俯仰，仅改变Y/Z坐标，X不变，MC中抬头/低头对应此轴）
        // 旋转矩阵：[1,0,0; 0,cosθ,-sinθ; 0,sinθ,cosθ]
        newY = y * Math.cos(rxRad) - z * Math.sin(rxRad);
        newZ = y * Math.sin(rxRad) + z * Math.cos(rxRad);
        y = newY;
        z = newZ;

        // 3. 绕Y轴旋转（左右，仅改变X/Z坐标，Y不变，MC中左右转对应此轴，适配游戏右手坐标系）
        // 旋转矩阵：[cosθ,0,sinθ; 0,1,0; -sinθ,0,cosθ]（MC空间专属，保证旋转方向贴合游戏操作）
        newX = x * Math.cos(ryRad) + z * Math.sin(ryRad);
        newZ = -x * Math.sin(ryRad) + z * Math.cos(ryRad);
        x = newX;
        z = newZ;

        // 4. 平移点回原中心坐标系，返回旋转结果
        return new Vec3d(x + center.x, y + center.y, z + center.z);
    }

    // ===================== 重载1：带X/Y双轴旋转的三角形 =====================
    public static void triangle(Vec3d pos, double r, double angle, int particleDensity, ParticleEffect c,
                                double rx, double ry) {
        for (int theta = 0; theta < 3; theta++) {
            // 原始2D点计算（与原有逻辑完全一致，不改动）
            double x = pos.x + r * Math.cos((theta * 120 + angle) * DEG_TO_RAD);
            double z = pos.z + r * Math.sin((theta * 120 + angle) * DEG_TO_RAD);
            Vec3d p1 = new Vec3d(x, pos.y, z);

            double x1 = pos.x + r * Math.cos(((theta + 1) * 120 + angle) * DEG_TO_RAD);
            double z1 = pos.z + r * Math.sin(((theta + 1) * 120 + angle) * DEG_TO_RAD);
            Vec3d p2 = new Vec3d(x1, pos.y, z1);

            // 核心：双轴旋转后绘制线段
            Vec3d p1Rotated = rotatePoint(p1, pos, rx, ry);
            Vec3d p2Rotated = rotatePoint(p2, pos, rx, ry);
            line(p1Rotated, p2Rotated, particleDensity, c);
        }
    }

    // 原有2D三角形方法（保持不变，兼容旧代码）
    public static void triangle(Vec3d pos, double r, double angle, int particleDensity, ParticleEffect c) {
        triangle(pos, r, angle, particleDensity, c, 0, 0);
    }

    // ===================== 重载2：带X/Y双轴旋转的花朵 =====================
    public static void flower(Vec3d pos, double r, double r2, double angle, int particleDensity, int particleDensity2, ParticleEffect c,
                              double rx, double ry) {
        double a = 0;
        double stepAngle = 360.0 / particleDensity2;
        for (int theta = 0; theta < particleDensity2; theta++) {
            // 原始2D点计算（与原有逻辑完全一致）
            double x = pos.x + r * Math.cos((a + angle) * DEG_TO_RAD);
            double z = pos.z + r * Math.sin((a + angle) * DEG_TO_RAD);
            Vec3d p1 = new Vec3d(x, pos.y, z);

            double x1 = pos.x + r2 * Math.cos((a + angle) * DEG_TO_RAD);
            double z1 = pos.z + r2 * Math.sin((a + angle) * DEG_TO_RAD);
            Vec3d p2 = new Vec3d(x1, pos.y, z1);

            // 核心：双轴旋转后绘制线段
            Vec3d p1Rotated = rotatePoint(p1, pos, rx, ry);
            Vec3d p2Rotated = rotatePoint(p2, pos, rx, ry);
            line(p1Rotated, p2Rotated, particleDensity, c);
            a += stepAngle;
        }
    }

    // 原有2D花朵方法（保持不变，兼容旧代码）
    public static void flower(Vec3d pos, double r, double r2, double angle, int particleDensity, int particleDensity2, ParticleEffect c) {
        flower(pos, r, r2, angle, particleDensity, particleDensity2, c, 0, 0);
    }

    // ===================== 重载3：带X/Y双轴旋转的角度线段 =====================
    public static void line(Vec3d pos, double r, double angle, int particleDensity, ParticleEffect c,
                            double rx, double ry) {
        // 原始2D点计算（与原有逻辑完全一致）
        double x = pos.x + r * Math.cos(angle * DEG_TO_RAD);
        double z = pos.z + r * Math.sin(angle * DEG_TO_RAD);
        Vec3d p1 = new Vec3d(x, pos.y, z);

        double x1 = pos.x - r * Math.cos(angle * DEG_TO_RAD);
        double z1 = pos.z - r * Math.sin(angle * DEG_TO_RAD);
        Vec3d p2 = new Vec3d(x1, pos.y, z1);

        // 核心：双轴旋转后绘制线段
        Vec3d p1Rotated = rotatePoint(p1, pos, rx, ry);
        Vec3d p2Rotated = rotatePoint(p2, pos, rx, ry);
        line(p1Rotated, p2Rotated, particleDensity, c);
    }

    // 原有2D角度线段方法（保持不变，兼容旧代码）
    public static void line(Vec3d pos, double r, double angle, int particleDensity, ParticleEffect c) {
        line(pos, r, angle, particleDensity, c, 0, 0);
    }

    // ===================== 重载4：带X/Y双轴旋转的圆形 =====================
    public static void drawCircle(ParticleEffect s, Vec3d center, double radius, double step,
                                  double rx, double ry) {
        // 原始圆形点计算（与原有逻辑一致，0到2π弧度）
        for (double theta = 0; theta < 2 * Math.PI; theta += step) {
            double x = center.x + radius * Math.cos(theta);
            double z = center.z + radius * Math.sin(theta);
            Vec3d originalPoint = new Vec3d(x, center.y, z);
            // 核心：双轴旋转后绘制粒子点
            Vec3d rotatedPoint = rotatePoint(originalPoint, center, rx, ry);
            point(s, rotatedPoint);
        }
    }

    // 原有2D圆形方法（保持不变，兼容旧代码）
    public static void drawCircle(ParticleEffect s, Vec3d center, double radius, double step) {
        drawCircle(s, center, radius, step, 0, 0);
    }

    // ===================== 原有底层方法（完全未改动，复用所有粒子绘制逻辑） =====================
    public static void line(Vec3d a, Vec3d b, int particleDensity, ParticleEffect c) {
        if (all_client == null) return;
        double distance = a.distanceTo(b);
        if (distance < 1e-6) {
            point(c, a);
            return;
        }
        Vec3d direction = b.subtract(a).normalize();
        int totalParticles = (int) (distance * particleDensity);
        double stepDistance = distance / totalParticles;
        for (int i = 0; i <= totalParticles; i++) {
            Vec3d pos = a.add(direction.multiply(stepDistance * i));
            point(c, pos);
        }
    }

    public static void point(ParticleEffect s, Vec3d P) {
        if (all_client.world != null) {
            all_client.world.addParticleClient(
                    s,
                    true,
                    true,
                    P.x,
                    P.y,
                    P.z,
                    0,
                    0,
                    0
            );

        }
    }
}