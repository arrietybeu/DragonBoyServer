package nro.server.manager;

import lombok.Getter;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.entity.player.PlayerMagicTree;
import nro.server.service.model.template.MagicTreeTemplate;
import nro.server.service.repositories.DatabaseFactory;
import nro.server.system.LogServer;
import nro.server.config.ConfigDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public final class MagicTreeManager implements IManager {

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


    private void loadIconMagicTree() {
        try (Connection conn = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
            String query = "SELECT gender, level, icon_id FROM magic_tree_icon ";
            try (PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var level = rs.getInt("level");
                    var gender = rs.getInt("gender");
                    var icon = rs.getInt("icon_id");
                    var magicTreePositions = this.loadPositionMagicTree(level);
                    var timeUpgrade = this.loadTimeUpgradeMagicTree(level);
                    var magicTreeLevel = this.loadMagicTreeLevel(level);
                    MagicTreeTemplate template = new MagicTreeTemplate(level, gender, icon, magicTreePositions, timeUpgrade, magicTreeLevel);
                    iconsMagicTree.add(template);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MagicTreeTemplate.MagicTreeLevel loadMagicTreeLevel(int level) {
        try (Connection conn = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
            String query = "SELECT * FROM magic_tree_level WHERE level = ?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setInt(1, level);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        var itemId = resultSet.getInt("item_id");
                        var optionId = resultSet.getInt("option_id");
                        var optionParam = resultSet.getInt("option_param");
                        var maxPea = resultSet.getInt("max_pea");
                        return new MagicTreeTemplate.MagicTreeLevel(itemId, optionId, optionParam, maxPea);
                    }
                }
            }
        } catch (Exception e) {
            LogServer.LogException("Error load magic tree level: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private MagicTreeTemplate.MagicTreeTimeUpgrade loadTimeUpgradeMagicTree(int level) {
        try (Connection conn = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
            String query = "SELECT days, hours, minutes, gold FROM magic_tree_pea_upgrade WHERE upgrade_level = ?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setInt(1, level);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        var days = resultSet.getInt("days");
                        var hours = resultSet.getInt("hours");
                        var minutes = resultSet.getInt("minutes");
                        var gold = resultSet.getInt("gold");
                        return new MagicTreeTemplate.MagicTreeTimeUpgrade(days, hours, minutes, gold);
                    }
                }
            }
        } catch (Exception exception) {
            LogServer.LogException("Error load time upgrade magic tree: " + exception.getMessage());
            exception.printStackTrace();
        }
        return null;
    }

    private List<MagicTreeTemplate.MagicTreePosition> loadPositionMagicTree(int level) {
        List<MagicTreeTemplate.MagicTreePosition> magicTreePositions = new ArrayList<>();
        try (Connection conn = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
            String query = "SELECT pos_x, pos_y FROM magic_tree_pea_positions WHERE level = ?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setInt(1, level);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        var posX = resultSet.getInt("pos_x");
                        var posY = resultSet.getInt("pos_y");
                        magicTreePositions.add(new MagicTreeTemplate.MagicTreePosition(posX, posY));
                    }
                }
            }
        } catch (Exception exception) {
            LogServer.LogException("Error load position magic tree: " + exception.getMessage());
            exception.printStackTrace();
        }
        return magicTreePositions;
    }

    public short getIconMagicTree(Player player) {
        PlayerMagicTree playerMagicTree = player.getPlayerMagicTree();
        for (MagicTreeTemplate template : this.iconsMagicTree) {
            if (template.getLevel() == playerMagicTree.getLevel() && template.getGender() == player.getGender()) {
                return template.getIcon();
            }
        }
        return -1;
    }

    public List<MagicTreeTemplate.MagicTreePosition> getMagicTreePosition(int level) {
        for (MagicTreeTemplate template : this.iconsMagicTree) {
            if (template.getLevel() == level) {
                return template.getPositions();
            }
        }
        return null;
    }

    public MagicTreeTemplate.MagicTreeTimeUpgrade getMagicTreeTimeUpgrade(int level) {
        for (MagicTreeTemplate template : this.iconsMagicTree) {
            if (template.getLevel() == level) {
                return template.getTimeUpgrades();
            }
        }
        return null;
    }

    public MagicTreeTemplate.MagicTreeLevel getMagicTreeLevel(int level) {
        for (MagicTreeTemplate template : this.iconsMagicTree) {
            if (template.getLevel() == level) {
                return template.getMagicTreeLevel();
            }
        }
        return null;
    }

    public int getLevelMagicTreeByItemId(int itemId) {
        for (MagicTreeTemplate template : this.iconsMagicTree) {
            if (template.getMagicTreeLevel().itemId() == itemId) {
                return template.getLevel();
            }
        }
        return -1;
    }

    public String getNameMagicTree(int level) {
        return "Đậu thần cấp " + level;
    }

}
