package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;

// TODO: scrap that and replace with ContainerGrid
public class WidgetNodeInputOptions extends WidgetNode implements WidgetNodeInput<Integer> {

    private int selectedOption;
    private Array<String> options = new Array<>(true, 3);

    // rendering - get defaults from theme.
    public float layoutChildSpacing = 33;
    public float layoutInnerSpacing = 8;
    public Color colorSelected = Color.valueOf("0075FF");
    public Color colorUnselected = Color.valueOf("767676");
    public float radius = 7;

    public WidgetNodeInputOptions(String... options) {
        for (String option : options) {
            if (option == null) continue;
            this.options.add(option);
        }
        this.selectedOption = 0;
    }

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        float offset_x = -getWidth() * 0.25f;
        for (int i = 0; i < options.size; i++) {
            String option = options.get(i);
            drawOption(renderer2D, option, i == selectedOption, x + offset_x, y, deg, sclX, sclY);
            offset_x += getOptionWidth(option) + layoutChildSpacing;
        }
    }

    protected void drawOption(Renderer2D renderer2D, String option, boolean selected, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(selected ? colorSelected : colorUnselected);
        renderer2D.drawCircleFilled(radius, 20, x - getOptionWidth(option) * 0.5f - layoutInnerSpacing, y, deg, sclX, sclY);
        renderer2D.setColor(Widgets.themeTextColor);
        renderer2D.drawStringLine(option, Widgets.themeTextSize, Widgets.themeTextAntialiasing, x, y, deg, sclX, sclY);
    }


    private float getOptionWidth(String option) {
        return radius * 2 + layoutInnerSpacing + Renderer2D.calculateStringLineWidth(option, null, Widgets.themeTextSize, Widgets.themeTextAntialiasing);
    }

    private float getOptionHeight(String option) {
        return Math.max(radius * 2, Widgets.themeTextSize);
    }

    @Override
    protected float getWidth() {
        float sum = 0;
        for (String option : options) {
            sum += getOptionWidth(option) + layoutChildSpacing;
        }
        sum -= layoutChildSpacing;
        return sum;
    }

    @Override
    protected float getHeight() {
        float max = 0;
        max = Math.max(max, Widgets.themeTextSize);
        max = Math.max(max, radius * 2);
        return max;
    }

    @Override
    public Integer getValue() {
        return selectedOption;
    }

    @Override
    protected boolean onMouseLeftClickDefault(Event.EventMouseLeftClick e) {
        // TODO: select option
        return true;
    }

    @Override
    public void setValue(Integer value) {
        this.selectedOption = value;
        if (this.selectedOption >= options.size) this.selectedOption = 0;
    }

}
