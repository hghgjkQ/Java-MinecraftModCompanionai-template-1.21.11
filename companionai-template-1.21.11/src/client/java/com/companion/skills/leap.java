package com.companion.skills;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static com.companion.effect.tool.miniLeap;
import static com.companion.effect.tool.tipGame;
import static com.companion.global.*;

public class leap {
    public static void update(MinecraftClient client){
        ClientPlayerEntity player =client.player;
        if(player==null || client.world==null)return;

        Vec3d blockPos=player.getBlockPos().toCenterPos();

        if(leapState){
            if (leapWait==4){
                if(player.getBlockPos().getY()<330){
                    if (getTopYByScan(player.getBlockX(), player.getBlockZ())>player.getBlockY()){
                        leapState=false;
                        leapWait=-1;
                        tipGame("跃迁","上方有方块取消跃迁");
                    }else{ //可以起飞
                        player.setPos(blockPos.x,blockPos.y-0.5,blockPos.z);
                        leapWait=0;
                    }
                }
            }else if(leapWait==0){
                if(player.getBlockPos().getY()<330) {
                    miniLeap(blockPos.x,blockPos.y-0.5+leapMaxD,blockPos.z,leapMaxD);
                }else{
                    leapWait=1;
                }
            }else if(leapWait==1){
                if (miniLeap(leapGladPos.getX(),blockPos.y-0.5,leapGladPos.getZ(),leapMaxD)){
                    leapWait=2;
                    leapGladPos= BlockPos.ofFloored(new Vec3d(player.getBlockX(),getTopYByScan(leapGladPos.getX(), leapGladPos.getZ())+3,player.getBlockZ()));
                }
            }else if(leapWait==2){
                if (miniLeap(leapGladPos.getX(),blockPos.y-0.5,leapGladPos.getZ(),leapMaxD)){
                    leapWait=3;
                }
            }else if(leapWait==3){
                tipGame("跃迁","完成，取消跃迁");
                leapState=false;
            }
        }
    }



    /**
     * 【客户端 - 遍历方块法】获取指定 XZ 的最高非空气方块 Y
     * @param targetX 目标 X 坐标
     * @param targetZ 目标 Z 坐标
     * @return 最高方块 Y，未找到返回 -1
     */
    public static int getTopYByScan(int targetX, int targetZ) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientWorld world = mc.world;

        // 客户端空安全（必须加）
        if (world == null) return -1;

        // 获取世界的 最高Y / 最低Y（自动适配 主世界/下界/末地）
        int topY = 322;
        int bottomY = world.getBottomY();

        // 从最高往下遍历，找第一个非空气方块
        for (int y = topY - 1; y >= bottomY; y--) {
            BlockPos pos = new BlockPos(targetX, y, targetZ);

            BlockState state = world.getBlockState(pos);
            // 找到非空气方块 = 最高方块
            if (!state.isAir()) {
                return y;
            }
        }

        return -1;
    }
}
