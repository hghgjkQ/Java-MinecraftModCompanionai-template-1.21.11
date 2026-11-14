package com.companion.python;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.companion.global.*;
import static com.companion.effect.tool.error;


public class main {

    public static void reg(){
        ClientTickEvents.END_CLIENT_TICK.register(main::manage); // 每帧执行代码
    }

    public static void manage(MinecraftClient client){
        if(Objects.equals(state, "follow")){
            follow();
        }
    }

    public static void follow(){
        try {
            // 极简异步调用findPath（无任何多余代码）
            CompletableFuture.supplyAsync(() ->
                    find.findPath(MinecraftClient.getInstance().world, MinecraftClient.getInstance().player.getBlockPos(), getPlayerPosByName(FollowPlayer))
            ).whenComplete((path, ex) -> MinecraftClient.getInstance().execute(() -> {
                // 此处仅保留结果接收，如需处理结果/异常可在此补充
                Path = path;

            }));
        } catch (Exception e) {
            state=null;
            error(e);
        }
    }
    // 根据玩家名称获取其坐标（返回BlockPos，如需精确坐标可改用Vec3d）
    private static BlockPos getPlayerPosByName(String targetPlayerName) {
        MinecraftClient client = MinecraftClient.getInstance();
        // 空值校验：确保游戏世界/玩家列表不为空
        if (client.world == null) return null;
        // 遍历所有在线玩家，匹配名称（忽略大小写，符合MC实际交互习惯）
        for (PlayerEntity player : client.world.getPlayers()) {
            String playerName = player.getStringifiedName();
            // 精准匹配（如需模糊匹配可改用contains()）
            if (playerName.equalsIgnoreCase(targetPlayerName)) {
                return player.getBlockPos(); // 方块坐标（整数）
                // 如需精确三维坐标，替换为：return player.getPos(); // 返回Vec3d（带小数）
            }
        }
        return null; // 未找到该玩家
    }
}
