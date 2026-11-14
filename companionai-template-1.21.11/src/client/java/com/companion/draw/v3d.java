package com.companion.draw;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import net.minecraft.client.render.RenderLayer;

import java.util.Map;
import java.util.Optional;
import com.mojang.blaze3d.pipeline.RenderPipeline;



@Environment(EnvType.CLIENT)
public class v3d     {

    private static final int RED_COLOR = 0xFFFF0000; // ARGB 纯红色

    public static void init() {
        WorldRenderEvents.END_MAIN.register(context -> {
            renderRedCube(context.matrices(), context.consumers(), MinecraftClient.getInstance().getEntityRenderDispatcher().camera, context);
        });
    }

    private static void renderRedCube(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Camera camera ,WorldRenderContext context) {
        if (matrices == null || vertexConsumers == null) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        double x = 0;
        double y = 0;
        double z = 0;

        // 相机偏移
        Vec3d cameraPos = camera.getCameraPos();
        x =-cameraPos.x;
        y = -cameraPos.y;
        z = -cameraPos.z;

        matrices.push();
        matrices.translate(x, y, z);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);


        Matrix4f matrix = matrices.peek().getPositionMatrix();

        // 绘制立方体六个面
        float size = 1.5f;

        // 前面 (Z+)
        drawQuad(buffer, matrix,
                -size, -size, size,
                size, -size, size,
                size,  size, size,
                -size,  size, size,
                RED_COLOR
        );

        matrices.pop();
    }

    private static void drawQuad(VertexConsumer buffer, Matrix4f matrix,
                                 float x1, float y1, float z1,
                                 float x2, float y2, float z2,
                                 float x3, float y3, float z3,
                                 float x4, float y4, float z4,
                                 int color) {

        // 顶点1
        buffer.vertex(matrix, x1, y1, z1)
                .color(color)
                .texture(0, 0);


        // 顶点2
        buffer.vertex(matrix, x2, y2, z2)
                .color(color)
                .texture(0, 0);


        // 顶点3
        buffer.vertex(matrix, x3, y3, z3)
                .color(color)
                .texture(0, 0);


        // 顶点4
        buffer.vertex(matrix, x4, y4, z4)
                .color(color)
                .texture(0, 0);
    }
}