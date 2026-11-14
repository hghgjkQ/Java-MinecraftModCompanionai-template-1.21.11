package com.companion.effect.TunnelBulid;

import com.companion.ParticleManage;
import com.companion.draw.base;
import com.companion.draw.mix;
import com.companion.render3D.Color;
import com.companion.render3D.screen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static com.companion.draw.base.line;
import static com.companion.global.*;
import static com.companion.render3D.screen.points;
import static com.companion.render3D.show.worldToScreen;

public class Show {
    public static void PlayerPos(PlayerEntity player){ //绘制当前玩家位置
        if (TunnelPos.contains(player.getBlockPos())) {
            base.point(ParticleManage.RED_POINT_PARTICLE,player.getBlockPos().toCenterPos());
        }else{
            base.point(ParticleManage.ORANGE_POINT_PARTICLE,player.getBlockPos().toCenterPos());
        }
    }
    public static void PlayerChooseLine(PlayerEntity player){
        if (!TunnelPos.isEmpty()){

            Vec3d a=new Vec3d(player.getBlockPos().toCenterPos().getX(),TunnelPos.getLast().toCenterPos().getY(),TunnelPos.getLast().toCenterPos().getZ());
            Vec3d b=new Vec3d(player.getBlockPos().toCenterPos().getX(),player.getBlockPos().toCenterPos().getY(),TunnelPos.getLast().toCenterPos().getZ());
            Vec3d c=new Vec3d(player.getBlockPos().toCenterPos().getX(),player.getBlockPos().toCenterPos().getY(),player.getBlockPos().toCenterPos().getZ());


            base.line(TunnelPos.getLast().toCenterPos(),a,1,ParticleManage.PURPLE_POINT_PARTICLE);
            base.line(a,b,1,ParticleManage.PURPLE_POINT_PARTICLE);
            base.line(b,c,1,ParticleManage.PURPLE_POINT_PARTICLE);
        }
    }
    public static void TunnelPath(){
        BlockPos posTemp=null;
        if (!TunnelPos.isEmpty()){

            for(BlockPos pos: TunnelPos){
                mix.block(pos.toCenterPos(),2,ParticleManage.PINK_POINT_PARTICLE);
                if(posTemp!=null) {
                    line(pos.toCenterPos(), posTemp.toCenterPos(),1, ParticleManage.BROWN_POINT_PARTICLE);
                }
                posTemp=pos;
            }

        }
    }
    public static void TunnelCreate(){
        if (!TunnelBuildPos.isEmpty()) {
            for (BlockPos pos : TunnelBuildPos.keySet()) {
                if(TunnelBuildPos.get(pos)<10) {
                    base.point(ParticleManage.WHITE_POINT_PARTICLE, pos.toCenterPos());
                }
            }
        }
    }
    public static void TunnelCheck() {
        points.clear();
        if (!TunnelBuildPos.isEmpty()) {
            for (BlockPos pos : TunnelBuildPos.keySet()) {
                if(TunnelBuildPos.get(pos)<10) {
                    points.add(new screen.PointWithColor(pos.toCenterPos(), "浅紫"));
                }
            }
        }

    }
}
