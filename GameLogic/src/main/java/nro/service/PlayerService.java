package nro.service;

import nro.consts.ConstError;
import nro.consts.ConstMsgSubCommand;
import nro.consts.ConstPlayer;
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
import nro.server.manager.ItemManager;

import javax.xml.crypto.Data;
import java.io.DataOutputStream;
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
        try {
            Player player = PlayerLoader.getInstance().loadPlayer(session);
            if (player == null) {
                Service.initSelectChar(session);
            } else {
                session.setPlayer(player);
                this.onPlayerLoginSuccess(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogServer.LogException(e.getMessage());
            Service.dialogMessage(session, String.format("Đã xảy ra lỗi trong lúc tải dữ liệu vui lòng thử lại sau\n[Error %s]",
                    ConstError.ERROR_LOADING_DATABASE_FOR_PLAYER));
        }
    }

    public void onPlayerLoginSuccess(Player player) {
        SpeacialSkillService.getInstance().sendSpeacialSkill(player);// 112
        this.sendPointForMe(player);// -42
        TaskService.getInstance().sendTaskMain(player);// 40
        MapService.clearMap(player);// -22
        this.sendPointForMe(player);// -42
        this.sendInfoPlayer(player);// -30
        ClanService.getInstance().sendClanInfo(player);// -53
        InventoryService.getInstance().sendFlagBag(player);// -64
        this.sendPlayerBody(player);// -90
        MapService.getInstance().sendMapInfo(player);// -24
        this.sendStamina(player);// -68
        this.sendMaxStamina(player);// -69
        this.sendUpdateActivePoint(player);// -97
        this.sendHaveDisciple(player);// -107
        this.sendPlayerRank(player);// -119
        this.sendCurrencyHpMp(player);// -30
        this.sendSkillShortCut(player);// -113
    }

    private void sendHaveDisciple(Player player) {
        try (Message message = new Message(-107)) {
            message.writer().writeByte(player.getDisciple() == null ? 0 : 1);
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendDisciple: " + e.getMessage());
        }
    }

    private void sendSkillShortCut(Player player) {
        try (Message message = new Message(-113)) {
            DataOutputStream out = message.writer();
            byte[] skillShortCut = player.getPlayerSkill().getSkillShortCut();
            for (byte skill : skillShortCut) {
                out.writeByte(skill);
            }
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendSkillShortCut: " + e.getMessage());
        }
    }

    private void sendPlayerRank(Player player) {
        try (Message message = new Message(-119)) {
            DataOutputStream out = message.writer();
            out.writeInt(player.getRank());
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendRank: " + e.getMessage());
        }
    }

    private void sendPlayerBody(Player player) {
        try (Message message = new Message(-90)) {
            DataOutputStream out = message.writer();
            out.writeByte(1);
            out.writeInt(player.getId());
            out.writeShort(player.getPlayerFashion().getHead());
            out.writeShort(player.getPlayerFashion().getBody());
            out.writeShort(player.getPlayerFashion().getLeg());
            out.writeByte(player.getPlayerSkill().isMonkey() ? 1 : 0);
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendPlayerBody: " + e.getMessage());
        }
    }

    private void sendCurrencyHpMp(Player player) {
        try (Message message = new Message(-30)) {
            DataOutputStream out = message.writer();
            out.writeByte(ConstMsgSubCommand.UPDATE_MY_CURRENCY_HPMP);
            out.writeLong(player.getPlayerCurrencies().getGold());
            out.writeInt(player.getPlayerCurrencies().getGem());
            out.writeLong(player.getStats().getCurrentHP());
            out.writeLong(player.getStats().getCurrentMP());
            out.writeInt(player.getPlayerCurrencies().getRuby());
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendCurrencyHpMp: " + e.getMessage());
        }
    }

    private void sendMaxStamina(Player player) {
        try (Message msg = new Message(-69)) {
            DataOutputStream out = msg.writer();
            out.writeShort(player.getStats().getMaxStamina());
            player.sendMessage(msg);
        } catch (Exception e) {
            LogServer.LogException("Error sendMaxStamina: " + e.getMessage());
        }
    }

    private void sendUpdateActivePoint(Player player) {
        try (Message msg = new Message(-97)) {
            DataOutputStream out = msg.writer();
            out.writeInt(player.getActivePoint());
            player.sendMessage(msg);
        } catch (Exception e) {
            LogServer.LogException("Error sendUpdateActivePoint: " + e.getMessage());
        }
    }

    private void sendStamina(Player player) {
        try (Message msg = new Message(-68)) {
            DataOutputStream out = msg.writer();
            out.writeShort(player.getStats().getStamina());
            player.sendMessage(msg);
        } catch (Exception e) {
            LogServer.LogException("Error sendStamina: " + e.getMessage());
        }
    }

    private void sendPointForMe(Player player) {
        try (Message msg = new Message(-42)) {
            PlayerStats stats = player.getStats();
            DataOutputStream out = msg.writer();
            out.writeInt(stats.getBaseHP());
            out.writeInt(stats.getBaseMP());
            out.writeInt(stats.getBaseDamage());
            out.writeLong(stats.getMaxHP());
            out.writeLong(stats.getMaxMP());
            out.writeLong(stats.getCurrentHP());
            out.writeLong(stats.getCurrentMP());
            out.writeByte(stats.getMovementSpeed());
            out.writeByte(stats.getHpPer1000Potential());
            out.writeByte(stats.getMpPer1000Potential());
            out.writeByte(stats.getDamagePer1000Potential());
            out.writeLong(stats.getTotalDamage());
            out.writeLong(stats.getTotalDefense());
            out.writeByte(stats.getTotalCriticalChance());
            out.writeLong(stats.getPotentialPoints());
            out.writeShort(stats.getExpPerStatIncrease());
            out.writeInt(stats.getBaseDefense());
            out.writeByte(stats.getBaseCriticalChance());
            player.sendMessage(msg);
        } catch (Exception e) {
            LogServer.LogException("Error sendPointForMe: " + e.getMessage());
        }
    }

    private void sendInfoPlayer(Player player) {
        ItemManager itemManager = ItemManager.getInstance();
        int gender = player.getGender();

        try (Message msg = new Message(-30)) {
            DataOutputStream out = msg.writer();
            out.writeByte(ConstMsgSubCommand.INIT_MY_CHARACTER);
            out.writeInt(player.getId());
            out.writeByte(player.getPlayerTask().getTaskMain().getId());
            out.writeByte(gender);
            out.writeShort(player.getPlayerFashion().getHead());
            out.writeUTF(player.getName());
            out.writeByte(0);// cpk
            out.writeByte(player.getTypePk());
            out.writeLong(player.getStats().getPower());
            out.writeShort(0);// eff5BuffHp
            out.writeShort(0);// eff5BuffMp
            out.writeByte(gender);

            // ============ Send Skill ============

            List<SkillInfo> skills = player.getPlayerSkill().getSkills();
            out.writeByte(skills.size());

            for (SkillInfo skill : skills) {
                out.writeShort(skill.getSkillId());
            }

            // ============ Send Currencies ============
            out.writeLong(player.getPlayerCurrencies().getGold());
            out.writeInt(player.getPlayerCurrencies().getRuby());
            out.writeInt(player.getPlayerCurrencies().getGem());

            // ============ Send Equipment To Body ============
            List<Item> itemsBody = player.getPlayerInventory().getItemsBody();
            sendInventoryForPlayer(msg, itemsBody);

            // ============ Send Equipment To Bag ============

            List<Item> itemsBag = player.getPlayerInventory().getItemsBag();
            sendInventoryForPlayer(msg, itemsBag);

            // ============ Send Equipment To Box ============

            List<Item> itemsBox = player.getPlayerInventory().getItemsBox();
            sendInventoryForPlayer(msg, itemsBox);

            out.write(itemManager.getDataItemhead());
            sendPlayerBirdFrames(out, gender);

            // type fusion = 0 return 0 = false
            out.writeByte(player.getPlayerFusion().getTypeFusion() != 0 ? 1 : 0);
            out.writeInt(19062006);

            out.writeByte(player.isNewPlayer() ? 1 : 0);
            out.writeShort(player.getAura());
            out.writeByte(player.getIdEffSetItem());
            out.writeShort(player.getIdHat());
            player.sendMessage(msg);
        } catch (Exception e) {
            LogServer.LogException("Error sendInfoPlayer: " + e.getMessage());
        }
    }

    private void sendPlayerBirdFrames(DataOutputStream out, int gender) throws IOException {
        short frame1, frame2, avatar;

        switch (gender) {
            case ConstPlayer.TRAI_DAT -> {
                frame1 = 281;
                frame2 = 361;
                avatar = 351;
            }
            case ConstPlayer.NAMEC -> {
                frame1 = 512;
                frame2 = 513;
                avatar = 536;
            }
            default -> {
                frame1 = 514;
                frame2 = 515;
                avatar = 537;
            }
        }
        out.writeShort(frame1);
        out.writeShort(frame2);
        out.writeShort(avatar);
    }

    private void sendInventoryForPlayer(Message message, List<Item> items) throws IOException {
        message.writer().writeByte(items.size());
        for (Item item : items) {
            if (item == null) {
                message.writer().writeShort(-1);
                continue;
            }
            message.writer().writeShort(item.getTemplate().id());
            message.writer().writeInt(item.getQuantity());
            message.writer().writeUTF(item.getInfo());
            message.writer().writeUTF(item.getContent());
            message.writer().writeByte(item.getItemOptions().size());
            for (int j = 0; j < item.getItemOptions().size(); j++) {
                ItemOption itemOption = item.getItemOptions().get(j);
                if (itemOption == null) {
                    continue;
                }
                message.writer().writeShort(itemOption.getOptionTemplate().id());
                message.writer().writeInt(itemOption.getParam());
            }
        }
    }

    private void sendPlayerMove(Player player) {
        try (Message message = new Message(-7)) {

        } catch (Exception e) {
            LogServer.LogException("Error sendPlayerMove for player Id: " + player.getId());
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
