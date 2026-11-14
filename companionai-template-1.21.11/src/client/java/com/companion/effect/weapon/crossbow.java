package com.companion.effect.weapon;


import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;



import static com.companion.global.*;
import static com.companion.effect.tool.tip;
import static com.companion.effect.weapon.base.sendPos;
import static com.companion.skills.Tkill.faceEntity;

public class crossbow {

    public static void power(ItemStack main){
        if(control && main.getItem() == Items.CROSSBOW ){
            player.stopUsingItem();
            player.setPitch((float) Math.random()*-80-10);
            player.setYaw((float) Math.random()*360-180);
        }
    }
    public static void setControl(){
        control= !control;
        tip("§econtrol："+control);
    }
}
