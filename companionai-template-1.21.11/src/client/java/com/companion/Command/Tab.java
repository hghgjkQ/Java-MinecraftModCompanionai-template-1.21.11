package com.companion.Command;

import com.companion.draw.base;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.companion.effect.tool.tipGame;
import static com.companion.global.*;
import static com.companion.render3D.screen.Text;
import static com.companion.render3D.screen.blocks;


public class Tab {

    // 建议提供者：自动从字典 KEY 生成提示词
    private static final SuggestionProvider<FabricClientCommandSource> DICT_KEY_SUGGESTIONS = (context, builder) -> {
        // 把你字典的所有 key 变成命令提示词
        for (String key : YOUR_DICT.keySet()) {
            builder.suggest(key); // 每个 key 都会显示在命令补全里
        }
        return builder.buildFuture();
    };
    private static final SuggestionProvider<FabricClientCommandSource> DICT_KEY_SUGGESTIONS2 = (context, builder) -> {
        // 把你字典的所有 key 变成命令提示词
        for (String key : Item_English_DICT.keySet()) {
            builder.suggest(key); // 每个 key 都会显示在命令补全里
        }
        return builder.buildFuture();
    };

    public static LiteralArgumentBuilder<FabricClientCommandSource> reg(LiteralArgumentBuilder<FabricClientCommandSource> tab){
        tab.then(ClientCommandManager.literal("find")
                .then(ClientCommandManager.argument("key", StringArgumentType.string())
                        .suggests(DICT_KEY_SUGGESTIONS) // 绑定提示词
                        .executes(ctx -> {
                            // 获取输入的 key
                            String inputKey = StringArgumentType.getString(ctx, "key");
                            String value = YOUR_DICT.get(inputKey);

                            tipGame("提示词",inputKey+"对应："+value);
                            all_client.keyboard.setClipboard(value);
                            return 1;
                        })
                )
        );

        tab.then(ClientCommandManager.literal("check")
                .then(ClientCommandManager.argument("key", StringArgumentType.string())
                        .suggests(DICT_KEY_SUGGESTIONS) // 绑定提示词
                        .executes(ctx -> {
                            // 获取输入的 key
                            String inputKey = StringArgumentType.getString(ctx, "key");
                            Item value =Item_DICT.get(inputKey);

                            return FindChest.find(value);
                        })
                )
        );
        tab.then(ClientCommandManager.literal("lookfor")
                .executes(ctx -> {
                    // 获取输入的 key
                    return FindChest.find(player.getMainHandStack().getItem());
                })

        );
        tab.then(ClientCommandManager.literal("discover")
                .then(ClientCommandManager.argument("key", StringArgumentType.string())
                        .suggests(DICT_KEY_SUGGESTIONS2) // 绑定提示词
                        .executes(ctx -> {
                            // 获取输入的 key
                            String inputKey = StringArgumentType.getString(ctx, "key");
                            Item value =Item_DICT.get(inputKey);

                            return FindChest.find(value);
                        })
                )
        );
        tab.then(ClientCommandManager.literal("clear")

                        .executes(ctx -> {
                            blocks.clear();
                            Text.clear();
                            return 1;
                        })

        );
        tab.then(ClientCommandManager.literal("time")
                .executes(ctx -> {
                    tipGame("美国时间：",t());
                    return 1;
                })
        );

        tab.then(ClientCommandManager.literal("bedrock")
                .executes(ctx -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    ClientPlayerEntity player = client.player;
                    World world = client.world;

                    if (player == null || world == null) return 0;

                    int px = (int) player.getX();
                    int pz = (int) player.getZ();
                    int y = 127; // 固定高度

                    // 遍历 X: -20 ~ +20
                    for (int x = px - 30; x <= px + 30; x++) {
                        // 遍历 Z: -20 ~ +20
                        for (int z = pz - 30; z <= pz + 30; z++) {
                            BlockPos pos = new BlockPos(x, y, z);
                            // 判断是不是基岩
                                if (world.getBlockState(pos.down()).isOf(Blocks.BEDROCK) ||world.getBlockState(pos.down(2)).isOf(Blocks.BEDROCK)||world.getBlockState(pos.down(3)).isOf(Blocks.BEDROCK)||world.getBlockState(pos.down(4)).isOf(Blocks.BEDROCK)||world.getBlockState(pos.down(5)).isOf(Blocks.BEDROCK)||world.getBlockState(pos.down(6)).isOf(Blocks.BEDROCK)) {
                                    continue;
                                }

                            base.line(pos.toCenterPos(),pos.up(10).toCenterPos(),5,ParticleTypes.HAPPY_VILLAGER);

                        }
                    }
                    return 1;
                })
        );
        return tab;
    }
    public static String t() {
        // 对应 Python: p='零一二三四五六七'
        char[] weekChars = "零一二三四五六七".toCharArray();
        // 获取当前本地时间（等价 time.localtime()）
        LocalDateTime now = LocalDateTime.now();

        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();
        // Python: tm_wday 0=周一,6=周日
        int weekDay = now.getDayOfWeek().getValue() - 1;

        int weekNum;
        String timePeriod;
        String timeStr;

        // 严格复刻你的判断逻辑
        if (hour > 12) {
            weekNum = weekDay + 1;
            timePeriod = "早上";
            timeStr = String.format("%d:%d:%d", hour - 12, minute, second);
        } else {
            if (weekDay == 0) {
                weekNum = 7;
            } else {
                weekNum = weekDay;
            }
            timePeriod = "晚上";
            timeStr = String.format("%d:%d:%d", hour, minute, second);
        }

        // 输出结果（和Python打印完全一致）
        return   String.format("星期%c的%s %s%n",weekChars[weekNum], timePeriod,timeStr);
    }

}
