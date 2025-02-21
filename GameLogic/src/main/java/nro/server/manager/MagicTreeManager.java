package nro.server.manager;

import lombok.Getter;
import nro.model.player.Player;
import nro.model.player.PlayerMagicTree;
import nro.model.template.MagicTreeTemplate;
import nro.repositories.DatabaseConnectionPool;
import nro.server.config.ConfigDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MagicTreeManager implements IManager {

    @Getter
    private static final MagicTreeManager instance = new MagicTreeManager();

    private final List<MagicTreeTemplate> iconsMagicTree = new ArrayList<>();

    @Override
    public void init() {
        this.loadIconMagicTree();
    }

    @Override
    public void reload() {
        this.clear();
        this.init();
    }

    @Override
    public void clear() {
    }

    public void loadIconMagicTree() {
        try (Connection conn = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
            String query = "SELECT gender, level, icon_id FROM magic_tree_icon ";
            try (PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var level = rs.getInt("level");
                    var gender = rs.getInt("gender");
                    var icon = rs.getInt("icon_id");
                    MagicTreeTemplate template = new MagicTreeTemplate(level, gender, icon);
                    iconsMagicTree.add(template);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public short getIconMagicTree(Player player) {
        PlayerMagicTree playerMagicTree = player.getPlayerMagicTree();
        for (MagicTreeTemplate template : iconsMagicTree) {
            if (template.getLevel() == playerMagicTree.getLevel() && template.getGender() == player.getGender()) {
                return template.getIcon();
            }
        }
        return -1;
    }

    public String getNameMagicTree(int level) {
        return "Đậu thần cấp " + level;
    }
}
