package com.companion.skills;

import static com.companion.draw.base.line;

import static com.companion.effect.tool.miniLeap;

import static com.companion.global.*;
import static com.companion.python.find.findPath;


import com.companion.ParticleManage;
import com.companion.draw.mix;
import com.companion.effect.tool;
import com.companion.skills.findpathcut;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class cutTree {

    private static Set<BlockState> Tree = new HashSet<>();

    static {
        // 经典原木
        Tree.add(Blocks.OAK_LOG.getDefaultState());          // 橡木原木
        Tree.add(Blocks.SPRUCE_LOG.getDefaultState());      // 云杉原木
        Tree.add(Blocks.BIRCH_LOG.getDefaultState());       // 桦木原木
        Tree.add(Blocks.JUNGLE_LOG.getDefaultState());      // 丛林原木
        Tree.add(Blocks.ACACIA_LOG.getDefaultState());     // 金合欢原木
        Tree.add(Blocks.DARK_OAK_LOG.getDefaultState());    // 深色橡木原木
        // 下界原木（茎）
        Tree.add(Blocks.CRIMSON_STEM.getDefaultState());    // 绯红原木
        Tree.add(Blocks.WARPED_STEM.getDefaultState());     // 诡异原木
        // 新增原木
        Tree.add(Blocks.MANGROVE_LOG.getDefaultState());    // 红树林原木
        Tree.add(Blocks.CHERRY_LOG.getDefaultState());      // 樱花原木（1.20+核心新增）
        Tree.add(Blocks.BAMBOO_BLOCK.getDefaultState());    // 竹子原木（1.20+）

    }

    private static boolean isLog(MinecraftClient client, BlockPos pos) {
        // 1. 基础判空：客户端/世界为空直接返回false
        if (client == null || client.world == null) {
            return false;
        }

        // 2. 原有判断：当前方块是否是原木
        BlockState currentState = client.world.getBlockState(pos);
        if (!Tree.contains(currentState)) {
            return false;
        }

        // 3. 新增判断：上下左右（六向）相邻至少有一个是空气
        // 定义6个相邻方向（上、下、东、西、南、北）
        Direction[] directions = {
                Direction.UP,    // 上
                Direction.DOWN,  // 下
                Direction.EAST,  // 东（右）
                Direction.WEST,  // 西（左）
                Direction.SOUTH, // 南（前）
                Direction.NORTH  // 北（后）
        };

        // 遍历所有方向，检查是否有至少一个空气方块
        for (Direction dir : directions) {
            BlockPos neighborPos = pos.offset(dir); // 获取相邻方块坐标
            BlockState neighborState = client.world.getBlockState(neighborPos);
            // 判断相邻方块是否是空气（isAir() 是Minecraft内置的空气判断方法，更通用）
            if (neighborState.isAir()) {
                return true; // 只要有一个是空气，立即返回true
            }
        }

        // 4. 若所有相邻都不是空气，返回false
        return false;
    }

    public static void reg(){
        ClientTickEvents.END_CLIENT_TICK.register((client)->{
            draw(client);
            update(client);

        }); // 每帧执行代码
    }



    private static BlockPos GetArea(MinecraftClient client){ //得到最近区域
        if (client==null ||client.world==null ||client.player==null)return null;
        double minD=Double.MAX_VALUE;
        BlockPos l=new BlockPos(0,0,0);
        for(BlockPos pos:CutTreeWasFinallyTree){
            double D=player.getEntityPos().distanceTo(pos.toCenterPos());
            if (D<minD){
                minD=D;
                l=pos;
            }

        }
        if(minD!=Double.MAX_VALUE) {
            return l;
        }else {
            return null;
        }
    }

    private static BlockPos GetLog(MinecraftClient client){ //得到最近木头
        if (client==null ||client.world==null ||client.player==null)return null;
        double minD=Double.MAX_VALUE;
        BlockPos l=new BlockPos(0,0,0);
        for(BlockPos pos:CutTreeWithCheckTree){
            double D=player.getEntityPos().distanceTo(pos.toCenterPos());
            if (D<minD){
                minD=D;
                l=pos;
            }

        }
        if(minD!=Double.MAX_VALUE) {
            return l;
        }else {
            return null;
        }
    }

    private static boolean InArea(MinecraftClient client) {
        // 1. 基础判空：客户端/世界/玩家不能为空
        if (client == null || client.world == null || client.player == null) {
            return false;
        }
        // 2. 集合判空：避免遍历空集合抛出异常
        if (CutTreeWasFinallyTree == null || CutTreeWasFinallyTree.isEmpty()) {
            return false;
        }

        // 3. 获取玩家坐标（使用判空后的 client.player，避免空指针）
        Vec3d posW = client.player.getBlockPos().toCenterPos();

        // 4. 遍历区域并判断（包含边界的严谨判断）
        for (BlockPos pos : CutTreeWasFinallyTree) {
            Vec3d pos1 = pos.toCenterPos().add(-CutTreeHalf, 0, -CutTreeHalf);
            Vec3d pos2 = pos.toCenterPos().add(CutTreeHalf, 0, CutTreeHalf);

            // 修正：使用 >=/<= 包含边界，避免边界坐标判断错误
            boolean inXRange = pos1.getX() <= posW.getX() && posW.getX() <= pos2.getX();
            boolean inZRange = pos1.getZ() <= posW.getZ() && posW.getZ() <= pos2.getZ();

            if (inXRange && inZRange) {
                return true;
            }
        }
        return false;
    }
    private static Set<BlockPos>CheckArea(MinecraftClient client,BlockPos pos){
        Set<BlockPos>Check=new HashSet<>();
        for(int x=-(int) CutTreeHalf;x<(int) CutTreeHalf+1;x++){
            for(int y=-10;y<30;y++){
                for(int z=-(int) CutTreeHalf;z<(int) CutTreeHalf+1;z++){
                    var t=pos.add(x,y,z);
                    if(isLog(client,t)){
                        Check.add(t);
                    }
                }
            }
        }
        return Check;
    }
    public static void CutTreeUpdate(MinecraftClient client){
        update(client);
    }


    private static void draw(MinecraftClient client){

        if(CutTreeR!=0) {
            CutTreeHalf=CutTreeAreaSize/2.0;
            player.setVelocity(0,0,0);
            player.fallDistance = 0.0F;
            player.setOnGround(false);

            mix.block(CutTreePos.toCenterPos(),2, ParticleManage.GREEN_POINT_PARTICLE); //起点
            //绘制小单元
            for(BlockPos pos:CutTreeWasFinallyTree){
                mix.drawRect(pos.toCenterPos().add(-CutTreeHalf,0,-CutTreeHalf),pos.toCenterPos().add(CutTreeHalf,0,CutTreeHalf),2, ParticleManage.YELLOW_POINT_PARTICLE);
            }
            for(BlockPos pos:CutTreeWithCheckTree){
                mix.block(pos.toCenterPos(),2,ParticleManage.BLUE_POINT_PARTICLE);
            }
            breakUnderPlayer(client);
            BlockPos posTemp=null;
            if (!CutTreeGoalPath.isEmpty()){

                for(BlockPos pos: CutTreeGoalPath){
                    mix.block(pos.toCenterPos(),5,ParticleManage.GREEN_POINT_PARTICLE);
                    if(posTemp!=null) {
                        line(pos.toCenterPos(), posTemp.toCenterPos(),3, ParticleManage.RED_POINT_PARTICLE);
                    }
                    posTemp=pos;
                }

            }
        }
    }
    private static  void update(MinecraftClient client){
        if(CutTreeR!=0){

            tipGame("CutTreePathGoal："+CutTreePathGoal);

            //决策
            if(CutTreeWasFinallyTree.isEmpty()) { //如果全部完成
                CutTreeR++;
                tipGame("开始处理半径："+CutTreeR*CutTreeAreaSize);
                cleanCutTreeWasFinallyTree();
            }
            if(!CutTreeWasFinallyTree.isEmpty()){ //如果未完成
                boolean a=InArea(client);
                tipGame("如果在区域中："+a);
                if (a) { //如果在区域中
                    CutTreeWithCheckTree=CheckArea(client,CutTreeGoalPos);
                    //判断距离


                    if(!CutTreeWithCheckTree.isEmpty()){
                        BlockPos L= GetLog(client);
                        tipGame("L："+L);
                        if (player.getEntityPos().distanceTo(L.toCenterPos())>CutTreeForD){ //如果到木头的距离超过了挖掘范围
                            tipGame(1111);
                            if(!CutTreePathGoal) {//如果状态正确
                                CutTreeGoalPath.clear();
                                CutTreeGoalPath = FindPath(client.world, player.getBlockPos(), L);
                                CutTreeGoalPathIndex = 0;
                                CutTreePathGoal=true;
                                if(CutTreeGoalPath.isEmpty()){ //完全无路可走情况
                                    CutTreePathGoal=false;
                                    cleanCutTreeWasFinallyTree();
                                    CutTreeGoalPath.clear();
                                    tipGame("砍树出现问题，无法寻路，被迫终止：砍树");
                                    CutTreeGoalPathIndex = 0;
                                    tipGame(CutTreeWasFinallyTree);
                                    if (CutTreeWasFinallyTree.contains(CutTreeGoalPos)) {
                                        CutTreeWasFinallyTree.remove(CutTreeGoalPos)   ;
                                    }else {
                                        CutTreeWasFinallyTree.remove(GetArea(client));
                                    }

                                }
                            }if(CutTreePathGoal){
                                if(miniLeap(CutTreeGoalPath.get(CutTreeGoalPathIndex).toCenterPos().add(0,-0.5,0),CutTreeLeapMinD)){
                                    CutTreeGoalPathIndex++;
                                    if(CutTreeGoalPathIndex>=CutTreeGoalPath.size()){
                                        CutTreeGoalPath.clear();
                                        CutTreePathGoal=false;
                                    }
                                }
                            }
                        }
                    }
                    if(CutTreeWithCheckTree.isEmpty()) { //如果此区域完成
                        CutTreeGoalPathIndex = 0;
                        CutTreeGoalPath.clear();
                        tipGame(CutTreeWasFinallyTree);
                        if (CutTreeWasFinallyTree.contains(CutTreeGoalPos)) {
                            CutTreeWasFinallyTree.remove(CutTreeGoalPos)   ;
                        }else {
                            CutTreeWasFinallyTree.remove(GetArea(client));
                        }
                    }

                }
                if(!a) {
                    if(CutTreeGoalPath.isEmpty()) { //如果不在区域中且无路可走
                        CutTreeGoalPos = GetArea(client);
                        CutTreeGoalPathIndex=0;
                        CutTreeGoalPath=FindPath(client.world, player.getBlockPos() ,AvoidNotAir(client,CutTreeGoalPos));
                        CutTreePathGoal=false;
                        if(CutTreeGoalPath.isEmpty()){ //完全无路可走情况
                            cleanCutTreeWasFinallyTree();
                            CutTreeGoalPath.clear();
                            tipGame("砍树出现问题，无法寻路，被迫终止");
                            CutTreeGoalPathIndex = 0;
                            tipGame(CutTreeWasFinallyTree);
                            if (CutTreeWasFinallyTree.contains(CutTreeGoalPos)) {
                                CutTreeWasFinallyTree.remove(CutTreeGoalPos)   ;
                            }else {
                                CutTreeWasFinallyTree.remove(GetArea(client));
                            }
                        }
                    }
                    if(!CutTreeGoalPath.isEmpty()){ //按照寻路走
                        if(miniLeap(CutTreeGoalPath.get(CutTreeGoalPathIndex).toCenterPos().add(0,-0.5,0),10)){
                            CutTreeGoalPathIndex++;
                            if(CutTreeGoalPathIndex>=CutTreeGoalPath.size()){
                                CutTreeGoalPath.clear();
                                CutTreePathGoal=false;
                            }
                        }
                    }
                }

            }

        }
    }

    private static BlockPos AvoidNotAir(MinecraftClient client,BlockPos pos){
        if (client==null ||client.world==null ||client.player==null)return pos;
        ClientWorld world= client.world;
        short y=1;
        if (!world.getBlockState(pos).isAir()){//如果不是可穿方块
            while (!world.getBlockState(pos.add(0,y,0)).isAir()){
                y++;
                if (y>321){
                    break;
                }
            }
            return pos.add(0,y,0);
        }else {
            return pos;
        }
    }

    public static void cleanCutTreeWasFinallyTree(){
        CutTreeWasFinallyTree.clear();
        for (int c = 0; c < 4; c++) {
            long fixedX = 0, fixedZ = 0; // 固定轴的值
            long varX = 0, varZ = 0;     // 变化轴的系数（1表示变化，0表示不变）

            // 根据c确定当前边：0-上边，1-下边，2-右边，3-左边
            if (c == 0) { // 上边：z固定为 +R*5，x变化
                varX = 1;
                fixedZ = (long) (CutTreeR * CutTreeAreaSize);
            } else if (c == 1) { // 下边：z固定为 -R*5，x变化
                varX = 1;
                fixedZ = (long) (-CutTreeR * CutTreeAreaSize);
            } else if (c == 2) { // 右边：x固定为 +R*5，z变化
                varZ = 1;
                fixedX = (long) (CutTreeR * CutTreeAreaSize);
            } else { // 左边：x固定为 -R*5，z变化
                varZ = 1;
                fixedX = (long) (-CutTreeR * CutTreeAreaSize);
            }

            // 生成当前边上的点，包括两端（up从0到2R）
            for (long up = 0; up <= CutTreeR * 2; up++) {
                long offset = (long) ((up - CutTreeR) * CutTreeAreaSize); // 从 -R*5 到 R*5 步长5
                int x = (int) (offset * varX + fixedX);
                int z = (int) (offset * varZ + fixedZ);
                CutTreeWasFinallyTree.add(CutTreePos.add(x, 0, z));
            }
        }
    }

    private static void breakUnderPlayer(MinecraftClient client) {
        ClientPlayerInteractionManager manager = client.interactionManager;

        if (client.player == null || manager == null) return;


        double radius = CutTreeBreakMaxD; // 可自行修改半径
        BlockPos centerPos = client.player.getBlockPos(); // pos是你的圆心BlockPos对象


        for (double xOffset = -radius; xOffset <= radius; xOffset++) { // 遍历X/Z轴偏移（-radius到+radius），Y仅偏移0和1
            for (double zOffset = -radius; zOffset <= radius; zOffset++) {
                for (double yOffset = 1-radius; yOffset <= radius; yOffset++) { //眼睛高度补偿
                    double distanceSq = xOffset * xOffset + yOffset * yOffset + zOffset * zOffset; // 计算偏移量的距离平方（球形判断核心，避免开根号）
                    if (distanceSq <= radius * radius) {
                        BlockPos pos = centerPos.add((int) xOffset, (int) yOffset, (int) zOffset);  // 生成实际的方块坐标（圆心+偏移量）
                        if (isLog(client,pos)) { //验证是不是原木
                            manager.attackBlock(pos, Direction.UP);
                        }
                    }
                }
            }
        }
    }




    private static List<BlockPos> FindPath(     ClientWorld world,BlockPos start,BlockPos target )
    {
        var a=findPath(world,start,target);
        if(a.isEmpty()){
            a=findpathcut.findPath(world,start,target);
            if(a.isEmpty()) {
                a=findPath(world,start,AvoidNotAir(MinecraftClient.getInstance(),target));
                if(a.isEmpty()) {
                    a=findpathcut.findPath(world,start,AvoidNotAir(MinecraftClient.getInstance(),target));
                }
            }
        }
        return new ArrayList<>(a) ;
    }
    private static void tipGame(Object x){
        //tool.tipGame( x);
    }

}

