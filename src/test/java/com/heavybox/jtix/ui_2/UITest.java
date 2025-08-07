package com.heavybox.jtix.ui_2;

import com.heavybox.jtix.collections.ArrayInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class UITest {

    @Test
    public void wordBreak() {
        String line_1 = "AAA BBB";
        String[] words_1 = line_1.trim().split("\\s+");
        int[] startIndices_1 = new int[words_1.length];
        int index_1 = 0; // To track the current position in the string
        for (int i = 0; i < words_1.length; i++) {
            // Find the index of the current word starting from the current position
            index_1 = line_1.indexOf(words_1[i], index_1);
            startIndices_1[i] = index_1;
            index_1 += words_1[i].length(); // Move the index past the current word
        }
        Assertions.assertEquals(2, startIndices_1.length);
        Assertions.assertEquals(0, startIndices_1[0]);
        Assertions.assertEquals(4, startIndices_1[1]);

        String line_2 = "AAA AAA";
        String[] words_2 = line_2.trim().split("\\s+");
        int[] startIndices_2 = new int[words_2.length];
        int index_2 = 0; // To track the current position in the string
        for (int i = 0; i < words_2.length; i++) {
            // Find the index of the current word starting from the current position
            index_2 = line_2.indexOf(words_2[i], index_2);
            startIndices_2[i] = index_2;
            index_2 += words_2[i].length(); // Move the index past the current word
        }
        Assertions.assertEquals(2, startIndices_2.length);
        Assertions.assertEquals(0, startIndices_2[0]);
        Assertions.assertEquals(4, startIndices_2[1]);
    }

    @Test
    public void insertNewlines() {
        String line_1 = "AAA BBB";
        String[] words_1 = line_1.trim().split("\\s+");
        int[] indices = new int[words_1.length];
        int index_1 = 0; // To track the current position in the string
        for (int i = 0; i < words_1.length; i++) {
            // Find the index of the current word starting from the current position
            index_1 = line_1.indexOf(words_1[i], index_1);
            indices[i] = index_1;
            index_1 += words_1[i].length(); // Move the index past the current word
        }

        StringBuilder result = new StringBuilder(line_1);
        // Insert '\n' at each index, shifting as we go
        for (int i = 0; i < indices.length; i++) {
            int adjustedIndex = indices[i] + i; // Account for previous insertions shifting the string
            if (adjustedIndex <= result.length()) {
                result.insert(adjustedIndex, '\n');
            } else {
                throw new IllegalArgumentException("Index out of bounds: " + indices[i]);
            }
        }

        Assertions.assertEquals("\nAAA \nBBB", result.toString());
    }

    @Test
    public void getWordIndicesTest() {
        String str1 = "AA BB CC";
        ArrayInt indices = getWordIndices(str1);

        Assertions.assertEquals(3, indices.size);
        Assertions.assertEquals(0, indices.get(0));
        Assertions.assertEquals(3, indices.get(1));
        Assertions.assertEquals(6, indices.get(2));

        String str2 = "GGGG   CC";
        indices = getWordIndices(str2);
        Assertions.assertEquals(2, indices.size);
        Assertions.assertEquals(0, indices.get(0));
        Assertions.assertEquals(7, indices.get(1));
    }

    private ArrayInt getWordIndices(final String str) {
        ArrayInt indices = new ArrayInt();
        boolean inWord = false;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            // Detect the start of a word
            if (!Character.isWhitespace(c)) {
                if (!inWord) {
                    indices.add(i);  // Capture the start index of the word
                    inWord = true;
                }
            } else {
                inWord = false;  // Reset flag when a space is found
            }
        }

        return indices;
    }

}