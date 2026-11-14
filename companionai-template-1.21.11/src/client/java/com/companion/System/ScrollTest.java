package com.companion.System;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;

import static com.companion.global.spearPower;

public class ScrollTest {
    // 保存【原版游戏的滚轮回调】，必须存，否则物品栏不能滚动
    private static GLFWScrollCallback originalScrollCallback;

    public static void register() {
        MinecraftClient mc = MinecraftClient.getInstance();
        // 1. 获取游戏窗口的 GLFW 句柄
        long windowHandle = mc.getWindow().getHandle();

        // 2. 先获取并保存原版回调（关键！不存会覆盖原版逻辑）
        originalScrollCallback = GLFW.glfwSetScrollCallback(windowHandle, null);

        // 3. 设置我们自己的回调
        GLFW.glfwSetScrollCallback(windowHandle, (window, scrollX, scrollY) -> {
            // ======================
            // 滚轮事件核心
            // scrollY = 垂直滚轮（你要的）
            // scrollX = 水平滚轮（几乎不用）
            // ======================
            double amount = scrollY;

            // 你的逻辑：打印滚轮方向
            if (amount > 0) {
                spearPower++;
            } else if (amount < 0) {
                spearPower--;
            }

            // 4. 必须调用原版回调！让游戏正常处理物品栏滚动
            if (originalScrollCallback != null) {
                originalScrollCallback.invoke(window, scrollX, scrollY);
            }
        });
    }
}