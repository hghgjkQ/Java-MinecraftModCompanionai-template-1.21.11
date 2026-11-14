package com.companion.effect;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Language;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static com.companion.global.*;

public class say {
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    // 封装成可多次调用的方法
    public static String getFormattedCurrentTime() {
        long currentTimeMillis = System.currentTimeMillis();
        Date date = new Date(currentTimeMillis);
        return DATE_FORMATTER.format(date);
    }
    public static void run(){
        /*
        ClientSendMessageEvents.ALLOW_CHAT.register((message) -> {
            // 返回 false 取消发送
            if (message.contains("spam")) {
                return false;
            }
            return true;
        });

         */

        // 修改消息内容
        ClientSendMessageEvents.MODIFY_CHAT.register(say::Change);
    }
    // 全局正则：匹配 {find 任意内容}
    private static final Pattern FIND_PATTERN = Pattern.compile("\\{find ([^}]+)\\}");

    public static String Change(String s) {
        // 1. 保留你原有的替换逻辑
        s = s.replace("{time}", getFormattedCurrentTime());
        s = s.replace("{pos}", String.valueOf(player.getEntityPos()));

        // 2. 核心：处理 {find 物品中文名} → 替换为 物品英文名(minecraft:xxx)
        Matcher matcher = FIND_PATTERN.matcher(s);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            // 提取 {} 内的 物品中文名（去除空格）
            String chineseName = matcher.group(1).trim();
            // 根据中文名查找 物品ID
            String itemId = YOUR_DICT.getOrDefault(chineseName, "{}");
            // 替换整个 {find xxx} 为物品ID
            matcher.appendReplacement(sb, itemId);
        }
        matcher.appendTail(sb);

        return sb.toString();
    }


}
