package com.companion.skills.ChestManage;


import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.companion.global.*;
import static com.companion.ChestPosStorage.*;
import static com.companion.ChestPosStorage.getCurrentChestPos;
import static com.companion.effect.tool.*;
import static com.companion.skills.ChestManage.FileSave.saveData;
import static com.companion.skills.ChestManage.cheak.*;

public class main {
    private static BlockPos TarPos;
    private static HashMap<Item, Integer> TarItem=new HashMap<>();
    public static void reg(){
        ClientTickEvents.END_CLIENT_TICK.register(main::update); // 每帧执行代码
    }

    private static void update(MinecraftClient client)   {
        if (client==null || client.world==null ||client.player==null)return;
        List<HashMap<RegistryKey<World>, BlockPos>> tempR=new ArrayList<>();
        if(Instant.now().getEpochSecond()%10==0){//每十秒检查一次
            var y=getAllChests();
            for(HashMap<RegistryKey<World>, BlockPos> x: CheckChestData.keySet()){
                RegistryKey<World> wd=client.player.getEntityWorld().getRegistryKey();
                if (x.containsKey(wd)){ //如果维度正确
                    if(x.get(wd)==null || x.get(wd).equals(new BlockPos(0,0,0))){
                        continue;
                    }
                    if (player.getEntityPos().distanceTo(Vec3d.of(x.get(wd)))<64 && !y.contains(x.get(wd)) && client.world.getBlockState(x.get(wd)).getBlock() != Blocks.VOID_AIR){//如果不存在
                        tempR.add(x);

                    }
                }
            }
            for(HashMap<RegistryKey<World>, BlockPos> x:tempR){
                CheckChestData.remove(x);

            }
            saveFile();
        }
        if(isPlayerOpeningChest()) {
            if (getCurrentChestPos() == null) {
                if (TarItem!=getChestItems()) {
                    save(client, TarPos);
                }
            }else{
                TarPos = getCurrentChestPos();
                save(client, TarPos);
                setCurrentChestPos(null);
            }
        }
    }
    private static void save(MinecraftClient client,BlockPos pos){
        HashMap<RegistryKey<World>, BlockPos> temp =new HashMap<>();
        temp.put(client.player.getEntityWorld().getRegistryKey(),pos);
        TarItem=getChestItems();
        CheckChestData.put(temp,TarItem);
        saveFile();

    }
    private static void saveFile(){
        try {
            saveData(CheckChestData, getUniqueWorldOrServerId()+".txt");
        } catch (IOException e) {
            error(e);
        }
    }


}
