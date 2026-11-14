package com.companion.effect.weapon;

import com.companion.ParticleManage;
import com.companion.effect.show;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import static com.companion.Key.configTool.cfg;
import static com.companion.effect.tool.tip;
import static com.companion.effect.weapon.base.*;
import net.minecraft.util.math.Vec3d;
import static com.companion.effect.ability.*;
import static com.companion.effect.weapon.tool.*;
import java.util.List;
import java.util.Set;

import static com.companion.CompanionAIClient.*;
import static com.companion.draw.base.line;
import static com.companion.draw.high.MagicArray;
import static com.companion.skills.Tkill.*;
import static com.companion.global.*;

public class Spear {
    private static boolean killState = false;
    private static boolean spearState=false;
    private static Vec3d pos=new Vec3d(0,0,0);
    public static void spear(boolean leftPressed, ItemStack main){

        if (cfg.SpearState.get()) {
            if (TarEntity == null || !isSpearItemByEnumeration(main)) {
                MidText = "";
            } else {
                show.s();
                MidText = "[" + ModName + "] 长矛已瞄准[" + TarEntity.getStringifiedName() + "]！";
            }

            int SPEAR_count = player.getItemUseTime();
            if (SPEAR_count <= 0) {
                pos = player.getEntityPos();
            }
            Vec3d lookVec;


            if (TarEntity != null && isSpearItemByEnumeration(main)) {
                /*
                if (leftPressed) {
                    MinecraftClient.getInstance().options.getFov().setValue(Math.max(30, fov - SPEAR_count * SPEAR_count));
                } else {
                    MinecraftClient.getInstance().options.getFov().setValue(fov);
                }

                */
                pos = player.getEntityPos();
                double x = pos.getX();
                double y = pos.getY();
                double z = pos.getZ();
                if (TarEntity.isAlive() && SPEAR_count > 0) {
                    var c = faceEntity(player, TarEntity, leftPressed);
                    midEffect(SPEAR_count);


                    lookVec = player.getRotationVec(1.0F).multiply(cfg.SpearPower);
                    for (int i = 0; i < 4; ++i)
                        sendPos(x, y, z, false, c.get(0), c.get(1));
                    sendPos(x - lookVec.x, y - lookVec.y, z - lookVec.z, false, c.get(0), c.get(1));
                    sendPos(x, y, z, false, c.get(0), c.get(1));

                    /*
                    if(killState){

                        if(miniLeap(TarEntity.getX(),TarEntity.getEyeY()+1,TarEntity.getZ(),10)) {
                            killState = false;
                        }


                    }else{
                        if(miniLeap(TarEntity.getX(),TarEntity.getEyeY()+10,TarEntity.getZ(),10)) {
                            killState = true;
                        }
                    }

                     */




                } else if (TarEntity.getHealth() <= 0) {
                    MinecraftClient.getInstance().options.getFov().setValue(fov);
                    killEffect();
                    releaseBow();
                }
            }
            if ((leftPressed && isSpearItemByEnumeration(main)) || (TarEntity == null && SPEAR_count <= 0 && isSpearItemByEnumeration(main))) {
                run();
            }
        }else{
            MidText = "";
            if (isSpearItemByEnumeration(main)) {
                pos = player.getEntityPos();

                double x = pos.getX();
                double y = pos.getY();
                double z = pos.getZ();
                Vec3d lookVec = player.getRotationVec(1.0F).multiply(1);
                com.companion.draw.base.line(pos,new Vec3d(x + lookVec.x, y + lookVec.y, z + lookVec.z),1, ParticleManage.YELLOW_POINT_PARTICLE);
                if (leftPressed && spearState) {


                    lookVec = player.getRotationVec(1.0F).multiply(1);
                    for (int i = 0; i < 4; ++i)
                        sendPos(x, y, z, false);
                    sendPos(x - lookVec.x, y - lookVec.y, z - lookVec.z, false);
                    sendPos(x, y, z, false);

                    lookVec = player.getRotationVec(1.0F).multiply(5);
                    for (int i = 0; i < 4; ++i)
                        sendPos(x, y, z, false);
                    sendPos(x - lookVec.x, y - lookVec.y, z - lookVec.z, false);
                    sendPos(x, y, z, false);

                    spearState=false;
                    }else{
                    if(!spearState) {
                        spearState = true;
                    }
                }
            }
        }

    }

    public static void midEffect(int SPEAR_count){
        // F3显示角度的归一化逻辑：无界原生值 → 0~360°正范围
        List<Float> c= faceEntity(player.getEntityPos(),TarEntity.getEntityPos());
        float rawYaw = c.get(0);
        float showYaw = (rawYaw + 180.0F) % 360.0F; // 先补偿基准，再取模
        if (showYaw < 0.0F) {
            showYaw += 360.0F; // 修正负数，保证0~360°
        }
        float xx=180 - (c.get(1)+ 90);
        float yy= 360 - showYaw;

        Vec3d lookVec = vec3dVectorOperation(player.getEntityPos(),TarEntity.getEntityPos()).multiply(Math.min(player.getEyePos().distanceTo(TarEntity.getEyePos()), SPEAR_count));

        MagicArray(player.getEyePos().add(lookVec), (double) 5 / 20 * Math.min(20, SPEAR_count), SPEAR_count * SPEAR_count, ParticleTypes.ELECTRIC_SPARK, 3, xx,yy);

    }

    public static void killEffect(){
        int y = 0;
        for (float i : splitFloatNumberToList(TarEntity.getMaxHealth() / 20, 20)) {
            MagicArray(TarEntity.getEntityPos().add(0, y, 0), i, 0, ParticleTypes.FLAME, 3, 0, 0);
            for (int ii = 0; ii < 10; ++ii) {
                line(TarEntity.getEyePos(), TarEntity.getEyePos().add(Math.random() * 10 - 5, 100, Math.random() * 10 - 5), 3, ParticleTypes.END_ROD);
            }
            if (all_client.world != null) {
                all_client.world.playSoundClient(
                        SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                        SoundCategory.WEATHER,
                        10.0F, player.getPitch()
                );
            }
            y += 1;
        }
        TarEntity = null;
    }
    /**
     * 枚举方式判断物品栈是否属于矛类（所有材质）
     * @param stack 待判断的物品栈
     * @return true=属于矛类，false=不属于矛类
     */
    public static boolean isSpearItemByEnumeration(ItemStack stack) {
        // 1. 防御性校验：空指针/空物品栈直接返回false
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        // 2. 所有矛类物品集合（集中管理，易扩展）
        Set<Item> SPEAR_ITEMS = Set.of(
                Items.DIAMOND_SPEAR,
                Items.STONE_SPEAR,
                Items.GOLDEN_SPEAR,
                Items.COPPER_SPEAR,
                Items.IRON_SPEAR,
                Items.WOODEN_SPEAR,
                Items.NETHERITE_SPEAR
        );

        // 3. 单次判断替代多个if
        return SPEAR_ITEMS.contains(stack.getItem());
    }


}
