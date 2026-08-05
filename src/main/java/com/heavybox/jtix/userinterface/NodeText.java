package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Renderer2D;

public class NodeText extends Node {

    /* public text params */
    public Color     color        = Color.WHITE.clone();
    public String    text         = "";
    public Font      font         = null;
    public int       size         = 18;
    public boolean   antialiasing = true;
    public float     lineSpacing  = 1.1f;
    public Alignment alignment    = null;

    /* prev values */
    private String    textPrev;
    private Font      fontPrev;
    private int       sizePrev;
    private boolean   antialiasingPrev;
    private float     lineSpacingPrev;
    private Alignment alignmentPrev;

    /* internally calculated values and metrics */
    private float width;
    private float height;
    private final Array<String> textLines  = new Array<>(true, 1);
    private final ArrayFloat linesWidths = new ArrayFloat(true, 1);

    public NodeText(String text) {
        this.text = text;
    }

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(color);
        renderer2D.setFont(font);
        for (int i = 0; i < textLines.size; i++) {
            final String line = textLines.get(i);
            float lineXOffset;
            if (alignment == null || alignment == Alignment.CENTER) lineXOffset = 0;
            else if (alignment == Alignment.LEFT) lineXOffset = -(width - linesWidths.get(i)) * 0.5f;
            else lineXOffset = (width - linesWidths.get(i)) * 0.5f;
            renderer2D.drawStringLine(line, size, antialiasing, x + lineXOffset, y + height * 0.5f - i * (size + lineSpacing), deg, sclX, sclY);
        }
    }

    @Override
    protected float getWidth() {
        return width;
    }

    @Override
    protected float getHeight() {
        return height;
    }

    @Override
    protected void onFixedUpdate(float delta) {
        if (text == null) text = "";
        if (text != textPrev || font != fontPrev || size != sizePrev ||
                antialiasing != antialiasingPrev ||
                lineSpacing != lineSpacingPrev ||
                alignment != alignmentPrev) {

            textPrev = text;
            fontPrev = font;
            sizePrev = size;
            antialiasingPrev = antialiasing;
            lineSpacingPrev = lineSpacing;
            alignmentPrev = alignment;

            // Rebuild text layout here.
            recalculateMetrics();
        }
        onFixedUpdateText(delta);
    }

    private void recalculateMetrics() {
        textLines.clear();
        String[] lines = text.split("\n");
        textLines.addAll(lines);

        linesWidths.clear();
        width = 0;
        height = lines.length * size + Math.max(0, lines.length - 1) * lineSpacing;
        for (String line : lines) {
            float lineWidth = Renderer2D.calculateStringLineWidth(line, font, size, antialiasing);
            width = Math.max(width, lineWidth);
            linesWidths.add(lineWidth);
        }
        System.out.println("recalc");
        // TODO: continue
    }

    protected void onFixedUpdateText(float delta) {}

    public enum Alignment {
        LEFT,
        CENTER,
        RIGHT,
        ;
    }

}
