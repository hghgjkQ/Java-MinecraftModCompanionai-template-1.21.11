package com.companion.System;

import com.companion.CompanionAIClient;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.companion.Key.configTool.cfg;
import static com.companion.effect.tool.*;
import static com.companion.skills.ChestManage.cheak.getUniqueWorldOrServerId;

public class input {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void register() {
        // 1. 其他玩家的聊天消息 / 服务器广播
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
            log("[聊天] " + message.getString());
        });

        // 2. 系统消息 / 命令反馈 / 死亡提示等（overlay=false 会进聊天栏）
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!overlay) {
                log("[系统] " + message.getString());
                if(Objects.equals(message.getString(), "[AntiLag] > Removing...")){

                    writeTimestamp();
                }
            }
        });


        // 4. ★ 玩家自己发送的聊天文字（否则聊天栏里你自己说的话捕获不到）
        ClientSendMessageEvents.CHAT.register(message -> {
            log("[我] " + message);
        });

        // 5. ★ 玩家自己发送的命令（如 /say、/msg 等）
        ClientSendMessageEvents.COMMAND.register(command -> {
            log("[命令] " + command);
        });
    }

    private static void log(String content) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.getCurrentServerEntry() == null) return;

        String a = mc.getCurrentServerEntry().address;
        if (a == null || a.isEmpty()) return;

        File LOG_FILE = new File(
                mc.runDirectory,
                "companion/" + a.replace(":", "_").replace("/", "_").replace("\\", "_") + ".txt"
        );

        String timestamp = LocalDateTime.now().format(FORMATTER);
        String line = "[" + timestamp + "] " + content;

        try {
            File parent = LOG_FILE.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            // ==============================================
            // 关键逻辑：确保第一行永远是 FIXED_FIRST_LINE
            // ==============================================
            boolean fileExists = LOG_FILE.exists();
            boolean isEmpty = fileExists && LOG_FILE.length() == 0;

            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(LOG_FILE, StandardCharsets.UTF_8, true))) {

                // 如果文件不存在 或 文件为空 → 写入固定第一行
                if (!fileExists || isEmpty) {
                    writer.write(a);
                    writer.newLine();
                }

                // 写入正常日志
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            error(e);
        }
    }

    private static final Path TIMESTAMP_FILE = Paths.get("last_timestamp.txt");

    /**
     * 写入当前时间戳
     */
    public static void writeTimestamp() {
        try {
            // 获取当前时间戳（毫秒）
            long timestamp = System.currentTimeMillis();
            // 写入文件
            Files.writeString(TIMESTAMP_FILE, String.valueOf(timestamp));
            CompanionAIClient.NRNBTime=readTimestamp();
        } catch (IOException e) {
            error(e);
        }
    }

    /**
     * 读取时间戳并转为时间
     */
    public static LocalDateTime readTimestamp() {
        if (!Files.exists(TIMESTAMP_FILE)) {
            return null;
        }

        try {
            String timestampStr = Files.readString(TIMESTAMP_FILE);
            long timestamp = Long.parseLong(timestampStr);
            return LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(timestamp),
                    ZoneId.systemDefault()
            );
        } catch (IOException | NumberFormatException e) {
            error(e);
            return null;
        }
    }
}