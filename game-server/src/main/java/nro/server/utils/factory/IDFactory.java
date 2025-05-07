package nro.server.utils.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.BitSet;
import java.util.concurrent.locks.ReentrantLock;

public class IDFactory {

    private static final Logger log = LoggerFactory.getLogger(IDFactory.class);

    private static final int INVALID_ID_BIT_MASK = 0b0010011111100110101111111111100;
    private static final int INVALID_ID_BITCHECK = 0b0000000000000000001100101010100; // 6484


    private final BitSet idList = new BitSet();

    private final ReentrantLock lock = new ReentrantLock();

    private volatile int nextMinId = 1;

    private IDFactory() {
        lockIds(0);
        initializeUsedIds();
        log.info("IDFactory: {} IDs used.", getUsedCount());
    }

    private void initializeUsedIds() {
//        lockIds(Objects.requireNonNull(PlayerDAO.getUsedIDs()));
//        lockIds(Objects.requireNonNull(InventoryDAO.getUsedIDs()));
//        lockIds(Objects.requireNonNull(PlayerRegisteredItemsDAO.getUsedIDs()));
//        lockIds(Objects.requireNonNull(LegionDAO.getUsedIDs()));
//        lockIds(Objects.requireNonNull(MailDAO.getUsedIDs()));
//        lockIds(Objects.requireNonNull(GuideDAO.getUsedIDs()));
//        lockIds(Objects.requireNonNull(HousesDAO.getUsedIDs()));
//        lockIds(Objects.requireNonNull(PlayerPetsDAO.getUsedIDs()));
    }

    private void lockIds(int... ids) {
        for (int id : ids) {
            if (idList.get(id)) {
                throw new IDFactoryError("ID " + id + " is already taken, fatal error!!!");
            }
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
        int id = idList.nextClearBit(searchIndex);
        if (id == Integer.MIN_VALUE) {
            throw new IDFactoryError("All IDs are used, please clear your database");
        }
        return isInvalidId(id) ? nextValidId(id + 1) : id;
    }

    private boolean isInvalidId(int id) {
        return (id & INVALID_ID_BIT_MASK) == INVALID_ID_BITCHECK;
    }

    public void releaseId(int id) {
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

    public static IDFactory getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final IDFactory instance = new IDFactory();
    }

}
