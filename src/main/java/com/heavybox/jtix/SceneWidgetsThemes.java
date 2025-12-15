package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.tools.ToolsThemeGenerator;
import com.heavybox.jtix.widgets_4.Theme;
import com.heavybox.jtix.widgets_4.Widget;
import com.heavybox.jtix.widgets_4.WidgetNodeInputCheckbox;
import com.heavybox.jtix.widgets_4.Widgets;
import org.lwjgl.opengl.GL11;

public class SceneWidgetsThemes implements Scene {

    private final Renderer2D renderer2D = new Renderer2D();

    Widget widget = new Widget();
    WidgetNodeInputCheckbox checkbox;

    Texture ninePatch;

    @Override
    public void setup() {
        try {
            ToolsThemeGenerator.themeCheckboxColorCheckmarkBackground = Color.CHARTREUSE;
            ToolsThemeGenerator.themeCheckboxImageChecked = "assets/user-interface-theme/checkbox-checked.png";
            ToolsThemeGenerator.themeCheckboxImageUnchecked = "assets/user-interface-theme/checkbox-unchecked.png";
            ToolsThemeGenerator.generateTheme("assets/user-interface-theme", "widgets-theme");
        } catch (Exception e) {

        }

        Assets.loadTexture("assets/engine-tests/ninepatch.png");
        Assets.loadTheme("assets/user-interface-theme/widgets-theme.yml");
        Assets.finishLoading();
    }

    @Override
    public void start() {
        ninePatch = Assets.get("assets/engine-tests/ninepatch.png");
        Theme theme = Assets.get("assets/user-interface-theme/widgets-theme.yml");
        Widgets.setGlobalTheme(theme);
        checkbox = new WidgetNodeInputCheckbox();
        widget.addNode(checkbox);
    }

    @Override
    public void update() {
        widget.update();

        FrameBufferBinder.bind(null);
        GL11.glClearColor(0.01f,0.01f,0.01f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil

        renderer2D.begin();
        widget.render(renderer2D);
        renderer2D.drawStringLine("hello abcdefg", 18, true, 0,-200,0,1,1);
        renderer2D.drawStringLine("hello abcdefg", 34, true, 0,300,0,1,1);
        renderer2D.end();
    }

    @Override
    public void finish() {

    }
}
