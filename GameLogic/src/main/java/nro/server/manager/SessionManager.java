package nro.server.manager;

import lombok.Getter;
import nro.model.player.Player;
import nro.server.network.Session;
import nro.repositories.player.PlayerUpdate;
import nro.server.LogServer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SessionManager {

    @Getter
    private static final SessionManager instance = new SessionManager();

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final ConcurrentHashMap<Integer, Session> sessions = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Session getSessionById(int id) {
        this.lock.readLock().lock();
        try {
            return sessions.get(id);
        } catch (Exception e) {
            LogServer.LogException("Error getSessionById: " + e.getMessage());
        } finally {
            this.lock.readLock().unlock();
        }
        return null;
    }

    public Session getSession(Session session) {
        this.lock.readLock().lock();
        try {
            if (session == null) {
                LogServer.LogException("Error getSession: session null");
                return null;
            }

            Session existingSession = this.sessions.get(session.getSessionInfo().getId());
            if (existingSession != null && existingSession != session) {
                return existingSession;
            }
            return null;

        } catch (Exception e) {
            LogServer.LogException("Error getSession: " + e.getMessage());
            e.printStackTrace();
        } finally {
            this.lock.readLock().unlock();
        }
        return null;
    }

    public void add(Session user) {
        this.lock.writeLock().lock();
        try {
            if (user == null) {
                LogServer.LogException("Error add Client: user null");
                return;
            }
            if (this.sessions.containsKey(user.getSessionInfo().getId())) {// id session da ton tai = remove
                LogServer.DebugLogic("Session da ton tai: " + user.getSessionInfo().getId());
                this.remove(user);
            }
            this.sessions.put(user.getSessionInfo().getId(), user);
        } catch (Exception e) {
            LogServer.LogException("Error add Client: " + e.getMessage());
            e.printStackTrace();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Kiểm tra các session đã inactive trong khoảng thời gian timeout
     * và kick session đó
     * nếu client communicate với server thì sẽ update lại thời gian active
     * <code>currentTime = System.currentTimeMillis();</code>
     * nếu client không giao tiếp gì thì
     * <code>session.getLastActiveTime() = 0</code>
     * currentTime - 0 > timeout => kick session
     */

    public void checkInactiveSessions(long timeout) {
        this.lock.writeLock().lock();
        try {
            long currentTime = System.currentTimeMillis();
            sessions.forEach((userId, session) -> {
                try {
                    var lastActiveTime = currentTime - session.getClientInfo().getLastActiveTime();
//                    System.out.println("LastActiveTime: " + lastActiveTime + " | Timeout: " + timeout);
                    if (currentTime - session.getClientInfo().getLastActiveTime() > timeout) {
//                        this.kickSession(session); // TODO command code
//                        LogServer.DebugLogic("Remove session id: " + userId);
                    }
                } catch (Exception e) {
                    LogServer.LogException("Error khi check session time out: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            LogServer.LogException("Error checkInactiveSessions: " + e.getMessage());
            e.printStackTrace();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private void remove(Session session) {
        this.lock.writeLock().lock();
        try {
            sessions.remove(session.getSessionInfo().getId());
        } catch (Exception e) {
            e.printStackTrace();
            LogServer.LogException("Error remove Client: " + e.getMessage());
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void kickAllPlayer(String status) {
        this.lock.writeLock().lock();
        try {
            LogServer.DebugLogic("Tổng số sessions trước khi kick: " + sessions.size());

            sessions.forEach((userId, session) -> {
                LogServer.DebugLogic(String.format(
                        "KICK SESSION | [UserId: %d] | [LastActiveTime: %d] | [Lý do: %s]",
                        userId,
                        session.getClientInfo().getLastActiveTime(),
                        status
                ));
                this.kickSession(session);
            });

            LogServer.DebugLogic("Tổng số sessions sau khi kick: " + sessions.size());
        } catch (Exception e) {
            LogServer.LogException("Error kickAllPlayersAndSaveForMaintenance: " + e.getMessage());
            e.printStackTrace();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void kickSession(Session session) {
        System.out.println("Kick session: " + session.getSessionInfo().getId());
        this.lock.writeLock().lock();
        try {
            this.dispose(session);// save data player
            this.remove(session);// remove key session
            session.close();// close du lieu session khi da vao game
        } catch (Exception e) {
            LogServer.LogException("Error kickSession: " + e.getMessage());
            e.printStackTrace();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private void dispose(Session session) {
        Player player = session.getPlayer();
        if (player != null) {
            // save data player va giai phong du lieu  khi player da vao game
            player.dispose();
            PlayerUpdate.getInstance().savePlayer(player);
        }
    }

    /**
     * tao 1 luong de check session nao khong ton tai trong list session thi clear
     */

    public void startSessionChecker() {
        this.executor.scheduleAtFixedRate(() -> {
//            this.checkInactiveSessions(300_000); // 5 phút 300_000
            this.checkInactiveSessions(100_000);
        }, 1, 1, TimeUnit.MINUTES);
    }

    public int getSizeSession() {
        return this.sessions.size();
    }

}
