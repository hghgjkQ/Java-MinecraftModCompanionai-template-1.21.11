package com.companion.python;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

// 完全复刻示例风格，仅保留Python启动/关闭核心逻辑，新增每次运行强制覆盖Python文件
public final class manage {
    // ========== 核心管控变量（复刻示例） ==========
    private static final Object LOCK = new Object();
    private static volatile Process PY_PROCESS;
    private static volatile Thread OUTPUT_READER;

    // ========== 内置资源路径（需将python脚本/exe放入模组resources/python目录） ==========
    private static final String PY_SCRIPT_RES = "/python/main.py"; // 内置脚本路径
    private static final String PY_EXE_RES = "/python/python.exe"; // 内置Python解释器路径

    // ========== 临时目录（Fabric游戏目录下） ==========
    private static final Path TEMP_DIR = FabricLoader.getInstance()
            .getGameDir()
            .resolve("mc_python_temp"); // 临时目录名
    private static final Path PY_SCRIPT_FILE = TEMP_DIR.resolve("python_server.py");
    private static final Path PY_EXE_FILE = TEMP_DIR.resolve("python.exe");

    // ========== 资源解压标记（原子布尔保证线程安全，仅用于解释器，脚本每次都覆盖） ==========
    private static final AtomicBoolean EXE_EXTRACTED = new AtomicBoolean(false);

    // ========== 资源解压核心方法（修改：脚本每次强制覆盖，解释器仅解压一次） ==========
    private static void ensureResourcesExtracted() throws IOException {
        synchronized (LOCK) {
            // 创建临时目录（确保目录存在）
            Files.createDirectories(TEMP_DIR);

            // 🔴 核心修改：强制覆盖Python脚本，不再检查是否存在
            extractResourceForce(PY_SCRIPT_RES, PY_SCRIPT_FILE);

            // Python解释器仍保留“仅解压一次”逻辑（避免重复覆盖可执行文件）
            if (!EXE_EXTRACTED.get() && manage.class.getResource(PY_EXE_RES) != null) {
                extractResourceIfMissing(PY_EXE_RES, PY_EXE_FILE);
                EXE_EXTRACTED.set(true);
            }
        }
    }

    // 🔴 新增：强制覆盖资源的方法（无存在性检查，每次都写入）
    private static void extractResourceForce(String resPath, Path target) throws IOException {
        try (InputStream in = manage.class.getResourceAsStream(resPath)) {
            if (in == null) throw new IOException("内置资源不存在: " + resPath);
            // 强制替换目标文件，无论是否存在
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // 单个资源解压逻辑（仅用于解释器，保留原“缺失时解压”逻辑）
    private static void extractResourceIfMissing(String resPath, Path target) throws IOException {
        if (Files.exists(target) && Files.size(target) > 0) return;
        try (InputStream in = manage.class.getResourceAsStream(resPath)) {
            if (in == null) throw new IOException("内置资源不存在: " + resPath);
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // ========== 启动Python核心方法（复刻示例异步+锁逻辑） ==========
    public static void startOnce() {
        CompletableFuture.runAsync(() -> {
            synchronized (LOCK) {
                // 检查是否已有进程运行
                if (PY_PROCESS != null && PY_PROCESS.isAlive()) {
                    System.err.println("❌ Python程序已在运行！");
                    return;
                }

                try {
                    // 🔴 每次启动都确保重新解压（覆盖）Python脚本
                    ensureResourcesExtracted();

                    // 选择Python解释器（优先用内置的，没有则用系统python）
                    Path pythonExe = Files.exists(PY_EXE_FILE) ? PY_EXE_FILE : Path.of("python");

                    // 启动Python进程（复刻ProcessBuilder）
                    PY_PROCESS = new ProcessBuilder(
                            pythonExe.toAbsolutePath().toString(),
                            PY_SCRIPT_FILE.toAbsolutePath().toString()
                    ).start();

                    // 读取Python输出（复刻线程读取）
                    OUTPUT_READER = new Thread(() -> {
                        try (BufferedReader br = new BufferedReader(
                                new InputStreamReader(PY_PROCESS.getInputStream()))) {
                            String line;
                            while ((line = br.readLine()) != null) {
                                System.out.println("[Python输出] " + line); // 打印输出
                            }
                        } catch (IOException e) {
                            System.err.println("❌ 读取Python输出异常: " + e.getMessage());
                        }
                    });

                    // 守护线程+启动
                    OUTPUT_READER.setDaemon(true);
                    OUTPUT_READER.start();

                    // 等待进程结束（可选）
                    PY_PROCESS.waitFor();
                    OUTPUT_READER.join();

                    System.out.println("✅ Python程序正常退出");

                } catch (IOException | InterruptedException e) {
                    System.err.println("❌ 启动Python失败: " + e.getMessage());
                }
            }
        });
    }

    // ========== 停止Python进程（复刻示例锁+销毁逻辑） ==========
    public static void stop() {
        synchronized (LOCK) {
            // 销毁进程
            if (PY_PROCESS != null) {
                PY_PROCESS.destroyForcibly();
                PY_PROCESS = null;
            }
            // 中断读取线程
            if (OUTPUT_READER != null) {
                try {
                    OUTPUT_READER.interrupt();
                    OUTPUT_READER.join(100); // 等待100ms确保结束
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                OUTPUT_READER = null;
            }
            System.out.println("✅ Python进程已强制关闭");
        }
    }

    // 私有构造器（禁止实例化，复刻示例）
    private void PythonLauncher() {}
}