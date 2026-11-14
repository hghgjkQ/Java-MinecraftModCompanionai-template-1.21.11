package com.companion.System;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.companion.global.*;
import static com.companion.effect.tool.error;
import static com.companion.effect.tool.tip;

/**
 * 手动输入种子计算末地城位置的工具类
 * 支持：代码内手动传参seed / 游戏内命令行输入seed
 */
public class findEndCity {
    // 末地城生成核心常量（1.21.11原版规则，不可改）
    private static final int SPACING = 20;   // 区域间距（区块）
    private static final int SEPARATION = 11; // 最小间隔（区块）
    private static final long SALT = 10387313L; // 末地城专属盐值（原版固定）
    private static final int DEFAULT_Y = 64; // 1.21.11末地城默认Y坐标（外岛标准高度）

    // 常量定义（更清晰）
    private static final long REGION_MULTIPLIER_X = 341873128712L;
    private static final long REGION_MULTIPLIER_Z = 132897987541L;

    /**
     * 核心方法：手动传入种子 + 区域坐标，计算单个末地城位置
     * @param seed 手动输入的世界种子（long类型，MC原版所有种子均支持）
     * @param regionX 末地城区域X索引（任意整数，如0/1/-2，不同区域对应不同末地城）
     * @param regionZ 末地城区域Z索引（任意整数，如0/1/-2）
     * @return 末地城方块坐标（BlockPos），可直接用于传送/定位
     */
    public static BlockPos calculateByManualSeed(long seed, int regionX, int regionZ) {
        // ✅ 修正：使用 XOR（异或）运算，而不是加法
        // 这是 1.13+ 版本的结构生成算法

        // 1. 计算基础区域种子（使用 XOR）
        long baseRegionSeed = (long) regionX * REGION_MULTIPLIER_X ^
                (long) regionZ * REGION_MULTIPLIER_Z ^
                seed;

        // 2. 创建一个Random对象，消耗第一个随机数
        Random tempRandom = new Random(baseRegionSeed);
        tempRandom.nextInt(); // 消耗一个随机数

        // 3. 获取实际的区域种子（原版算法的一部分）
        long regionSeed = tempRandom.nextLong();

        // 4. 应用末地城专属盐值
        Random random = new Random(regionSeed + SALT);

        // ✅ 重要：末地城有50%的概率不生成
        // 如果你想要只计算实际生成的位置，取消下面的注释
        /*
        if (random.nextInt(2) == 0) {
            return null; // 50%概率不生成
        }
        */

        // 5. 计算区块内偏移（原版算法，保证间距/分离规则）
        int blockX = (regionX * SPACING + random.nextInt(SPACING - SEPARATION)) * 16;
        int blockZ = (regionZ * SPACING + random.nextInt(SPACING - SEPARATION)) * 16;

        // 6. 返回末地城坐标（Y固定64，1.21.11外岛标准高度）
        // 注意：实际游戏中可能会有1-2格的微调，但基础高度是64
        return new BlockPos(blockX, DEFAULT_Y, blockZ);
    }

    /**
     * 扩展方法：手动传入种子，获取指定半径内所有末地城位置
     * @param seed 手动输入的世界种子
     * @param radius 区域半径（如2表示X/Z从-2到2，共25个区域）
     * @return 末地城坐标列表，适合批量定位
     */
    public static List<BlockPos> getRangeByManualSeed(long seed, int radius)  {
        List<BlockPos> endCities = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos pos = calculateByManualSeed(seed, x, z);
                // ✅ 如果启用50%概率检查，需要过滤null值
                // if (pos != null) {
                //     endCities.add(pos);
                // }
                endCities.add(pos); // 暂时保持原来的逻辑
            }
        }
        try {
            Files.createDirectories(FabricLoader.getInstance().getConfigDir().resolve(ModName));
            Files.write(FabricLoader.getInstance().getConfigDir().resolve(ModName).resolve("endcity.txt"),
                    (endCities.toString()).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {error(e);}
        return endCities;
    }

    /**
     * 可选：计算实际会生成的末地城（考虑50%概率）
     */
    public static List<BlockPos> getActualEndCities(long seed, int radius) {
        List<BlockPos> endCities = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // 计算每个区域的生成
                long baseRegionSeed = (long) x * REGION_MULTIPLIER_X ^
                        (long) z * REGION_MULTIPLIER_Z ^
                        seed;

                Random tempRandom = new Random(baseRegionSeed);
                tempRandom.nextInt();
                long regionSeed = tempRandom.nextLong();

                Random random = new Random(regionSeed + SALT);

                // 检查是否生成
                if (random.nextInt(2) == 0) {
                    // 生成末地城
                    int blockX = (x * SPACING + random.nextInt(SPACING - SEPARATION)) * 16;
                    int blockZ = (z * SPACING + random.nextInt(SPACING - SEPARATION)) * 16;
                    endCities.add(new BlockPos(blockX, DEFAULT_Y, blockZ));
                }
            }
        }
        return endCities;
    }
}