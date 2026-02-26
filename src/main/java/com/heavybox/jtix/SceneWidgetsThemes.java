package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.tools.ToolsThemeGenerator;
import com.heavybox.jtix.widgets_4.*;
import org.lwjgl.opengl.GL11;

public class SceneWidgetsThemes implements Scene {

    private final Renderer2D renderer2D = new Renderer2D();

    Widget widget = new Widget();
    WidgetNodeInputCheckbox checkbox;
    WidgetNodeInputOptions options;

    WidgetNodeContainerGrid grid = new WidgetNodeContainerGrid();

    Texture2D ninePatch;

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
        options = new WidgetNodeInputOptions(3,"hello", "bye", "123", "hhhhh", "kkkkk", "jjjjjj");

        grid.boxPaddingBottom = 2;
        grid.boxPaddingTop = 20;
        grid.boxPaddingLeft = 233;
        grid.boxPaddingRight = 44;
        grid.layout = WidgetNodeContainerGrid.Layout.FILL_ROWS;
        grid.layoutRowCapacity = 4;

        widget.addNode(options);
    }

    @Override
    public void update() {
        if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
            WidgetNodeShapeRectangle rect = new WidgetNodeShapeRectangle(40 + MathUtils.randomUniformFloat(0,0),
                    40 + MathUtils.randomUniformFloat(0,0), Color.RED);
            grid.addChild(rect);
        }

        widget.update();

        FrameBufferBinder.bind(null);
        GL11.glClearColor(0.01f,0.01f,0.01f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil

        renderer2D.begin();
        widget.render(renderer2D);
        renderer2D.end();
    }

    @Override
    public void finish() {

    }
}
