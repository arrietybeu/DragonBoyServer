package nro.server.service.core.economy;

import lombok.Getter;
import nro.consts.ConstShop;
import nro.consts.ConstsCmd;
import nro.server.service.model.item.Item;
import nro.server.service.model.template.item.ItemOption;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.server.network.Message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class ShopService {

    @Getter
    private static final ShopService instance = new ShopService();

    public void showShop(Player player, String keyword, int type, List<Item> items, String... tableHeader) {
        try (Message message = new Message(ConstsCmd.SHOP)) {
            DataOutputStream writer = message.writer();

            var sizeTable = tableHeader.length;
            writer.writeByte(type);
            writer.writeByte(sizeTable);
            for (int i = 0; i < sizeTable - 1; i++) {
                writer.writeUTF(tableHeader[i]);
                if (type == ConstShop.SHOP_KY_GUI) {
                    boolean isMaxPage = items.size() / 20 > 0;
                    int maxPageShop = (isMaxPage ? items.size() / 20 : 1);
                    writer.writeByte(maxPageShop);
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
                        writer.writeShort(item.getTemplate().id());// item id
                        writer.writeInt(1);// buy coin
                        writer.writeInt(-1);// buy gold
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

                    writer.writeByte(0);// is new Item
                    writer.writeByte(0);// is show cai trang
                    /**
                     *  is show cai trang = true
                     *  int headTemp = msg.reader().readShort();
                     *  int bodyTemp = msg.reader().readShort();
                     *  int legTemp = msg.reader().readShort();
                     *  int bagTemp = msg.reader().readShort();
                     */

                    if (type == ConstShop.SHOP_KY_GUI) {
                        writer.writeUTF(keyword + " index: " + j);// nguoi ky gui
                    }
                }
            }

            this.writeItemBag(player, writer);

            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("ShopService.showShop: " + e.getMessage(), e);
        }
    }

    private void writeItemBag(Player player, DataOutputStream writer) throws IOException {
        List<Item> itemsBag = player.getPlayerInventory().getItemsBag();
        writer.writeUTF("");

        writer.writeByte(0);
        writer.writeByte(itemsBag.size());

        for (int j = 0; j < itemsBag.size(); j++) {
            Item item = itemsBag.get(j);
            if (item.getTemplate() == null || item.getTemplate().id() == -1) {
                writer.writeShort(-1);
                continue;
            }

            writer.writeShort(item.getTemplate().id());
            writer.writeShort(item.getTemplate().id());// item id
            writer.writeInt(1);// buy coin
            writer.writeInt(-1);// buy gold
            writer.writeByte(1);// buy type
            writer.writeInt(1);// quantity
            writer.writeByte(1);// boolean is me sell

            // write item options
            writer.writeByte(item.getItemOptions().size());
            for (int k = 0; k < item.getItemOptions().size(); k++) {
                ItemOption itemOption = item.getItemOptions().get(k);
                writer.writeShort(itemOption.getId());
                writer.writeInt(itemOption.getParam());
            }

            writer.writeByte(0);// is new Item
            writer.writeByte(0);// is show cai trang
            /**
             *  is show cai trang = true
             *  int headTemp = msg.reader().readShort();
             *  int bodyTemp = msg.reader().readShort();
             *  int legTemp = msg.reader().readShort();
             *  int bagTemp = msg.reader().readShort();
             */

            writer.writeUTF(" index: " + j);// nguoi ky gui
        }
    }
}
