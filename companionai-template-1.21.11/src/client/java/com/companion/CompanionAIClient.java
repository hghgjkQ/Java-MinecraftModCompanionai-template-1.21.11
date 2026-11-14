package com.companion;

import com.companion.Key.config;
import com.companion.System.PlayerPresenceDetector;
import com.companion.System.ScrollTest;
import com.companion.System.input;
import com.companion.draw.particle.SparkleParticle;
import com.companion.effect.TaskScheduler;
import com.companion.effect.ability;
import com.companion.effect.noticePlayer;
import com.companion.effect.say;
import com.companion.effect.weapon.ModFilterConfig;
import com.companion.python.Python;
import com.companion.python.main;
import com.companion.python.manage;
import com.companion.python.show;
import com.companion.render3D.screen;
import com.companion.skills.cutTree;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.simple.internal.SimpleProvider;


import static com.companion.Key.configTool.cfg;
import static com.companion.System.input.readTimestamp;
import static com.companion.effect.tool.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;


import static com.companion.skills.ChestManage.FileSave.loadData;

import static com.companion.skills.ChestManage.cheak.getUniqueWorldOrServerId;

import static com.companion.global.*;

public class CompanionAIClient implements ClientModInitializer {
	private boolean isPythonStarted = false;
	private static String InputPythonText=null;
	public Python python;


	public static LocalDateTime NRNBTime=readTimestamp();

	@Override
	public void onInitializeClient() {

		cfg = config.load();//文件读取
		com.companion.draw.factory.manage.particleReg();

		// 3. 监听「退出游戏（离开世界/关闭客户端）」事件：关闭Python连接
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			if (python != null && isPythonStarted) {
				python.shutdown(); // 关闭连接+线程池
				isPythonStarted = false; // 重置标记
				manage.stop();
				System.out.println("[PythonChat] 游戏退出，已关闭Python通信连接");
			}
		});

		Runtime.getRuntime().addShutdownHook(new Thread(TaskScheduler::shutdown));

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {

			if (client.player != null && !isPythonStarted) {

				// 替换为你的Python路径和脚本路径（绝对路径！）
				manage.startOnce();
				TaskScheduler.schedule(this::managePython, 1);


				isPythonStarted = true; // 标记为已启动，避免重复触发
			}

			CompletableFuture.runAsync(() -> {
				handler.getPlayerList().forEach(e -> online.add(e.getProfile().name()));
				fov= client.options.getFov().getValue();
                try {
                    CheckChestData =loadData(getUniqueWorldOrServerId()+".txt");
                } catch (IOException e) {
                    error(e);
                }
                //v3d.init();
				if (client.getServer() == null) { // 是远程服务器
					client.execute(() -> {
						if (client.player != null) {




							TaskScheduler.schedule(() -> {
								SimpleProvider.Config.INSTANCE.toString(); // 配置文件读取
								client.player.sendMessage(Text.literal("§a["+ModName+"] 已加载"), false);
								YOUR_DICT=com.companion.System.temp.mainly();


								if (gamer.equals(client.player.getName().getString())){ //如果我是随从
									//client.player.networkHandler.sendChatCommand("l lolo_1314"); //自动登入
									PlayerPresenceDetector.registerPlayerPresenceDetector();
								}else {
									if(cfg.enableAutoLogin.get()) {
										client.player.networkHandler.sendChatCommand(cfg.loginCommand);
									}//自动登入
								}
							}, 1);


						}
					});
				}


			}).thenRunAsync(() -> {
				// 回到主线程进行后续操作
			});


		});
		ClientTickEvents.END_CLIENT_TICK.register(ability::onTick); // 每帧执行代码
		HudRenderCallback.EVENT.register(show::renderSquare);
		HudRenderCallback.EVENT.register(screen::renderSquare);//大部分渲染
		com.companion.Key.main.speed();
		say.run();
		com.companion.Key.main.start();
		input.register();
		noticePlayer.run();
		main.reg();
		cutTree.reg();
		com.companion.skills.ChestManage.main.reg();
		ModFilterConfig.load();

		//com.companion.skills.MapDraw.Renderer3D.register();

		com.companion.Command.main.reg(); // 注册命令
		com.companion.skills.MapDraw.main.reg();



		//HudRenderCallback.EVENT.register(show::renderSquare);



	}

	// 通信启停核心方法（复用之前的逻辑）
	public void managePython() {
		// 启动新连接（Python类内已做“避免重复连接”的判断）
		python.startChatConnection();
		System.out.println("[PythonChat] 进入游戏，已启动Python通信连接");

		// 测试：5秒后发送第一条消息
		new Thread(() -> {
			try {
				Thread.sleep(5000);
				python.sendMsgToPython("你好Python！我进入MC游戏啦～");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}


}
/*
 * §0 黑色
 * §1 深蓝色
 * §2 深绿色
 * §3 深青色
 * §4 深红色
 * §5 紫色
 * §6 金色
 * §7 浅灰色
 * §8 深灰色
 * §9 蓝色
 * §a 浅绿色
 * §b 浅青色
 * §c 浅红色
 * §d 粉色
 * §e 黄色
 * §f 白色
 */