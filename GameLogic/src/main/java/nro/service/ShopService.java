package nro.service;

import lombok.Getter;
import nro.consts.ConstShop;
import nro.consts.ConstsCmd;
import nro.model.item.Item;
import nro.model.item.ItemOption;
import nro.model.player.Player;
import nro.server.LogServer;
import nro.server.network.Message;

import java.io.DataOutputStream;
import java.util.List;

public class ShopService {

    @Getter
    private static final ShopService instance = new ShopService();

    /**
     * Show shop
     *
     * @param sizeTable: số lượng tab trong shop
     */
    public void showShop(Player player, int type, int sizeTable, String nameTable, List<Item> items) {
        try (Message message = new Message(ConstsCmd.SHOP)) {
            DataOutputStream writer = message.writer();

            writer.writeByte(type);
            writer.writeByte(sizeTable);
            for (int i = 0; i < sizeTable; i++) {
                writer.writeUTF(nameTable);
                if (type == ConstShop.SHOP_KY_GUI) {
                    // GameCanvas.panel.maxPageShop[num70] = msg.reader().readUnsignedByte();
                }
                writer.writeByte(items.size());

                for (int j = 0; j < items.size(); j++) {
                    Item item = items.get(j);
                    writer.writeShort(item.getTemplate().id());
                    if (type == ConstShop.SHOP_REBUY_ITEM) {
                        writer.writeInt(1);// buy coin
                        writer.writeInt(1);// buy gold
                        writer.writeInt(1);// quantity
                    }
                    if (type == ConstShop.RUONG_PHU) {
                        writer.writeUTF("Lý do");
                    }
                    if (type == ConstShop.SHOP_NORMAL) {
                        writer.writeInt(1);// buy coin
                        writer.writeInt(1);// buy gold
                    }
                    if (type == ConstShop.SHOP_LEARN_SKILL) {
                        writer.writeLong(1);// power require
                    }
                    if (type == ConstShop.SHOP_KY_GUI) {
                        writer.writeShort(1);// item id
                        writer.writeInt(1);// buy coin
                        writer.writeInt(1);// buy gold
                        writer.writeByte(1);// buy type
                        writer.writeInt(1);// quantity
                        writer.writeByte(1);// boolean is me sell
                    }
                    if (type == ConstShop.SPECIAL_SHOP) {
                        writer.writeShort(1);// icon id
                        writer.writeShort(1);// buy spectial
                    }
                    // write item options
                    writer.writeByte(item.getItemOptions().size());
                    for (int k = 0; k < item.getItemOptions().size(); k++) {
                        ItemOption itemOption = item.getItemOptions().get(k);
                        writer.writeShort(itemOption.getId());
                        writer.writeInt(itemOption.getParam());
                    }

                    writer.writeByte(1);// is new Item
                    writer.writeByte(0);// is show cai trang
                    /**
                     *  is show cai trang = true
                     *  int headTemp = msg.reader().readShort();
                     *  int bodyTemp = msg.reader().readShort();
                     *  int legTemp = msg.reader().readShort();
                     *  int bagTemp = msg.reader().readShort();
                     */

                    if (type == ConstShop.SHOP_KY_GUI) {
                        writer.writeUTF("arriety");// nguoi ky gui
                    }
                }
            }
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("ShopService.showShop: " + e.getMessage(), e);
        }
    }
}
