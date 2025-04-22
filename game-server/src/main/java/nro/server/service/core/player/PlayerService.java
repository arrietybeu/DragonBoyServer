package nro.server.service.core.player;

import nro.consts.ConstError;
import nro.consts.ConstItem;
import nro.consts.ConstMsgSubCommand;
import nro.consts.ConstsCmd;
import nro.server.service.core.map.MapService;
import nro.server.service.core.social.ClanService;
import nro.server.service.core.system.ServerService;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.item.Item;
import nro.server.service.model.item.ItemMap;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.entity.Points;
import nro.server.service.model.task.TaskMain;
import nro.server.service.model.template.entity.SkillInfo;
import nro.server.manager.SessionManager;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.commons.database.DatabaseFactory;
import nro.server.service.repositories.player.PlayerCreator;
import nro.server.service.repositories.player.PlayerLoader;
import nro.server.system.LogServer;
import nro.server.config.ConfigDB;
import nro.server.manager.CaptionManager;
import nro.server.manager.ItemManager;
import nro.server.service.core.item.DropItemMap;
import nro.server.service.core.item.ItemFactory;
import nro.server.service.core.item.ItemService;
import nro.utils.Util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class PlayerService {

    private static final class SingletonHolder {
        private static final PlayerService instance = new PlayerService();
    }

    public static PlayerService getInstance() {
        return PlayerService.SingletonHolder.instance;
    }

    private static final Message MESSAGE_REVIVE = new Message(-16);

    public void finishUpdateHandler(Session session) {
        try {
            Player player = PlayerLoader.getInstance().loadPlayer(session);
            if (player == null) {
                ServerService.initSelectChar(session);
            } else {
                session.setPlayer(player);
                this.onPlayerLoginSuccess(player);
            }
        } catch (Exception e) {
            LogServer.LogException(e.getMessage(), e);
            ServerService.dialogMessage(session,
                    String.format("Đã xảy ra lỗi trong lúc tải dữ liệu vui lòng thử lại sau\n[Error %s]",
                            ConstError.ERROR_LOADING_DATABASE_FOR_PLAYER));
            SessionManager.getInstance().kickSession2Second(session);
        }
    }

    private void onPlayerLoginSuccess(Player player) {
        if (!player.getSession().getSessionInfo().isLogin()) return;
        player.getSession().getSessionInfo().setLoadData(true);
        player.getArea().addEntity(player);
        ServerService serverService = ServerService.getInstance();
        this.sendSelectSkillShortCut(player, "KSkill");
        this.sendSelectSkillShortCut(player, "OSkill");
        SpeacialSkillService.getInstance().sendSpeacialSkill(player);// 112
        this.sendPointForMe(player);// -42
        TaskService.getInstance().sendTaskMain(player);// 40
        MapService.clearMap(player);// -22
        this.sendInfoPlayer(player);// -30
        ClanService.getInstance().sendClanInfo(player);// -53
        InventoryService.getInstance().sendFlagBag(player);// -64
        this.sendPlayerBody(player);// -90
        MapService.getInstance().sendMapInfo(player);// -24
        this.sendCurrencyHpMp(player);// -30
        this.sendThongBaoInfoTask(player, serverService);
        this.sendMaxStamina(player);// -69
        this.sendStamina(player);// -68
        this.sendUpdateActivePoint(player);// -97
        this.sendHaveDisciple(player);// -107
        this.sendPlayerRank(player);// -119
        this.sendSkillShortCut(player);// -113
        serverService.sendGameNotify(player);// 50
        this.sendCaptionForPlayer(player);// -41
        player.getPlayerTask().sendInfoTaskForNpcTalkByUI(player);
        SkillService.getInstance().sendSkillCooldown(player);
        DropItemMap.dropMissionItems(player);
        player.getPlayerTask().checkDoneTaskGoMap();
    }

    private void sendCaptionForPlayer(Player player) {
        try (Message message = new Message(-41)) {
            CaptionManager captionManager = CaptionManager.getInstance();
            var gender = player.getGender();
            byte[] dataToSend;
            switch (gender) {
                case 0 -> dataToSend = captionManager.getTraiDat();
                case 1 -> dataToSend = captionManager.getNamec();
                case 2 -> dataToSend = captionManager.getXayda();
                default -> {
                    LogServer.LogException("SendCaptionForPlayer invalid  gender: " + gender);
                    return;
                }
            }
            message.writer().write(dataToSend);
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException(
                    "Error send Caption For Player id: " + player.getName() + " info: " + ex.getMessage(), ex);
        }
    }

    private void sendThongBaoInfoTask(Player player, ServerService serverService) {
        try {
            TaskMain taskMain = player.getPlayerTask().getTaskMain();
            List<TaskMain.SubName> subNames = taskMain.getSubNameList();
            String subNameTask = "Nhiệm vụ của bạn là " + subNames.get(taskMain.getIndex()).getNameMapByGender(player.getGender());
            serverService.sendChatGlobal(player.getSession(), null, subNameTask, false);
        } catch (Exception e) {
            LogServer.LogException("Error sendThongBaoInfoTask: " + e.getMessage(), e);
        }
    }

    private void sendHaveDisciple(Player player) {
        try (Message message = new Message(-107)) {
//            message.writer().writeByte(player.getDisciple() == null ? 0 : 1);
            message.writer().writeByte(0);
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendDisciple: " + e.getMessage(), e);
        }
    }

    private void sendSkillShortCut(Player player) {
        try (Message message = new Message(-113)) {
            DataOutputStream out = message.writer();
            byte[] skillShortCut = player.getSkills().getSkillShortCut();
            for (byte skill : skillShortCut) {
                out.writeByte(skill);
            }
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendSkillShortCut: " + e.getMessage(), e);
        }
    }

    public void sendSelectSkillShortCut(Player player, String text) {
        try (Message message = new Message(-30)) {
            DataOutputStream data = message.writer();
            data.writeByte(ConstMsgSubCommand.UPDATE_SKILL_SHORTCUT);
            data.writeUTF(text);
            data.writeInt(player.getSkills().getSkillShortCut().length);
            data.write(player.getSkills().getSkillShortCut());
            player.sendMessage(message);
        } catch (Exception exception) {
            LogServer.LogException("Error sendSelectSkillShortCut: " + exception.getMessage(), exception);
        }
    }

    private void sendPlayerRank(Player player) {
        try (Message message = new Message(-119)) {
            message.writer().writeInt(player.getRank());
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendRank: " + e.getMessage(), e);
        }
    }

    public void sendPlayerBody(Player player) {
        try (Message message = new Message(-90)) {
            DataOutputStream out = message.writer();
            out.writeByte(1);
            out.writeInt(player.getId());
            out.writeShort(player.getFashion().getHead());
            out.writeShort(player.getFashion().getBody());
            out.writeShort(player.getFashion().getLeg());
            out.writeByte(player.getSkills().isMonkey() ? 1 : 0);
            player.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception e) {
            LogServer.LogException("Error sendPlayerBody: " + e.getMessage(), e);
        }
    }

    public void sendCurrencyHpMp(Player player) {
        try (Message message = new Message(-30)) {
            DataOutputStream out = message.writer();
            out.writeByte(ConstMsgSubCommand.UPDATE_MY_CURRENCY_HPMP);
            out.writeLong(player.getPlayerCurrencies().getGold());
            out.writeInt(player.getPlayerCurrencies().getGem());
            out.writeLong(player.getPoints().getCurrentHP());
            out.writeLong(player.getPoints().getCurrentMP());
            out.writeInt(player.getPlayerCurrencies().getRuby());
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendCurrencyHpMp: " + e.getMessage(), e);
        }
    }

    private void sendMaxStamina(Player player) {
        try (Message msg = new Message(-69)) {
            DataOutputStream out = msg.writer();
            out.writeShort(player.getPoints().getMaxStamina());
            player.sendMessage(msg);
        } catch (Exception e) {
            LogServer.LogException("Error sendMaxStamina: " + e.getMessage(), e);
        }
    }

    private void sendUpdateActivePoint(Player player) {
        try (Message msg = new Message(-97)) {
            DataOutputStream out = msg.writer();
            out.writeInt(player.getActivePoint());
            player.sendMessage(msg);
        } catch (Exception e) {
            LogServer.LogException("Error sendUpdateActivePoint: " + e.getMessage(), e);
        }
    }

    public void sendPlayerUpExp(Player player, int type, int quantity) {
        try (Message message = new Message(-3)) {
            DataOutputStream out = message.writer();
            out.writeByte(type); // 0 = power, 1 = tiem nang, 2 = all
            out.writeInt(quantity);
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("sendPlayerUpExp: " + ex.getMessage(), ex);
        }
    }

    public void sendStamina(Player player) {
        try (Message msg = new Message(-68)) {
            DataOutputStream out = msg.writer();
            out.writeShort(player.getPoints().getStamina());
            player.sendMessage(msg);
        } catch (Exception e) {
            LogServer.LogException("Error sendStamina: " + e.getMessage(), e);
        }
    }

    public void sendHpForPlayer(Player player) {
        try (Message message = new Message(-30)) {
            DataOutputStream out = message.writer();
            out.writeByte(ConstMsgSubCommand.UPDATE_MY_HP);
            out.writeLong(player.getPoints().getCurrentHP());
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendHpForPlayer: " + e.getMessage(), e);
        }
    }

    public void sendMpForPlayer(Player player) {
        try (Message message = new Message(-30)) {
            DataOutputStream out = message.writer();
            out.writeByte(ConstMsgSubCommand.UPDATE_MY_MP);
            out.writeLong(player.getPoints().getCurrentMP());
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendHpForPlayer: " + e.getMessage(), e);
        }
    }

    public void sendPointForMe(Player player) {
        try (Message msg = new Message(-42)) {
            Points stats = player.getPoints();
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
            out.writeLong(stats.getCurrentDamage());
            out.writeLong(stats.getTotalDefense());
            out.writeByte(stats.getTotalCriticalChance());
            out.writeLong(stats.getPotentialPoints());
            out.writeShort(stats.getExpPerStatIncrease());
            out.writeInt(stats.getBaseDefense());
            out.writeByte(stats.getBaseCriticalChance());
            player.sendMessage(msg);
        } catch (Exception e) {
            LogServer.LogException("Error sendPointForMe: " + Arrays.toString(e.getStackTrace()), e);
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
            out.writeShort(player.getFashion().getHead());
            out.writeUTF(player.getName());
            out.writeByte(0);// cpk
            out.writeByte(player.getTypePk());
            out.writeLong(player.getPoints().getPower());
            out.writeShort(player.getPoints().getEff5BuffHp());// eff5BuffHp
            out.writeShort(player.getPoints().getEff5BuffMp());// eff5BuffMp
            out.writeByte(gender);

            // ============ Send Skills ============

            List<SkillInfo> skills = player.getSkills().getSkills();
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
            sendInventoryForPlayer(out, itemsBody);

            // ============ Send Equipment To Bag ============

            List<Item> itemsBag = player.getPlayerInventory().getItemsBag();
            sendInventoryForPlayer(out, itemsBag);

            // ============ Send Equipment To Box ============

            List<Item> itemsBox = player.getPlayerInventory().getItemsBox();
            sendInventoryForPlayer(out, itemsBox);

            out.write(itemManager.getDataItemhead());
            sendPlayerBirdFrames(out, player);

            // status fusion = 0 return 0 = false
            out.writeByte(player.getFusion().getTypeFusion() != 0 ? 1 : 0);
            out.writeInt(19062006);

            out.writeByte(player.isNewPlayer() ? 1 : 0);
            out.writeShort(player.getFashion().getAura());
            out.writeByte(player.getFashion().getEffSetItem());
            out.writeShort(player.getFashion().getIdHat());
            player.sendMessage(msg);
        } catch (Exception e) {
            LogServer.LogException("Error sendInfoPlayer: " + e.getMessage(), e);
        }
    }

    private void sendPlayerBirdFrames(DataOutputStream out, Player player) throws IOException {
        short[] frames = player.getPlayerBirdFrames();
        out.writeShort(frames[0]); // frame1
        out.writeShort(frames[1]); // frame2
        out.writeShort(frames[2]); // avatar
    }

    public void sendInventoryForPlayer(DataOutputStream data, List<Item> items) throws IOException {
        data.writeByte(items.size());
        for (Item item : items) {

            if (item.getTemplate() == null) {
                data.writeShort(-1);
                continue;
            }

            data.writeShort(item.getTemplate().id());
            data.writeInt(item.getQuantity());
            data.writeUTF("");
            data.writeUTF("");
            item.writeDataOptions(data);
        }
    }

    public void sendMenuPlayerInfo(Player player, int playerId) {
        try (Message message = new Message(-79)) {
            Player playerInArea = player.getArea().getPlayer(playerId);
            message.writer().writeInt(playerInArea.getId());
            message.writer().writeLong(playerInArea.getPoints().getPower());
            message.writer().writeUTF(CaptionManager.getInstance().getCaptionsByPower(playerInArea.getPoints().getPower(), playerInArea.getGender()));
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("sendMenuPlayerInfo: " + ex.getMessage(), ex);
        }
    }

    public void sendPlayerDie(Player player) {
        try (Message message = new Message(-17)) {
            DataOutputStream out = message.writer();
            out.writeByte(player.getTypePk());
            out.writeShort(player.getX());
            out.writeShort(player.getY());
            out.writeLong(player.getPoints().getPower());
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("sendPlayerDie: " + ex.getMessage(), ex);
        }
    }

    public void sendPlayerDeathToArea(Entity entity) {
        try (Message message = new Message(-8)) {
            DataOutputStream out = message.writer();
            out.writeInt(entity.getId());
            out.writeByte(entity.getTypePk());
            out.writeShort(entity.getX());
            out.writeShort(entity.getY());
            entity.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendPlayerDeathToArea: " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

    public void sendPlayerRevive(Player player) {
        try {
            player.sendMessage(MESSAGE_REVIVE);
        } catch (Exception e) {
            LogServer.LogException("Error sendPlayerRevive: " + e.getMessage(), e);
        }
    }

    public void sendPlayerReviveToArea(Player player) {
        try (Message message = new Message(-30)) {
            DataOutputStream out = message.writer();
            out.writeByte(ConstMsgSubCommand.CHAR_REVIVE);
            out.writeInt(player.getId());
            out.writeLong(player.getPoints().getCurrentHP());
            out.writeLong(player.getPoints().getMaxHP());
            out.writeShort(player.getX());
            out.writeShort(player.getY());
            player.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendPLayerReviveToArea: " + ex.getMessage(), ex);
        }
    }

    public void sendSetPosition(Entity entity, int effectId) {
        try (Message message = new Message(ConstsCmd.SET_POS)) {
            DataOutputStream writer = message.writer();
            writer.writeInt(entity.getId());
            writer.writeShort(entity.getX());
            writer.writeShort(entity.getY());
            writer.writeByte(effectId);
            entity.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception e) {
            LogServer.LogException("Error sendSetPosition: " + e.getMessage(), e);
        }
    }

    public void pickItem(Player player, int itemMapID) {
        try {
            if (this.pickItemTask(player, itemMapID)) return;
            ItemMap itemMap = player.getArea().getItemsMapById(itemMapID);
            if (itemMap == null) return;
            if (itemMap.getId() == itemMapID) {
                ServerService serverService = ServerService.getInstance();

                if (itemMap.getItem().getTemplate().type() == ConstItem.TYPE_VE_TINH) return;

                // TODO check inventory full
                if (player.getPlayerInventory().isBagFull()) {
                    serverService.sendChatGlobal(player.getSession(), null, "Hành trang đã đầy.", false);
                    return;
                }

                if (Util.getDistance(itemMap.getX(), itemMap.getY(), player.getX(), player.getY()) > 100) {
                    String notify = "Không thể nhặt vật phẩm ở khoảng cách quá xa";
                    serverService.sendChatGlobal(player.getSession(), null, notify, false);
                    return;
                }

                if (itemMap.getPlayerId() != -1 && itemMap.getPlayerId() != player.getId()) {
                    var notify = "Không thể nhặt vật phẩm của người khác";
                    serverService.sendChatGlobal(player.getSession(), null, notify, false);
                    return;
                }

                final var item = itemMap.getItem();
                final var idItem = item.getTemplate().id();
                final var quantity = item.getQuantity();
                final var itemType = item.getTemplate().type();

                var notify = "";
                switch (itemType) {
                    case ConstItem.TYPE_GOLD -> player.getPlayerCurrencies().addGold(quantity);
                    case ConstItem.TYPE_GEM -> player.getPlayerCurrencies().addGem(quantity);
                    case ConstItem.TYPE_RUBY -> player.getPlayerCurrencies().addRuby(quantity);
                    default -> {
                        switch (idItem) {
                            case ConstItem.DUI_GA -> player.getPlayerTask().checkDoneTaskPickItem(idItem);
                            default -> {
                                if (!player.getPlayerInventory().addItemBag(item)) {
                                    // serverService.sendChatGlobal(player.getSession(), null, "", false);
                                    return;
                                }
                            }
                        }
                    }
                }
                ItemService.getInstance().sendPickItemMap(player, itemMap.getId(), itemType, quantity, notify);
                this.sendPLayerPickItemMap(player, itemMap.getId());
                player.getArea().removeItemMap(itemMap.getId());
            }
        } catch (Exception ex) {
            LogServer.LogException("pickItem: " + ex.getMessage(), ex);
        }
    }

    private boolean pickItemTask(Player player, int itemMapId) {
        TaskMain taskMain = player.getPlayerTask().getTaskMain();
        boolean isTask = false;
        String notify = null;
        var type = -1;

        if (player.getArea().getMap().isMapHouseByGender(player.getGender()) && itemMapId == player.getPlayerContext().getIdItemTask()) {
            player.getPoints().healPlayer();
            notify = "Bạn vừa ăn Đùi gà nướng";
            isTask = true;
            type = 27;
        }

        if (taskMain.getId() == 3 && taskMain.getIndex() == 1) {
            isTask = this.handleTaskPickItem(player, itemMapId);
            notify = "Wow, một cậu bé dễ thương";
            type = 11;
        }

        if (isTask) {
            ItemService.getInstance().sendPickItemMap(player, itemMapId, type, 1, notify);
        }
        return isTask;
    }

    private boolean handleTaskPickItem(Player player, int itemMapId) {
        int idItem = ConstItem.DUA_BE;
        if (player.getPlayerContext().getIdItemTask() == itemMapId) {
            Item duaBe = ItemFactory.getInstance().createItemOptionsBase(idItem, player.getId(), 1);
            if (!player.getPlayerInventory().addItemBag(duaBe)) return false;
            player.getPlayerTask().checkDoneTaskPickItem(idItem);
            return true;
        }
        return false;
    }

    public void sendPLayerPickItemMap(Player player, int itemMapId) {
        try (Message message = new Message(-19)) {
            DataOutputStream writer = message.writer();
            writer.writeShort(itemMapId);
            writer.writeInt(player.getId());
            player.getArea().sendMessageToPlayersInArea(message, player);
        } catch (Exception ex) {
            LogServer.LogException("sendPLayerPickItemMap: " + ex.getMessage(), ex);
        }
    }

    public void sendEntityChangerTypePlayerKill(Entity entity) {
        try (Message message = new Message(ConstsCmd.SUB_COMMAND)) {
            DataOutputStream write = message.writer();
            write.writeByte(ConstMsgSubCommand.UPDATE_CHAR_PK_TYPE);
            write.writeInt(entity.getId());
            write.writeByte(entity.getTypePk());
            entity.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception e) {
            LogServer.LogException("sendEntityChangerTypePlayerKill: " + e.getMessage(), e);
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
        if (session.getUserInfo() == null) {
            LogServer.LogException("Error handleCharacterCreation: UserInfo is null for session: " + session);
            SessionManager.getInstance().kickSession2Second(session);
            return false;
        }
        final String QUERY_CHECK = "SELECT 1 FROM player WHERE name = ? OR account_id = ?";
        try (Connection connection = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_DYNAMIC)) {
            if (connection == null) return false;
            try (PreparedStatement psCheck = connection.prepareStatement(QUERY_CHECK)) {

                psCheck.setString(1, name);
                psCheck.setInt(2, session.getUserInfo().getId());

                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        ServerService.dialogMessage(session, "Tên nhân vật hoặc tài khoản đã tồn tại.");
                        return false;
                    }
                }

                boolean isCreated = PlayerCreator.getInstance().createPlayer(connection, session.getUserInfo().getId(),
                        name, gender, hair);

                if (!isCreated) {
                    ServerService.dialogMessage(session, "Tạo nhân vật thất bại.");
                }
                return isCreated;
            }
        } catch (SQLException e) {
            LogServer.LogException(String.format(
                    "Error creating character for account_id: %d, name: %s, gender: %d, hair: %d. Error: %s",
                    session.getUserInfo().getId(), name, gender, hair, e.getMessage()), e);
            ServerService.dialogMessage(session,
                    "Đã xảy ra lỗi khi tạo nhân vật. Vui lòng thử lại, nếu vẫn không thể thao tác được vui lòng báo cáo lại Admin.");
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
