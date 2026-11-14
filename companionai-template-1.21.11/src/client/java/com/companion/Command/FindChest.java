package com.companion.Command;

import com.companion.render3D.screen;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.companion.effect.tool.tipGame;
import static com.companion.global.*;
import static com.companion.global.CheckChestData;
import static com.companion.render3D.screen.Text;
import static com.companion.render3D.screen.blocks;

public class FindChest {
    public static int find(Item value){
        blocks.clear();
        Text.clear();
        HashMap<BlockPos,Double> temp=new HashMap<>();
        HashMap<BlockPos,Integer> temp1=new HashMap<>();
        for(var i1: CheckChestData.keySet()){ //{{世界:位置}:{物品:数量}}
            var ii1= Arrays.stream(i1.keySet().toArray()).toList().getFirst(); //世界
            if(ii1==all_client.player.getEntityWorld().getRegistryKey()){ //筛选同世界的
                if(CheckChestData.get(i1).containsKey(value)) { //如果包括目标物品
                    temp.put(i1.get(ii1), player.getEntityPos().distanceTo(i1.get(ii1).toCenterPos()));
                    temp1.put(i1.get(ii1),CheckChestData.get(i1).get(value));
                }
            }
        }
        if(temp.isEmpty()) { //如果是空的
            HashMap<BlockPos,String> temp2=new HashMap<>();
            for(var i1: CheckChestData.keySet()){ //{{世界:位置}:{物品:数量}}
                var ii1=Arrays.stream(i1.keySet().toArray()).toList().getFirst(); //世界
                if(CheckChestData.get(i1).containsKey(value)) { //如果包括目标物品
                    temp.put(i1.get(ii1), player.getEntityPos().distanceTo(i1.get(ii1).toCenterPos()));
                    temp1.put(i1.get(ii1),CheckChestData.get(i1).get(value));
                    temp2.put(i1.get(ii1), (String) ii1);

                }
            }
            if(temp.isEmpty()) { //如果是空的
                tipGame("查找物品", "当前记录世界不存在此物品");
                return 1;
            }
            temp = (HashMap<BlockPos, Double>) sortMapByValueAsc(temp);
            tipGame("查找物品", "当前维度不存在：");
            for (var a : temp.keySet()) {
                tipGame(temp2.get(a)+"查找物品", "[" + a.getX() + "," + a.getY() + "," + a.getZ() + "]数量：" + temp1.get(a));
            }
        }else{
            temp = (HashMap<BlockPos, Double>) sortMapByValueAsc(temp);
            tipGame("查找物品", "当前世界存在：");

            for (var a : temp.keySet()) {
                blocks.add(new screen.BlockWithColor(a, "纯红"));
                Text.put(new screen.BlockWithColor(a, "天蓝"), String.valueOf(temp1.get(a)));
                tipGame("查找物品", "[" + a.getX() + "," + a.getY() + "," + a.getZ() + "]数量：" + temp1.get(a));
            }
        }

        return 1;
    }
    // 用法：把你的 Map 放进去，按值升序排序
    public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValueAsc(Map<K, V> map) {
        // 空值防护，防止崩溃
        if (map == null || map.isEmpty()) {
            return new LinkedHashMap<>();
        }

        // 核心：按值 从小到大 排序，转有序Map
        return map.entrySet()
                .stream()
                // 按值升序排序
                .sorted(Map.Entry.comparingByValue())
                // 收集为 LinkedHashMap（保证顺序）
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        // 冲突处理（不会触发）
                        (oldVal, newVal) -> oldVal,
                        LinkedHashMap::new
                ));
    }
}
