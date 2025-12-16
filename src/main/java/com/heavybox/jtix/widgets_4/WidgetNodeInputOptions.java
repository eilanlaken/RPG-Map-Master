package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;

public class WidgetNodeInputOptions extends WidgetNodeContainerGrid implements WidgetNodeInput<Integer> {

    private int selectedOption = 0;
    private final Array<String> options = new Array<>(true, 3);

    // rendering - get defaults from theme.
    public float layoutChildSpacing = 33;
    public float layoutInnerSpacing = 8;
    public Color colorSelected = Color.valueOf("0075FF"); // TODO: grab from theme
    public Color colorUnselected = Color.valueOf("767676"); // TODO: grab from theme
    public float radius = 7;

    public WidgetNodeInputOptions(int defaultSelected, String... options) {
        layout = Layout.FILL_ROWS;
        layoutHeightSizing = Sizing.DYNAMIC;
        layoutWidthSizing = Sizing.DYNAMIC;
        layoutRowCapacity = 5;
        layoutColumnCapacity = 5;
        layoutOverflowX = Overflow.VISIBLE;
        layoutOverflowY = Overflow.VISIBLE;
        boxBackgroundVisible = false;
        boxBorderSize = 0;

        for (String option : options) {
            if (option == null) continue;
            this.options.add(option);
        }

        if (defaultSelected < 0 || defaultSelected >= this.options.size) defaultSelected = 0;
        for (int i = 0; i < this.options.size; i++) {
            String option = this.options.get(i);
            boolean selected = i == defaultSelected;
            WidgetNodeOption optionWidget = new WidgetNodeOption(option, selected ? Color.valueOf("0075FF") : Color.valueOf("767676"));
            optionWidget.selected = selected;
            addChild(optionWidget);
        }
    }

    @Override
    public Integer getValue() {
        return selectedOption;
    }

    @Override
    public void setValue(Integer value) {
        if (value == null || value < 0 || value >= options.size) {
            selectedOption = 0;
            return;
        }

        selectedOption = value;

        for (int i = 0; i < children.size; i++) {
            WidgetNode child = children.get(i);
            if (!(child instanceof WidgetNodeOption)) continue;
            WidgetNodeOption option = (WidgetNodeOption) child;
            option.selected = i == selectedOption;
            option.circle.color = option.selected ? colorSelected : colorUnselected;
        }
    }

    private void selectOption(WidgetNodeOption option) {
        for (int i = 0; i < children.size; i++) {
            WidgetNode child = children.get(i);
            if (option == child) {
                setValue(i);
                return;
            }
        }
    }

    private static final class WidgetNodeOption extends WidgetNodeContainerHorizontal {

        private boolean selected = false;

        private final WidgetNodeShapeCircle circle;
        private final WidgetNodeText optionText;

        WidgetNodeOption(String option, final Color color) {
            WidgetNodeOption.this.boxBackgroundVisible = false;
            WidgetNodeOption.this.layoutOverflowX = Overflow.VISIBLE;
            WidgetNodeOption.this.layoutOverflowY = Overflow.VISIBLE;
            WidgetNodeOption.this.layoutWidthSizing = Sizing.DYNAMIC;
            WidgetNodeOption.this.layoutHeightSizing = Sizing.DYNAMIC;
            WidgetNodeOption.this.boxBorderSize = 0;
            WidgetNodeOption.this.boxChildSpacing = 5;
            WidgetNodeOption.this.layoutAddScrollbar = false;

            circle = new WidgetNodeShapeCircle(8, 16, color);
            optionText = new WidgetNodeText(option);

            addChild(circle);
            addChild(optionText);
        }

        @Override
        protected boolean onMouseLeftClickDefault(Event.EventMouseLeftClick e) {
            WidgetNodeInputOptions parent = (WidgetNodeInputOptions) getParent();
            parent.selectOption(this);
            return false;
        }

    }

}
