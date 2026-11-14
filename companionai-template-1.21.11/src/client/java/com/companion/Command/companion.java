package com.companion.Command;

import com.companion.effect.TunnelBulid.Show;
import com.companion.effect.TunnelBulid.Use;
import com.companion.render3D.screen;
import com.companion.skills.ChestControl;
import com.companion.skills.MapDraw.Workpiece;
import com.companion.skills.bat.AutoRaid;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;


import static com.companion.Key.config.setV;
import static com.companion.Key.configTool.cfg;
import static com.companion.effect.tool.tipGame;
import static com.companion.effect.weapon.tool.ClickChat;
import static com.companion.global.*;
import static com.companion.global.TunnelBuildPos;
import static com.companion.python.find.findPath;
import static com.companion.render3D.screen.blocks;
import static com.companion.render3D.screen.points;
import static com.companion.skills.ChestManage.cheak.getAllChests;
import static com.companion.skills.MapDraw.Workpiece.loadMapFromFile;
import static com.companion.skills.MiningBot.FindPos.findNearestAncientDebris;
import static com.companion.skills.MiningBot.MiningBot.getPlayerInventoryItems;
import static com.companion.skills.cutTree.CutTreeUpdate;
import static com.companion.skills.cutTree.cleanCutTreeWasFinallyTree;

