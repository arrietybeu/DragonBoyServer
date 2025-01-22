package nro.server.manager.item;

import lombok.Getter;
import nro.server.config.ConfigDB;
import nro.repositories.DatabaseConnectionPool;
import nro.server.manager.IManager;
import nro.model.template.item.ItemOptionTemplate;
import nro.server.LogServer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ItemOptionManager implements IManager {

    @Getter
    private static final ItemOptionManager instance = new ItemOptionManager();

    private final List<ItemOptionTemplate> itemOptionTemplates = new ArrayList<>();

    @Override
    public void init() {
        this.loadItemOptionTemplate();

    }

    @Override
    public void reload() {

    }

    @Override
    public void clear() {
    }

    private void loadItemOptionTemplate() {
        String query = "SELECT * FROM item_option_template";

        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC); PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                var id = rs.getInt("id");
                var name = rs.getString("name");
                var type = rs.getInt("type");
                ItemOptionTemplate itemOptionManager = new ItemOptionTemplate(id, name, type);
                this.itemOptionTemplates.add(itemOptionManager);
            }
            LogServer.LogInit("ItemOptionManager initialized size: " + this.itemOptionTemplates.size());
        } catch (Exception e) {
            LogServer.LogException("Error loadItemOptionTemplate: " + e.getMessage());
//            e.printStackTrace();
        }
    }

    public ItemOptionTemplate getItemOptionTemplate(int id) {
        return itemOptionTemplates.get(id);
    }

    public List<ItemOptionTemplate> getItemOptionTemplates() {
        return itemOptionTemplates;
    }

}
