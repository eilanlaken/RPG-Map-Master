package com.heavybox.jtix.widgets;

import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Renderer2D;

// TODO: add: polygons, rectangles with rounded corners, circles with refinement
public final class Widgets {

    private static int count = 0;

    public static boolean debugMode = true;

    private static final StringBuilder wordWrapStringBuilder    = new StringBuilder();
    private static final ArrayInt      wordWrapStartIndices     = new ArrayInt();
    private static final ArrayInt      wordWrapLinebreakIndices = new ArrayInt();

    public static int getID() {
        count++;
        return count - 1;
    }

    /* == from Wikipedia ==
    SpaceLeft := LineWidth
    for each Word in Text
        if (Width(Word) + SpaceWidth) > SpaceLeft
            insert line break before Word in Text
            SpaceLeft := LineWidth - Width(Word)
        else
            SpaceLeft := SpaceLeft - (Width(Word) + SpaceWidth)
    */
    // TODO: optimize - instead of using a StringBuilder, write the linebreak indices into an output array.
    public static String[] wordWrap(final String line, float boundaryWidth, Font font, int fontSize, boolean fontAntialiasing) {
        /* preparation: clear buffers, trim trailing and leading spaces from input string line, adjust boundary length, break into words */
        String trimmed = line.trim();
        String[] words = trimmed.split("\\s+");
        boundaryWidth = Math.max(boundaryWidth - fontSize, fontSize);
        wordWrapStringBuilder.setLength(0);
        wordWrapStringBuilder.append(trimmed);
        wordWrapStartIndices.clear();
        wordWrapLinebreakIndices.clear();

        /* get the starting index of each word */
        boolean inWord = false;
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (!Character.isWhitespace(c)) {
                if (!inWord) {
                    wordWrapStartIndices.add(i);
                    inWord = true;
                }
            } else {
                inWord = false;
            }
        }

        /* the greedy word wrap algorithm */
        float spaceLeft = boundaryWidth;
        final float space_width = Renderer2D.calculateStringLineWidth(" ", 0, 1, font, fontSize, fontAntialiasing);
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            float width = Renderer2D.calculateStringLineWidth(word, 0, word.length(), font, fontSize, fontAntialiasing);
            if (width + space_width > spaceLeft) { // overflow
                wordWrapLinebreakIndices.add(wordWrapStartIndices.get(i));
                spaceLeft = boundaryWidth - width;
            } else {
                spaceLeft = spaceLeft - (width + space_width);
            }
        }

        /* Insert '\n' at each index, shifting as we go */
        for (int i = 0; i < wordWrapLinebreakIndices.size; i++) {
            int adjustedIndex = wordWrapLinebreakIndices.get(i) + i; // Account for previous insertions shifting the string
            if (adjustedIndex <= wordWrapStringBuilder.length()) {
                wordWrapStringBuilder.insert(adjustedIndex, '\n');
            } else {
                throw new IllegalArgumentException("Index out of bounds: " + wordWrapLinebreakIndices.get(i));
            }
        }

        return wordWrapStringBuilder.toString().split("\n"); // TODO optimize memory and runtime to only fill an ArrayInt "newlines"
    }


}
