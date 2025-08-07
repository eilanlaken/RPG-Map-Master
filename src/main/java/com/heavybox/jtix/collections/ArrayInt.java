package com.heavybox.jtix.collections;

import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.memory.MemoryPool;

import java.util.Arrays;

public class ArrayInt implements MemoryPool.Reset {

    public int[]   items;
    public int     size;
    public boolean ordered;

    public ArrayInt() {
        this(true, 16);
    }

    public ArrayInt(int capacity) {
        this(true, capacity);
    }

    public ArrayInt(boolean ordered, int capacity) {
        this.ordered = ordered;
        this.items = new int[capacity];
    }

    public void add(final int value) {
        int[] items = this.items;
        if (size + 1 >= items.length) items = resize(Math.max(8, size * 2));
        items[size] = value;
        size++;
    }

    public void add(final int v1, final int v2) {
        int[] items = this.items;
        if (size + 1 >= items.length) items = resize(Math.max(8, size * 2));
        items[size] = v1;
        items[size + 1] = v2;
        size += 2;
    }

    public void add(final int v1, final int v2, final int v3) {
        int[] items = this.items;
        if (size + 1 >= items.length) items = resize(Math.max(8, size * 2));
        items[size] = v1;
        items[size + 1] = v2;
        items[size + 2] = v3;
        size += 3;
    }

    public int get(int index) {
        if (index >= size) throw new IndexOutOfBoundsException("Tried to retrieve array element at index " + index + " >= " + size);
        return items[index];
    }

    public void set (int index, int value) {
        if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
        items[index] = value;
    }

    public int getCyclic(int index) {
        if (size == 0) throw new CollectionsException(Array.class.getSimpleName() + " is empty.");
        if (index >= size) return items[index % size];
        else if (index < 0) return items[index % size + size];
        return items[index];
    }

    public boolean contains(int value) {
        int i = size - 1;
        int[] items = this.items;
        while (i >= 0)
            if (items[i--] == value) return true;
        return false;
    }

    public long removeIndex(int index) {
        if (index >= size) throw new IndexOutOfBoundsException("Tried to retrieve array element at index " + index + " >= " + size);
        int[] items = this.items;
        long value = items[index];
        size--;
        if (ordered)
            System.arraycopy(items, index + 1, items, index, size - index);
        else
            items[index] = items[size];
        return value;
    }

    public boolean removeValue(long value) {
        int[] items = this.items;
        for (int i = 0, n = size; i < n; i++) {
            if (items[i] == value) {
                removeIndex(i);
                return true;
            }
        }
        return false;
    }

    public long first() {
        if (size == 0) throw new IllegalStateException("Empty array cannot access index 0.");
        return items[0];
    }

    /** Removes and returns the last item. */
    public int pop() {
        return items[--size];
    }

    /** Returns the last item. */
    public int peek () {
        return items[size - 1];
    }

    public boolean notEmpty () {
        return size > 0;
    }

    public boolean isEmpty () {
        return size == 0;
    }

    public void clear () {
        size = 0;
    }

    /** Reduces the size of the backing array to the size of the actual items. This is useful to release memory when many items
     * have been removed, or if it is known that more items will not be added. */
    public int[] pack() {
        if (items.length != size) resize(size);
        return items;
    }

    private int[] resize(int newSize) {
        int[] newItems = new int[newSize];
        int[] items = this.items;
        System.arraycopy(items, 0, newItems, 0, Math.min(size, newItems.length));
        this.items = newItems;
        return newItems;
    }

    /** Increases the size of the backing array to accommodate the specified number of additional items. Useful before adding many
     * items to avoid multiple backing array resizes.
     * @return {@link #items} */
    public int[] ensureCapacity (int additionalCapacity) {
        if (additionalCapacity < 0) throw new CollectionsException("additionalCapacity must be >= 0: " + additionalCapacity);
        int sizeNeeded = size + additionalCapacity;
        if (sizeNeeded > items.length) resize(Math.max(Math.max(8, sizeNeeded), (int)(size * 1.75f)));
        return items;
    }

    public void sort() {
        Arrays.sort(items);
    }

    public void shuffle() {
        int[] items = this.items;
        for (int i = size - 1; i >= 0; i--) {
            int ii = MathUtils.randomUniformInt(0, i);
            int temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    @Override
    public void reset() {
        clear();
        this.ordered = true;
    }

    @Override
    public int hashCode() {
        if (!ordered) return super.hashCode();
        int[] items = this.items;
        int h = 1;
        if (size > 0) {
            h = h * 31 + items[0];
        }
        return h;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) return true;
        if (!ordered) return false;
        if (!(object instanceof ArrayInt)) return false;
        ArrayInt array = (ArrayInt) object;
        if (!array.ordered) return false;
        int n = size;
        if (n != array.size) return false;
        int[] items1 = this.items, items2 = array.items;
        for (int i = 0; i < n; i++)
            if (items1[i] != items2[i]) return false;
        return true;
    }

    @Override
    public String toString () {
        if (size == 0) return "[]";
        int[] items = this.items;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('[');
        buffer.append(items[0]);
        for (int i = 1; i < size; i++) {
            buffer.append(", ");
            buffer.append(items[i]);
        }
        buffer.append(']');
        return buffer.toString();
    }

}
