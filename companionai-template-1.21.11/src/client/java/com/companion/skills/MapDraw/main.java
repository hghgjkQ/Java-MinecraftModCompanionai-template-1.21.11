package com.companion.skills.MapDraw;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

import static com.companion.global.*;

public class main {
    public static void reg(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> showSide());

    }
    public static void showSide(){
        if (player!=null) {
            double[] pos = new double[2];
            pos[0] = player.getX()+64;
            pos[1] = player.getZ()+64;
            long[][] top = Workpiece.getMapVertices((long) pos[0], (long) pos[1]);
            draw.line(
                    new Vec3d((double) top[0][0], player.getY(), (double) top[0][1]),
                    new Vec3d((double) top[0][0], player.getY(), (double) top[1][1]),
                    1,
                    ParticleTypes.ELECTRIC_SPARK
            );
            draw.line(
                    new Vec3d((double) top[0][0], player.getY(), (double) top[0][1]),
                    new Vec3d((double) top[1][0], player.getY(), (double) top[0][1]),
                    1,
                    ParticleTypes.ELECTRIC_SPARK
            );
            draw.line(
                    new Vec3d((double) top[1][0], player.getY(), (double) top[0][1]),
                    new Vec3d((double) top[1][0], player.getY(), (double) top[1][1]),
                    1,
                    ParticleTypes.ELECTRIC_SPARK
            );
            draw.line(
                    new Vec3d((double) top[0][0], player.getY(), (double) top[1][1]),
                    new Vec3d((double) top[1][0], player.getY(), (double) top[1][1]),
                    1,
                    ParticleTypes.ELECTRIC_SPARK
            );
        }
    }
}
