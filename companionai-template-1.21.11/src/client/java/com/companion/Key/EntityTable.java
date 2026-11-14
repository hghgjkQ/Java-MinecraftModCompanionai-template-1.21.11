package com.companion.Key;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EntityTable {

    /**
     * 生成「翻译键 → 数字编号」的有序字典
     * 按翻译键 a-z 字典序排序，编号从 0 开始递增
     * @return 保持排序顺序的映射表
     */
    public static Map<String, String> generateTranslationKeyMap() {
        // 1. 筛选所有活体实体，提取翻译键并排序
        List<EntityType<?>> sortedTranslationKeys = Registries.ENTITY_TYPE.stream()
                // 过滤：只保留 LivingEntity 及其子类的实体类型
                .filter(entityType -> entityType.getBaseClass().isAssignableFrom(LivingEntity.class))
                // 提取翻译键，格式示例：entity.minecraft.zombie
                // 按 a-z 字典序自然排序
                .sorted()
                .toList();

        // 2. 按排序顺序分配编号，LinkedHashMap 保证遍历顺序与排序一致
        Map<String, String> keyToIdMap = new LinkedHashMap<>();
        for (int i = 0; i < sortedTranslationKeys.size(); i++) {
            keyToIdMap.put(sortedTranslationKeys.get(i).getTranslationKey(), String.valueOf(sortedTranslationKeys.get(i).getName()));
        }

        return keyToIdMap;
    }

    public static List<String> generateIdToKeyList() {
        return new ArrayList<>(
                Registries.ENTITY_TYPE.stream()
                        // 修正 isAssignableFrom 方向
                        .filter(entityType -> entityType.getBaseClass().isAssignableFrom(LivingEntity.class))
                        .map(EntityType::getTranslationKey)
                        .sorted()
                        .toList()
        );
    }
}