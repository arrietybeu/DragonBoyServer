package nro.commons.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Arriety
 */
public class Bag<E> implements Iterable<E> {

    private E[] data;
    private int size = 0;

    /**
     * Creates a new bag with default initial capacity.
     */
    @SuppressWarnings("unchecked")
    public Bag() {
        this(16);
    }

    /**
     * Creates a new bag with the specified initial capacity.
     *
     * @param capacity the initial capacity
     */
    @SuppressWarnings("unchecked")
    public Bag(int capacity) {
        this.data = (E[]) new Object[capacity];
    }

    /**
     * Gets the element at the specified index.
     *
     * @param index the index
     * @return the element at the index, or null if index >= size
     */
    public E get(int index) {
        if (index >= data.length) {
            return null;
        }
        return data[index];
    }

    /**
     * Sets the element at the specified index.
     * Automatically grows the bag if necessary.
     *
     * @param index   the index
     * @param element the element to set
     */
    public void set(int index, E element) {
        if (index >= data.length) {
            grow(index * 2);
        }

        if (element != null && index >= size) {
            size = index + 1;
        } else if (element == null && index < size) {
            // Check if we need to reduce size
            for (int i = size - 1; i >= 0; i--) {
                if (data[i] != null) {
                    size = i + 1;
                    break;
                }
                if (i == 0) {
                    size = 0;
                }
            }
        }

        data[index] = element;
    }

    /**
     * Adds an element to the end of the bag.
     *
     * @param element the element to add
     */
    public void add(E element) {
        if (size >= data.length) {
            grow();
        }
        data[size++] = element;
    }

    /**
     * Removes and returns the last element.
     *
     * @return the last element, or null if the bag is empty
     */
    public E removeLast() {
        if (size == 0) {
            return null;
        }
        E element = data[--size];
        data[size] = null;
        return element;
    }

    /**
     * Removes the element at the specified index.
     *
     * @param index the index of the element to remove
     * @return the removed element, or null if not found
     */
    public E remove(int index) {
        if (index >= size || index < 0) {
            return null;
        }

        E element = data[index];
        data[index] = data[--size];
        data[size] = null;
        return element;
    }

    /**
     * Removes the first occurrence of the specified element.
     *
     * @param element the element to remove
     * @return true if the element was removed, false otherwise
     */
    public boolean remove(E element) {
        for (int i = 0; i < size; i++) {
            if (element.equals(data[i])) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the bag contains the specified element.
     *
     * @param element the element to check for
     * @return true if the element is found, false otherwise
     */
    public boolean contains(E element) {
        for (int i = 0; i < size; i++) {
            if (element.equals(data[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Ensures the bag can hold at least the specified capacity.
     *
     * @param capacity the minimum capacity
     */
    public void ensureCapacity(int capacity) {
        if (capacity > data.length) {
            grow(capacity);
        }
    }

    /**
     * Gets the number of elements in the bag.
     *
     * @return the size
     */
    public int size() {
        return size;
    }

    /**
     * Gets the current capacity of the bag.
     *
     * @return the capacity
     */
    public int capacity() {
        return data.length;
    }

    /**
     * Checks if the bag is empty.
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Clears all elements from the bag.
     */
    public void clear() {
        Arrays.fill(data, 0, size, null);
        size = 0;
    }

    /**
     * Grows the bag to the default new size (double current capacity).
     */
    private void grow() {
        grow(data.length * 2);
    }

    /**
     * Grows the bag to the specified capacity.
     *
     * @param newCapacity the new capacity
     */
    @SuppressWarnings("unchecked")
    private void grow(int newCapacity) {
        E[] newData = (E[]) new Object[newCapacity];
        System.arraycopy(data, 0, newData, 0, data.length);
        data = newData;
    }

    @Override
    public Iterator<E> iterator() {
        return new BagIterator();
    }

    private class BagIterator implements Iterator<E> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return data[index++];
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Bag{size=").append(size).append(", elements=[");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(data[i]);
        }
        sb.append("]}");
        return sb.toString();
    }
}