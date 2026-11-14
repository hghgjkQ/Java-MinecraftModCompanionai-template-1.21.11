package com.companion.render3D;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Method;

import static com.companion.effect.tool.error;

public class show {

    private static Method getFovMethod;
    private static boolean initialized = false;

    private static void init() {
        if (initialized) return;

        try {
            // 尝试获取 getFov 方法
            getFovMethod = GameRenderer.class.getDeclaredMethod("getFov",
                    Camera.class, float.class, boolean.class);
            getFovMethod.setAccessible(true);
            System.out.println("[Show3D] Found getFov method: " + getFovMethod);
        } catch (NoSuchMethodException e) {
            System.err.println("[Show3D] getFov method not found, will use fallback");
            getFovMethod = null;
        } catch (Exception e) {
            e.printStackTrace();
            getFovMethod = null;
        }

        initialized = true;
    }

    public static double[] worldToScreen(Vec3d worldPos, float tickDelta) {
        init(); // 确保初始化

        MinecraftClient mc = MinecraftClient.getInstance();
        GameRenderer gameRenderer = mc.gameRenderer;
        Camera camera = gameRenderer.getCamera();
        Vec3d cameraPos = camera.getCameraPos();

        try {
            // 获取 FOV
            float fov;
            if (getFovMethod != null) {
                fov = (Float) getFovMethod.invoke(gameRenderer, camera, tickDelta, true);
            } else {
                // 备用：手动计算 FOV
                fov = calculateFovFallback(mc, camera, tickDelta);
            }

            // 获取投影矩阵（public 方法，无需反射）
            Matrix4f projectionMatrix = gameRenderer.getBasicProjectionMatrix(fov);

            // 获取相机旋转并创建视图矩阵
            Quaternionf quaternion = camera.getRotation().conjugate(new Quaternionf());
            Matrix4f viewMatrix = new Matrix4f().rotation(quaternion);

            // 相对相机位置
            Vec3d relativePos = worldPos.subtract(cameraPos);

            // 应用变换
            Vector4f pos = new Vector4f(
                    (float) relativePos.x,
                    (float) relativePos.y,
                    (float) relativePos.z,
                    1.0f
            );

            pos.mul(viewMatrix);
            pos.mul(projectionMatrix);

            // 透视除法
            if (pos.w != 0.0f) {
                pos.x /= pos.w;
                pos.y /= pos.w;
                pos.z /= pos.w;
            }

            // 转换为屏幕坐标
            int screenWidth = mc.getWindow().getScaledWidth();
            int screenHeight = mc.getWindow().getScaledHeight();

            double screenX = (pos.x * 0.5 + 0.5) * screenWidth;
            double screenY = (1.0 - (pos.y * 0.5 + 0.5)) * screenHeight;

            return new double[] { screenX, screenY, pos.z };

        } catch (Exception e) {
            error(e);
            return new double[] { 0, 0, -1 };
        }
    }

    // 手动计算 FOV（复制 GameRenderer 的逻辑）
    private static float calculateFovFallback(MinecraftClient mc, Camera camera, float tickDelta) {
        if (mc.gameRenderer.isRenderingPanorama()) {
            return 90.0F;
        }

        // 基础 FOV
        float fov = (float)(int) mc.options.getFov().getValue();

        // 获取 FOV 倍数（疾跑效果等）
        // 注意：这里无法获取 lastFovMultiplier 和 fovMultiplier，因为它们也是私有的
        // 简单模拟：如果玩家在疾跑，增加 FOV
        /*
        if (mc.player != null && mc.player.isSprinting()) {
            fov *= 1.05f;
        }

         */

        return fov;
    }

    public static boolean isOnScreen(double[] screenPos) {
        return screenPos[2] >= 0.0 && screenPos[2] <= 1.0;
    }
}