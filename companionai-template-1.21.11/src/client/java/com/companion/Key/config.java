package com.companion.Key;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class config {
    public AtomicBoolean enableAutoLogin=new AtomicBoolean(true);
    public AtomicBoolean RaidState=new AtomicBoolean(false);;
    public AtomicBoolean BowState=new AtomicBoolean(true);;
    public AtomicBoolean SpearState=new AtomicBoolean(true);;
    public AtomicBoolean TpState=new AtomicBoolean(true);;
    public AtomicBoolean enableNoFall=new AtomicBoolean(false);
    public AtomicBoolean enableSpeedShow=new AtomicBoolean(false);
    public AtomicBoolean enableAutoRaid=new AtomicBoolean(false);
    public AtomicBoolean enableAutoAfk=new AtomicBoolean(false);
    public AtomicBoolean enableMace=new AtomicBoolean(false);

    public AtomicBoolean EasyElytraState=new AtomicBoolean(false);
    public double SpearPower = 1;
    public double MacePower = 5;
    public double ElytraSpeed = 1;
    public double RaidR = 10;


    public  String loginCommand = "";

    private static final File FILE = new File("config/companion.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static config load() {
        if (!FILE.exists()) save(new config());
        try (Reader r = new FileReader(FILE)) {
            return GSON.fromJson(r, config.class);
        } catch (IOException e) {
            return new config();
        }
    }
    private static final Object lock = new Object();

    public static void setV(config cfg, AtomicBoolean x, boolean c) {
        synchronized (lock) {  // 加锁，保证原子性
            x.set(c);
            save(cfg);
        }
    }

    public static void save(config cfg) {
        FILE.getParentFile().mkdirs();
        try (Writer w = new FileWriter(FILE)) {
            GSON.toJson(cfg, w);
        } catch (IOException ignored) {}
    }
}