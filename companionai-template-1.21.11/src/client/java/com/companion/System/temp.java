package com.companion.System;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Language;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.companion.effect.tool.error;
import static com.companion.global.*;

public class temp {
    // 兼容所有Java版本，无需Stream API
    public static List<Item> getAllItemsLegacy() {
        List<Item> items = new ArrayList<>();
        // 遍历注册表的所有条目
        for (Item item : Registries.ITEM) {
            if (item != null && item != Items.AIR) {
                items.add(item);
            }
        }
        return items;
    }
    public static Map<String,String> mainly() {
        Item_DICT.clear();
        Item_English_DICT.clear();
        Map<String, String> content = new HashMap<>();
        var s = getAllItemsLegacy();

        for (Item c : s) {
            content.put(c.getDefaultStack().getItemName().getString(), getLastPartAfterDot(c.getTranslationKey()));
            Item_DICT.put(c.getDefaultStack().getItemName().getString(), c);
            Item_English_DICT.put(getLastPartAfterDot(c.getTranslationKey()),c);
        }

        return content;

    }
    /**
     * 截取字符串最后一个 . 后面的内容
     * 例：minecraft.diamond → diamond
     *     a.b.c → c
     *     test → test（无点则返回原文）
     */
    private static String getLastPartAfterDot(String str) {
        if (str == null || str.isEmpty()) return str;
        int lastDotIndex = str.lastIndexOf('.');
        // 无点 或 点在最后一位，返回原字符串
        if (lastDotIndex == -1 || lastDotIndex == str.length() - 1) {
            return str;
        }
        // 截取最后一个点之后的内容
        return str.substring(lastDotIndex + 1);
    }
}
