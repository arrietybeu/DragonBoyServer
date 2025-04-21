package nro.server.manager;

import lombok.Getter;
import nro.server.config.ConfigDB;
import nro.server.service.core.item.ItemFactory;
import nro.server.service.model.item.ItemShop;
import nro.server.service.model.shop.Shop;
import nro.server.service.model.shop.TabShop;
import nro.server.service.repositories.DatabaseFactory;
import nro.server.system.LogServer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public final class ShopManager implements IManager {

    @Getter
    private static final ShopManager instance = new ShopManager();

    private static final String SELECT_SHOP_QUERY = "SELECT * FROM shop";
    private static final String SELECT_TAB_SHOP_QUERY = "SELECT * FROM shop_tab WHERE shop_id = ? ORDER BY index_tab";
    private static final String SELECT_ITEM_SHOP_QUERY = "SELECT * FROM shop_item WHERE tab_id = ? AND is_sell = 1 ORDER BY index_item";

    private final HashMap<Integer, Shop> shops = new HashMap<>();

    @Override
    public void init() {
        this.loadShop();
    }

    @Override
    public void reload() {
        this.clear();
        this.init();
    }

    @Override
    public void clear() {
        for (Shop shop : shops.values()) shop.getTabShops().clear();
        shops.clear();
    }

    private void loadShop() {
        try (Connection connection = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_STATIC);
             PreparedStatement stmt = connection.prepareStatement(SELECT_SHOP_QUERY)) {
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    if (shops.containsKey(id)) continue;
                    Shop shop = new Shop();
                    shop.setId(id);
                    shop.setNpcId(resultSet.getByte("npc_id"));
                    shop.setTypeShop(resultSet.getByte("type_shop"));
                    shop.setTabShops(this.loadTabShops(connection, id));
                    shops.put(id, shop);
                }
            }
        } catch (Exception e) {
            LogServer.LogException("loadShop: " + e.getMessage(), e);
        }
    }

    private List<TabShop> loadTabShops(Connection connection, int shopId) {
        List<TabShop> tabShops = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SELECT_TAB_SHOP_QUERY)) {
            ps.setInt(1, shopId);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    TabShop tabShop = new TabShop();
                    tabShop.setId(resultSet.getInt("id"));
                    tabShop.setName(resultSet.getString("name"));
                    tabShop.setItemShopMap(this.loadItemShop(connection, tabShop.getId()));
                    tabShops.add(tabShop);
                }
            }
        } catch (Exception e) {
            LogServer.LogException("loadTabShops: " + e.getMessage(), e);
        }
        return tabShops;
    }

    private Map<Short, ItemShop> loadItemShop(Connection connection, int tabId) {
        Map<Short, ItemShop> itemShops = new LinkedHashMap<>();
        try (PreparedStatement ps = connection.prepareStatement(SELECT_ITEM_SHOP_QUERY)) {
            ps.setInt(1, tabId);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    int indexItem = resultSet.getInt("index_item");
                    short itemId = resultSet.getShort("item_id");
                    boolean isSell = resultSet.getByte("is_sell") == 1;
                    boolean isNewItem = resultSet.getByte("is_new_item") == 1;
                    byte typeSell = resultSet.getByte("type_sell");
                    int sellGold = resultSet.getInt("sell_gold");
                    int sellGem = resultSet.getInt("sell_gem");
                    int sellSpectCost = resultSet.getInt("sell_spec_count");
                    short sellSpectIcon = resultSet.getShort("sell_spec_icon");
                    ItemShop itemShop = ItemFactory.getInstance().createItemShopOptionsBase(itemId);

                    itemShop.setTabId(tabId);
                    itemShop.setIndexItem(indexItem);
                    itemShop.setSell(isSell);
                    itemShop.setNewItem(isNewItem);
                    itemShop.setTypeSell(typeSell);
                    itemShop.setSellGold(sellGold);
                    itemShop.setSellGem(sellGem);
                    itemShop.setCostSellSpec(sellSpectCost);
                    itemShop.setIconSpec(sellSpectIcon);

                    itemShops.put(itemId, itemShop);
                }
            }
        } catch (Exception e) {
            LogServer.LogException("loadItemShop: " + e.getMessage(), e);
        }
        return itemShops;
    }

    public Shop getShop(int id) {
        return shops.get(id);
    }
}
