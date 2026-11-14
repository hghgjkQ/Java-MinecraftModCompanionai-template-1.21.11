package com.companion.python;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.particle.ParticleTypes;

import java.util.Objects;


import static com.companion.CompanionAIClient.*;
import static com.companion.effect.show.line;
import static com.companion.effect.weapon.Spear.*;
import static com.companion.global.*;

public class show   {



    public static void renderSquare(DrawContext context, RenderTickCounter c) {
        if (!Objects.equals(MidText, "")) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            // 获取屏幕尺寸
            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();

            // 正方形大小和位置
            int x = (screenWidth) / 2;
            int y = (screenHeight) / 2;


            context.drawText(client.textRenderer, MidText, x, y, 0x80FF0000, true);
        }

    }



    public static void path(){
        if (Path!=null) {
            for (int i = 0; i < Path.size() - 1; i++) {
                line(Path.get(i), Path.get(i + 1), 5, ParticleTypes.FLAME);
            }
        }
    }

}