public class companion {
    static BlockPos PosEnd=new BlockPos(0,0,0);
    static BlockPos PosStart=new BlockPos(0,0,0);
    public static LiteralArgumentBuilder<FabricClientCommandSource> reg(LiteralArgumentBuilder<FabricClientCommandSource> Command){
        // temp子命令
        Command.then(ClientCommandManager.literal("mapdraw")
                .then(ClientCommandManager.literal("setpos")
                        .executes(ctx -> {
                            double[] pos = new double[2];
                            pos[0] = player.getX() + 64;
                            pos[1] = player.getZ() + 64;
                            long[][] top = Workpiece.getMapVertices((long) pos[0], (long) pos[1]);
                            MapDrawPos = top[0];
                            tipGame("已设定起点位置为" + Arrays.toString(MapDrawPos));
                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("start")
                        .executes(ctx -> {
                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("load")
                        .executes(ctx -> {
                            loadMapFromFile("map.txt");
                            return 1;
                        })
                )

        );
        Command.then(ClientCommandManager.literal("temp")
                .then(ClientCommandManager.literal("1")
                        .executes(ctx -> {
                            //throwNonCobble(player);
                            if (all_client.world != null) {
                                all_client.world.setBlockState(BlockPos.ofFloored(player.getEntityPos().add(0, 2, 0)), Blocks.STONE.getDefaultState(), 1);
                            }

                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("2")
                        .executes(ctx -> {
                            blocks.clear();
                            var a = getAllChests();
                            for (BlockPos x : a) {
                                if (CheckChestData.containsKey(x)) {
                                    blocks.add(new screen.BlockWithColor(x, "纯红"));
                                } else {
                                    blocks.add(new screen.BlockWithColor(x, "天蓝"));
                                    CheckTarget.add(x);
                                }
                            }


                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("3")
                        .executes(ctx -> {
                            ChestControl.openChest(new BlockPos(0,0,0));


                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("4")
                        .executes(ctx -> {


                            tipGame( AutoRaid.hasRaidBossBar(ctx.getSource().getClient()));
                            return 1;
                        })
                )

        );
        Command.then(ClientCommandManager.literal("bow")
                .then(ClientCommandManager.literal("on")
                        .executes(ctx -> {
                            setV(cfg,cfg.BowState,true);
                            tipGame("Bow（光粒）已打开");
                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("off")
                        .executes(ctx -> {
                            setV(cfg,cfg.BowState,false);
                            tipGame("Bow（光粒）已关闭");
                            return 1;
                        })
                )

        );
        Command.then(ClientCommandManager.literal("raid")
                .then(ClientCommandManager.literal("on")
                        .executes(ctx -> {
                            setV(cfg,cfg.RaidState,true);
                            if(cfg.BowState.get()){
                                tipGame("Raid（神杀）已打开，此方法基于Bow");
                            }else{
                                tipGame("Raid（神杀）已打开，同时已启动Bow");
                            }
                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("off")
                        .executes(ctx -> {
                            setV(cfg,cfg.RaidState,false);
                            tipGame("Raid（神杀）已关闭");
                            return 1;
                        })
                )

        );
        Command.then(ClientCommandManager.literal("spear")
                .then(ClientCommandManager.literal("on")
                        .executes(ctx -> {
                            setV(cfg,cfg.SpearState,true);
                            tipGame("Spear（狙击枪）已打开");
                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("off")
                        .executes(ctx -> {
                            setV(cfg,cfg.SpearState,false);
                            tipGame("Spear（狙击枪）已关闭");
                            return 1;
                        })
                )

        );
        Command.then(ClientCommandManager.literal("tp")
                .then(ClientCommandManager.literal("on")
                        .executes(ctx -> {
                            setV(cfg,cfg.TpState,true);
                            tipGame("tp（冲刺）已打开");
                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("off")
                        .executes(ctx -> {
                            setV(cfg,cfg.TpState,false);
                            tipGame("tp（冲刺）已关闭");
                            return 1;
                        })
                )

        );
        Command.then(ClientCommandManager.literal("miningbot")
                .then(ClientCommandManager.literal("on")
                        .executes(ctx -> {

                            MiningBotOldPath.clear();
                            MiningBotOldPath.add(player.getBlockPos());
                            MiningBotOldPath.add(player.getBlockPos().up());
                            MiningBotStayItem.clear();
                            MiningBotStayItem=getPlayerInventoryItems();
                            MiningBotStayItem.add(Items.QUARTZ);
                            MiningBotStayItem.add(Items.NETHER_QUARTZ_ORE);
                            MiningBotStayItem.add(Items.GOLD_NUGGET);
                            MiningBotStayItem.add(Items.NETHER_GOLD_ORE);
                            MiningBotStayItem.add(Items.ANCIENT_DEBRIS);
                            MiningBotGoalPath.clear();
                            MiningBotGoalPos=findNearestAncientDebris(100);
                            MiningBotGoalPathIndex=0;
                            MiningBotGoalBox=new BlockPos(0,0,0);

                            MiningBotBlockBan.clear();
                            MiningBotGoalBlock.clear();
                            MiningBotGoalItems.clear();

                            if (all_client.world != null) {
                                if(all_client.world.getRegistryKey() == World.OVERWORLD) {
                                    MiningBotGoalBlock.add(Blocks.REDSTONE_ORE);
                                    MiningBotGoalBlock.add(Blocks.DEEPSLATE_REDSTONE_ORE);
                                    //MiningBotGoalBlock.add(Blocks.DIAMOND_ORE);
                                    //MiningBotGoalBlock.add(Blocks.DEEPSLATE_DIAMOND_ORE);
                                    //MiningBotGoalBlock.add(Blocks.EMERALD_ORE);
                                    //MiningBotGoalBlock.add(Blocks.DEEPSLATE_EMERALD_ORE);

                                    MiningBotGoalItems.add(Items.DEEPSLATE_REDSTONE_ORE);
                                    MiningBotGoalItems.add(Items.REDSTONE_ORE);
                                    //MiningBotGoalItems.add(Items.DIAMOND_ORE);
                                    //MiningBotGoalItems.add(Items.EMERALD_ORE);
                                    //MiningBotGoalItems.add(Items.DEEPSLATE_EMERALD_ORE);
                                    //MiningBotGoalItems.add(Items.DEEPSLATE_DIAMOND_ORE);

                                    //MiningBotGoalItems.add(Items.DIAMOND);
                                    //MiningBotGoalItems.add(Items.EMERALD);
                                    MiningBotGoalItems.add(Items.REDSTONE);
                                    MiningBotGoalItems.add(Items.SHULKER_BOX);

                                    MiningBotMaxY=30;
                                    MiningBotMinY=-60;
                                }else{ //不是主世界
                                    MiningBotGoalBlock.add(Blocks.ANCIENT_DEBRIS);

                                    MiningBotGoalItems.add(Items.ANCIENT_DEBRIS);

                                    MiningBotMaxY=22;
                                    MiningBotMinY=6;
                                }
                            }
                            MiningBotState =true;
                            tipGame("MiningBot（挖矿机器人）已打开");

                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("off")
                        .executes(ctx -> {
                            MiningBotState=false;
                            tipGame("MiningBot（挖矿机器人）已关闭");
                            return 1;
                        })
                )

        );
        Command.then(ClientCommandManager.literal("findpath")
                .then(ClientCommandManager.literal("find")
                        .executes(ctx -> {
                            BeFoundPath = findPath(MinecraftClient.getInstance().world, PosStart, PosEnd);
                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("start")
                        .executes(ctx -> {
                            PosStart = player.getBlockPos();
                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("end")
                        .executes(ctx -> {
                            PosEnd = player.getBlockPos();
                            BeFoundPath = findPath(MinecraftClient.getInstance().world, PosStart, PosEnd);
                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("come")
                        .executes(ctx -> {
                            wasComePath = 0;
                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("stop")
                        .executes(ctx -> {
                            wasComePath = -1;
                            return 1;
                        })
                )

        );
        Command.then(ClientCommandManager.literal("cuttree")
                .then(ClientCommandManager.literal("start")
                        // 1. 跃迁距离参数（可选，默认10）
                        .then(ClientCommandManager.argument("leapDistance", DoubleArgumentType.doubleArg(1, 50))
                                // 2. 区域大小参数（可选，默认5）
                                .then(ClientCommandManager.argument("areaSize", DoubleArgumentType.doubleArg(1, 20))
                                        // 3. 砍树范围参数（可选，默认6）
                                        .then(ClientCommandManager.argument("breakRange", DoubleArgumentType.doubleArg(1, 20))
                                                // 4. 寻树范围参数（可选，默认4）
                                                .then(ClientCommandManager.argument("findRange", DoubleArgumentType.doubleArg(1, 50))
                                                        .executes(ctx -> {
                                                            // 赋值命令传入的参数
                                                            CutTreeLeapMinD = DoubleArgumentType.getDouble(ctx, "leapDistance");
                                                            CutTreeAreaSize = DoubleArgumentType.getDouble(ctx, "areaSize");
                                                            CutTreeBreakMaxD = DoubleArgumentType.getDouble(ctx, "breakRange");
                                                            CutTreeForD = DoubleArgumentType.getDouble(ctx, "findRange");

                                                            // 原有逻辑完全保留
                                                            CutTreePathGoal=false;
                                                            CutTreeR = 1;
                                                            CutTreePos = player.getBlockPos();
                                                            cleanCutTreeWasFinallyTree();
                                                            CutTreeGoalPath.clear();

                                                            return 1;
                                                        })
                                                )
                                                // 寻树范围省略时用默认值
                                                .executes(ctx -> {
                                                    CutTreeLeapMinD = DoubleArgumentType.getDouble(ctx, "leapDistance");
                                                    CutTreeAreaSize = DoubleArgumentType.getDouble(ctx, "areaSize");
                                                    CutTreeBreakMaxD = DoubleArgumentType.getDouble(ctx, "breakRange");
                                                    CutTreeForD = 4; // 默认寻树范围

                                                    CutTreePathGoal=false;
                                                    CutTreeR = 1;
                                                    CutTreePos = player.getBlockPos();
                                                    cleanCutTreeWasFinallyTree();
                                                    CutTreeGoalPath.clear();

                                                    return 1;
                                                })
                                        )
                                        // 砍树范围省略时用默认值
                                        .executes(ctx -> {
                                            CutTreeLeapMinD = DoubleArgumentType.getDouble(ctx, "leapDistance");
                                            CutTreeAreaSize = DoubleArgumentType.getDouble(ctx, "areaSize");
                                            CutTreeBreakMaxD = 6; // 默认砍树范围
                                            CutTreeForD = 4; // 默认寻树范围

                                            CutTreePathGoal=false;
                                            CutTreeR = 1;
                                            CutTreePos = player.getBlockPos();
                                            cleanCutTreeWasFinallyTree();
                                            CutTreeGoalPath.clear();

                                            return 1;
                                        })
                                )
                                // 区域大小省略时用默认值
                                .executes(ctx -> {
                                    CutTreeLeapMinD = DoubleArgumentType.getDouble(ctx, "leapDistance");
                                    CutTreeAreaSize = 5; // 默认区域大小
                                    CutTreeBreakMaxD = 6; // 默认砍树范围
                                    CutTreeForD = 4; // 默认寻树范围

                                    CutTreePathGoal=false;
                                    CutTreeR = 1;
                                    CutTreePos = player.getBlockPos();
                                    cleanCutTreeWasFinallyTree();
                                    CutTreeGoalPath.clear();

                                    return 1;
                                })
                        )
                        // 所有参数省略时用默认值（原有逻辑）
                        .executes(ctx -> {
                            // 保留默认参数值
                            CutTreeLeapMinD=10; //最大跃迁距离
                            CutTreeAreaSize=5; //区域大小
                            CutTreeBreakMaxD=6; //最大砍树范围
                            CutTreeForD=4; //寻树范围

                            // 原有逻辑完全不变
                            CutTreePathGoal=false;
                            CutTreeR = 1;
                            CutTreePos = ctx.getSource().getClient().player.getBlockPos();
                            cleanCutTreeWasFinallyTree();
                            CutTreeGoalPath.clear();

                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("stop")
                        .executes(ctx -> {
                            CutTreePathGoal=false;
                            CutTreeR = 0;
                            cleanCutTreeWasFinallyTree();
                            CutTreeGoalPath.clear();
                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("tick")
                        .executes(ctx -> {
                            CutTreeUpdate(MinecraftClient.getInstance());
                            return 1;
                        })
                )

        );


        Command.then(ClientCommandManager.literal("tunnel")
                .then(ClientCommandManager.literal("on")
                        .executes(ctx -> {
                            TunnelTargetBlock= player.getMainHandStack().getItem();
                            TunnelState=true;
                            tipGame("隧道建造开启");
                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("off")
                        .executes(ctx -> {
                            TunnelState=false;
                            tipGame("隧道建造关闭");
                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("save")
                        .executes(ctx -> {
                            Use.write(player.getMainHandStack());
                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("load")
                        .executes(ctx -> {
                            Use.read(player.getMainHandStack());
                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("create")
                        .executes(ctx -> {
                            Use.create();

                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("clear")
                        .executes(ctx -> {
                            TunnelPos.clear();
                            TunnelBuildPos.clear();
                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("check")
                        .executes(ctx -> {
                            Show.TunnelCheck();

                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("checkclear")
                        .executes(ctx -> {
                            points.clear();
                            return 1;
                        })
                )
        );
        Command.then(ClientCommandManager.literal("autoraid")
                .then(ClientCommandManager.literal("add")
                        .executes(ctx -> {
                            RaidIn.add(getTargetBlockPos());
                            tipGame(RaidIn);
                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("clear")
                        .executes(ctx -> {
                            RaidIn.clear();
                            RaidStep=-1;
                            RaidOut.clear();
                            tipGame("清理完成");
                            return 1;
                        })
                )
        );
        // leap子命令 - 原有部分保持不变
        Command.then(ClientCommandManager.literal("leap")
                        .then(ClientCommandManager
                                .argument("x", DoubleArgumentType.doubleArg())
                                .suggests((ctx, builder) -> builder.suggest((int) player.getX()).buildFuture())
                                .then(ClientCommandManager
                                        .argument("y", DoubleArgumentType.doubleArg())
                                        .suggests((ctx, builder) -> builder.suggest((int) player.getY()).buildFuture())
                                        .then(ClientCommandManager
                                                .argument("z", DoubleArgumentType.doubleArg())
                                                .suggests((ctx, builder) -> builder.suggest((int) player.getZ()).buildFuture())
                                                .then(ClientCommandManager
                                                        .argument("最大跃迁距离", DoubleArgumentType.doubleArg(Double.MIN_VALUE))
                                                        .suggests((ctx, builder) -> builder.suggest(10).buildFuture())

                                                                .executes(ctx -> {
                                                                    leapGladPos = new BlockPos(
                                                                            (int) DoubleArgumentType.getDouble(ctx, "x"),
                                                                            (int) DoubleArgumentType.getDouble(ctx, "y"),
                                                                            (int) DoubleArgumentType.getDouble(ctx, "z")
                                                                    );

                                                                    leapMaxD = DoubleArgumentType.getDouble(ctx, "最大跃迁距离");
                                                                    leapState=true;
                                                                    leapWait=4;
                                                                    return 1;
                                                                })

                                                )
                                        )
                                )
                        )
                .then(ClientCommandManager.literal("stop")
                        .executes(ctx -> {
                            leapState=false;
                            leapWait=-1;
                            return 1;
                        })
                )
        );


        Command.then(ClientCommandManager.literal("macefilter")
                .then(ClientCommandManager.literal("entity")
                        .then(ClientCommandManager.literal("add")
                                .then(ClientCommandManager
                                        .argument("name", StringArgumentType.string())
                                        .executes(ctx -> {
                                            String t=StringArgumentType.getString(ctx, "name");
                                            if(filter.contains(t)) {
                                                tipGame("MaceFilter", Text.literal("§a实体§5"+t+"§a已经被过滤了§2").append(ClickChat("[","取消过滤","/companionai macefilter entity remove "+t,"]")));

                                            }else{
                                                filter.add(t);
                                                tipGame("MaceFilter",Text.literal("§a实体§5"+t+"§a将被过滤§2").append(ClickChat("[","再次取消过滤","/companionai macefilter entity remove "+t,"]")));
                                            }
                                            return 1;
                                        })
                                )
                        )
                        .then(ClientCommandManager.literal("remove")
                                .then(ClientCommandManager
                                        .argument("name", StringArgumentType.string())
                                        .executes(ctx -> {
                                            String t=StringArgumentType.getString(ctx, "name");
                                            if(filter.contains(t)) {
                                                filter.remove(t);
                                                tipGame("MaceFilter",Text.literal("§4实体§5"+t+"§4不会被过滤§2").append(ClickChat("[","再次过滤","/companionai macefilter entity add "+t,"]")));
                                            }else{
                                                tipGame("MaceFilter",Text.literal("§4实体§5"+t+"§4本就不会被过滤§2").append(ClickChat("[","过滤它","/companionai macefilter entity add "+t,"]")));
                                            }
                                            return 1;
                                        })
                                )
                        )
                )

                .then(ClientCommandManager.literal("player")
                        .then(ClientCommandManager.literal("add")
                                .then(ClientCommandManager
                                        .argument("name", StringArgumentType.string())
                                        .executes(ctx -> {
                                            String t=StringArgumentType.getString(ctx, "name");
                                            if(filterPlayer.contains(t)) {
                                                tipGame("MaceFilter",Text.literal("§a玩家§5"+t+"§a已经被过滤了§2").append(ClickChat("[","再次取消过滤","/companionai macefilter player remove "+t,"]")));
                                            }else{
                                                filterPlayer.add(t);
                                                tipGame("MaceFilter",Text.literal("§a玩家§5"+t+"§a将被过滤§2").append(ClickChat("[","取消过滤","/companionai macefilter player remove "+t,"]")));
                                            }
                                            return 1;
                                        })
                                )
                        )
                        .then(ClientCommandManager.literal("remove")
                                .then(ClientCommandManager
                                        .argument("name", StringArgumentType.string())
                                        .executes(ctx -> {
                                            String t=StringArgumentType.getString(ctx, "name");
                                            if(filterPlayer.contains(t)) {
                                                filterPlayer.remove(t);
                                                tipGame("MaceFilter",Text.literal("§4玩家§5"+t+"§4不会被过滤§2").append(ClickChat("[","再次过滤","/companionai macefilter player add "+t,"]")));
                                            }else{
                                                tipGame("MaceFilter",Text.literal("§4玩家§5"+t+"§4本就不会被过滤§2").append(ClickChat("[","过滤","/companionai macefilter player add "+t,"]")));
                                            }
                                            return 1;
                                        })
                                )
                        )

                    )

        );

        return Command;

    }



    /**
     * 获取玩家准星指向的方块位置 BlockPos
     * @return 准星指向方块的坐标，null=未指向任何方块
     */
    public static BlockPos getTargetBlockPos() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return null;

        // 获取准星目标（1.0f为完整帧，包含流体检测）
        HitResult hit = player.getCrosshairTarget(1.0f,player);

        // 判断是否指向方块
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            return blockHit.getBlockPos();
        }
        return player.getBlockPos();
    }
}
