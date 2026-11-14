package com.companion.skills.MapDraw;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static com.companion.global.*;
import static com.companion.effect.tool.*;

public class Workpiece {


    static {
        NameColor.put("0", new ItemStack(Items.WHITE_CARPET));
        NameColor.put("1", new ItemStack(Items.ORANGE_CARPET));
        NameColor.put("2", new ItemStack(Items.MAGENTA_CARPET));
        NameColor.put("3", new ItemStack(Items.LIGHT_BLUE_CARPET));
        NameColor.put("4", new ItemStack(Items.YELLOW_CARPET));
        NameColor.put("5", new ItemStack(Items.LIME_CARPET));
        NameColor.put("6", new ItemStack(Items.PINK_CARPET));
        NameColor.put("7", new ItemStack(Items.GRAY_CARPET));
        NameColor.put("8", new ItemStack(Items.LIGHT_GRAY_CARPET));
        NameColor.put("9", new ItemStack(Items.CYAN_CARPET));
        NameColor.put("a", new ItemStack(Items.PURPLE_CARPET));
        NameColor.put("b", new ItemStack(Items.BLUE_CARPET));
        NameColor.put("c", new ItemStack(Items.BROWN_CARPET));
        NameColor.put("d", new ItemStack(Items.GREEN_CARPET));
        NameColor.put("e", new ItemStack(Items.RED_CARPET));
        NameColor.put("f", new ItemStack(Items.BLACK_CARPET));

        MapDrawPos[0]=0;
        MapDrawPos[1]=0;
    }
    /**
     * 输入 MC 任意方块坐标 (x, z)，返回所在 0级地图（128x128）的顶点
     * 全 long 类型，适配 MC 超大坐标范围
     */
    public static long[][] getMapVertices(long x, long z) {
        // 核心逻辑：Math.floor 向下取整，兼容负数；全 long 避免溢出
        long mapX = (long) Math.floor((double) x / 128) * 128;
        long mapZ = (long) Math.floor((double) z / 128) * 128;

        long[][] vertices = new long[2][2];
        vertices[0] = new long[]{mapX-64, mapZ + 64};    // 左上
        vertices[1] = new long[]{mapX +64, mapZ-64};    // 右下



        return vertices;
    }


    public static void readAndTraverseFillCommands() {
        String filePath="fill_commands.txt";
        try {
            // 1. 读取文件为字符串列表（UTF-8编码，匹配Python生成的文件）
            Path path = Paths.get(filePath);
            List<String> commandList = Files.readAllLines(path, StandardCharsets.UTF_8);

            // 2. 遍历列表（核心逻辑，可替换为自定义操作）
            long count=0;
            for (String cmd : commandList) {
                String trimmedCmd = cmd.trim();
                if (!trimmedCmd.isEmpty()) { // 跳过空行
                    if (MinecraftClient.getInstance().player != null) {
                        MinecraftClient.getInstance().player.networkHandler.sendChatCommand(trimmedCmd);
                        count+=1;
                        if (count%100==0){
                            sleep(10);
                        }
                    }

                }
            }
        } catch (Exception e) {
            error(e);
        }
    }
    /**
     * 把背包里第一块与主手同类的方块放到任意世界坐标（空气也行）
     *
     * @param target 目标世界坐标
     */
    public static void placeAt(BlockPos target) {
        MinecraftClient.getInstance().execute(() -> {
            ClientPlayerInteractionManager im = all_client.interactionManager;
            if (player == null || im == null) return;
            /* 3. 伪造 BlockHitResult：假装玩家右键了“目标方块”本身 */
            BlockHitResult hit = new BlockHitResult(
                    Vec3d.ofCenter(target), // 命中点设在 target 上方一格的中心
                    Direction.DOWN,              // 表示点击的是 target.up() 的底面
                    target,                 // 被点击的方块坐标
                    false
            );
            /* 4. 发原版交互包 */
            im.interactBlock(player, Hand.MAIN_HAND, hit);
        });
    }

    /**
     * 从 map.txt 文件加载地毯布局到 MapDrawItem
     * 文件格式：第一行 "宽度*高度:" 后紧跟字符序列（无压缩）
     * 例如：128*128:0123...（共 16384 个字符）
     * @param filePath 文件路径，默认可传入 "map.txt"
     */
    public static void loadMapFromFile(String filePath) {
        try {
            // 1. 读取整个文件内容（UTF-8 编码，与 Python 生成一致）
            Path path = Paths.get(filePath);
            String content = Files.readString(path, StandardCharsets.UTF_8).trim();

            // 2. 解析宽度*高度和字符序列
            int colonIndex = content.indexOf(':');
            if (colonIndex == -1) {
                error("无效的文件格式：未找到冒号分隔符");
                return;
            }

            String dimensionPart = content.substring(0, colonIndex);
            String sequence = content.substring(colonIndex + 1);

            // 3. 解析宽度和高度
            String[] dims = dimensionPart.split("\\*");
            if (dims.length != 2) {
                error("无效的尺寸格式，应为 宽度*高度");
                return;
            }

            int width = Integer.parseInt(dims[0]);
            int height = Integer.parseInt(dims[1]);

            // 4. 验证字符序列长度
            if (sequence.length() != width * height) {
                error(String.format("字符序列长度 %d 与尺寸 %d*%d=%d 不匹配",
                        sequence.length(), width, height, width * height));
                return;
            }

            // 5. 清空现有列表并构建新的二维列表
            MapDrawItem.clear();

            for (int y = 0; y < height; y++) {
                List<ItemStack> row = new ArrayList<>();
                for (int x = 0; x < width; x++) {
                    char c = sequence.charAt(y * width + x);
                    String key = String.valueOf(c);
                    ItemStack stack = NameColor.get(key);
                    if (stack == null) {
                        error("未知的字符映射: " + c);
                        stack = ItemStack.EMPTY; // 或使用默认物品
                    }
                    row.add(stack.copy()); // 使用 copy() 避免后续修改影响原映射
                }
                MapDrawItem.add(row);
            }

            tipGame("成功加载地图，尺寸: " + width + "x" + height);

        } catch (IOException e) {
            error("读取文件失败: " + e.getMessage());
        } catch (NumberFormatException e) {
            error("解析数字失败: " + e.getMessage());
        }
    }



}
