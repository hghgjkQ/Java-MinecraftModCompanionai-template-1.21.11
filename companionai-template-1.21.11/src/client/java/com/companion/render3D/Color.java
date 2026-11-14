package com.companion.render3D;

import java.util.HashMap;
import java.util.Map;

// 适配DrawContext的100种颜色字典（ARGB格式，0xFF=完全不透明）
public class Color {
    private static final Map<String, Integer> COLOR = new HashMap<>();
    static {
        // ==================== 基础色系（15种） ====================
        COLOR.put("纯黑", 0xFF000000);
        COLOR.put("纯白", 0xFFFFFFFF);
        COLOR.put("纯红", 0xFFFF0000);
        COLOR.put("纯绿", 0xFF00FF00);
        COLOR.put("纯蓝", 0xFF0000FF);
        COLOR.put("纯黄", 0xFFFFFF00);
        COLOR.put("纯青", 0xFF00FFFF);
        COLOR.put("纯紫", 0xFF800080);
        COLOR.put("纯灰", 0xFF808080);
        COLOR.put("深红", 0xFF800000);
        COLOR.put("深绿", 0xFF008000);
        COLOR.put("深蓝", 0xFF000080);
        COLOR.put("深黄", 0xFF808000);
        COLOR.put("深青", 0xFF008080);
        COLOR.put("深紫", 0xFF400080);


        // ==================== 红色渐变（10种） ====================
        COLOR.put("鲜红", 0xFFFF2020);
        COLOR.put("玫瑰红", 0xFFFF007F);
        COLOR.put("橘红", 0xFFFF4500);
        COLOR.put("砖红", 0xFFB22222);
        COLOR.put("酒红", 0xFF8B0000);
        COLOR.put("粉红", 0xFFFFC0CB);
        COLOR.put("珊瑚红", 0xFFFF7F50);
        COLOR.put("番茄红", 0xFFFF6347);
        COLOR.put("鲑鱼红", 0xFFFA8072);
        COLOR.put("猩红", 0xFFDC143C);

        // ==================== 橙色渐变（8种） ====================
        COLOR.put("纯橙", 0xFFFFA500);
        COLOR.put("浅橙", 0xFFFFC87C);
        COLOR.put("深橙", 0xFFFF8C00);
        COLOR.put("杏色", 0xFFFFF8DC);
        COLOR.put("桃色", 0xFFFFE5B4);
        COLOR.put("琥珀色", 0xFFFFC300);
        COLOR.put("焦糖色", 0xFFCD853F);
        COLOR.put("土橙", 0xFFCC7722);

        // ==================== 黄色渐变（8种） ====================
        COLOR.put("亮黄", 0xFFFFFFE0);
        COLOR.put("柠檬黄", 0xFFF0FFF0);
        COLOR.put("金黄", 0xFFFFD700);
        COLOR.put("米黄", 0xFFF5F5DC);
        COLOR.put("奶油黄", 0xFFFFFACD);
        COLOR.put("芥末黄", 0xFFCACA00);
        COLOR.put("浅黄", 0xFFF0E68C);
        COLOR.put("土黄", 0xFFDEB887);

        // ==================== 绿色渐变（10种） ====================
        COLOR.put("草绿", 0xFF7CFC00);
        COLOR.put("薄荷绿", 0xFF98FB98);
        COLOR.put("橄榄绿", 0xFF808000);
        COLOR.put("翡翠绿", 0xFF00FF7F);
        COLOR.put("森林绿", 0xFF228B22);
        COLOR.put("海草绿", 0xFF3CB371);
        COLOR.put("豆绿", 0xFF90EE90);
        COLOR.put("墨绿", 0xFF006400);
        COLOR.put("浅绿", 0xFF90EE90);
        COLOR.put("青柠绿", 0xFF32CD32);

        // ==================== 蓝色渐变（10种） ====================
        COLOR.put("天蓝", 0xFF87CEEB);
        COLOR.put("海蓝", 0xFF00FFFF);
        COLOR.put("藏青", 0xFF00008B);
        COLOR.put("宝蓝", 0xFF0000CD);
        COLOR.put("钢蓝", 0xFF4682B4);
        COLOR.put("海军蓝", 0xFF191970);
        COLOR.put("浅蓝", 0xFFADD8E6);
        COLOR.put("钴蓝", 0xFF0047AB);
        COLOR.put("靛蓝", 0xFF4B0082);
        COLOR.put("普蓝", 0xFF000080);

        // ==================== 紫色渐变（10种） ====================
        COLOR.put("浅紫", 0xFF0000A0); // 你斜线常用色
        COLOR.put("紫罗兰", 0xFFEE82EE);
        COLOR.put("薰衣草紫", 0xFFE6E6FA);
        COLOR.put("洋紫", 0xFF8000FF);
        COLOR.put("品红", 0xFFFF00FF);
        COLOR.put("藕荷紫", 0xFFD8BFD8);
        COLOR.put("茄紫", 0xFF800080);
        COLOR.put("淡紫", 0xFFF8F8FF);
        COLOR.put("暗紫", 0xFF483D8B);
        COLOR.put("紫玫", 0xFF9370DB);

        // ==================== 灰色渐变（8种） ====================
        COLOR.put("深灰", 0xFF333333);
        COLOR.put("炭灰", 0xFF2F4F4F);
        COLOR.put("银灰", 0xFFC0C0C0);
        COLOR.put("烟灰", 0xFFD3D3D3);
        COLOR.put("浅灰", 0xFFF5F5F5);
        COLOR.put("石板灰", 0xFF708090);
        COLOR.put("铅灰", 0xFF5F9EA0);
        COLOR.put("鼠灰", 0xFF808080);

        // ==================== 游戏专属色（10种） ====================
        COLOR.put("MC草方块绿", 0xFF71B33C);
        COLOR.put("MC石头灰", 0xFF808080);
        COLOR.put("MC钻石蓝", 0xFF00FFFF);
        COLOR.put("MC金块黄", 0xFFFFD700);
        COLOR.put("MC红石红", 0xFFFF0000);
        COLOR.put("MC铁块银", 0xFFC0C0C0);
        COLOR.put("MC黑曜石黑", 0xFF141414);
        COLOR.put("MC羊毛白", 0xFFFFFFFF);
        COLOR.put("MC树叶绿", 0xFF228B22);
        COLOR.put("MC水蓝", 0xFF3498DB);

        // ==================== 半透明色系（10种） ====================
        COLOR.put("半透红", 0x80FF0000);
        COLOR.put("半透绿", 0x8000FF00);
        COLOR.put("半透蓝", 0x800000FF);
        COLOR.put("半透紫", 0x800000A0);
        COLOR.put("半透黄", 0x80FFFF00);
        COLOR.put("半透黑", 0x80000000);
        COLOR.put("半透白", 0x80FFFFFF);
        COLOR.put("半透灰", 0x80808080);
        COLOR.put("半透橙", 0x80FFA500);
        COLOR.put("半透粉", 0x80FFC0CB);
    }

    // 快速获取颜色的工具方法
    public static int getColor(String colorName) {
        return COLOR.getOrDefault(colorName, 0xFF0000A0); // 默认返回浅紫
    }
}