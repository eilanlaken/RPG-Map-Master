package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

// TODO: actually finish
public class NodeSlider_2 extends Node {

    /* state and data */
    public boolean integers = false;
    public float   minimum  = 0;
    public float   maximum  = 1;
    public float   value    = 0.5f; // sliding will change this fraction.

    /* TODO: theme. See how to make it flexible */
    public TextureRegion imageBackground = UserInterface.getTheme().sliderImageBackground;
    public TextureRegion imageFill       = UserInterface.getTheme().sliderImageFill;
    public TextureRegion imageThumb      = UserInterface.getTheme().sliderImageThumb;
    public float         length          = UserInterface.getTheme().sliderLength;
    public float         thickness       = UserInterface.getTheme().sliderThickness;
    public float         thumbSize       = UserInterface.getTheme().sliderThumbSize;
    public Color         colorBackground = UserInterface.getTheme().sliderColorBackground.clone();
    public Color         colorThumb      = UserInterface.getTheme().sliderColorThumb.clone();
    public Color         colorFill       = UserInterface.getTheme().sliderColorFill.clone();

    NodeGraphics background;
    NodeGraphics fillParent;
    NodeGraphics fill;
    NodeGraphics thumb;

    public NodeSlider_2() {
        if (imageBackground == null) {
            background = new NodeGraphics(length, thickness) {
                @Override
                protected boolean maskChildren() {
                    return true;
                }
            };
            background.color = colorBackground.toFloatBits();
        } else {
            background = new NodeGraphics(imageBackground) {
                @Override
                protected boolean maskChildren() {
                    return true;
                }
            };
            background.color = Color.WHITE_FLOAT;
        }

        fillParent = new NodeGraphics(background.getWidth(), background.getHeight());
        fillParent.color = Color.CLEAR_WHITE.toFloatBits();

        if (imageFill == null) {
            fill = new NodeGraphics(length, thickness) {
                @Override
                protected float getWidth() {
                    return background.getWidth();
                }
            };
            fill.color = colorFill.toFloatBits();
        } else {
            fill = new NodeGraphics(imageFill) {
                @Override
                protected float getWidth() {
                    return background.getWidth();
                }
            };
            fill.color = Color.WHITE_FLOAT;
        }

        if (imageThumb == null) {
            thumb = new NodeGraphics(thumbSize * 0.5f);
            thumb.color = colorThumb.toFloatBits();
        } else {
            thumb = new NodeGraphics(imageThumb);
            thumb.color = Color.WHITE_FLOAT;
        }

        background.connectChild(fillParent);
        fillParent.connectChild(fill);

        connectChild(background);
        connectChild(thumb);

        fillParent.anchor = Anchor.PARENT_CENTER_LEFT;
        fillParent.setShapeToRectangle(100,200);

        onMouseDragStartDefault(e -> {
            float value = 0.5f + e.mouseLocalX / length;
            setValue(value);
        });

        onMouseDragDefault(e -> {
            setValue(value + (e.mouseLocalX - e.mouseLocalXPrev) / length);
        });
    }

    @Override
    protected final void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        // just a parent containing the state of a slider.
    }



    @Override
    protected float getWidth() {
        return background.getWidth();
    }

    @Override
    protected float getHeight() {
        return Math.max(background.getHeight(), thumb.getHeight());
    }

    public float getValue() {
        return minimum + value * (maximum - minimum);
    }
    public void setValue(float value) {
        this.value = MathUtils.clampFloat(value, 0, 1);
    }

}
