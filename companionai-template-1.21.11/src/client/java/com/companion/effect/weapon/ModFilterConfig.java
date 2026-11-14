package com.companion.effect.weapon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.lang.reflect.Type;

import static com.companion.CompanionAI.MOD_ID;
import static com.companion.Key.EntityTable.generateIdToKeyList;
import static com.companion.global.*;

public class ModFilterConfig {
    private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".MEF.json");
    // 私有日志文件
    private static final Path LOG_FILE = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".log");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final DateTimeFormatter LOG_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    // 序列化中间数据结构（JSON实际存储的内容）
    private static class ConfigData {
        String entityTypeIds = "";
        Set<String> playerNames = new HashSet<>();
        Set<String> AllPlayerNames = new HashSet<>();
    }

    /**
     * 日志写入：普通信息
     */
    private static void writeLog(String message) {
        try {
            // 确保父目录存在
            if (!Files.exists(LOG_FILE.getParent())) {
                Files.createDirectories(LOG_FILE.getParent());
            }
            String time = LocalDateTime.now().format(LOG_TIME_FORMAT);
            String line = String.format("[%s] INFO: %s%n", time, message);
            // 追加写入
            try (BufferedWriter bw = Files.newBufferedWriter(
                    LOG_FILE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            )) {
                bw.write(line);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 日志写入：异常错误堆栈
     */
    private static void writeLog(String message, Throwable ex) {
        try {
            if (!Files.exists(LOG_FILE.getParent())) {
                Files.createDirectories(LOG_FILE.getParent());
            }
            String time = LocalDateTime.now().format(LOG_TIME_FORMAT);
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("[%s] ERROR: %s%n", time, message));
            // 打印异常堆栈
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            sb.append(sw);

            try (BufferedWriter bw = Files.newBufferedWriter(
                    LOG_FILE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            )) {
                bw.write(sb.toString());
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 从配置文件加载数据
     * 文件不存在时会自动创建默认配置
     */
    public static void load() {
        EntityTableCode2 = generateIdToKeyList();
        writeLog("EntityTableCode2 已初始化，列表内容: " + EntityTableCode2);
        if (!Files.exists(CONFIG_FILE)) {
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_FILE)) {
            Type type = new TypeToken<ConfigData>() {}.getType();
            ConfigData data = GSON.fromJson(reader, type);

            if (data == null) {
                save();
                return;
            }

            // 清空旧数据
            filter.clear();
            filterPlayer.clear();

            if (data.entityTypeIds != null) {
                for (int i = 0; i < EntityTableCode2.size(); i++) {
                    if (data.entityTypeIds.charAt(i) == '1') {
                        filter.add(EntityTableCode2.get(i));
                    }
                }
            }

            // 玩家名直接加载
            if (data.playerNames != null) {
                filterPlayer.addAll(data.playerNames);
            }
            if (data.AllPlayerNames != null) {
                AllPlayer.addAll(data.AllPlayerNames);
            }

        } catch (IOException e) {
            writeLog("[AI玩家] 配置加载失败: " + e.getMessage(), e);
            // 加载失败时初始化为空集合，避免崩溃
            filter = new HashSet<>();
            filter.add(EntityType.VILLAGER.getTranslationKey());

            filterPlayer = new HashSet<>();
        }
        writeLog("保存配置文件id"+filter);
    }

    private static String b(boolean a) {
        if (a) {
            return "1";
        } else {
            return "0";
        }
    }

    /**
     * 将当前集合数据写入配置文件
     * 修改集合后手动调用此方法才会持久化
     */
    public static void save() {
        ConfigData data = new ConfigData();
        StringBuilder sb = new StringBuilder();
        for (String entityType : EntityTableCode2) {
            sb.append(b(filter.contains(entityType)));
        }
        data.entityTypeIds = sb.toString();


        data.playerNames.addAll(filterPlayer);
        data.AllPlayerNames.addAll(AllPlayer);

        try (Writer writer = Files.newBufferedWriter(CONFIG_FILE)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            writeLog("保存配置文件异常", e);
        }
    }
}