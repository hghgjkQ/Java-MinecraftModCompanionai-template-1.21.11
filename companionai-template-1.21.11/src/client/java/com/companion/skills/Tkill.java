package com.companion.skills;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

import static com.companion.global.*;


public class Tkill {

    public static void run() {
        LivingEntity e=getEyeEntity(1000);
        if (TarEntity!=e && e!=null && e.isAlive() && e.getHealth()>0) {
            TarEntity = e;
        }
    }
    public static LivingEntity getEyeEntity(double maxDistance) {
        // 判空逻辑保持不变
        if (player == null || player.getEntityWorld() == null) return null;

        Vec3d eye  = player.getEyePos();
        Vec3d look = player.getRotationVec(1.0F);
        Vec3d end  = eye.add(look.multiply(maxDistance));

        // 获取视线穿过的所有实体
        Box box = player.getBoundingBox().stretch(look.multiply(maxDistance)).expand(1.0);
        var entities = player.getEntityWorld().getOtherEntities(player, box);

        // 关键修改1：将closest声明为LivingEntity类型，而非通用的Entity
        LivingEntity closest = null;
        double minDist = maxDistance;

        for (Entity e : entities) {
            // 关键修改2：增加类型判断，只处理LivingEntity类型的实体
            if (!(e instanceof LivingEntity)) {
                continue; // 非生物实体直接跳过
            }

            var hitResult = e.getBoundingBox().raycast(eye, end);
            if (hitResult.isPresent()) {
                double dist = eye.distanceTo(hitResult.get());
                if (dist < minDist) {
                    minDist = dist;
                    // 关键修改3：直接赋值（已通过instanceof校验，类型安全）
                    closest = (LivingEntity) e;
                }
            }
        }
        // 返回值类型匹配，无需额外处理
        return closest;
    }

    public static List<Float> faceEntity(ClientPlayerEntity player, LivingEntity target,boolean ac) {
        Vec3d playerEyePos = player.getEyePos();
        Vec3d targetCenterPos = target.getBoundingBox().getCenter();
        double dx = targetCenterPos.x - playerEyePos.x;
        double dy = targetCenterPos.y - playerEyePos.y;
        double dz = targetCenterPos.z - playerEyePos.z;
        double horizontalDistance = MathHelper.sqrt((float) (dx * dx + dz * dz));
        float yaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0F;
        float pitch = (float) (-Math.atan2(dy, horizontalDistance) * 180.0 / Math.PI);
        if (ac) {
            player.setYaw(MathHelper.wrapDegrees(yaw));
            player.setPitch(MathHelper.clamp(pitch, -90.0F, 90.0F));
        }
        List<Float> c=new ArrayList<>();
        c.add(yaw);
        c.add(pitch);
        return c;
    }
    public static List<Float> faceEntity(Vec3d playerEyePos, Vec3d targetCenterPos) {
        double dx = targetCenterPos.x - playerEyePos.x;
        double dy = targetCenterPos.y - playerEyePos.y;
        double dz = targetCenterPos.z - playerEyePos.z;
        double horizontalDistance = MathHelper.sqrt((float) (dx * dx + dz * dz));
        float yaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0F;
        float pitch = (float) (-Math.atan2(dy, horizontalDistance) * 180.0 / Math.PI);
        List<Float> c=new ArrayList<>();
        c.add(yaw);
        c.add(pitch);
        return c;

    }
}