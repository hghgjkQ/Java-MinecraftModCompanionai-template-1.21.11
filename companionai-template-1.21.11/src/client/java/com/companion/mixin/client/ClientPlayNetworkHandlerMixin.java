package com.companion.mixin.client;


import com.companion.ChestPosStorage;
import com.companion.render3D.screen;
import com.companion.skills.ChestControl;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.companion.ChestPosStorage.*;
import static com.companion.effect.tool.tip;
import static com.companion.effect.tool.tipGame;
import static com.companion.render3D.screen.blocks;
import static com.companion.skills.ChestManage.cheak.*;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayNetworkHandlerMixin {
    private int a=0;
    @Inject(method = "interactBlock", at = @At("HEAD"))
    private void onInteractBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        BlockPos pos = hitResult.getBlockPos();
        // 检查点击的是不是箱子
        if (player.getEntityWorld().getBlockState(pos).getBlock() instanceof net.minecraft.block.ChestBlock) {
            setCurrentChestPos(getMinChestPos(pos, (ClientWorld) player.getEntityWorld()));
            if(a==0) {
                //ChestControl.putItemIntoChest(Items.STONE, -1);
                a=1;
                tipGame("ok");
            }
        }
    }

    // 方法3：移除并重新添加（如果坐标不存在则添加，存在则更新颜色）
    @Unique
    private static void updateBlock(BlockPos pos, String colorName) {
        blocks.removeIf(bwc -> bwc.pos.equals(pos));
        blocks.add(new screen.BlockWithColor(pos, colorName));
    }

}