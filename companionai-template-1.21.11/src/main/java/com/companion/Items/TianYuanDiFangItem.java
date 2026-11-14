package com.companion.Items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;

public class TianYuanDiFangItem extends Item {
    // 构造方法完全原版，无自定义逻辑
    public TianYuanDiFangItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }

        // 【遍历方块法】获取最高Y坐标
        int targetX = (int) user.getX();
        int targetZ = (int) user.getZ();
        int topY = getTopBlockY(world, targetX, targetZ);
        double spawnY = topY + 1.0D;
        double offsetX = 0.0D;

        for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
            try {
                Entity entity = entityType.create(world, SpawnReason.SPAWN_ITEM_USE);
                if (!(entity instanceof LivingEntity)) continue;

                entity.setPosition(user.getX() + offsetX, spawnY, user.getZ());

                if (entity instanceof MobEntity mob) {
                    mob.setAiDisabled(true);
                    mob.setNoGravity(true);
                    mob.setPersistent();
                }
                entity.setVelocity(Vec3d.ZERO);

                serverWorld.spawnEntity(entity);

                Box bb = entity.getBoundingBox();
                offsetX += 3.0D + bb.getLengthX();

            } catch (Exception ignored) {}
        }

        user.getStackInHand(hand).decrement(1);
        user.sendMessage(Text.literal("§6[天圆地方] §a生成完成！"), true);
        return ActionResult.SUCCESS;
    }

    // 【你要的：遍历方块获取最高Y】纯客户端+服务端兼容
    private int getTopBlockY(World world, int x, int z) {
        int bottomY = world.getBottomY();

        for (int y = 320 - 1; y >= bottomY; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            if (!world.getBlockState(pos).isAir()) {
                return y;
            }
        }
        return bottomY;
    }
}