package com.companion.effect.weapon;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import static com.companion.effect.weapon.base.sendPos;
import static com.companion.global.player;

public class hook {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    /**
     * 获取玩家视线正前方指定距离坐标
     * @param distance 距离，需求填30
     * @return 空间三维坐标Vec3d；无玩家返回null
     */
    public static Vec3d getLookForwardPos(double distance) {
        PlayerEntity player = mc.player;
        if (player == null) return null;

        // 获取玩家朝向向量（原版原生API，不用手写三角函数，更稳定）
        Vec3d lookDir = player.getRotationVector();
        // 起点：玩家眼睛坐标（视线起点是眼睛，不是脚底）
        Vec3d eyePos = player.getEyePos();
        // 眼睛坐标 + 方向*距离
        return eyePos.add(lookDir.multiply(distance));
    }

    // 封装直接拿前方30格
    public static Vec3d getForward30Block() {
        return getLookForwardPos(30);
    }

    // 拆分四舍五入为方块整数坐标
    public static int[] getForward30BlockInt() {
        Vec3d pos = getForward30Block();
        if(pos == null) return new int[]{0,0,0};
        return new int[]{
                (int) Math.round(pos.x),
                (int) Math.round(pos.y),
                (int) Math.round(pos.z)
        };
    }
    /**
     * 发包收回鱼竿
     * @return true 成功发包；false 没拿钓竿/无玩家
     */
    public static boolean retractFishingRod() {
        ClientPlayerEntity player = mc.player;
        if (player == null) return false;

        // 判断主手是否拿着钓鱼竿
        ItemStack mainHand = player.getMainHandStack();
        if (!(mainHand.getItem() instanceof FishingRodItem)) {
            return false;
        }

        if (player.fishHook != null) {
            Entity k =player.fishHook.getHookedEntity();
            for (int i = 0; i < 4; ++i) sendPos(k.getX(),k.getY()+30,k.getZ(),false);

            player.fallDistance=0;
            player.setOnGround(false);
            player.addVelocity(0,1,0);
            //player.setPos(player.getX(),player.getY()+30,player.getZ());
            // 发送玩家右键物品数据包，服务器判定收竿
            PlayerInteractItemC2SPacket packet = new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 30,player.getPitch(), player.getYaw());
            player.networkHandler.sendPacket(packet);
            return true;
        }

        return false;
    }


}
