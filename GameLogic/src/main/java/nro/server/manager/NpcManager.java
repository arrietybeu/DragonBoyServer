package nro.server.manager;

import lombok.Getter;
import nro.server.config.ConfigDB;
import nro.repositories.DatabaseConnectionPool;
import nro.model.template.NpcTemplate;
import nro.server.LogServer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NpcManager implements IManager {

    @Getter
    private static final NpcManager instance = new NpcManager();

    private final List<NpcTemplate> NPC_TEMPLATE = new ArrayList<>();

    @Override
    public void init() {
        this.clear();
        this.loadNpcTemplates();
    }

    @Override
    public void reload() {
        this.init();
    }

    @Override
    public void clear() {
        this.NPC_TEMPLATE.clear();
    }

    private void loadNpcTemplates() {
        String query = "SELECT * FROM npc_template";
        try (Connection conn = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC);
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {

                var id = rs.getInt("id");
                var name = rs.getString("name");
                var head = rs.getShort("head");
                var body = rs.getShort("body");
                var leg = rs.getShort("leg");
                var menu = rs.getString("menu");

                NpcTemplate template = new NpcTemplate(id, name, head, body, leg, menu);
                this.NPC_TEMPLATE.add(template);
            }
            LogServer.LogInit("NpcManager initialized size: " + NPC_TEMPLATE.size());
        } catch (Exception e) {
            LogServer.LogException("Error loadNpcTemplates: " + e.getMessage());
        }
    }

    public NpcTemplate getNpcTemplate(int id) {
        return this.NPC_TEMPLATE.get(id);
    }

    public List<NpcTemplate> getNpcTemplates() {
        return this.NPC_TEMPLATE;
    }

    public int sizeNpc() {
        return this.NPC_TEMPLATE.size();
    }

}
