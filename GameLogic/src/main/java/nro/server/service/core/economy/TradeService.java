package nro.server.service.core.economy;

import lombok.Getter;
import nro.consts.ConstTrade;
import nro.consts.ConstsCmd;
import nro.server.network.Message;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.item.Item;
import nro.server.system.LogServer;

import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TradeService {

    @Getter
    private static final TradeService instance = new TradeService();

    @Getter
    private final Map<Integer, TradeSession> tradeSessions = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void sendTransactionToType(Player player, int type) {
        try (Message message = new Message(ConstsCmd.GIAO_DICH)) {
            DataOutputStream writer = message.writer();
            writer.writeByte(type);
            writer.writeInt(player.getId());
            player.sendMessage(message);
        } catch (Exception exception) {
            LogServer.LogException("Error sendTransactionToType: " + exception.getMessage(), exception);
        }
    }

    public boolean requestTrade(Player p1, Player p2) {
        this.lock.writeLock().lock();
        try {
            if (p1.equals(p2)) return false;
//            if (tradeSessions.containsKey(p1.getId()) || tradeSessions.containsKey(p2.getId())) return false;

            var session = new TradeSession(p1, p2);
            tradeSessions.put(p1.getId(), session);
            tradeSessions.put(p2.getId(), session);

            this.sendTransactionToType(p1, ConstTrade.TRANSACTION_REQUEST);
            this.sendTransactionToType(p2, ConstTrade.TRANSACTION_REQUEST);

            return true;
        } catch (Exception e) {
            LogServer.LogException("Error request trade: " + e.getMessage(), e);
        } finally {
            this.lock.writeLock().unlock();
        }
        return false;
    }

    public TradeSession getSession(Player player) {
        this.lock.readLock().lock();
        try {
            return tradeSessions.get(player.getId());
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void cancelTrade(Player player) {
        this.lock.writeLock().lock();
        try {
            TradeSession session = tradeSessions.remove(player.getId());
            if (session != null) {
                tradeSessions.remove(session.getOpponent(player).getId());
                session.reset();
            }
        } catch (Exception exception) {
            LogServer.LogException("Error cancel trade: " + exception.getMessage(), exception);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void lockTrade(Player player) {
        this.lock.writeLock().lock();
        try {
            TradeSession session = getSession(player);
            if (session != null) {
                session.lock(player);
                if (session.isBothLocked()) {
                    completeTrade(session);
                }
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private void completeTrade(TradeSession session) {
        try {
            Player p1 = session.getPlayer1();
            Player p2 = session.getPlayer2();

            // kiểm tra xem cả 2 người chơi có đủ chỗ trong túi không
            if (p1.getPlayerInventory().isBagFull() || p2.getPlayerInventory().isBagFull()) {
                cancelTrade(p1);
                return;
            }

            for (Item item : session.getOfferPlayer1()) {
                p2.getPlayerInventory().addItemBag(item);
            }
            for (Item item : session.getOfferPlayer2()) {
                p1.getPlayerInventory().addItemBag(item);
            }

            // Kết thúc giao dịch
            cancelTrade(p1);
        } catch (Exception exception) {
            LogServer.LogException("Error complete trade: " + exception.getMessage(), exception);
        }
    }


}
