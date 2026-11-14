package com.companion.skills.findpath;

import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

public final class BatchBlockPassUtil {
    private static final int MIN_Y = -64;
    private static final int MAX_Y = 318;

    private BatchBlockPassUtil() {}

    // 官方公开API批量获取（无私有字段访问）

    }