package com.companion.draw;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.companion.draw.base.line;

public class mix {
    private static final Set<Vec3d[]> side = new HashSet<>();
    static {
        // 底面（y=0 → y=-0.5）的4条边
        side.add(new Vec3d[]{new Vec3d(-0.5, -0.5, -0.5), new Vec3d(0.5, -0.5, -0.5)}); // 底面前边
        side.add(new Vec3d[]{new Vec3d(0.5, -0.5, -0.5), new Vec3d(0.5, -0.5, 0.5)});  // 底面右边
        side.add(new Vec3d[]{new Vec3d(0.5, -0.5, 0.5), new Vec3d(-0.5, -0.5, 0.5)});  // 底面后边
        side.add(new Vec3d[]{new Vec3d(-0.5, -0.5, 0.5), new Vec3d(-0.5, -0.5, -0.5)}); // 底面左边

        // 顶面（y=1 → y=0.5）的4条边
        side.add(new Vec3d[]{new Vec3d(-0.5, 0.5, -0.5), new Vec3d(0.5, 0.5, -0.5)}); // 顶面前边
        side.add(new Vec3d[]{new Vec3d(0.5, 0.5, -0.5), new Vec3d(0.5, 0.5, 0.5)});  // 顶面右边
        side.add(new Vec3d[]{new Vec3d(0.5, 0.5, 0.5), new Vec3d(-0.5, 0.5, 0.5)});  // 顶面后边
        side.add(new Vec3d[]{new Vec3d(-0.5, 0.5, 0.5), new Vec3d(-0.5, 0.5, -0.5)}); // 顶面左边

        // 垂直边（连接调整后的底面和顶面）的4条边
        side.add(new Vec3d[]{new Vec3d(-0.5, -0.5, -0.5), new Vec3d(-0.5, 0.5, -0.5)}); // 左前垂直边
        side.add(new Vec3d[]{new Vec3d(0.5, -0.5, -0.5), new Vec3d(0.5, 0.5, -0.5)});  // 右前垂直边
        side.add(new Vec3d[]{new Vec3d(0.5, -0.5, 0.5), new Vec3d(0.5, 0.5, 0.5)});    // 右后垂直边
        side.add(new Vec3d[]{new Vec3d(-0.5, -0.5, 0.5), new Vec3d(-0.5, 0.5, 0.5)});  // 左后垂直边
    }

    public static void block(Vec3d pos, int x, ParticleEffect c) {
        for (Vec3d[] poss : side) {
            line(pos.add(poss[0]), pos.add(poss[1]), x, c);
        }
    }
    /**
     * 根据两个3D坐标（长方体的对角点）生成粒子边框
     * @param pos1 第一个3D坐标点（对角点1）
     * @param pos2 第二个3D坐标点（对角点2）
     * @param particleDensity 粒子密度（控制线条上粒子的数量）
     * @param particleEffect 要绘制的粒子类型
     */
    public static void drawRegion(Vec3d pos1, Vec3d pos2, int particleDensity, ParticleEffect particleEffect) {
        // 计算长方体8个顶点的坐标
        double minX = Math.min(pos1.x, pos2.x);
        double maxX = Math.max(pos1.x, pos2.x);
        double minY = Math.min(pos1.y, pos2.y);
        double maxY = Math.max(pos1.y, pos2.y);
        double minZ = Math.min(pos1.z, pos2.z);
        double maxZ = Math.max(pos1.z, pos2.z);

        // 定义长方体的8个顶点
        Vec3d p1 = new Vec3d(minX, minY, minZ); // 左下后
        Vec3d p2 = new Vec3d(maxX, minY, minZ); // 右下后
        Vec3d p3 = new Vec3d(maxX, minY, maxZ); // 右下前
        Vec3d p4 = new Vec3d(minX, minY, maxZ); // 左下前
        Vec3d p5 = new Vec3d(minX, maxY, minZ); // 左上后
        Vec3d p6 = new Vec3d(maxX, maxY, minZ); // 右上后
        Vec3d p7 = new Vec3d(maxX, maxY, maxZ); // 右上前
        Vec3d p8 = new Vec3d(minX, maxY, maxZ); // 左上前

        // 定义长方体的12条边（底面4条 + 顶面4条 + 垂直4条）
        List<Vec3d[]> edges = new ArrayList<>();
        // 底面边
        edges.add(new Vec3d[]{p1, p2});
        edges.add(new Vec3d[]{p2, p3});
        edges.add(new Vec3d[]{p3, p4});
        edges.add(new Vec3d[]{p4, p1});
        // 顶面边
        edges.add(new Vec3d[]{p5, p6});
        edges.add(new Vec3d[]{p6, p7});
        edges.add(new Vec3d[]{p7, p8});
        edges.add(new Vec3d[]{p8, p5});
        // 垂直边
        edges.add(new Vec3d[]{p1, p5});
        edges.add(new Vec3d[]{p2, p6});
        edges.add(new Vec3d[]{p3, p7});
        edges.add(new Vec3d[]{p4, p8});

        // 绘制每条边的粒子线条
        for (Vec3d[] edge : edges) {
            line(edge[0], edge[1], particleDensity, particleEffect);
        }
    }
    /**
     * 根据两个3D坐标绘制平面矩形（固定y坐标为第一个坐标的y值）
     * @param pos1 第一个3D坐标（决定矩形的y值，同时提供x/z参考）
     * @param pos2 第二个3D坐标（仅提供x/z参考）
     * @param particleDensity 粒子密度（控制线条上粒子的数量）
     * @param particleEffect 要绘制的粒子类型
     */
    public static void drawRect(Vec3d pos1, Vec3d pos2, int particleDensity, ParticleEffect particleEffect) {
        // 固定y坐标为第一个坐标的y值
        double fixedY = pos1.y;
        // 计算矩形的最小/最大x和z值（确保矩形坐标正确，不受输入顺序影响）
        double minX = Math.min(pos1.x, pos2.x);
        double maxX = Math.max(pos1.x, pos2.x);
        double minZ = Math.min(pos1.z, pos2.z);
        double maxZ = Math.max(pos1.z, pos2.z);

        // 定义矩形的4个顶点（所有顶点y值相同）
        Vec3d p1 = new Vec3d(minX, fixedY, minZ); // 左下
        Vec3d p2 = new Vec3d(maxX, fixedY, minZ); // 右下
        Vec3d p3 = new Vec3d(maxX, fixedY, maxZ); // 右上
        Vec3d p4 = new Vec3d(minX, fixedY, maxZ); // 左上

        // 定义矩形的4条边
        List<Vec3d[]> edges = new ArrayList<>();
        edges.add(new Vec3d[]{p1, p2}); // 下边
        edges.add(new Vec3d[]{p2, p3}); // 右边
        edges.add(new Vec3d[]{p3, p4}); // 上边
        edges.add(new Vec3d[]{p4, p1}); // 左边

        // 绘制每条边的粒子线条
        for (Vec3d[] edge : edges) {
            line(edge[0], edge[1], particleDensity, particleEffect);
        }
    }

}