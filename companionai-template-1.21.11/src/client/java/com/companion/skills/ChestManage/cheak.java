package com.companion.skills.ChestManage;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.WorldChunk;

import java.util.*;

import static com.companion.global.*;

public class cheak {



    public static List<BlockPos> getAllChests() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientWorld world = mc.world;
        if (world == null) return Collections.emptyList();

        Set<BlockPos> result = new HashSet<>();
        ChunkManager cm = world.getChunkManager();

        // 获取已加载区块数量，估算范围（视距通常是 8-32）
        int viewDistance = mc.options.getViewDistance().getValue();
        int maxChunk = viewDistance + 2; // 加2个区块缓冲

        BlockPos playerPos = mc.player != null ? mc.player.getBlockPos() : BlockPos.ORIGIN;
        int playerChunkX = ChunkSectionPos.getSectionCoord(playerPos.getX());
        int playerChunkZ = ChunkSectionPos.getSectionCoord(playerPos.getZ());

        // 只遍历玩家周围的已加载区块
        for (int dx = -maxChunk; dx <= maxChunk; dx++) {
            for (int dz = -maxChunk; dz <= maxChunk; dz++) {
                int x = playerChunkX + dx;
                int z = playerChunkZ + dz;

                WorldChunk wc = cm.getWorldChunk(x, z);
                if (wc == null) continue;

                for (BlockEntity be : wc.getBlockEntities().values()) {
                    if (!(be instanceof ChestBlockEntity)) continue;

                    BlockPos pos = be.getPos();
                    BlockState state = world.getBlockState(pos);
                    BlockPos keep = pos;

                    if (state.getBlock() instanceof ChestBlock) {
                        ChestType chestType = state.get(ChestBlock.CHEST_TYPE);
                        if (chestType != ChestType.SINGLE) {
                            Direction facing = state.get(ChestBlock.FACING);
                            Direction otherDir = chestType == ChestType.LEFT
                                    ? facing.rotateYClockwise()
                                    : facing.rotateYCounterclockwise();
                            BlockPos paired = pos.offset(otherDir);

                            if (World.isValid(paired)) {
                                keep = sum(pos) <= sum(paired) ? pos : paired;
                            }
                        }
                    }
                    result.add(keep);
                }
            }
        }

        return new ArrayList<>(result);
    }

    private static int sum(Vec3i pos) {
        return pos.getX() + pos.getY() + pos.getZ();
    }

    public static boolean open(BlockPos TARGET_CHEST_POS){

        if (all_client.world != null) {
            player.openHandledScreen(
                    all_client.world.getBlockState(TARGET_CHEST_POS).createScreenHandlerFactory(all_client.world, TARGET_CHEST_POS));
            return true;
        }
        return false;
    }
    public static HashMap<Item, Integer> getChestItems() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return new HashMap<>();

        ScreenHandler handler = mc.player.currentScreenHandler;
        if (!(handler instanceof GenericContainerScreenHandler containerHandler)) {
            return new HashMap<>();
        }

        HashMap<Item, Integer> itemMap = new HashMap<>();

        // 获取箱子部分的槽位数量（54 for 双箱，27 for 单箱）
        int chestSlotCount = containerHandler.slots.size() - 36; // 减去玩家背包36格

        for (int i = 0; i < chestSlotCount; i++) {
            ItemStack stack = containerHandler.getSlot(i).getStack();
            if (stack.isEmpty()) continue;

            itemMap.merge(stack.getItem(), stack.getCount(), Integer::sum);
        }

        return itemMap;
    }

    // 辅助方法：合并两个物品统计Map（使用Item作为key）
    private static void mergeItemMaps(HashMap<Item, Integer> target, HashMap<Item, Integer> source) {
        for (Map.Entry<Item, Integer> entry : source.entrySet()) {
            Item item = entry.getKey();
            int count = entry.getValue();

            target.merge(item, count, Integer::sum);
        }
    }




    public static boolean isPlayerOpeningChest() {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null) return false;

        ScreenHandler screenHandler = player.currentScreenHandler;

        // 排除自身背包
        if (screenHandler == player.playerScreenHandler) return false;
        if (screenHandler instanceof GenericContainerScreenHandler) {
            return true;
        }

        return false;
    }
    public static void forceCloseOpenedChest() {
        // 直接调用玩家的「关闭容器界面」核心API
        MinecraftClient.getInstance().player.closeHandledScreen();
    }

    /**
     * 获取 当前服务器/单人存档 的唯一标识名
     * 用途：作为文件名、Map key、存储目录名，永不重复
     */
    /**
     * 获取当前服务器/单人存档的唯一标识（完全匹配1.21.11原生源码逻辑）
     * @return 唯一字符串，永不重复
     */
    public static String getUniqueWorldOrServerId() {
        MinecraftClient client = MinecraftClient.getInstance();

        // ========== 1. 多人服务器判断（匹配原生isConnectedToServer()逻辑） ==========
        // 核心：有网络处理器 + 不是本地集成服务器，或本地服务器是远程的（局域网联机）
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        boolean isRemoteServer = networkHandler != null &&
                (!client.isIntegratedServerRunning() || (client.getServer() != null && client.getServer().isRemote()));

        if (isRemoteServer) {
            // 获取服务器信息（IP+端口）
            ServerInfo serverInfo = client.getCurrentServerEntry();
            if (serverInfo != null && serverInfo.address != null) {
                // 替换特殊字符，兼容文件名/MapKey
                String safeAddress = serverInfo.address.replace(":", "_").replace("/", "_").replace("\\", "_");
                return "Server_" + safeAddress;
            }
            // 兜底：用网络连接的真实地址
            if (networkHandler.getConnection() != null) {
                String connAddress = networkHandler.getConnection().getAddress().toString()
                        .replace(":", "_").replace("/", "_");
                return "Server_" + connAddress;
            }
        }

        // ========== 2. 单人游戏/本地局域网服务器 ==========
        if (client.isInSingleplayer() && client.world != null && client.getLevelStorage() != null) {

            return "Single_" + Objects.requireNonNull(client.getServer()).getSaveProperties().getLevelName();


        }

        // ========== 3. 极端情况兜底（无世界/无服务器） ==========
        return "Unknown_" + UUID.randomUUID();
    }

}
