package nro.commons.utils;

import java.util.Arrays;

/**
 * @author Arriety
 */
public class BitVector {

    private static final int BITS_PER_WORD = 64;
    private static final int WORD_SHIFT = 6; // log2(64)
    private static final long ALL_ONES = -1L;

    private long[] words;
    private int size;

    /**
     * Creates a new bit vector with default initial capacity.
     */
    public BitVector() {
        this(64);
    }

    /**
     * Creates a new bit vector with the specified initial capacity.
     * @param capacity the initial capacity in bits
     */
    public BitVector(int capacity) {
        words = new long[wordIndex(capacity) + 1];
        size = 0;
    }

    /**
     * Sets the bit at the specified index to true.
     * @param index the bit index
     */
    public void set(int index) {
        ensureCapacity(index);
        words[wordIndex(index)] |= (1L << index);
        if (index >= size) {
            size = index + 1;
        }
    }

    /**
     * Sets the bit at the specified index to the specified value.
     * @param index the bit index
     * @param value the value to set
     */
    public void set(int index, boolean value) {
        if (value) {
            set(index);
        } else {
            clear(index);
        }
    }

    /**
     * Sets the bit at the specified index to false.
     * @param index the bit index
     */
    public void clear(int index) {
        if (index >= size) {
            return;
        }
        words[wordIndex(index)] &= ~(1L << index);
        updateSize();
    }

    /**
     * Sets all bits to false.
     */
    public void clear() {
        Arrays.fill(words, 0L);
        size = 0;
    }

    /**
     * Gets the value of the bit at the specified index.
     * @param index the bit index
     * @return true if the bit is set, false otherwise
     */
    public boolean get(int index) {
        if (index >= size) {
            return false;
        }
        return (words[wordIndex(index)] & (1L << index)) != 0;
    }

    /**
     * Performs a logical AND operation with another bit vector.
     * @param other the other bit vector
     * @return a new bit vector containing the result
     */
    public BitVector and(BitVector other) {
        BitVector result = new BitVector();
        int minWords = Math.min(words.length, other.words.length);
        result.ensureCapacity(Math.min(size, other.size));

        for (int i = 0; i < minWords; i++) {
            result.words[i] = words[i] & other.words[i];
        }

        result.updateSize();
        return result;
    }

    /**
     * Performs a logical OR operation with another bit vector.
     * @param other the other bit vector
     * @return a new bit vector containing the result
     */
    public BitVector or(BitVector other) {
        BitVector result = new BitVector();
        int maxWords = Math.max(words.length, other.words.length);
        result.ensureCapacity(Math.max(size, other.size));

        for (int i = 0; i < maxWords; i++) {
            long thisWord = i < words.length ? words[i] : 0L;
            long otherWord = i < other.words.length ? other.words[i] : 0L;
            result.words[i] = thisWord | otherWord;
        }

        result.updateSize();
        return result;
    }

    /**
     * Checks if this bit vector intersects with another bit vector.
     * @param other the other bit vector
     * @return true if they have any bits in common, false otherwise
     */
    public boolean intersects(BitVector other) {
        int minWords = Math.min(words.length, other.words.length);
        for (int i = 0; i < minWords; i++) {
            if ((words[i] & other.words[i]) != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this bit vector contains all bits set in another bit vector.
     * @param other the other bit vector
     * @return true if this contains all bits from other, false otherwise
     */
    public boolean containsAll(BitVector other) {
        for (int i = 0; i < other.words.length; i++) {
            long otherWord = other.words[i];
            long thisWord = i < words.length ? words[i] : 0L;
            if ((thisWord & otherWord) != otherWord) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if this bit vector is empty (no bits set).
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Gets the number of bits currently tracked by this vector.
     * @return the size
     */
    public int size() {
        return size;
    }

    /**
     * Gets the number of bits set to true.
     * @return the cardinality
     */
    public int cardinality() {
        int count = 0;
        for (int i = 0; i < words.length; i++) {
            count += Long.bitCount(words[i]);
        }
        return count;
    }

    /**
     * Gets the index of the next set bit starting from the specified index.
     * @param fromIndex the index to start searching from
     * @return the index of the next set bit, or -1 if none found
     */
    public int nextSetBit(int fromIndex) {
        if (fromIndex >= size) {
            return -1;
        }

        int wordIndex = wordIndex(fromIndex);
        long word = words[wordIndex] & (ALL_ONES << fromIndex);

        while (true) {
            if (word != 0) {
                return (wordIndex * BITS_PER_WORD) + Long.numberOfTrailingZeros(word);
            }
            if (++wordIndex >= words.length) {
                return -1;
            }
            word = words[wordIndex];
        }
    }

    /**
     * Ensures the bit vector can hold at least the specified bit index.
     * @param bitIndex the bit index
     */
    private void ensureCapacity(int bitIndex) {
        int requiredWords = wordIndex(bitIndex) + 1;
        if (requiredWords > words.length) {
            words = Arrays.copyOf(words, Math.max(requiredWords, words.length * 2));
        }
    }

    /**
     * Updates the size to reflect the highest set bit.
     */
    private void updateSize() {
        int newSize = 0;
        for (int i = words.length - 1; i >= 0; i--) {
            if (words[i] != 0) {
                newSize = (i * BITS_PER_WORD) + (BITS_PER_WORD - Long.numberOfLeadingZeros(words[i]));
                break;
            }
        }
        size = newSize;
    }

    /**
     * Gets the word index for the specified bit index.
     * @param bitIndex the bit index
     * @return the word index
     */
    private static int wordIndex(int bitIndex) {
        return bitIndex >> WORD_SHIFT;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        BitVector other = (BitVector) obj;
        int maxWords = Math.max(words.length, other.words.length);

        for (int i = 0; i < maxWords; i++) {
            long thisWord = i < words.length ? words[i] : 0L;
            long otherWord = i < other.words.length ? other.words[i] : 0L;
            if (thisWord != otherWord) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(words);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BitVector{bits=");
        for (int i = 0; i < size; i++) {
            sb.append(get(i) ? '1' : '0');
        }
        sb.append(", cardinality=").append(cardinality()).append("}");
        return sb.toString();
    }
}