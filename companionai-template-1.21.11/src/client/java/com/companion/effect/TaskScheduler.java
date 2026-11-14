package com.companion.effect;

import net.minecraft.client.MinecraftClient;
import java.util.concurrent.*;

/**
 * 线程安全的延迟任务调度器
 * 可重复使用，无资源泄漏风险
 */
public class TaskScheduler {
    // 懒加载单例线程池，volatile 确保线程安全
    private static volatile ScheduledExecutorService executor;

    // 获取或创建线程池（双重检查锁）
    private static ScheduledExecutorService getExecutor() {
        if (executor == null) {
            synchronized (TaskScheduler.class) {
                if (executor == null) {
                    executor = Executors.newScheduledThreadPool(
                            2, // 2个线程足够处理所有延迟任务
                            r -> {
                                Thread thread = new Thread(r, "YourMod-Scheduler");
                                thread.setDaemon(true);  // 守护线程，游戏退出自动终止
                                thread.setPriority(Thread.NORM_PRIORITY - 1); // 降低优先级，避免影响游戏
                                return thread;
                            }
                    );
                }
            }
        }
        return executor;
    }

    /**
     * 核心函数：延迟执行任意代码
     * @param action 要执行的函数（Lambda表达式）
     * @param seconds 延迟秒数
     */
    public static void schedule(Runnable action, long seconds) {
        getExecutor().schedule(() -> {
            // 自动切换回 Minecraft 主线程，确保线程安全
            MinecraftClient.getInstance().execute(action);
        }, seconds, TimeUnit.SECONDS);
    }

    /**
     * 游戏退出时清理资源（在ClientModInitializer中调用）
     */
    public static void shutdown() {
        if (executor != null) {
            executor.shutdown();
            try {
                // 等待5秒让剩余任务完成
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow(); // 强制关闭
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            executor = null;
        }
    }
}