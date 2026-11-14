package com.companion.effect.TunnelBulid;

import com.companion.effect.book.ReadWrite;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.companion.effect.tool.tipGame;
import static com.companion.global.*;

public class Use {



    public static void read(ItemStack main){
        if(main.getItem()== Items.WRITABLE_BOOK){
            try {
                TunnelPos = parse(ReadWrite.readBook());
                tipGame("读取成功！");
            }catch (Exception e){
                tipGame("读取失败："+e);
            }
        }
    }
    public static void write(ItemStack main){
        if(main.getItem()== Items.WRITABLE_BOOK){
            ReadWrite.write(String.valueOf(TunnelPos));
            tipGame("蓝图写入完成");
        }
    }

    private static final List<BlockPos> xyzv=new ArrayList<>();
    static {
        xyzv.add(new BlockPos(1,0,0));
        xyzv.add(new BlockPos(-1,0,0));
        xyzv.add(new BlockPos(0,0,1));
        xyzv.add(new BlockPos(0,0,-1));

        xyzv.add(new BlockPos(0,1,0));
        xyzv.add(new BlockPos(0,-1,0));
    }

    /**
     * 生成待建造位置
     */
    public static void create(){
        if(TunnelPos.isEmpty()){
            tipGame("隧道","无法加载，原因：无蓝图");
            return;
        }
        TunnelBuildPos.clear();
        var temp1=generateLinePositions(TunnelPos);
        List<BlockPos> temp=new ArrayList<>();
        for(BlockPos pos:temp1){
            temp.add(pos);
            temp.add(pos.add(0,1,0));
        }
        for(BlockPos pos:temp){
            for (BlockPos v: xyzv){
                var c=pos.add(v);
                if ((!TunnelBuildPos.containsKey(c)) && (!temp.contains(c))){
                    TunnelBuildPos.put(c,0);
                }
            }
        }
        TunnelBuildPosSize=TunnelBuildPos.size();
        tipGame("隧道","加载完成");
    }
    // 正则：匹配所有 [x,y,z] 格式的坐标（支持负数、多坐标）

    // 仅匹配你的 class_2338 坐标格式
    private static final Pattern REGEX = Pattern.compile("class_2338\\{x=(-?\\d+),\\s*y=(-?\\d+),\\s*z=(-?\\d+)\\}");

    /**
     * 输入：List<String> 破碎字符串
     * 输出：仅解析后的原始 List<BlockPos>（无任何额外操作）
     */
    public static List<BlockPos> parse(List<String> fragments) {
        // 1. 仅拼接所有碎片
        String fullStr = String.join("", fragments);

        // 2. 仅解析坐标，返回原始List
        List<BlockPos> result = new ArrayList<>();
        REGEX.matcher(fullStr).results().forEach(match -> {
            int x = Integer.parseInt(match.group(1));
            int y = Integer.parseInt(match.group(2));
            int z = Integer.parseInt(match.group(3));
            result.add(new BlockPos(x, y, z));
        });
        return result;
    }
    /**
     * 传入一串按顺序排列的 BlockPos（都是横平竖直直线）
     * 返回包含所有中间点的完整 Set<BlockPos>
     */
    public static Set<BlockPos> generateLinePositions(List<BlockPos> posList) {
        Set<BlockPos> result = new HashSet<>();
        if (posList == null || posList.size() <= 1) {
            posList.forEach(result::add);
            return result;
        }

        // 遍历每两个连续的点
        for (int i = 0; i < posList.size() - 1; i++) {
            BlockPos start = posList.get(i);
            BlockPos end = posList.get(i + 1);
            // 填充两点之间的所有坐标
            fillLineBetween(start, end, result);
        }

        return result;
    }

    /**
     * 核心：填充 横平竖直 两点之间的所有 BlockPos（你保证两点只有一维变化，无需判断）
     */
    private static void fillLineBetween(BlockPos start, BlockPos end, Set<BlockPos> set) {
        int x1 = start.getX(), y1 = start.getY(), z1 = start.getZ();
        int x2 = end.getX(),   y2 = end.getY(),   z2 = end.getZ();

        // X 轴方向直线
        if (x1 != x2) {
            int min = Math.min(x1, x2);
            int max = Math.max(x1, x2);
            for (int x = min; x <= max; x++) {
                set.add(new BlockPos(x, y1, z1));
            }
        }
        // Y 轴方向直线
        else if (y1 != y2) {
            int min = Math.min(y1, y2);
            int max = Math.max(y1, y2);
            for (int y = min; y <= max; y++) {
                set.add(new BlockPos(x1, y, z1));
            }
        }
        // Z 轴方向直线
        else if (z1 != z2) {
            int min = Math.min(z1, z2);
            int max = Math.max(z1, z2);
            for (int z = min; z <= max; z++) {
                set.add(new BlockPos(x1, y1, z));
            }
        }
        // 同一个点
        else {
            set.add(start);
        }
    }
}
