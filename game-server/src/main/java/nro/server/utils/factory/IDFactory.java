package nro.server.utils.factory;

import nro.server.dao.PlayerDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @Author Arriety
 */
public class IDFactory {

    private static final Logger log = LoggerFactory.getLogger(IDFactory.class);

    private static final int INVALID_ID_BIT_MASK = 0b0010011111100110101111111111100;
    private static final int INVALID_ID_BITCHECK = 0b0000000000000000001100101010100; // 6484

    private final BitSet idList = new BitSet();
    private final ReentrantLock lock = new ReentrantLock();
    private volatile int nextMinId = 1;

    private final BitSet negativeIdList = new BitSet();
    private final ReentrantLock negativeLock = new ReentrantLock();
    private volatile int nextMinNegativeIndex = 1;

    private IDFactory() {
        lockIds(0);
        initializeUsedIds();
        log.info("IDFactory: {} IDs used.", getUsedCount());
    }

    private void initializeUsedIds() {
        lockIds(Objects.requireNonNull(PlayerDAO.getUsedIDs()));
    }

    private static void ______________QUAN_LY_SO_DUONG___________________() {
        // các phương thức bên dưới sẽ quản lý các số ID dương
    }

    private void lockIds(int... ids) {
        for (int id : ids) {
            if (id < 0)
                throw new IDFactoryError("ID " + id + " id này phải là số dương, fatal error!!!");
            if (idList.get(id))
                throw new IDFactoryError("ID " + id + " is already taken, fatal error!!!");
            idList.set(id);
        }
    }

    public int getUsedCount() {
        lock.lock();
        try {
            return idList.cardinality();
        } finally {
            lock.unlock();
        }
    }

    public int nextId() {
        lock.lock();
        try {
            int id = nextValidId(nextMinId);
            idList.set(id);
            nextMinId = id + 1;
            return id;
        } finally {
            lock.unlock();
        }
    }

    private int nextValidId(int searchIndex) {
        while (true) {
            int id = idList.nextClearBit(searchIndex);
            if (id < 0)
                throw new IDFactoryError("All IDs are used, please clear your database");

            if (!isInvalidId(id))
                return id;

            searchIndex = id + 1;
        }
    }

    private boolean isInvalidId(int id) {
        return (id & INVALID_ID_BIT_MASK) == INVALID_ID_BITCHECK;
    }

    public void releaseId(int id) {
        if (id < 0) throw new IDFactoryError("All IDs are used, please clear your database");
        lock.lock();
        try {
            if (!release(id)) {
                log.warn("Couldn't release ID {} because it wasn't taken", id, new IllegalArgumentException());
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean release(int id) {
        if (!idList.get(id))
            return false;
        idList.clear(id);
        if (id < nextMinId || nextMinId == Integer.MIN_VALUE)
            nextMinId = id;
        return true;
    }

    private static void ______________QUAN_LY_SO_AM___________________() {
        // các phương thức bên dưới sẽ quản lý các số ID âm
    }

    /**
     * Khóa các ID âm đã tồn tại (ví dụ: từ database của Boss).
     *
     * @param ids Mảng các ID âm cần khóa.
     */
    private void lockNegativeIds(int... ids) {
        for (int id : ids) {
            if (id >= 0) continue;
            int index = -id;
            if (negativeIdList.get(index)) {
                throw new IDFactoryError("Negative ID " + id + " (index " + index + ") is already taken!");
            }
            negativeIdList.set(index);
        }
    }

    /**
     * Lấy một ID ÂM duy nhất cho các đối tượng tạm thời (Boss, Pet...).
     *
     * @return ID âm duy nhất.
     */
    public int nextTemporaryId() {
        negativeLock.lock();
        try {
            int index = negativeIdList.nextClearBit(nextMinNegativeIndex);
            if (index < 0) {
                throw new IDFactoryError("All temporary (negative) IDs are used.");
            }
            negativeIdList.set(index);
            nextMinNegativeIndex = index + 1;
            return -index;
        } finally {
            negativeLock.unlock();
        }
    }

    /**
     * Thu hồi một ID ÂM để có thể tái sử dụng.
     *
     * @param id ID âm cần thu hồi (ví dụ: ID của Mini Pet khi người chơi thoát game).
     */
    public void releaseTemporaryId(int id) {
        if (id >= 0) return;
        int index = -id;
        negativeLock.lock();
        try {
            if (negativeIdList.get(index)) {
                negativeIdList.clear(index);
                if (index < nextMinNegativeIndex) {
                    nextMinNegativeIndex = index;
                }
            } else {
                log.warn("Couldn't release temporary ID {} (index {}) because it wasn't taken", id, index);
            }
        } finally {
            negativeLock.unlock();
        }
    }

    /**
     * Lấy số lượng ID âm đang được sử dụng.
     *
     * @return Số lượng ID âm đã dùng.
     */
    public int getUsedNegativeCount() {
        negativeLock.lock();
        try {
            return negativeIdList.cardinality();
        } finally {
            negativeLock.unlock();
        }
    }

    public String getDebugInfo(int limit) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n===== IDFactory Debug Report =====");

        lock.lock();
        try {
            sb.append("\n[+] Positive IDs | Total Used: ").append(getUsedCount());
            List<Integer> usedPositiveIds = idList.stream()
                    .limit(limit)
                    .boxed()
                    .collect(Collectors.toList());
            sb.append("\n    First ").append(usedPositiveIds.size()).append(" IDs in use: ").append(usedPositiveIds);
        } finally {
            lock.unlock();
        }

        negativeLock.lock();
        try {
            sb.append("\n[-] Negative IDs | Total Used: ").append(getUsedNegativeCount());
            List<Integer> usedNegativeIds = negativeIdList.stream()
                    .limit(limit)
                    .map(index -> -index)
                    .boxed()
                    .collect(Collectors.toList());
            sb.append("\n    First ").append(usedNegativeIds.size()).append(" IDs in use: ").append(usedNegativeIds);
        } finally {
            negativeLock.unlock();
        }

        sb.append("\n====================================");
        return sb.toString();
    }


    public static IDFactory getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final IDFactory instance = new IDFactory();
    }

}
