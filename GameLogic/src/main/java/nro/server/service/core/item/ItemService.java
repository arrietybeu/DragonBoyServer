package nro.server.service.core.item;

import lombok.Getter;
import nro.consts.ConstItem;
import nro.consts.ConstsCmd;
import nro.server.service.model.item.ItemTime;
import nro.server.service.model.template.item.Flag;
import nro.server.service.model.template.item.FlagImage;
import nro.server.service.model.item.ItemMap;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.server.manager.ItemManager;
import nro.server.network.Message;

import java.io.DataOutputStream;
import java.util.List;

@SuppressWarnings("ALL")
public class ItemService {

    @Getter
    private static final ItemService instance = new ItemService();

    public void sendShowListFlag(Player player) {
        try (Message message = new Message(-103);
             DataOutputStream writer = message.writer()) {

            List<Flag> flags = ItemManager.getInstance().getFlags();
            writer.writeByte(0);
            writer.writeByte(flags.size());

            for (Flag flag : flags) {
                var item = flag.itemFlagBag();
                writer.writeShort(item.getTemplate().id());
                writer.writeByte(item.getItemOptions().size());

                for (var option : item.getItemOptions()) {
                    writer.writeShort(option.getId());
                    writer.writeInt(option.getParam());
                }
            }

            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("sendShowListFlagBag: " + ex.getMessage(), ex);
        }
    }

    public void sendChangeFlag(Player player, int index) {
        try (Message message = new Message(-103);
             DataOutputStream writer = message.writer()) {
            writer.writeByte(1);
            writer.writeInt(player.getId());
            writer.writeByte(index);
            player.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendChangeFlag: " + ex.getMessage(), ex);
        }
    }

    public void sendImageFlag(Player player, int index, int icon) {
        try (Message message = new Message(-103);
             DataOutputStream writer = message.writer()) {
            writer.writeByte(2);
            writer.writeByte(index);
            writer.writeShort(icon);
            player.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendChangeFlag: " + ex.getMessage(), ex);
        }
    }

    public void sendFlagBag(Player player) {
        try (Message message = new Message(ConstsCmd.UPDATE_BAG)) {
            DataOutputStream writer = message.writer();
            writer.writeInt(player.getId());
            writer.writeShort(player.getFashion().getFlagBag());
            player.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendFlagBag: " + ex.getMessage(), ex);
        }
    }

    public void sendFlagBagImage(Player player, FlagImage flagImage) {
        try (Message message = new Message(ConstsCmd.GET_BAG)) {
            DataOutputStream writer = message.writer();
            writer.writeShort(flagImage.getId());
            writer.writeByte(flagImage.getIconEffect().length);
            for (short iconId : flagImage.getIconEffect()) {
                writer.writeShort(iconId);
            }
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("sendFlagBagImage: " + ex.getMessage(), ex);
        }
    }

    public void sendDropItemMap(Player player, ItemMap itemMap, boolean isSendToAll) {
        try (Message message = new Message(68)) {
            DataOutputStream writer = message.writer();
            writer.writeShort(itemMap.getId());
            writer.writeShort(itemMap.getItem().getTemplate().id());
            writer.writeShort(itemMap.getX());
            writer.writeShort(itemMap.getY());
            writer.writeInt(player.getId());
            if (player.getId() != -2) {
                writer.writeShort(itemMap.getRange());
            }
            if (isSendToAll) {
                player.getArea().sendMessageToPlayersInArea(message, null);
            } else {
                player.sendMessage(message);
            }
        } catch (Exception ex) {
            LogServer.LogException("sendDropItemMap: " + ex.getMessage(), ex);
        }
    }

    public void sendPickItemMap(Player player, int itemMapID, int type, int quantity, String notify) {
        try (Message message = new Message(-20)) {
            DataOutputStream writer = message.writer();
            writer.writeShort(itemMapID);
            writer.writeUTF(notify);
            writer.writeShort(quantity);
            if (type == ConstItem.TYPE_GOLD || type == ConstItem.TYPE_GEM || type == ConstItem.TYPE_RUBY) {
                writer.writeShort(quantity);
            }
            player.sendMessage(message);
        } catch (Exception exception) {
            LogServer.LogException("sendPickItemMap: " + exception.getMessage(), exception);
        }
    }

    public void sendRemoveItemMap(ItemMap itemMap) {
        try (Message message = new Message(-21)) {
            message.writer().writeShort(itemMap.getId());
            itemMap.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendRemoveItemMap: " + ex.getMessage(), ex);
        }
    }

    public void sendItemTime(Player player, ItemTime itemTime) {
        try (Message message = new Message(ConstsCmd.ITEM_TIME)) {
            DataOutputStream writer = message.writer();
            writer.writeShort(itemTime.getIconId());
            writer.writeShort(itemTime.getSecond());
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("sendItemTime: " + ex.getMessage(), ex);
        }
    }

}
