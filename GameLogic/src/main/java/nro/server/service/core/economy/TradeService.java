package nro.server.service.core.economy;

import nro.consts.ConstTrade;
import nro.consts.ConstsCmd;
import nro.server.network.Message;
import nro.server.realtime.system.player.TradeSystem;
import nro.server.service.core.item.ItemFactory;
import nro.server.service.core.system.ServerService;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.entity.player.PlayerInventory;
import nro.server.service.model.item.Item;
import nro.server.system.LogServer;

import java.io.DataOutputStream;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TradeService {

    private static final class SingletonHolder {
        private static final TradeService instance = new TradeService();
    }

    public static TradeService getInstance() {
        return TradeService.SingletonHolder.instance;
    }

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Gửi thông điệp giao dịch đến người chơi
     *
     * @param player   người chơi gửi
     * @param opponent người chơi nhận
     * @param type     type transaction
     */
    public void sendTransactionToPlayerFocus(Player player, Player opponent, int type) {
        try (Message message = new Message(ConstsCmd.GIAO_DICH)) {
            DataOutputStream writer = message.writer();
            writer.writeByte(type);
            writer.writeInt(player.getId());
            opponent.sendMessage(message);
        } catch (Exception exception) {
            LogServer.LogException("Error sendTransactionToType: " + exception.getMessage(), exception);
        }
    }

    public void sendCloseTransaction(Player player) {
        try (Message message = new Message(ConstsCmd.GIAO_DICH)) {
            DataOutputStream writer = message.writer();
            writer.writeByte(ConstTrade.SUSSCESS_TRADE);
            player.sendMessage(message);
        } catch (Exception exception) {
            LogServer.LogException("Error sendTransactionToType: " + exception.getMessage(), exception);
        }
    }

    /**
     * @param p1 player gửi yêu cầu
     * @param p2 player nhận yêu cầu
     * @return true nếu thành công
     */
    public boolean requestTrade(Player p1, Player p2) {
        try {
            if (p1.equals(p2)) return false;
            if (p1.getPlayerState().getIdTrade() > 0 || p2.getPlayerState().getIdTrade() > 0) return false;
            this.sendTransactionToPlayerFocus(p1, p2, ConstTrade.TRANSACTION_REQUEST);
            return true;
        } catch (Exception e) {
            LogServer.LogException("Error request trade: " + e.getMessage(), e);
        }
        return false;
    }

    public void acceptTrade(Player p1, Player p2) {
        this.lock.writeLock().lock();
        try {
            if (p1 == null || p2 == null) return;

            TradeSystem tradeSystem = TradeSystem.getInstance();
            int tradeId = tradeSystem.increaseIdTrade();

            TradeSession session = new TradeSession(tradeId, p1, p2);
            session.setCreateTime(System.currentTimeMillis());
            tradeSystem.register(session);

            this.sendTransactionToPlayerFocus(p1, p2, ConstTrade.TRANSACTION_ACCEPT);
            this.sendTransactionToPlayerFocus(p2, p1, ConstTrade.TRANSACTION_ACCEPT);
        } catch (Exception exception) {
            LogServer.LogException("Error accept trade: " + exception.getMessage(), exception);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void cancelAddItem(Player player, int index) {
        try (Message message = new Message(ConstsCmd.GIAO_DICH)) {
            DataOutputStream writer = message.writer();
            writer.writeByte(ConstTrade.SELECT_ITEM);
            writer.writeByte(index);
            player.sendMessage(message);
        } catch (Exception exception) {
            LogServer.LogException("Error sendTransactionToType: " + exception.getMessage(), exception);
        }
    }

    public void sendLockTransaction(Player player, int gold, List<Item> itemsTrade) {
        try (Message message = new Message(ConstsCmd.GIAO_DICH)) {
            DataOutputStream writer = message.writer();
            writer.writeByte(ConstTrade.DONE_TRADE);
            writer.writeInt(gold);
            writer.writeByte(itemsTrade.size());
            for (Item item : itemsTrade) {
                if (item.getTemplate() == null) {
                    writer.writeShort(-1);
                    continue;
                }
                writer.writeShort(item.getTemplate().id());
                writer.writeInt(item.getQuantity());
                item.writeDataOptions(writer);
            }
            player.sendMessage(message);
        } catch (Exception exception) {
            LogServer.LogException("Error sendTransactionToType: " + exception.getMessage(), exception);
        }
    }

    public void addItemToTrade(Player player, int index, int quantity) {
        this.lock.writeLock().lock();
        try {
            TradeSession session = this.getSession(player);
            if (session != null) {
                Item original = player.getPlayerInventory().getItemTrade(index, quantity);
                if (original != null) {
                    Item copy = ItemFactory.getInstance().clone(original);
                    if (!session.addItem(player, copy)) {
                        this.cancelAddItem(player, index);
                    }
                } else {
                    this.cancelAddItem(player, index);
                }
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void addGoldToTrade(Player player, int gold) {
        this.lock.writeLock().lock();
        try {
            TradeSession session = this.getSession(player);
            if (session != null) {
                session.addGold(player, gold);
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public TradeSession getSession(Player player) {
        this.lock.readLock().lock();
        try {
            int tradeId = player.getPlayerState().getIdTrade();
            if (tradeId < 0) return null;
            return TradeSystem.getInstance().getTradeSessions().get(tradeId);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void cancelTrade(Player player) {
        this.lock.writeLock().lock();
        try {
            TradeSystem tradeSystem = TradeSystem.getInstance();
            int tradeId = player.getPlayerState().getIdTrade();
            if (tradeId < 0) return;

            TradeSession session = tradeSystem.getTradeSessions().remove(tradeId);
            if (session != null) {
                Player p1 = session.getPlayer1();
                Player p2 = session.getPlayer2();
                ServerService service = ServerService.getInstance();
                if (p1 != null) {
                    this.sendCloseTransaction(p1);
                    service.sendChatGlobal(p1.getSession(), null, "Giao dịch bị hủy bỏ", false);
                }

                if (p2 != null) {
                    this.sendCloseTransaction(p2);
                    service.sendChatGlobal(p2.getSession(), null, "Giao dịch bị hủy bỏ", false);
                }
                session.reset();
            }
        } catch (Exception exception) {
            LogServer.LogException("Error cancel trade: " + exception.getMessage(), exception);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void cancelTradeBySession(TradeSession session) {
        this.lock.writeLock().lock();
        try {
            Player p1 = session.getPlayer1();
            Player p2 = session.getPlayer2();
            ServerService service = ServerService.getInstance();

            if (p1 != null) {
                this.sendCloseTransaction(p1);
                service.sendChatGlobal(p1.getSession(), null, "Giao dịch bị hủy bỏ", false);
            }
            if (p2 != null) {
                this.sendCloseTransaction(p2);
                service.sendChatGlobal(p2.getSession(), null, "Giao dịch bị hủy bỏ", false);
            }
            session.reset();
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
                Player player1 = session.getPlayer1();
                Player player2 = session.getPlayer2();
                if (player1 == null || player2 == null) return;
                if (player.equals(player1)) {
                    this.sendLockTransaction(player2, session.getGoldPlayer1(), session.getOfferPlayer1());
                } else if (player.equals(player2)) {
                    this.sendLockTransaction(player1, session.getGoldPlayer2(), session.getOfferPlayer2());
                }
                session.lock(player);
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void doneTrade(Player player) {
        this.lock.writeLock().lock();
        try {
            TradeSession session = this.getSession(player);
            if (session != null) {
                session.done(player);
                if (session.isBothDone()) {
                    this.completeTrade(session);
                }
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private void completeTrade(TradeSession session) throws RuntimeException {
        try {
            Player p1 = session.getPlayer1();
            Player p2 = session.getPlayer2();
            if (p1 == null || p2 == null) return;

            if (p1.getPlayerInventory().isBagFull() || p2.getPlayerInventory().isBagFull()) {
                String cancelMessage = "Túi đồ đã đầy, không thể giao dịch.";
                ServerService.getInstance().sendChatGlobal(p1.getSession(), null, cancelMessage, false);
                ServerService.getInstance().sendChatGlobal(p2.getSession(), null, cancelMessage, false);
                this.cancelTrade(p1);
                return;
            }

            for (Item item : session.getOfferPlayer1()) {

                Item itemClone = ItemFactory.getInstance().clone(item);

                checkValidateCompleteTrade(p1, p2, item, p1.getPlayerInventory(), p2.getPlayerInventory(), itemClone);
            }

            for (Item item : session.getOfferPlayer2()) {

                Item itemClone = ItemFactory.getInstance().clone(item);

                checkValidateCompleteTrade(p1, p2, item, p2.getPlayerInventory(), p1.getPlayerInventory(), itemClone);
            }

            this.sendDoneTrade(p1);
        } catch (Exception exception) {
            LogServer.LogException("Error complete trade: " + exception.getMessage(), exception);
        }
    }

    private void sendDoneTrade(Player player) {
        this.lock.writeLock().lock();
        try {
            TradeSystem tradeSystem = TradeSystem.getInstance();
            int tradeId = player.getPlayerState().getIdTrade();
            if (tradeId < 0) return;

            TradeSession session = tradeSystem.getTradeSessions().remove(tradeId);
            if (session != null) {
                Player p1 = session.getPlayer1();
                Player p2 = session.getPlayer2();
                ServerService service = ServerService.getInstance();
                if (p1 != null) {
                    this.sendCloseTransaction(p1);
                    service.sendChatGlobal(p1.getSession(), null, "Giao dịch thành công", false);
                }

                if (p2 != null) {
                    this.sendCloseTransaction(p2);
                    service.sendChatGlobal(p2.getSession(), null, "Giao dịch thành công", false);
                }
                session.reset();
            }
        } catch (Exception exception) {
            LogServer.LogException("Error cancel trade: " + exception.getMessage(), exception);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private void checkValidateCompleteTrade(Player p1, Player p2, Item item, PlayerInventory senderInv, PlayerInventory receiverInv, Item cloneForReceiver) {
        if (item.getTemplate() == null) {
            throw new RuntimeException("TradeService: completeTrade: item template null for player 1: " + p1.getName() + " player 2: " + p2.getName());
        }
        if (item.getQuantity() <= 0 || item.getQuantity() > 99) {
            throw new RuntimeException("TradeService: completeTrade: item quantity invalid: " + item.getTemplate().id());
        }

//        if (!senderInv.hasItemQuantity(item)) {
//            throw new RuntimeException("Trade failed: player no longer has item");
//        }

        senderInv.subQuantityItemsBag(item, item.getQuantity());
        receiverInv.addItemBag(cloneForReceiver);
    }


}
