package com.companion.skills.ChestManage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.companion.effect.tool.error;

public class FileSave {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path DATA_DIR = Paths.get("companionai");

    // 确保文件夹存在
    static {
        try {
            Files.createDirectories(DATA_DIR);
        } catch (IOException e) {
            error(e);
        }
    }

    /**
     * 保存数据到指定文件（JSON格式）
     * @param data     要保存的数据（外层和内层均为 HashMap）
     * @param fileName 文件名（例如 "mydata.json"）
     * @throws IOException 文件写入异常
     */
    public static void saveData(HashMap<HashMap<RegistryKey<World>, BlockPos>, HashMap<Item, Integer>> data, String fileName) throws IOException {
        // 转换为可序列化的中间 Map
        HashMap<String, HashMap<String, Integer>> serializableMap = new HashMap<>();
        for (HashMap.Entry<HashMap<RegistryKey<World>, BlockPos>, HashMap<Item, Integer>> entry : data.entrySet()) {
            String keyStr = encodeOuterKey(entry.getKey());
            HashMap<String, Integer> valueMap = encodeInnerValue(entry.getValue());
            serializableMap.put(keyStr, valueMap);
        }

        Path filePath = DATA_DIR.resolve(fileName);
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            GSON.toJson(serializableMap, writer);
        }
    }

    /**
     * 从指定文件加载数据
     * @param fileName 文件名
     * @return 加载的数据（外层和内层均为 HashMap），若文件不存在则返回空 HashMap
     * @throws IOException 文件读取异常
     */
    public static HashMap<HashMap<RegistryKey<World>, BlockPos>, HashMap<Item, Integer>> loadData(String fileName) throws IOException {
        Path filePath = DATA_DIR.resolve(fileName);
        if (!Files.exists(filePath)) {
            return new HashMap<>();
        }

        Type type = new TypeToken<HashMap<String, HashMap<String, Integer>>>() {}.getType();
        try (Reader reader = Files.newBufferedReader(filePath)) {
            HashMap<String, HashMap<String, Integer>> serializableMap = GSON.fromJson(reader, type);
            if (serializableMap == null) {
                return new HashMap<>();
            }

            HashMap<HashMap<RegistryKey<World>, BlockPos>, HashMap<Item, Integer>> result = new HashMap<>();
            for (HashMap.Entry<String, HashMap<String, Integer>> entry : serializableMap.entrySet()) {
                HashMap<RegistryKey<World>, BlockPos> outerKey = decodeOuterKey(entry.getKey());
                HashMap<Item, Integer> innerValue = decodeInnerValue(entry.getValue());
                result.put(outerKey, innerValue);
            }
            return result;
        }
    }

    // 将外层键（HashMap<RegistryKey<World>, BlockPos>）编码为稳定字符串
    private static String encodeOuterKey(HashMap<RegistryKey<World>, BlockPos> keyMap) {
        List<String> parts = new ArrayList<>();
        for (HashMap.Entry<RegistryKey<World>, BlockPos> innerEntry : keyMap.entrySet()) {
            RegistryKey<World> worldKey = innerEntry.getKey();
            BlockPos pos = innerEntry.getValue();
            if(pos==null) pos=new BlockPos(0,0,0);
            String worldStr = worldKey.getValue().toString();   // 例如 "minecraft:overworld"
            String posStr = pos.getX() + "," + pos.getY() + "," + pos.getZ();
            parts.add(worldStr + "@" + posStr);
        }
        Collections.sort(parts); // 保证相同内容总是生成相同字符串
        return String.join(";", parts);
    }

    // 将字符串解码为外层键（HashMap）
    private static HashMap<RegistryKey<World>, BlockPos> decodeOuterKey(String keyStr) {
        HashMap<RegistryKey<World>, BlockPos> result = new HashMap<>();
        if (keyStr.isEmpty()) return result;
        String[] parts = keyStr.split(";");
        for (String part : parts) {
            String[] worldAndPos = part.split("@", 2);
            if (worldAndPos.length != 2) continue;
            String worldId = worldAndPos[0];
            String posStr = worldAndPos[1];
            String[] coords = posStr.split(",");
            if (coords.length != 3) continue;
            try {
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                int z = Integer.parseInt(coords[2]);
                Identifier worldIdentifier = Identifier.of(worldId);
                RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, worldIdentifier);
                BlockPos pos = new BlockPos(x, y, z);
                result.put(worldKey, pos);
            } catch (NumberFormatException e) {
                // 忽略格式错误的条目
            }
        }
        return result;
    }

    // 将内部值（HashMap<Item, Integer>）编码为字符串形式的 HashMap
    private static HashMap<String, Integer> encodeInnerValue(HashMap<Item, Integer> itemMap) {
        HashMap<String, Integer> result = new HashMap<>();
        for (HashMap.Entry<Item, Integer> entry : itemMap.entrySet()) {
            Item item = entry.getKey();
            Identifier itemId = Registries.ITEM.getId(item);
            result.put(itemId.toString(), entry.getValue());
        }
        return result;
    }

    // 将字符串 HashMap 解码为 HashMap<Item, Integer>
    private static HashMap<Item, Integer> decodeInnerValue(HashMap<String, Integer> stringMap) {
        HashMap<Item, Integer> result = new HashMap<>();
        for (HashMap.Entry<String, Integer> entry : stringMap.entrySet()) {
            String itemIdStr = entry.getKey();
            Identifier itemId = Identifier.of(itemIdStr);
            Item item = Registries.ITEM.get(itemId);
            if (item != null) {
                result.put(item, entry.getValue());
            } else {
                // 可记录日志：物品不存在
                System.err.println("Unknown item: " + itemIdStr);
            }
        }
        return result;
    }
}