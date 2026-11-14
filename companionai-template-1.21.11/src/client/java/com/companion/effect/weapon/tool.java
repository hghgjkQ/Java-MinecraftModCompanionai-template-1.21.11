package com.companion.effect.weapon;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import static com.companion.Key.configTool.cfg;
import static com.companion.effect.ability.*;
import static com.companion.effect.weapon.base.*;
import java.util.ArrayList;
import java.util.List;



import static com.companion.effect.tool.*;
import static com.companion.effect.tool.tp;
import static com.companion.global.*;
public class tool {
    public static void TraceHP(){
        if (traceDMGentity!=null){
            if (TraceEntityHeart!=traceDMGentity.getHealth()){
                tip("§4["+traceDMGentity.getStringifiedName()+"]受到伤害："+(traceDMGentity.getHealth() -TraceEntityHeart));
                TraceEntityHeart=traceDMGentity.getHealth();
            }
        }
    }
    public static void PathShow(){
        if (Path!=null) {
            com.companion.python.show.path();
            if (!Path.isEmpty()) {
                if (miniLeap(Path.get(0).getX(), Path.get(0).getY(), Path.get(0).getZ(), 10)) {
                    Path.remove(0);
                }
            }else {
                Path=null;
            }
        }
    }
    public static void Run(boolean emptyHand,boolean sprinting,boolean leftPressed){
        // 4. 首次按下（防止连发）
        if (emptyHand && sprinting && cfg.TpState.get()) {
            if (!wasPressed && leftPressed) {
                // 在这里写你要执行的逻辑

                teleport(5);
            }
        }
    }


    private static final float PRECISION = 1e-6f;

    /**
     * 将浮点型总数值按指定浮点型单值拆分为列表
     * @param num 待拆分的总数值（浮点型，需大于0）
     * @param part 拆分的单值（浮点型，需大于0）
     * @return 拆分后的Float类型列表
     * @throws IllegalArgumentException 入参为非正数时抛出
     */
    public static List<Float> splitFloatNumberToList(float num, float part) {
        // 边界校验：浮点型非正数判断（小于精度阈值即视为0或负数）
        if (num < PRECISION || part < PRECISION) {
            throw new IllegalArgumentException("待拆分数值和拆分单值必须为大于0的浮点型数值");
        }

        List<Float> resultList = new ArrayList<>();
        // 计算完整拆分份数（浮点转整型，自动取整，如5.6→5、6.99→6）
        int quotient = (int) (num / part);
        // 计算余数：替代%取余，浮点运算更精准，避免直接取余的精度误差
        float remainder = num - quotient * part;

        // 添加quotient个拆分单值part（浮点型）
        for (int i = 0; i < quotient; i++) {
            resultList.add(part);
        }

        // 余数大于精度阈值时才添加（排除微小运算误差，如0.0000001视为0）
        if (remainder > PRECISION) {
            resultList.add(remainder);
        }

        return resultList;
    }
    /**
     * 两个Vec3d的核心向量运算
     * @param a 起点Vec3d
     * @param b 终点Vec3d
     */
    public static Vec3d vec3dVectorOperation(Vec3d a, Vec3d b) {
        // 1. 获取从A到B的「差值向量」（原始向量，未归一化，长度=A到B的直线距离）
        Vec3d diffVec = b.subtract(a); // 等价于：new Vec3d(b.x-a.x, b.y-a.y, b.z-a.z)

        return diffVec.normalize();
    }
    /**
     * 为了便于进行跃迁的方法
     * @param leap_x 目标x
     * @param leap_y 目标y
     * @param leap_z 目标z
     * @param leap_distance 最大跃迁距离
     */
    public static boolean miniLeap(double leap_x,double leap_y,double leap_z,double leap_distance){

        double subX = leap_x - player.getEntityPos().getX();
        double subY = leap_y - player.getEntityPos().getY();
        double subZ =  leap_z - player.getEntityPos().getZ();

        double d =Math.sqrt(subX * subX + subZ * subZ+subY*subY); //距离
        if(d>leap_distance) {
            double scale = leap_distance / d; // 计算水平距离后归一化放大 10 倍
            tp(
                    subX * scale,
                    subY * scale,
                    subZ * scale
            );

            return false;
        }else {
            tp(
                    subX,
                    subY,
                    subZ
            );

            return true;
        }
    }

    public static Text ClickChat(String prefix, String clickText, String cmd, String suffix) {
        Text clickPart = Text.literal(clickText)
                .styled(s -> s
                        .withClickEvent(new ClickEvent.RunCommand(cmd))
                        .withHoverEvent(new HoverEvent.ShowText(Text.literal("执行指令：" + cmd)))
                );
        return Text.literal(prefix).append(clickPart).append(Text.literal(suffix));
    }

}


