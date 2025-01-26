package nro.service;

import nro.consts.ConstMsgSubCommand;
import nro.model.item.Item;
import nro.model.item.ItemOption;
import nro.model.player.Player;
import nro.model.player.PlayerStats;
import nro.model.template.entity.SkillInfo;
import nro.network.Message;
import nro.network.Session;
import nro.repositories.DatabaseConnectionPool;
import nro.repositories.player.PlayerCreator;
import nro.repositories.player.PlayerLoader;
import nro.server.LogServer;
import nro.server.config.ConfigDB;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class PlayerService {

    private static PlayerService instance;

    public static PlayerService getInstance() {
        if (instance == null) {
            instance = new PlayerService();
        }
        return instance;
    }

    public void finishUpdateHandler(Session session) {
        Player player = PlayerLoader.getInstance().loadPlayer(session);
        if (player == null) {
            Service.initSelectChar(session);
        } else {
            session.setPlayer(player);
            this.onPlayerLoginSuccess(player);
        }
    }

    /**
     * <pre>
     *     {@link #onPlayerLoginSuccess}
     *     {@code
     *     Server gửi: [112] => SPEACIAL_SKILL
     *     Server gửi: [-42] => ME_LOAD_POINT
     *     Server gửi: [40] => ITEM_SPLIT, TASK_GET
     *     Server gửi: [-22] => MAP_CLEAR
     *     Server gửi: [-42] => ME_LOAD_POINT
     *     Server gửi: [-30] => SUB_COMMAND
     *     }
     * </pre>
     */
    public void onPlayerLoginSuccess(Player player) {
        SpeacialSkillService.getInstance().sendSpeacialSkill(player);// 112
        this.sendPointForMe(player);// -42
        TaskService.getInstance().sendTaskMain(player);// 40
        MapService.clearMap(player);// -22
        this.sendInfoPlayer(player);// -30
    }

    /**
     * <pre>
     *     {@link #sendPointForMe}
     *     {@code
     *   Char.myCharz().cHPGoc = msg.readInt3Byte();
     *   Char.myCharz().cMPGoc = msg.readInt3Byte();
     *   Char.myCharz().cDamGoc = msg.reader().readInt();
     *   Char.myCharz().cHPFull = msg.reader().readLong();
     *   Char.myCharz().cMPFull = msg.reader().readLong();
     *   Char.myCharz().cHP = msg.reader().readLong();
     *   Char.myCharz().cMP = msg.reader().readLong();
     *   Char.myCharz().cspeed = msg.reader().readByte();
     *   Char.myCharz().hpFrom1000TiemNang = msg.reader().readByte();
     *   Char.myCharz().mpFrom1000TiemNang = msg.reader().readByte();
     *   Char.myCharz().damFrom1000TiemNang = msg.reader().readByte();
     *   Char.myCharz().cDamFull = msg.reader().readLong();
     *   Char.myCharz().cDefull = msg.reader().readLong();
     *   Char.myCharz().cCriticalFull = msg.reader().readByte();
     *   Char.myCharz().cTiemNang = msg.reader().readLong();
     *   Char.myCharz().expForOneAdd = msg.reader().readShort();
     *   Char.myCharz().cDefGoc = msg.reader().readInt();
     *   Char.myCharz().cCriticalGoc = msg.reader().readByte();
     *   InfoDlg.hide();
     *     }
     * </pre>
     */
    private void sendPointForMe(Player player) {
        try (Message msg = new Message(-42)) {
            PlayerStats stats = player.getStats();
            msg.writer().writeInt(stats.getCHPGoc());
            msg.writer().writeInt(stats.getCMPGoc());
            msg.writer().writeInt(stats.getCDamGoc());
            msg.writer().writeLong(stats.getCHPFull());
            msg.writer().writeLong(stats.getCMPFull());
            msg.writer().writeLong(stats.getCHP());
            msg.writer().writeLong(stats.getCMP());
            msg.writer().writeByte(stats.getCspeed());
            msg.writer().writeByte(stats.getHpFrom1000TiemNang());
            msg.writer().writeByte(stats.getMpFrom1000TiemNang());
            msg.writer().writeByte(stats.getDamFrom1000TiemNang());
            msg.writer().writeLong(stats.getCDamFull());
            msg.writer().writeLong(stats.getCDefull());
            msg.writer().writeByte(stats.getCCriticalFull());
            msg.writer().writeLong(stats.getCTiemNang());
            msg.writer().writeShort(stats.getExpForOneAdd());
            msg.writer().writeInt(stats.getCDefGoc());
            msg.writer().writeByte(stats.getCCriticalGoc());
            player.sendMessage(msg);
        } catch (Exception e) {
            LogServer.LogException("Error sendPointForMe: " + e.getMessage());
        }
    }

    private void sendInfoPlayer(Player player) {
        try (Message message = new Message(-30)) {
            message.writer().writeByte(ConstMsgSubCommand.INIT_MY_CHARACTER);
            message.writer().writeInt(player.getId());
            message.writer().writeByte(player.getPlayerTask().getTaskMain().getId());
            message.writer().writeByte(player.getGender());
            message.writer().writeShort(player.getPlayerFashion().getHead());
            message.writer().writeUTF(player.getName());
            message.writer().writeByte(0);
            message.writer().writeByte(player.getTypePk());
            message.writer().writeLong(player.getStats().getPower());
            message.writer().writeShort(0);// eff5BuffHp
            message.writer().writeShort(0);// eff5BuffMp
            message.writer().writeByte(player.getGender());// nClass

            // ============ Send Skill ============

            List<SkillInfo> skills = player.getPlayerSkill().getSkills();
            message.writer().writeByte(skills.size());

            for (SkillInfo skill : skills) {
                message.writer().writeShort(skill.getSkillId());
            }

            // ============ Send Currencies ============
            message.writer().writeLong(player.getPlayerCurrencies().getGold());
            message.writer().writeInt(player.getPlayerCurrencies().getRuby());
            message.writer().writeInt(player.getPlayerCurrencies().getGem());

            // ============ Send Equipment To Body ============
            List<Item> itemsBody = player.getPlayerInventory().getItemsBody();
            sendInventoryForPlayer(message, itemsBody);

            // ============ Send Equipment To Bag ============

            List<Item> itemsBag = player.getPlayerInventory().getItemsBag();
            sendInventoryForPlayer(message, itemsBag);

            // ============ Send Equipment To Box ============

            List<Item> itemsBox = player.getPlayerInventory().getItemsBox();
            sendInventoryForPlayer(message, itemsBox);

            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendInfoPlayer: " + e.getMessage());
        }
    }

    private void sendInventoryForPlayer(Message message, List<Item> items) throws IOException {
        message.writer().writeByte(items.size());
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item == null) {
                message.writer().writeShort(-1);
                continue;
            }
            message.writer().writeShort(item.getTemplate().getId());
            message.writer().writeInt(item.getQuantity());
            message.writer().writeUTF(item.getInfo());
            message.writer().writeUTF(item.getContent());
            message.writer().writeByte(item.getItemOptions().size());
            for (int j = 0; j < item.getItemOptions().size(); j++) {
                ItemOption itemOption = item.getItemOptions().get(j);
                if (itemOption == null) {
                    continue;
                }
                message.writer().writeByte(itemOption.getOptionTemplate().id());
                message.writer().writeShort(itemOption.getParam());
            }
        }
    }

    /**
     * Xử lý logic tạo nhân vật trong cơ sở dữ liệu.
     *
     * @param session Session hiện tại
     * @param name    Tên nhân vật
     * @param gender  Hành tinh nhân vật
     * @param hair    Kiểu tóc nhân vật
     * @return true nếu tạo thành công, false nếu không
     * @throws SQLException Nếu xảy ra lỗi cơ sở dữ liệu
     */

    public boolean handleCharacterCreation(Session session, String name, byte gender, byte hair) throws SQLException {
        final String QUERY_CHECK = "SELECT 1 FROM player WHERE name = ? OR account_id = ?";

        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_DYNAMIC)) {
            assert connection != null : "Connection is null";
            try (PreparedStatement psCheck = connection.prepareStatement(QUERY_CHECK)) {

                psCheck.setString(1, name);
                psCheck.setInt(2, session.getUserInfo().getId());

                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        Service.dialogMessage(session, "Tên nhân vật hoặc tài khoản đã tồn tại.");
                        return false;
                    }
                }

                boolean isCreated = PlayerCreator.getInstance().createPlayer(
                        connection,
                        session.getUserInfo().getId(),
                        name,
                        gender,
                        hair
                );

                if (!isCreated) {
                    Service.dialogMessage(session, "Tạo nhân vật thất bại.");
                }
                return isCreated;

            }
        } catch (SQLException e) {
            LogServer.LogException(String.format(
                    "Error creating character for account_id: %d, name: %s, gender: %d, hair: %d. Error: %s",
                    session.getUserInfo().getId(), name, gender, hair, e.getMessage()
            ));
            Service.dialogMessage(session, "Đã xảy ra lỗi khi tạo nhân vật. Vui lòng thử lại, nếu vẫn không thể thao tác được vui lòng báo cáo lại Admin.");
            throw e;
        }
    }

    /**
     * Kiểm tra tính hợp lệ của dữ liệu player.
     *
     * @param name   Tên nhân vật
     * @param gender Hành Tinh nhân vật
     * @return Thông báo không hợp lệ, null nếu hợp lệ
     */

    public String validateCharacterData(String name, byte gender) {
        if (name.length() < 5 || name.length() > 10) {
            return "Tên nhân vật phải từ 5 - 10 kí tự!";
        }

        if (gender < 0 || gender > 2) {
            return "Hành tinh không hợp lệ!";
        }

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]+$");
        if (!pattern.matcher(name).matches()) {
            return "Tên nhân vật không được chứa ký tự đặc biệt, chỉ cho phép a-z, A-Z, 0-9, và _";
        }
        return null;
    }

}
