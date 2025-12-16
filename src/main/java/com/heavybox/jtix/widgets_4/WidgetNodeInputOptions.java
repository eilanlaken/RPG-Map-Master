package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.Vector2;
import org.jetbrains.annotations.NotNull;

// TODO: must consider scale.
public class WidgetNodeInputOptions extends WidgetNode implements WidgetNodeInput<String> {

    private String selectedOption;
    private Array<String> options = new Array<>(true, 3);

    // rendering - get defaults from theme.
    @NotNull
    public Layout layout = Layout.VERTICAL;
    public float  layoutChildSpacing = 5;
    public float  layoutInnerSpacing = 5;
    public Color  colorSelected = Color.valueOf("0075FF");
    public Color  colorUnselected = Color.valueOf("767676");
    public float  radius = 7;

    public WidgetNodeInputOptions(String... options) {
        for (String option : options) {
            if (option == null) continue;
            this.options.add(option);
        }
        this.selectedOption = this.options.first();
    }

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (layout == Layout.VERTICAL) {
            float current_y = getHeight() * 0.5f;
            // TODO transform the offset
            for (String option : options) {
                drawOption(renderer2D, option, true, x,y + current_y,deg,sclX,sclY);
                current_y -= getOptionHeight(option) + layoutChildSpacing;
            }
            return;
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
        if (layout == Layout.VERTICAL) {
            float max = 0;
            for (String option : options) {
                max = Math.max(max, Renderer2D.calculateStringLineWidth(option, null, Widgets.themeTextSize, Widgets.themeTextAntialiasing));
            }
            return max + radius * 2 + layoutInnerSpacing;
        }

        return 0;
    }

    @Override
    protected float getHeight() {
        if (layout == Layout.VERTICAL) {
            float sum = 0;
            for (String option : options) {
                sum += getOptionHeight(option) + layoutChildSpacing;
            }
            return sum - layoutChildSpacing;
        }

        return 0;
    }

    @Override
    public String getValue() {
        return selectedOption;
    }

    @Override
    protected boolean onMouseLeftClickDefault(Event.EventMouseLeftClick e) {
        // TODO: select option
        return true;
    }

    @Override
    public void setValue(String value) {
        if (options.contains(value, true)) {
            this.selectedOption = options.first();
            return;
        }
        this.selectedOption = value;
    }

    public enum Layout {
        VERTICAL,
        HORIZONTAL,
        ;
    }

}
