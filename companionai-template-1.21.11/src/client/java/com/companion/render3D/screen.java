package com.companion.render3D;

import com.companion.skills.GetNRNBCleanTime;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;


import static com.companion.effect.tool.tip;
import static com.companion.global.MidText;
import static com.companion.render3D.show.worldToScreen;

// 修正类名规范：首字母大写（Java驼峰命名法）
public class screen {
    // ========== 1. 定义带颜色的存储结构 ==========
    // 存储点坐标 + 颜色名
    public static class PointWithColor {
        Vec3d pos;
        String colorName; // 关联color类中的颜色名

        public PointWithColor(Vec3d pos, String colorName) {
            this.pos = pos;
            this.colorName = colorName;
        }
    }

    // 存储方块坐标 + 颜色名
    public static class BlockWithColor {
        public BlockPos pos;
        String colorName;

        public BlockWithColor(BlockPos pos, String colorName) {
            this.pos = pos;
            this.colorName = colorName;
        }
    }

    // 存储线的顶点列表 + 颜色名
    private static class LineWithColor {
        List<Vec3d> points;
        String colorName;

        public LineWithColor(List<Vec3d> points, String colorName) {
            this.points = points;
            this.colorName = colorName;
        }
    }

    // ========== 2. 修改存储列表的类型（绑定颜色） ==========
    public static final List<PointWithColor> points = new ArrayList<>();
    private static final List<LineWithColor> lines = new ArrayList<>();
    public static final List<BlockWithColor> blocks = new ArrayList<>();
    public static final HashMap<BlockWithColor,String> Text = new HashMap<>();

    /*
    static {
        // 初始化点：每个点指定专属颜色（可自定义）
        points.add(new PointWithColor(new Vec3d(0, 0, 0), "浅紫"));
        points.add(new PointWithColor(new Vec3d(0, 1, 0), "纯红"));
        points.add(new PointWithColor(new Vec3d(0, 2, 0), "纯绿"));
        points.add(new PointWithColor(new Vec3d(0, 3, 0), "纯蓝"));
        points.add(new PointWithColor(new Vec3d(0, 4, 0), "纯黄"));
        points.add(new PointWithColor(new Vec3d(0, 4, 1), "MC钻石蓝"));
        points.add(new PointWithColor(new Vec3d(0, 4, 2), "MC金块黄"));
        points.add(new PointWithColor(new Vec3d(0, 4, 3), "MC红石红"));
        points.add(new PointWithColor(new Vec3d(0, 4, 4), "半透紫"));
        points.add(new PointWithColor(new Vec3d(0, 4, -1), "天蓝"));
        points.add(new PointWithColor(new Vec3d(0, 4, -2), "玫瑰红"));
        points.add(new PointWithColor(new Vec3d(0, 4, -3), "草绿"));
        points.add(new PointWithColor(new Vec3d(0, 4, -4), "半透红"));


        // 初始化方块（示例：可自行添加）
        blocks.add(new BlockWithColor(new BlockPos(1, 1, 1), "MC石头灰"));
        blocks.add(new BlockWithColor(new BlockPos(2, 2, 2), "MC黑曜石黑"));
    }

     */

