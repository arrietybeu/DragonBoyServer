package nro.commons.utils;

/**
 * @author Arriety
 */

/**
 * Một hàng đợi hai đầu (deque) có thể thay đổi kích thước của các số nguyên được tối ưu hóa cho các hoạt động ECS.
 * Cung cấp các thao tác thêm/xóa hiệu quả ở cả hai đầu.
 *
 * @author Arriety
 */
public class IntDeque {

    private int[] elements;
    private int head = 0;
    private int tail = 0;
    private int size = 0;

    /**
     * Tạo một deque mới với dung lượng ban đầu mặc định.
     */
    public IntDeque() {
        this(16);
    }

    /**
     * Tạo một deque mới với dung lượng ban đầu được chỉ định.
     *
     * @param capacity dung lượng ban đầu
     */
    public IntDeque(int capacity) {
        elements = new int[capacity];
    }

    /**
     * Thêm một phần tử vào đầu deque.
     *
     * @param element phần tử cần thêm
     */
    public void addFirst(int element) {
        if (size == elements.length) {
            grow();
        }

        head = (head - 1 + elements.length) % elements.length;
        elements[head] = element;
        size++;
    }

    /**
     * Thêm một phần tử vào cuối deque.
     *
     * @param element phần tử cần thêm
     */
    public void addLast(int element) {
        if (size == elements.length) {
            grow();
        }

        elements[tail] = element;
        tail = (tail + 1) % elements.length;
        size++;
    }

    /**
     * Xóa và trả về phần tử đầu tiên.
     *
     * @return phần tử đầu tiên
     * @throws IllegalStateException nếu deque rỗng
     */
    public int removeFirst() {
        if (size == 0) {
            throw new IllegalStateException("Deque rỗng");
        }

        int element = elements[head];
        head = (head + 1) % elements.length;
        size--;
        return element;
    }

    /**
     * Xóa và trả về phần tử cuối cùng.
     *
     * @return phần tử cuối cùng
     * @throws IllegalStateException nếu deque rỗng
     */
    public int removeLast() {
        if (size == 0) {
            throw new IllegalStateException("Deque rỗng");
        }

        tail = (tail - 1 + elements.length) % elements.length;
        int element = elements[tail];
        size--;
        return element;
    }

    /**
     * Lấy phần tử đầu tiên mà không xóa nó.
     *
     * @return phần tử đầu tiên
     * @throws IllegalStateException nếu deque rỗng
     */
    public int peekFirst() {
        if (size == 0) {
            throw new IllegalStateException("Deque rỗng");
        }
        return elements[head];
    }

    /**
     * Lấy phần tử cuối cùng mà không xóa nó.
     *
     * @return phần tử cuối cùng
     * @throws IllegalStateException nếu deque rỗng
     */
    public int peekLast() {
        if (size == 0) {
            throw new IllegalStateException("Deque rỗng");
        }
        return elements[(tail - 1 + elements.length) % elements.length];
    }

    /**
     * Xóa lần xuất hiện đầu tiên của phần tử được chỉ định.
     *
     * @param element phần tử cần xóa
     * @return true nếu phần tử đã được xóa, false nếu không
     */
    public boolean remove(int element) {
        for (int i = 0; i < size; i++) {
            int index = (head + i) % elements.length;
            if (elements[index] == element) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Kiểm tra xem deque có chứa phần tử được chỉ định không.
     *
     * @param element phần tử cần kiểm tra
     * @return true nếu tìm thấy phần tử, false nếu không
     */
    public boolean contains(int element) {
        for (int i = 0; i < size; i++) {
            int index = (head + i) % elements.length;
            if (elements[index] == element) {
                return true;
            }
        }
        return false;
    }

    /**
     * Lấy số lượng phần tử trong deque.
     *
     * @return kích thước
     */
    public int size() {
        return size;
    }

    /**
     * Kiểm tra xem deque có rỗng không.
     *
     * @return true nếu rỗng, false nếu không
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Xóa tất cả các phần tử khỏi deque.
     */
    public void clear() {
        head = 0;
        tail = 0;
        size = 0;
    }

    /**
     * Chuyển đổi deque thành một mảng.
     *
     * @return một mảng chứa tất cả các phần tử theo thứ tự
     */
    public int[] toArray() {
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = elements[(head + i) % elements.length];
        }
        return result;
    }

    /**
     * Xóa phần tử tại vị trí được chỉ định (tương đối so với head).
     *
     * @param position vị trí cần xóa
     */
    private void removeAt(int position) {
        if (position < 0 || position >= size) {
            throw new IndexOutOfBoundsException();
        }

        if (position < size / 2) {
            // Dịch chuyển các phần tử từ đầu
            for (int i = position; i > 0; i--) {
                int fromIndex = (head + i - 1) % elements.length;
                int toIndex = (head + i) % elements.length;
                elements[toIndex] = elements[fromIndex];
            }
            head = (head + 1) % elements.length;
        } else {
            // Dịch chuyển các phần tử từ cuối
            for (int i = position; i < size - 1; i++) {
                int fromIndex = (head + i + 1) % elements.length;
                int toIndex = (head + i) % elements.length;
                elements[toIndex] = elements[fromIndex];
            }
            tail = (tail - 1 + elements.length) % elements.length;
        }
        size--;
    }

    /**
     * Tăng dung lượng của deque.
     */
    private void grow() {
        int[] newElements = new int[elements.length * 2];
        for (int i = 0; i < size; i++) {
            newElements[i] = elements[(head + i) % elements.length];
        }
        elements = newElements;
        head = 0;
        tail = size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IntDeque{size=").append(size).append(", elements=[");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(elements[(head + i) % elements.length]);
        }
        sb.append("]}");
        return sb.toString();
    }
}