package nro.server.service.core.economy;

import lombok.Getter;
import nro.consts.ConstTrade;
import nro.consts.ConstsCmd;
import nro.server.network.Message;
import nro.server.realtime.system.player.TradeSystem;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.item.Item;
import nro.server.system.LogServer;

import java.io.DataOutputStream;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TradeService {

    @Getter
    private static final TradeService instance = new TradeService();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Gửi thông điệp giao dịch đến người chơi
     *
     * @param player  người chơi gửi
     * @param opponent người chơi nhận
     * @param type    loại giao dịch
     */
    public void sendTransactionToType(Player player, Player opponent, int type) {
        try (Message message = new Message(ConstsCmd.GIAO_DICH)) {
            DataOutputStream writer = message.writer();
            writer.writeByte(type);
            writer.writeInt(player.getId());
            opponent.sendMessage(message);
        } catch (Exception exception) {
            LogServer.LogException("Error sendTransactionToType: " + exception.getMessage(), exception);
        }
    }

    /**
     *
     * @param p1 player gửi yêu cầu
     * @param p2 player nhận yêu cầu
     * @return true nếu thành công
     */
    public boolean requestTrade(Player p1, Player p2) {
        this.lock.writeLock().lock();
        try {
            if (p1.equals(p2)) return false;
//            if (tradeSessions.containsKey(p1.getId()) || tradeSessions.containsKey(p2.getId())) return false;

            var tradeSessions = TradeSystem.getInstance().getTradeSessions();
            var session = new TradeSession(p1, p2);
            tradeSessions.put(p1.getId(), session);
            tradeSessions.put(p2.getId(), session);

            this.sendTransactionToType(p1, p2, ConstTrade.TRANSACTION_REQUEST);

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
            return TradeSystem.getInstance().getTradeSessions().get(player.getId());
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void cancelTrade(Player player) {
        this.lock.writeLock().lock();
        try {
            var tradeSessions = TradeSystem.getInstance().getTradeSessions();

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
            TradeSession session = this.getSession(player);
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
