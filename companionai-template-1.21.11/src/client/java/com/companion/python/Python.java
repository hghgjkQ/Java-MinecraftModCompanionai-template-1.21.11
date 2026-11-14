package com.companion.python;

import com.companion.effect.tool;
import net.minecraft.client.MinecraftClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.companion.effect.tool.*;


public class Python {
    private final String pythonHost;
    private final int pythonPort;
    private Socket socket;
    private BufferedReader in;
    private OutputStreamWriter out;
    // 核心：连接状态标记 + 线程池支持重新创建
    private boolean isConnected = false;
    private ScheduledExecutorService executor; // 不再初始化，按需创建
    private final MinecraftClient mc;

    public Python(MinecraftClient mc) {
        this.mc = mc;
        this.pythonHost = "127.0.0.1";
        this.pythonPort = 8888;
    }

    public Python(MinecraftClient mc, String pythonHost, int pythonPort) {
        this.mc = mc;
        this.pythonHost = pythonHost;
        this.pythonPort = pythonPort;
    }

    // 新增：获取连接状态
    public boolean isConnected() {
        return isConnected && socket != null && !socket.isClosed();
    }

    public void startChatConnection() {
        // 先判断：已连接则直接返回，避免重复启动
        if (isConnected()) {
            sendMsgToMC("⚠️ 已和Python建立连接，无需重复启动");
            return;
        }

        // 重新创建线程池（解决shutdown后无法重启的问题）
        executor = Executors.newSingleThreadScheduledExecutor();

        executor.schedule(() -> {
            try {
                socket = new Socket(pythonHost, pythonPort);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                out = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
                isConnected = true; // 标记为已连接
                sendMsgToMC("✅ 已连接Python服务端（" + pythonHost + ":" + pythonPort + "）");

                new Thread(this::receiveMsgLoop, "Python-Receive").start();

            } catch (IOException e) {
                isConnected = false; // 标记为未连接
                tool.error(e);
            }
        }, 1, TimeUnit.SECONDS);
    }

    // 1. 先声明全局锁和volatile变量（确保线程安全，需放在类中，而非方法内）
    private static final Object INPUT_LOCK = new Object();
    private volatile String InputPythonText = null; // 必须加volatile保证可见性

    // 2. 修改后的receiveMsgLoop方法
    private void receiveMsgLoop() {
        String pythonMsg;
        try {
            while (isConnected() && (pythonMsg = in.readLine()) != null) {
                sendMsgToMC("📥 Python回复：" + pythonMsg);

                // 🔴 替换原空循环：低CPU等待InputPythonText非null
                synchronized (INPUT_LOCK) {
                    while (InputPythonText == null) {
                        try {
                            INPUT_LOCK.wait(); // 释放锁并休眠，CPU占用≈0
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt(); // 恢复中断标记
                            throw new IOException("等待输入被中断", e); // 抛出异常终止循环
                        }
                    }
                }

                // 发送消息并重置
                sendMsgToPython(InputPythonText);
                InputPythonText = null;
            }
        } catch (IOException e) {
            isConnected = false;
            tool.error("❌ 和Python的连接已断开！\n"+e);
        } finally {
            closeConnection();
        }
    }

    // 🔴 配套：给InputPythonText赋值的方法（必须加这个，否则会永久等待）
    public void setInputPythonText(String text) {
        synchronized (INPUT_LOCK) {
            InputPythonText = text;
            INPUT_LOCK.notifyAll(); // 唤醒等待的receiveMsgLoop线程
        }
    }

    public void sendMsgToPython(String msg) {
        if (!isConnected()) {
            sendMsgToMC("❌ 未连接Python，无法发送消息！");
            return;
        }

        try {
            out.write(msg + "\n");
            out.flush();
            sendMsgToMC("📤 发送给Python：" + msg);
        } catch (IOException e) {
            isConnected = false;
            sendMsgToMC("❌ 发送消息失败：" + e.getMessage());
            closeConnection();
        }
    }

    private void sendMsgToMC(String msg) {
        mc.execute(() -> {
            if (mc.player != null) {
                tip(msg);
            }
            System.out.println("[PythonChat] " + msg);
        });
    }

    public void closeConnection() {
        if (!isConnected) return;

        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            isConnected = false;
            sendMsgToMC("🔌 已关闭和Python的连接");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 优化shutdown：关闭后允许重新创建线程池
    public void shutdown() {
        closeConnection(); // 先关连接
        if (executor != null) {
            executor.shutdownNow(); // 强制关闭线程池
            executor = null; // 置空，方便重新创建
        }
        sendMsgToMC("🔚 已关闭Python通信的线程池");
    }
}