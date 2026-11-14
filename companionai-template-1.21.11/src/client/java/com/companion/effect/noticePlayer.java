package com.companion.effect;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import static com.companion.global.*;
import static com.companion.CompanionAIClient.*;
import static com.companion.effect.tool.*;
public class noticePlayer {
    /* ===== 玩家进出检测 ===== */

    public static void run(){
        /* 3️⃣ 每 tick 轻量轮询：检测玩家进出 */
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            player = client.player; /* 2️⃣ 初始化在线缓存（连接成功时） */
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd[HH:mm:ss]"); //添加时间
            Date date = new Date();
            String time = dateFormat.format(date);
            if (client.player == null || client.getNetworkHandler() == null) return;
            Set<String> now = client.getNetworkHandler().getPlayerList().stream().map(e -> e.getProfile().name()).collect(java.util.stream.Collectors.toSet());
            /* 新加入 */
            now.stream().filter(n -> !online.contains(n)).forEach(n -> tip("§e"+time+ n + " 加入了游戏"));
            /* 已离开 */
            online.stream().filter(n -> !now.contains(n)).forEach(n -> tip("§e"+time+ n + " 离开了游戏"));
            online.clear();
            online.addAll(now);
        });
    }
}
