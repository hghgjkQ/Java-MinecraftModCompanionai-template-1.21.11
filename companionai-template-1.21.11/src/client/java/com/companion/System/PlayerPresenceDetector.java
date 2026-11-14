package com.companion.System;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import java.util.*;
import java.util.stream.Collectors;

import static com.companion.global.*;
import static com.companion.effect.tool.*;

public class PlayerPresenceDetector {
    // 静态变量：保存上一帧的玩家列表（用UUID作为唯一标识，避免重名问题）
    private static final Map<UUID, String> LAST_PLAYER_MAP = new HashMap<>();

    // 初始化方法：注册Tick事件，启动玩家检测
    public static void registerPlayerPresenceDetector() {
        // 注册客户端Tick结束事件（每一帧执行一次检测）
        ClientTickEvents.END_CLIENT_TICK.register(PlayerPresenceDetector::detectPlayerChanges);
    }

    // 核心方法：检测玩家出现/消失并显示提示
    private static void detectPlayerChanges(MinecraftClient client) {
        // 1. 获取当前帧的所有已加载玩家
        List<PlayerEntity> currentPlayers = getAllLoadedPlayerEntities();

        // 2. 将当前玩家转换为UUID-名称的Map（UUID唯一标识玩家，避免重名误判）
        Map<UUID, String> currentPlayerMap = new HashMap<>();
        for (PlayerEntity player : currentPlayers) {
            if (player != null && player.getUuid() != null) {
                currentPlayerMap.put(player.getUuid(), player.getName().getString());
            }
        }

        // 3. 检测新增玩家（出现）：当前有但历史没有的玩家
        for (Map.Entry<UUID, String> entry : currentPlayerMap.entrySet()) {
            UUID playerId = entry.getKey();
            String playerName = entry.getValue();

            if (!LAST_PLAYER_MAP.containsKey(playerId)) {
                // 显示“xxx玩家出现”提示（聊天框绿色提示）
                showPlayerPrompt(client, playerName, "你来啦");
            }
        }

        // 4. 检测移除玩家（消失）：历史有但当前没有的玩家
        for (Map.Entry<UUID, String> entry : LAST_PLAYER_MAP.entrySet()) {
            UUID playerId = entry.getKey();
            String playerName = entry.getValue();

            if (!currentPlayerMap.containsKey(playerId)) {
                // 显示“xxx玩家消失”提示（聊天框红色提示）
                showPlayerPrompt(client, playerName, "你人呢");
            }
        }

        // 5. 更新历史列表为当前列表，用于下一帧对比
        LAST_PLAYER_MAP.clear();
        LAST_PLAYER_MAP.putAll(currentPlayerMap);
    }

    // 辅助方法：显示玩家提示（封装聊天框输出逻辑）
    private static void showPlayerPrompt(MinecraftClient client, String playerName, String status) {
        if (Objects.equals(playerName, gamer)) return;
        if (Objects.equals(playerName, owner)) {
            if (Objects.equals(status, "你来啦")) {
                say("hi "+playerName);
            }else{
                say("bye "+playerName);
            }
        }else {
            say(playerName+status);
        }


    }



    // 你的原有方法：获取所有已加载玩家实体（保留并优化）
    public static List<PlayerEntity> getAllLoadedPlayerEntities() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null) {
            return Collections.emptyList(); // 更安全的空列表
        }

        World world = client.world;
        List<? extends PlayerEntity> rawPlayers = world.getPlayers();

        // 流式转换为List<PlayerEntity>，并过滤null值
        return rawPlayers.stream()
                .filter(Objects::nonNull) // 过滤null玩家，避免空指针
                .map(player -> (PlayerEntity) player)
                .collect(Collectors.toList());
    }
}