    /**
     * 高性能斜线绘制（无Tessellator、无逐像素卡顿）
     * @param context DrawContext渲染上下文
     * @param x1 起点x
     * @param y1 起点y
     * @param x2 终点x
     * @param y2 终点y
     * @param color 颜色（ARGB，如0xFF0000a0）
     * @param lineWidth 线宽（1-5，建议值）
     */
    private static void drawSlantLine(DrawContext context, int x1, int y1, int x2, int y2, int color, int lineWidth) {
        // 1. 计算斜线核心参数（Bresenham算法基础）
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        int e2;

        // 2. 预计算线宽偏移（让线条变粗，避免单像素）
        int halfWidth = lineWidth / 2;
        int currentX = x1;
        int currentY = y1;

        // 3. 缓存连续像素段，批量填充（核心优化：减少fill调用次数）
        int segmentStartX = currentX;
        int segmentStartY = currentY;

        while (true) {
            // 4. 当x/y连续变化时，合并成矩形批量填充
            e2 = 2 * err;
            boolean xChanged = false;
            boolean yChanged = false;

            if (e2 > -dy) {
                err -= dy;
                currentX += sx;
                xChanged = true;
            }
            if (e2 < dx) {
                err += dx;
                currentY += sy;
                yChanged = true;
            }

            // 5. 当方向变化/到达终点时，填充当前像素段
            if (xChanged || yChanged || (currentX == x2 && currentY == y2)) {
                // 计算填充区域（合并连续像素，减少fill调用）
                int minX = Math.min(segmentStartX - halfWidth, currentX - halfWidth);
                int maxX = Math.max(segmentStartX + halfWidth, currentX + halfWidth);
                int minY = Math.min(segmentStartY - halfWidth, currentY + halfWidth);
                int maxY = Math.max(segmentStartY + halfWidth, currentY + halfWidth);

                // 批量填充矩形（1次fill覆盖多个像素）
                context.fill(minX, minY, maxX + 1, maxY + 1, color);

                // 重置段起点
                segmentStartX = currentX;
                segmentStartY = currentY;
            }

            // 6. 到达终点退出
            if (currentX == x2 && currentY == y2) break;
        }
    }

    // 简化重载（默认线宽1，调用更方便）
    private static void drawSlantLine(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        drawSlantLine(context, x1, y1, x2, y2, color, 1);
    }

    public static void renderSquare(DrawContext context, RenderTickCounter c) {
        MinecraftClient client = MinecraftClient.getInstance();
        if ( client==null || client.player == null ) return;

        // 获取屏幕尺寸
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // ========== 渲染点（使用存储的颜色） ==========
        for (PointWithColor pwc : points) {
            Vec3d pos = pwc.pos;
            int color = Color.getColor(pwc.colorName); // 从color类获取对应颜色
            var x = worldToScreen(pos, 0);
            if (x[2] < 0 || x[2] > 1) {
                continue; // 在相机后方或超出远平面，跳过
            }

            double size = 10 / client.player.getEyePos().distanceTo(pos);
            context.fill((int) (x[0] - size), (int) (x[1] - size), (int) (x[0] + size), (int) (x[1] + size), color);
        }

        // ========== 渲染方块（使用存储的颜色） ==========
        for (BlockWithColor bwc : blocks) {
            BlockPos pos = bwc.pos;
            int color = Color.getColor(bwc.colorName); // 从color类获取对应颜色
            var x = worldToScreen(Vec3d.of(pos).add(0.5, 0.5, 0.5), 0);
            if (x[2] < 0 || x[2] > 1) {
                continue; // 在相机后方或超出远平面，跳过
            }

            double size = 10;
            context.fill((int) (x[0] - size), (int) (x[1] - size), (int) (x[0] + size), (int) (x[1] + size), color);
        }
        for (BlockWithColor bwc : Text.keySet()) {
            BlockPos pos = bwc.pos;
            int color = Color.getColor(bwc.colorName); // 从color类获取对应颜色
            var x = worldToScreen(Vec3d.of(pos).add(0.5, 0.5, 0.5), 0);
            if (x[2] < 0 || x[2] > 1) {
                continue; // 在相机后方或超出远平面，跳过
            }

            double size = 10 / client.player.getEyePos().distanceTo(Vec3d.of(pos));
            context.drawText(client.textRenderer, Text.get(bwc), (int) (x[0] - size), (int) (x[1] - size), color, true);

        }

        //context.drawText(client.textRenderer, "下次清理倒计时："+GetNRNBCleanTime.get()+"秒", 0,100, Color.getColor("浅蓝"), true);


        // ========== 渲染线（使用存储的颜色） ==========
        double[] temp;
        for (LineWithColor lwc : lines) {
            List<Vec3d> poss = lwc.points;
            int color = Color.getColor(lwc.colorName); // 从color类获取对应颜色
            temp = new double[]{};
            for (Vec3d pos : poss) {
                var x = worldToScreen(pos, 0);
                if (temp.length == 0) {
                    temp = x;
                    continue;
                }

                if (x[2] < 0 || x[2] > 1) {
                    continue; // 在相机后方或超出远平面，跳过
                }

                drawSlantLine(context, (int) x[0], (int) x[1], (int) temp[0], (int) temp[1], color);
                temp = x;
            }
        }
    }
}