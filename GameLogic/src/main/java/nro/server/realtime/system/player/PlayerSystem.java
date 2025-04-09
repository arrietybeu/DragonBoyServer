package nro.server.realtime.system.player;

import lombok.Getter;
import nro.server.realtime.core.ISystemBase;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Getter
public class PlayerSystem implements ISystemBase {

    @Getter
    private static final PlayerSystem instance = new PlayerSystem();
    private final List<Player> players = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void register(Object object) {
        if (object instanceof Player player) {
            lock.writeLock().lock();
            try {
                if (players.contains(player)) {
                    LogServer.LogException(player.getName() + " is already registered!");
                    return;
                }
                players.add(player);
            } catch (Exception exception) {
                LogServer.LogException("PlayerSystem error for player: " + player.getId(), exception);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public void unregister(Object object) {
        if ((object instanceof Player player)) {
            lock.writeLock().lock();
            try {
                if (!players.contains(player)) {
                    LogServer.LogException(player.getName() + " is not registered!");
                    return;
                }
                players.remove(player);
            } catch (Exception exception) {
                LogServer.LogException("PlayerSystem error for player: " + player.getId(), exception);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public void removeAll() {
        this.players.clear();
    }

    @Override
    public void update() {
        lock.readLock().lock();
        try {
            for (Player player : players) {
                try {
                    if (player.getPlayerMagicTree() != null) {
                        player.getPlayerMagicTree().update();
                    }
                } catch (Exception e) {
                    LogServer.LogException("PlayerSystem error for player: " + player.getId(), e);
                }
            }
        } catch (Exception e) {
            LogServer.LogException("PlayerSystem update error", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        return players.size();
    }

}