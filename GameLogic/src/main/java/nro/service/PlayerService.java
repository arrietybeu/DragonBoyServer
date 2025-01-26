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

    /**
     * <pre>
     *     {@link #sendInfoPlayer}
     *     {@code
     *     case 0: {
     *       GameCanvas.debug("SA21", 2);
     *       RadarScr.list = new MyVector();
     *       Teleport.vTeleport.removeAllElements();
     *       GameScr.vCharInMap.removeAllElements();
     *       GameScr.vItemMap.removeAllElements();
     *       Char.vItemTime.removeAllElements();
     *       GameScr.loadImg();
     *       GameScr.currentCharViewInfo = Char.myCharz();
     *       Char.myCharz().charID = msg.reader().readInt();
     *       Char.myCharz().ctaskId = msg.reader().readByte();
     *       Char.myCharz().cgender = msg.reader().readByte();
     *       Char.myCharz().head = msg.reader().readShort();
     *       Char.myCharz().cName = msg.reader().readUTF();
     *       Char.myCharz().cPk = msg.reader().readByte();
     *       Char.myCharz().cTypePk = msg.reader().readByte();
     *       Char.myCharz().cPower = msg.reader().readLong();
     *       Char.myCharz().applyCharLevelPercent();
     *       Char.myCharz().eff5BuffHp = msg.reader().readShort();
     *       Char.myCharz().eff5BuffMp = msg.reader().readShort();
     *       Char.myCharz().nClass = GameScr.nClasss[msg.reader().readByte()];
     *       Char.myCharz().vSkill.removeAllElements();
     *       Char.myCharz().vSkillFight.removeAllElements();
     *       GameScr.gI().dHP = Char.myCharz().cHP;
     *       GameScr.gI().dMP = Char.myCharz().cMP;
     *       sbyte b2 = msg.reader().readByte();
     *       for (sbyte b6 = 0; b6 < b2; b6++)
     *       {
     *           Skill skill3 = Skills.get(msg.reader().readShort());
     *           useSkill(skill3);
     *       }
     *       GameScr.gI().sortSkill();
     *       GameScr.gI().loadSkillShortcut();
     *       Char.myCharz().xu = msg.reader().readLong();
     *       Char.myCharz().luongKhoa = msg.reader().readInt();
     *       Char.myCharz().luong = msg.reader().readInt();
     *       Char.myCharz().xuStr = Res.formatNumber(Char.myCharz().xu);
     *       Char.myCharz().luongStr = mSystem.numberTostring(Char.myCharz().luong);
     *       Char.myCharz().luongKhoaStr = mSystem.numberTostring(Char.myCharz().luongKhoa);
     *       Char.myCharz().arrItemBody = new Item[msg.reader().readByte()];
     *       try
     *       {
     *           Char.myCharz().setDefaultPart();
     *           for (int k = 0; k < Char.myCharz().arrItemBody.Length; k++)
     *           {
     *               short num6 = msg.reader().readShort();
     *               if (num6 == -1)
     *               {
     *                   continue;
     *               }
     *               ItemTemplate itemTemplate = ItemTemplates.get(num6);
     *               int num7 = itemTemplate.type;
     *               Char.myCharz().arrItemBody[k] = new Item();
     *               Char.myCharz().arrItemBody[k].template = itemTemplate;
     *               Char.myCharz().arrItemBody[k].quantity = msg.reader().readInt();
     *               Char.myCharz().arrItemBody[k].info = msg.reader().readUTF();
     *               Char.myCharz().arrItemBody[k].content = msg.reader().readUTF();
     *               int num8 = msg.reader().readUnsignedByte();
     *               if (num8 != 0)
     *               {
     *                   Char.myCharz().arrItemBody[k].itemOption = new ItemOption[num8];
     *                   for (int l = 0; l < Char.myCharz().arrItemBody[k].itemOption.Length; l++)
     *                   {
     *                       ItemOption itemOption = readItemOption(msg);
     *                       if (itemOption != null)
     *                       {
     *                           Char.myCharz().arrItemBody[k].itemOption[l] = itemOption;
     *                       }
     *                   }
     *               }
     *               switch (num7)
     *               {
     *                   case 0:
     *                       Res.outz("toi day =======================================" + Char.myCharz().body);
     *                       Char.myCharz().body = Char.myCharz().arrItemBody[k].template.part;
     *                       break;
     *                   case 1:
     *                       Char.myCharz().leg = Char.myCharz().arrItemBody[k].template.part;
     *                       Res.outz("toi day =======================================" + Char.myCharz().leg);
     *                       break;
     *               }
     *           }
     *       }
     *       catch (Exception)
     *       {
     *       }
     *       Char.myCharz().arrItemBag = new Item[msg.reader().readByte()];
     *       GameScr.hpPotion = 0;
     *       GameScr.isudungCapsun4 = false;
     *       GameScr.isudungCapsun3 = false;
     *       for (int m = 0; m < Char.myCharz().arrItemBag.Length; m++)
     *       {
     *           short num9 = msg.reader().readShort();
     *           if (num9 == -1)
     *           {
     *               continue;
     *           }
     *           Char.myCharz().arrItemBag[m] = new Item();
     *           Char.myCharz().arrItemBag[m].template = ItemTemplates.get(num9);
     *           Char.myCharz().arrItemBag[m].quantity = msg.reader().readInt();
     *           Char.myCharz().arrItemBag[m].info = msg.reader().readUTF();
     *           Char.myCharz().arrItemBag[m].content = msg.reader().readUTF();
     *           Char.myCharz().arrItemBag[m].indexUI = m;
     *           sbyte b7 = msg.reader().readByte();
     *           if (b7 != 0)
     *           {
     *               Char.myCharz().arrItemBag[m].itemOption = new ItemOption[b7];
     *               for (int n = 0; n < Char.myCharz().arrItemBag[m].itemOption.Length; n++)
     *               {
     *                   ItemOption itemOption2 = readItemOption(msg);
     *                   if (itemOption2 != null)
     *                   {
     *                       Char.myCharz().arrItemBag[m].itemOption[n] = itemOption2;
     *                       Char.myCharz().arrItemBag[m].getCompare();
     *                   }
     *               }
     *           }
     *           if (Char.myCharz().arrItemBag[m].template.type == 6)
     *           {
     *               GameScr.hpPotion += Char.myCharz().arrItemBag[m].quantity;
     *           }
     *           switch (num9)
     *           {
     *               case 194:
     *                   GameScr.isudungCapsun4 = Char.myCharz().arrItemBag[m].quantity > 0;
     *                   break;
     *               case 193:
     *                   if (!GameScr.isudungCapsun4)
     *                   {
     *                       GameScr.isudungCapsun3 = Char.myCharz().arrItemBag[m].quantity > 0;
     *                   }
     *                   break;
     *           }
     *       }
     *       Char.myCharz().arrItemBox = new Item[msg.reader().readByte()];
     *       GameCanvas.panel.hasUse = 0;
     *       for (int num10 = 0; num10 < Char.myCharz().arrItemBox.Length; num10++)
     *       {
     *           short num11 = msg.reader().readShort();
     *           if (num11 == -1)
     *           {
     *               continue;
     *           }
     *           Char.myCharz().arrItemBox[num10] = new Item();
     *           Char.myCharz().arrItemBox[num10].template = ItemTemplates.get(num11);
     *           Char.myCharz().arrItemBox[num10].quantity = msg.reader().readInt();
     *           Char.myCharz().arrItemBox[num10].info = msg.reader().readUTF();
     *           Char.myCharz().arrItemBox[num10].content = msg.reader().readUTF();
     *           Char.myCharz().arrItemBox[num10].itemOption = new ItemOption[msg.reader().readByte()];
     *           for (int num12 = 0; num12 < Char.myCharz().arrItemBox[num10].itemOption.Length; num12++)
     *           {
     *               ItemOption itemOption3 = readItemOption(msg);
     *               if (itemOption3 != null)
     *               {
     *                   Char.myCharz().arrItemBox[num10].itemOption[num12] = itemOption3;
     *                   Char.myCharz().arrItemBox[num10].getCompare();
     *               }
     *           }
     *           GameCanvas.panel.hasUse++;
     *       }
     *       Char.myCharz().statusMe = 4;
     *       int num13 = Rms.loadRMSInt(Char.myCharz().cName + "vci");
     *       if (num13 < 1)
     *       {
     *           GameScr.isViewClanInvite = false;
     *       }
     *       else
     *       {
     *           GameScr.isViewClanInvite = true;
     *       }
     *       short num14 = msg.reader().readShort();
     *       Char.idHead = new short[num14];
     *       Char.idAvatar = new short[num14];
     *       for (int num15 = 0; num15 < num14; num15++)
     *       {
     *           Char.idHead[num15] = msg.reader().readShort();
     *           Char.idAvatar[num15] = msg.reader().readShort();
     *       }
     *       for (int num16 = 0; num16 < GameScr.info1.charId.Length; num16++)
     *       {
     *           GameScr.info1.charId[num16] = new int[3];
     *       }
     *       GameScr.info1.charId[Char.myCharz().cgender][0] = msg.reader().readShort();
     *       GameScr.info1.charId[Char.myCharz().cgender][1] = msg.reader().readShort();
     *       GameScr.info1.charId[Char.myCharz().cgender][2] = msg.reader().readShort();
     *       Char.myCharz().isNhapThe = msg.reader().readByte() == 1;
     *       Res.outz("NHAP THE= " + Char.myCharz().isNhapThe);
     *       GameScr.deltaTime = mSystem.currentTimeMillis() - (long)msg.reader().readInt() * 1000L;
     *       GameScr.isNewMember = msg.reader().readByte();
     *       Service.gI().updateCaption((sbyte)Char.myCharz().cgender);
     *       Service.gI().androidPack();
     *       try
     *       {
     *           Char.myCharz().idAuraEff = msg.reader().readShort();
     *           Char.myCharz().idEff_Set_Item = msg.reader().readSByte();
     *           Char.myCharz().idHat = msg.reader().readShort();
     *           break;
     *       }
     *       catch (Exception)
     *       {
     *           break;
     *       }
     *   }
     *     }
     * </pre>
     */

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
