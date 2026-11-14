package com.companion.System;

import java.io.*;
import java.util.Properties;
import java.util.function.UnaryOperator;

/**
 * 最普通的配置读写器：基于 Properties，线程不安全，够用即可
 */
public class Config {

    private final Properties props = new Properties();
    private final File file;

    public Config(File file) {
        this.file = file;
        load();   // 构造时自动读一次
    }

    /* ---------------- 读 ---------------- */
    public String get(String key, String def) {
        return props.getProperty(key, def);
    }

    public int getInt(String key, int def) {
        return Integer.parseInt(props.getProperty(key, String.valueOf(def)));
    }

    /* ---------------- 写 ---------------- */
    public void set(String key, String value) {
        props.setProperty(key, value);
        save();
    }

    /**
     * 函数式修改：给你旧值，你返回新值，然后帮你写盘
     */
    public void update(String key, UnaryOperator<String> operator) {
        String oldVal = props.getProperty(key);
        String newVal = operator.apply(oldVal);
        set(key, newVal);
    }

    /* ---------------- 持久化 ---------------- */
    private void load() {
        if (file.exists()) {
            try (Reader r = new FileReader(file)) {
                props.load(r);
            } catch (IOException e) {
                throw new RuntimeException("加载配置失败", e);
            }
        }
    }

    private void save() {
        try (Writer w = new FileWriter(file)) {
            props.store(w, "Companion Config");
        } catch (IOException e) {
            throw new RuntimeException("保存配置失败", e);
        }
    }
}