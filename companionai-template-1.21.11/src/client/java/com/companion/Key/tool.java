package com.companion.Key;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;

import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import static com.companion.global.*;


public class tool {
    public static KeyBinding[] keyBox =new KeyBinding[7];

    public static void reg(){
        KeyBinding.Category name=new KeyBinding.Category(Identifier.of(ModName, "main"));
        InputUtil.Type key= InputUtil.Type.KEYSYM;

        keyBox[0] = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "斩杀",          // 翻译键
                key,       // 键盘
                GLFW.GLFW_KEY_F4,             // 默认键
                name  // 分类翻译键
        ));
        keyBox[1] = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "刻秒",          // 翻译键
                key,       // 键盘
                GLFW.GLFW_KEY_F7,             // 默认键
                name     // 分类翻译键
        ));
        keyBox[2] = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "神明降世",          // 翻译键
                key,       // 键盘
                GLFW.GLFW_KEY_K,             // 默认键
                name     // 分类翻译键
        ));
        keyBox[3] = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "temp",          // 翻译键
                key,       // 键盘
                GLFW.GLFW_KEY_V,             // 默认键
                name     // 分类翻译键
        ));
        keyBox[4] = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "珍珠",          // 翻译键
                key,       // 键盘
                GLFW.GLFW_ANGLE_PLATFORM_TYPE_NONE,             // 默认键
                name     // 分类翻译键
        ));
        keyBox[5] = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "打开菜单",          // 翻译键
                key,       // 键盘
                GLFW.GLFW_KEY_I,             // 默认键
                name     // 分类翻译键
        ));
        keyBox[6] = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "上钩",          // 翻译键
                key,       // 键盘
                GLFW.GLFW_KEY_END,             // 默认键
                name     // 分类翻译键
        ));


    }

}
