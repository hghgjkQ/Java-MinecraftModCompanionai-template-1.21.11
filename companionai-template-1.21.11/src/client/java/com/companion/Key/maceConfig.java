package com.companion.Key;

import com.companion.effect.weapon.ModFilterConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.companion.effect.tool.tip;
import static com.companion.effect.tool.tipGame;
import static com.companion.global.*;


public class maceConfig extends Screen {
    private final Screen parent;
    private static double move=0;
    private static double moveV=0;
    private static double move2=0;
    private static double move2V=0;
    private static boolean leftPressed;
    private static int leftPressed2;
    private static GLFWScrollCallback originalScrollCallback;

    private TextFieldWidget EntityChoose; //声明文本框
    private TextFieldWidget PlayerChoose; //声明文本框

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 【必须放在最前面】先绘制背景、按钮、输入框等所有已有组件
        super.render(context, mouseX, mouseY, delta);

        // ========== 下面自定义绘制文字 ==========

        // 1. 顶部大标题（居中、加粗、金色）
        String title = ModName+" - 重锤过滤设置";
        int titleWidth = textRenderer.getWidth(title);
        context.drawText(
                textRenderer,
                Text.literal("§l§6" + title),
                (this.width - titleWidth) / 2,  // 水平居中
                6,                               // 顶部Y坐标
                0xFFFFFFFF,                      // 文字颜色（ARGB，不透明白）
                true                             // 是否带阴影
        );


        // 3. 输入框旁的提示标签
        context.drawText(textRenderer, Text.literal("§l§6生物过滤选择"), (this.width - titleWidth) / 2-200, 50, 0xFFFFFFFF, true);
        context.drawText(textRenderer, Text.literal("§6§l玩家过滤选择"), (this.width - titleWidth) / 2+200, 50, 0xFFFFFFFF, true);

        //手搓滑动条方法
        leftPressed = GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS; // 3. 左键按下
        if(leftPressed ){
            if(leftPressed2==0) {
                leftPressed2 = 1;
            }
        }else{
            leftPressed2=0;
        }


        int mx= mouseX;
        int my= mouseY;
        long windowHandle = client.getWindow().getHandle(); // 1. 获取游戏窗口的 GLFW 句柄
        GLFW.glfwSetScrollCallback(windowHandle, (window, scrollX, scrollY) -> { // 3. 设置我们自己的回调
            if (mx<this.width/2 && my>90 && my <300){
                moveV+= (scrollY*10);
            }
            else if (mx>this.width/2 && my>90 && my <300){
                move2V+= (scrollY*10);
            }
        });

        List<String> TE=new ArrayList<>(EntityTableCode2);
        Set<String> TP=new HashSet<>(AllPlayer);

        if(!Objects.equals(EntityChoose.getText(), "")){
            TE = TE.stream()
                    .filter(s -> s.contains(EntityChoose.getText()) || Text.translatable(s).getString().contains(EntityChoose.getText()))
                    .toList();
        }
        if(!Objects.equals(PlayerChoose.getText(), "")){
            TP = new HashSet<>(TP.stream()
                    .filter(s -> s.contains(PlayerChoose.getText()))
                    .toList());
        }


        moveV*=0.9;
        move+=moveV;
        int size=TE.size();


        if (move>0)move=0;
        if (move<-size*20+200)move=-size*20+200;
        context.drawText(textRenderer, Text.literal("§l------------------------------------------"), 3, 90, 0xFFC0C0C0, true);
        context.drawText(textRenderer, Text.literal("§l------------------------------------------"), 3, 300, 0xFFC0C0C0, true);
        for(int i=0;i<11;i++){
            int index =size>10 ? (i + Math.abs((int) move) / 20) : i;
            if (index>size-1)break;
            String raw=TE.get(index);
            String target=Text.translatable(raw).getString()+"("+raw.replaceFirst("entity.minecraft.","")+")";

            int posY=100+20*i+ ((int)move)%20;
            if (mx<this.width/2 && my>posY-6 && my <posY+14){
                context.drawText(textRenderer,Text.literal("§l§a"+target) , 6,posY , 0xFFFFFFFF, true);
                if(leftPressed2==1){
                    if(filter.contains(raw)) {
                        filter.remove(raw);
                    }
                    else{
                        filter.add(raw);
                    }
                    ModFilterConfig.save();
                    leftPressed2=2;
                }
            }else{
                context.drawText(textRenderer,Text.literal(target) , 6,posY , 0xFFFFFFFF, true);
            }

            if(filter.contains(raw)) {
                context.drawText(textRenderer, Text.literal("§4§l■"), this.width / 2, posY - 2, 0xFFFFFFFF, true);
            }else{
                context.drawText(textRenderer, Text.literal("■"), this.width / 2, posY - 2, 0xFFFFFFFF, true);
            }



        }

