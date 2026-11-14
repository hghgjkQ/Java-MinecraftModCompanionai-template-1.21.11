package com.companion.Key;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class BinTool {

    // 写入二进制字符串到文件
    public static void bitWrite(String filePath, String bitStr) throws Exception {
        FileOutputStream fos = new FileOutputStream(filePath);
        byte buf = 0;
        int cnt = 0;
        for (char c : bitStr.toCharArray()) {
            int bit = c - '0';
            buf = (byte) ((buf << 1) | bit);
            cnt++;
            if (cnt == 8) {
                fos.write(buf);
                buf = 0;
                cnt = 0;
            }
        }
        // 剩余不足8位，后面补0凑整字节写入
        if (cnt > 0) {
            buf = (byte) (buf << (8 - cnt));
            fos.write(buf);
        }
        fos.close();
    }

    // 读取整个文件，完整展开为01字符串（包含补齐的填充0）
    public static String bitReadAll(String filePath) throws Exception {
        FileInputStream fis = new FileInputStream(filePath);
        StringBuilder sb = new StringBuilder();
        int b;
        while ((b = fis.read()) != -1) {
            byte val = (byte) b;
            // 一个字节拆成8个bit，高位在前
            for (int i = 7; i >= 0; i--) {
                sb.append(((val >> i) & 1));
            }
        }
        fis.close();
        return sb.toString();
    }


}