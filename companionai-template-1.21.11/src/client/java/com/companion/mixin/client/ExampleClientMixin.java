package com.companion.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.companion.global.*;


// 仅客户端生效
@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public class ExampleClientMixin {
	// 核心：Mixin阴影注解，直接映射MinecraftClient的itemUseCooldown私有字段
	// Fabric会自动处理混淆，无需关心运行时字段名
	@Shadow
	private int itemUseCooldown;

	@Unique
	private boolean count=false;

	// 注入到MinecraftClient的tick()方法头部，每帧执行一次
	@Inject(at = @At("HEAD"), method = "tick()V")
	private void onClientTick(CallbackInfo ci) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.options == null) return;
		if (control) {
			count=true;
			this.itemUseCooldown = 0;
		} else {
			if (count) {
				this.itemUseCooldown = 4;
				count=false;
			}
		}

	}



}