        //玩家处


        move2V*=0.9;
        move2+=move2V;

        size= TP.size();

        int start=this.width / 2+10;
        if (move2>0)move2=0;
        if (move2<-size*20+200)move2=-size*20+200;
        context.drawText(textRenderer, Text.literal("§l------------------------------------------"), start, 90, 0xFFC0C0C0, true);
        context.drawText(textRenderer, Text.literal("§l------------------------------------------"), start, 300, 0xFFC0C0C0, true);

        for(int i=0;i<11;i++) {

            int index =size>10 ? (i + Math.abs((int) move2) / 20) : i;
            if (index > size - 1) break;

            String raw = TP.stream().toList().get(index);

            int posY = 100 + 20 * i + ((int) move2) % 20;
            if (mx > this.width / 2 && my > posY - 6 && my < posY + 14) {
                context.drawText(textRenderer, Text.literal("§l§a" + raw), start+6, posY, 0xFFFFFFFF, true);
                if (leftPressed2 == 1) {
                    if (filterPlayer.contains(raw)) {
                        filterPlayer.remove(raw);
                    } else {
                        filterPlayer.add(raw);
                    }
                    ModFilterConfig.save();
                    leftPressed2 = 2;
                }
            } else {
                context.drawText(textRenderer, Text.literal(raw), start+6, posY, 0xFFFFFFFF, true);
            }

            if (filterPlayer.contains(raw)) {
                context.drawText(textRenderer, Text.literal("§4§l■"), this.width-10, posY - 2, 0xFFFFFFFF, true);
            } else {
                context.drawText(textRenderer, Text.literal("■"), this.width-10, posY - 2, 0xFFFFFFFF, true);
            }
        }
    }


    public maceConfig(Screen parent) {
        super(Text.literal(ModName+"重锤过滤设置"));
        this.parent = parent;
        ModFilterConfig.load();
    }
    public void create_Button(AtomicBoolean target, int x, int y, int width, int height, String name){
        addDrawableChild(
                ButtonWidget.builder(
                        Text.literal((target.get() ? "§6" : "")+name+ onOff(target.get())),
                        b -> {
                            target.set(!target.get());
                            b.setMessage(Text.literal((target.get() ? "§6" : "")+name+ onOff(target.get())));
                        }).dimensions(x,y,width,height).build()
        );
    }


    @Override
    protected void init() {

        long windowHandle = client.getWindow().getHandle(); // 1. 获取游戏窗口的 GLFW 句柄
        originalScrollCallback = GLFW.glfwSetScrollCallback(windowHandle, null);

        EntityChoose = new TextFieldWidget(textRenderer, 6, 70, this.width/2-16, 20, Text.literal("实体筛选"));
        addDrawableChild(EntityChoose);

        PlayerChoose = new TextFieldWidget(textRenderer, this.width/2+6, 70, this.width/2-16, 20, Text.literal("玩家筛选"));
        addDrawableChild(PlayerChoose);

        // 保存并退出
        addDrawableChild(ButtonWidget.builder(
                Text.literal("<-返回"),
                b -> {
                    if (client != null) client.setScreen(parent);
                }).dimensions(20, 20, 100, 20).build());
    }

    private static String onOff(boolean b) {
        return b ? "开" : "关";
    }


    // 界面关闭时触发
    @Override
    public void close() {
        restoreOldScrollCallback();
        if(client != null) client.setScreen(parent);
    }

    // 界面被移除时兜底执行
    @Override
    public void removed() {
        restoreOldScrollCallback();
    }

    // 还原窗口原始回调
    private void restoreOldScrollCallback() {
        if (originalScrollCallback == null) return;
        long window = client.getWindow().getHandle();
        // 把之前保存的原版回调重新绑回窗口
        GLFW.glfwSetScrollCallback(window, originalScrollCallback);
        originalScrollCallback = null;
    }
}