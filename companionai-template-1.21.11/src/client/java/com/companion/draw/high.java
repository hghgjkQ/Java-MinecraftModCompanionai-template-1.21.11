package com.companion.draw;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;

import static com.companion.global.*;
import static com.companion.draw.base.*;


public class high {
    public static void MagicArray(Vec3d pos , double size, double angle, ParticleEffect c,int block,float x,float y){
        if (player==null) return;


        //tip("["+x+"][§6"+y+"]");
        drawCircle(c,pos,size,0.02,x,y);
        drawCircle(c,pos,size*1.5,0.02,x,y);
        drawCircle(c,pos,size*1.6,0.02,x,y);
        triangle(pos,size*1.5,angle,block,c,x,y);
        triangle(pos,size*1.5,angle+180,block,c,x,y);
        flower(pos,size*1.5,size*1.6,angle,block*5,20,c,x,y);
        double longth = Math.sqrt(3)*size*0.5;
        line(pos,longth,angle-30,block,c,x,y);
        line(pos,longth,angle-90,block,c,x,y);
        line(pos,longth,angle-150,block,c,x,y);

    }//24 31 57
}