/*
    private static boolean isCanUse(MinecraftClient client,BlockPos pos){
        return client.world != null && client.world.getBlockState(pos).getBlock()!=Blocks.VOID_AIR;
    }

 private static boolean YinCutTreeWasFinallyTree(BlockPos pos) {
        // 空指针防护：pos或CutTreePos为空直接返回false
        if (pos == null || CutTreePos == null) {
            return false;
        }

        // 计算中心坐标和边界值
        int centerX = (int) CutTreePos.getX();
        int centerZ = (int) CutTreePos.getZ();
        int innerRadius = (int) CutTreeR;       // 内层边界（边框内侧）
        int outerRadius = (int) (CutTreeR + 3);   // 外层边界（边框外侧）

        // 计算pos相对于中心的偏移量（仅X/Z轴，边框是平面环形）
        int offsetX = Math.abs(pos.getX() - centerX);
        int offsetZ = Math.abs(pos.getZ() - centerZ);

        // 判断是否在「边框区域」：偏移量 ≥ 内层半径 且 ≤ 外层半径
        // 即：不在内部（<内层半径），也不在外部（>外层半径），仅在边框上
        return (offsetX >= innerRadius && offsetX <= outerRadius)
                || (offsetZ >= innerRadius && offsetZ <= outerRadius);
    }



 // 扫描获取玩家附近最近的空气方块（客户端专用）
    private static BlockPos getLog(MinecraftClient client) {
        // 1. 校验客户端玩家和世界是否有效（客户端必须判空）
        if (client.player == null || client.world == null) {
            return null;
        }

        BlockPos playerPos = BlockPos.ofFloored(client.player.getEntityPos()); // 获取玩家当前方块坐标
        // 2. 从近到远逐层扫描（立方体范围，半径从小到大，找到即返回）
        // 无需限定最大范围，客户端只会扫描已加载区块内的方块
        for (int radius = 0;radius<128 ; radius++) {
            // 遍历当前半径下的所有方块（立方体遍历）
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        // 跳过半径层外的方块（优化：只扫描当前半径的外层，更快找到最近的）
                        if (Math.abs(x) != radius && Math.abs(y) != radius && Math.abs(z) != radius) {
                            continue;
                        }

                        BlockPos checkPos = playerPos.add(x,y,z);
                        // 3. 客户端关键：只检查已加载区块内的方块，避免访问未加载地图报错
                        if (!isCanUse(client,checkPos)) {
                            continue;
                        }

                        if (YinCutTreeWasFinallyTree(checkPos) && isLog(client,checkPos)) {
                            return checkPos; // 找到最近的空气方块，直接返回
                        }
                    }
                }
            }
        }
        return null;
    }
//树木判断并进入下一轮循环
            CutTreeWasFinallyTree.removeIf(pos -> simply(isFinally(client,pos)));
            for(BlockPos pos:CutTreeWasFinallyTree){
                point(GREEN_POINT_PARTICLE,pos.toCenterPos());
            }

            if(CutTreeWasFinallyTree.isEmpty()){
                cleanCutTreeWasFinallyTree();
                CutTreeR+=3;
            }



            //决策
            if(CutTreeGoalPath.isEmpty()){
                CutTreeGoalPathIndex=0;
                CutTreeGoalPos=getLog(client);
                if(CutTreeGoalPos==null){
                    cleanCutTreeWasFinallyTree();
                    CutTreeR+=3;
                    return;
                }
                CutTreeGoalPath=findPath(client.world, player.getBlockPos() ,CutTreeGoalPos);
                if(CutTreeGoalPath.isEmpty()){
                    CutTreeR=0;
                    cleanCutTreeWasFinallyTree();
                    CutTreeGoalPath.clear();
                    tipGame("砍树出现问题，无法寻路，被迫终止");
                }
            }else{
                if(miniLeap(CutTreeGoalPath.get(CutTreeGoalPathIndex).toCenterPos().add(0,-0.5,0),10)){
                    CutTreeGoalPathIndex++;
                    if(CutTreeGoalPathIndex>=CutTreeGoalPath.size()){
                        CutTreeGoalPath.clear();
                    }
                }
            }

                private static int isFinally(MinecraftClient client,BlockPos pos){
        for(int y=0;y<30;y++){ //即最高树木判断为高差30格
            if(isCanUse(client,pos.add(0,y,0))){ //如果被加载同时不是树木
                if(isLog(client,pos.add(0,y,0)))return 1;

                }else{
                    return 0;
                }
        }
        return 2;
    }
    private static boolean simply(int x){
        return x != 1;
    }

 */