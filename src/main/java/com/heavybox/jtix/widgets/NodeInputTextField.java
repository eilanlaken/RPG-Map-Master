package com.heavybox.jtix.widgets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;

public class NodeInputTextField extends NodeContainer implements NodeInput<String> {

    private boolean focused = false;
    private NodeTextCaret textNode = new NodeTextCaret("hello");

    public NodeInputTextField() {
        boxBorderSize = 1;
        boxBorderColor = Color.BLACK.clone();
        boxWidthSizing = Sizing.STATIC;
        boxWidth = 200;
        boxHeightSizing = Sizing.DYNAMIC;
        boxPaddingTop = 4;
        boxPaddingBottom = 2;
        boxPaddingLeft = 4;
        boxBackgroudColor = Color.WHITE.clone();
        textNode.color = Color.BLACK.clone();
        addChild(textNode);
    }

    @Override
    protected void setChildrenOffset(final Array<Node> children) {
        for (Node child : children) {
            child.offsetX = (textNode.calculateWidth() - boxWidth) * 0.5f + boxPaddingLeft + boxBorderSize;
        }
    }

    @Override
    public String getValue() {
        return textNode.text;
    }

    @Override
    public void setValue(String value) {
        textNode.text = value;
    }

    private class NodeTextCaret extends NodeText {

        public int caretIndex = 3;
        public Color caretColor = Color.BLACK.clone();
        public float caretBlinkSpeed = 0.5f;
        private boolean caretVisible = true;
        private float elapsedTime = 0;

        NodeTextCaret(String text) {
            super(text);
            //caretIndex = text.length();
        }

        @Override
        public void fixedUpdate(float delta) {
            elapsedTime += delta;
            if (elapsedTime >= caretBlinkSpeed) {
                elapsedTime -= caretBlinkSpeed;
                caretVisible = !caretVisible;
            }
        }

        @Override
        public void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
            super.render(renderer2D, x, y, deg, sclX, sclY); // render text
            // calculate caret offset.
            if (caretVisible) {
                renderer2D.setColor(caretColor);
                // calculate caret offset
                float caretOffsetX;
                if (caretIndex == 0) {
                    caretOffsetX = Renderer2D.calculateStringLineWidth(text, 0, caretIndex, font, size, antialiasing) - Renderer2D.calculateStringLineWidth(text, font, size, antialiasing) * 0.5f - size * 0.1f;
                } else if (caretIndex == text.length()) {
                    caretOffsetX = Renderer2D.calculateStringLineWidth(text, 0, caretIndex, font, size, antialiasing) - Renderer2D.calculateStringLineWidth(text, font, size, antialiasing) * 0.5f + size * 0.1f;
                } else {
                    float offset = Renderer2D.calculateStringLineWidth(text, 0, caretIndex, font, size, antialiasing);
                    caretOffsetX = offset - Renderer2D.calculateStringLineWidth(text, font, size, antialiasing) * 0.5f;
                }
                renderer2D.drawRectangleFilled(1, size, x + caretOffsetX, y, deg, sclX, sclY);
            }
        }


    }

}
