package com.companion.Command;

import com.companion.System.temp;
import com.companion.effect.TunnelBulid.Show;
import com.companion.effect.TunnelBulid.Use;
import com.companion.render3D.screen;
import com.companion.skills.ChestControl;
import com.companion.skills.MapDraw.Workpiece;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

import static com.companion.effect.tool.*;
import static com.companion.global.*;

import static com.companion.python.find.findPath;
import static com.companion.render3D.screen.*;
import static com.companion.skills.ChestManage.cheak.*;
import static com.companion.skills.MapDraw.Workpiece.*;
import static com.companion.skills.MiningBot.FindPos.findNearestAncientDebris;
import static com.companion.skills.MiningBot.MiningBot.getPlayerInventoryItems;
import static com.companion.skills.cutTree.CutTreeUpdate;
import static com.companion.skills.cutTree.cleanCutTreeWasFinallyTree;


public class main {





    public static void reg() {
        //注册命令及其子命令
        try {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
                // 创建根命令

                LiteralArgumentBuilder<FabricClientCommandSource> Command = ClientCommandManager.literal("companionai");
                LiteralArgumentBuilder<FabricClientCommandSource> tab = ClientCommandManager.literal("tab");

                companion.reg(Command);
                Tab.reg(tab);


                dispatcher.register(tab);
                dispatcher.register(Command);
            });
        }catch (Exception e){
            error(e);
        }
    }


}