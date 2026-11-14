package com.companion.Key;

import net.minecraft.client.font.Alignment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.companion.global.ModName;


public class configTool extends Screen {
    // 在你的 configTool 类中添加：
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 【必须放在最前面】先绘制背景、按钮、输入框等所有已有组件
        super.render(context, mouseX, mouseY, delta);

        // ========== 下面自定义绘制文字 ==========

        // 1. 顶部大标题（居中、加粗、金色）
        String title = ModName+"设置";
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
        context.drawText(textRenderer, Text.literal("§7威力(模拟高度)"), 220, 245, 0xFFFFFFFF, true);
        context.drawText(textRenderer, Text.literal("§7半径"), 340, 245, 0xFFFFFFFF, true);
    }

    private final Screen parent;
    public static config cfg = null;


    public configTool(Screen parent) {
        super(Text.literal(ModName+"设置"));
        this.parent = parent;
        cfg = config.load();
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

    private TextFieldWidget cmdField; //声明文本框
    private TextFieldWidget t1; //声明文本框
    private TextFieldWidget t2; //声明文本框
    private TextFieldWidget t3; //声明文本框
    private TextFieldWidget t4; //声明文本框



    @Override
    protected void init() {
        int center = this.width / 2;
        create_Button(cfg.TpState,20, 20, 200, 20,"空手疾跑左键瞬移: ");
        create_Button(cfg.enableAutoLogin,20, 40, 200, 20,"自动登入（下面是指令内容）: ");
        create_Button(cfg.enableNoFall,20, 80, 200, 20,"免疫摔落: ");
        // 登入指令文本框
        cmdField = new TextFieldWidget(textRenderer, 20, 60, 200, 20, Text.literal("登入指令"));
        cmdField.setText(cfg.loginCommand);
        addDrawableChild(cmdField);
        create_Button(cfg.enableSpeedShow,20, 100, 200, 20,"速度显示：");
        create_Button(cfg.RaidState,20, 120, 200, 20,"神杀：");
        create_Button(cfg.BowState,20, 140, 200, 20,"强弓：");

        create_Button(cfg.SpearState,20, 160, 200, 20,"狙击枪：");
        t1 = new TextFieldWidget(textRenderer, 220, 160, 50, 20, Text.literal("狙击枪威力"));
        t1.setText(String.valueOf(cfg.SpearPower));
        addDrawableChild(t1);

        create_Button(cfg.enableAutoRaid,20, 200, 200, 20,"自动袭击：");
        create_Button(cfg.enableAutoAfk,20, 220, 200, 20,"nrnb挂机：");

        create_Button(cfg.enableMace,20, 240, 200, 20,"湮灭：");
        t2 = new TextFieldWidget(textRenderer, 285, 240, 50, 20, Text.literal("威力"));
        t2.setText(String.valueOf(cfg.MacePower));
        addDrawableChild(t2);

        create_Button(cfg.EasyElytraState,20, 260, 200, 20,"简单鞘翅：");
        t3 = new TextFieldWidget(textRenderer, 220, 260, 50, 20, Text.literal("速度"));
        t3.setText(String.valueOf(cfg.ElytraSpeed));
        addDrawableChild(t3);

        addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("重锤过滤设置(mace filter)"),
                        b -> {

                            cfg.loginCommand = cmdField.getText();

                            cfg.SpearPower = Double.parseDouble(t1.getText());
                            cfg.MacePower = Double.parseDouble(t2.getText());
                            cfg.ElytraSpeed = Double.parseDouble(t3.getText());
                            cfg.RaidR = Double.parseDouble(t4.getText());
                            config.save(cfg);
                            var data=config.load();


                            client.setScreen(new maceConfig(client.currentScreen));
                        }).dimensions(410,240,120,20).build()
        );
        t4 = new TextFieldWidget(textRenderer, 360, 240, 50, 20, Text.literal("半径r"));
        t4.setText(String.valueOf(cfg.RaidR));
        addDrawableChild(t4);




        // 保存并退出
        addDrawableChild(ButtonWidget.builder(
                Text.literal("保存并关闭"),
                b -> {
                    cfg.loginCommand = cmdField.getText();

                    cfg.SpearPower = Double.parseDouble(t1.getText());
                    cfg.MacePower = Double.parseDouble(t2.getText());
                    cfg.ElytraSpeed = Double.parseDouble(t3.getText());
                    cfg.RaidR = Double.parseDouble(t4.getText());
                    config.save(cfg);
                    var data=config.load();
                    if (client != null) client.setScreen(parent);
                }).dimensions(20, 300, 200, 20).build());
    }

    private static String onOff(boolean b) {
        return b ? "开" : "关";
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(parent);

    }
}