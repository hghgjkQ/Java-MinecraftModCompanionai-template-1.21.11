package com.companion.effect.TunnelBulid;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

import static com.companion.effect.tool.tipGame;
import static com.companion.global.*;
import static com.companion.render3D.screen.points;

public class main {
    private static boolean rig=false;
    private static boolean l=false;
    public static void reg(ItemStack main,boolean right,boolean left){
        PlayerEntity player = MinecraftClient.getInstance().player;
        if(player==null) return;
        if (main.getItem()==TunnelChooseTool){ //如果拿起目标物品
            Show.PlayerPos(player);
            Show.PlayerChooseLine(player);

            if(right){
                if (!rig) {
                    if (TunnelPos.contains(player.getBlockPos())) {
                        TunnelPos.remove(player.getBlockPos());
                        tipGame("已删除位置[" + player.getBlockPos() + "]");
                    }else{
                        if (!TunnelPos.isEmpty()) {

                            BlockPos a = new BlockPos(player.getBlockPos().getX(), TunnelPos.getLast().getY(), TunnelPos.getLast().getZ());
                            BlockPos b = new BlockPos(player.getBlockPos().getX(), player.getBlockPos().getY(), TunnelPos.getLast().getZ());
                            BlockPos c = new BlockPos(player.getBlockPos().getX(), player.getBlockPos().getY(), player.getBlockPos().getZ());
                            if ((TunnelPos.getLast().getX() - player.getBlockPos().getX()) != 0) {
                                TunnelPos.add(a);
                            }
                            if ((TunnelPos.getLast().getY() - player.getBlockPos().getY()) != 0) {
                                TunnelPos.add(b);
                            }
                            if ((TunnelPos.getLast().getZ() - player.getBlockPos().getZ()) != 0) {
                                TunnelPos.add(c);
                            }
                        } else {
                            TunnelPos.add(player.getBlockPos());
                        }

                        tipGame("已添加位置[" + player.getBlockPos() + "]");
                    }
                    rig = true;
                }
            }else{
                if (rig) {
                    rig = false;
                }
            }
            if(left){
                if (!l) {
                    if(!TunnelPos.isEmpty()) {
                        tipGame("偏移量[x=" + (TunnelPos.getLast().getX() - player.getBlockPos().getX()) + " y=" + (TunnelPos.getLast().getY() - player.getBlockPos().getY()) + " z=" + (TunnelPos.getLast().getZ() - player.getBlockPos().getZ()) + "]");
                    }
                    l = true;
                }
            }else{
                if (l) {
                    l = false;
                }
            }
        }
        Show.TunnelPath();
        if(!points.isEmpty()){
            Show.TunnelCheck();
        }
        Show.TunnelCreate();
        // 过滤保留不符合条件的方块，直接赋值
        //防止未放置延迟干扰
        for(BlockPos p:TunnelBuildPos.keySet()){
            if(player.getEntityPos().distanceTo(p.toCenterPos()) < 100 && !player.getEntityWorld().getBlockState(p).isAir()){
                TunnelBuildPos.put(p,TunnelBuildPos.get(p)+1);
            }else{
                TunnelBuildPos.put(p,0);
            }
        }
        TunnelBuildPos.entrySet().removeIf(pos -> pos.getValue() > 10); //删除判断了十次的
        if(TunnelState && !TunnelBuildPos.isEmpty()){
            if(main.getItem()==TunnelTargetBlock){
                BlockPos MinBlock=TunnelBuildPos.keySet().stream().toList().getFirst();
                for(BlockPos pos:TunnelBuildPos.keySet()) {
                    if (TunnelBuildPos.get(pos)==0) {
                        if (player.getEntityPos().distanceTo(pos.toCenterPos()) < player.getEntityPos().distanceTo(MinBlock.toCenterPos())) {
                            MinBlock = pos;
                        }
                    }
                }
                if (player.getEntityPos().distanceTo(MinBlock.toCenterPos())<5.5) {
                    //tipGame("隧道","建造进度："+((((double) TunnelBuildPos.size()/(double) TunnelBuildPosSize)*100))+"%");

                    com.companion.effect.TunnelBulid.Bulid.placeAt(MinBlock);
                    TunnelBuildPos.put(MinBlock,1); //避免多放
                }
            }
        }
        if(TunnelBuildPos.isEmpty() && TunnelBuildPosSize>0){
            TunnelBuildPosSize=0;
            //TunnelPos.clear();
            points.clear();
            tipGame("隧道","建造完成");
        }
    }
}
