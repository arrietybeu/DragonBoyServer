package nro.server.service.core.npc;

import nro.consts.ConstNpc;
import nro.consts.ConstsCmd;
import nro.server.service.core.system.ServerService;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.npc.Npc;
import nro.server.service.model.entity.npc.NpcFactory;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.entity.player.PlayerMagicTree;
import nro.server.service.model.template.MagicTreeTemplate;
import nro.server.manager.MagicTreeManager;
import nro.server.network.Message;
import nro.server.system.LogServer;

import java.io.DataOutputStream;
import java.util.List;

public class NpcService {

    private static final class SingletonHolder {
        private static final NpcService instance = new NpcService();
    }

    public static NpcService getInstance() {
        return NpcService.SingletonHolder.instance;
    }

    public void openMenuNpc(Player player, int npcId) {
        Npc npc = player.getArea().getNpcById(npcId);

        if (npc == null) {
            if (npcId == ConstNpc.LY_TIEU_NUONG) {
                Npc lyTieuNuong = NpcFactory.getNpc(ConstNpc.LY_TIEU_NUONG);
                if (lyTieuNuong != null) {
                    lyTieuNuong.openMenu(player);
                } else {
                    NpcService.getInstance().sendNpcTalkUI(player, 5, "Có lỗi xảy ra vui lòng thử lại sau.", -1);
                }
            } else {
                NpcService.getInstance().sendNpcTalkUI(player, 5, "Có lỗi xảy ra vui lòng thử lại sau.", -1);
            }
            return;
        }
        if (npc.isHide()) return;
        npc.openMenu(player);
    }

    public void confirmMenu(Player player, int npcId, int select) {
        Npc npc = player.getArea().getNpcById(npcId);
        if (npc == null) {
            if (npcId == ConstNpc.CON_MEO) {
                Npc conMeo = NpcFactory.getNpc(ConstNpc.CON_MEO);
                if (conMeo == null) {
                    throw new RuntimeException("Npc con mèo id: " + ConstNpc.CON_MEO + " không tồn tại");
                }
                conMeo.openUIConfirm(player, select);
            } else {
                ServerService.getInstance().sendHideWaitDialog(player);
            }
            return;
        }
        if (npc.isHide()) return;
        npc.openUIConfirm(player, select);
    }

    public void createMenu(Player player, int npcId, int indexMenu, String npcSay, String... menus) {
        try (Message message = new Message(32)) {
            player.getPlayerContext().setIndexMenu(indexMenu);
            DataOutputStream data = message.writer();
            data.writeShort(npcId);
            data.writeUTF(npcSay);
            data.writeByte(menus.length);
            for (var menu : menus) {
                data.writeUTF(menu);
            }
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("Error createMenu: " + ex.getMessage(), ex);
        }
    }

    public void sendNpcTalkUI(Player player, int npcId, String text, int avatarId) {
        try (Message message = new Message(38)) {
            DataOutputStream data = message.writer();
            data.writeShort(npcId);
            data.writeUTF(text);
            if (avatarId != -1) {
                data.writeShort(avatarId);
            }
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("Error sendNpcTalkMessage: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void sendNpcChatAllPlayerInArea(Player player, Npc npc, String text) {
        try (Message message = new Message(124)) {
            message.writer().writeShort(npc.getTempId());
            message.writer().writeUTF(text);
            player.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendNpcChatAllPlayerInArea: " + ex.getMessage());
        }
    }

    public void loadMagicTree(Player player, int type, List<String> texts) {
        try (Message message = new Message(-34)) {
            DataOutputStream data = message.writer();
            MagicTreeManager magicTreeManager = MagicTreeManager.getInstance();
            PlayerMagicTree plMagicTree = player.getPlayerMagicTree();
            List<MagicTreeTemplate.MagicTreePosition> magicTreePositions = magicTreeManager.getMagicTreePosition(plMagicTree.getLevel());
            Npc npc = player.getArea().getNpcById(4);
            data.writeByte(type);
            switch (type) {
                case 0: {
                    data.writeShort(magicTreeManager.getIconMagicTree(player));
                    data.writeUTF(magicTreeManager.getNameMagicTree(plMagicTree.getLevel()));
                    data.writeShort(npc.getX());
                    data.writeShort(npc.getY());
                    data.writeByte(plMagicTree.getLevel());
                    data.writeShort(plMagicTree.getCurrPeas());
                    data.writeShort(plMagicTree.getMaxPea());
                    data.writeUTF("");
                    data.writeInt(plMagicTree.isUpgrade() ? plMagicTree.getSecondUpgrade() : plMagicTree.getSecondPea());
                    data.writeByte(magicTreePositions.size());
                    for (var magicTreePosition : magicTreePositions) {
                        data.writeByte(magicTreePosition.x());
                        data.writeByte(magicTreePosition.y());
                    }
                    data.writeBoolean(plMagicTree.isUpgrade());
                    break;
                }
                case 1: {//
                    for (var text : texts) {
                        data.writeUTF(text);
                    }
                    break;
                }
                case 2: {// thu hoach dau
                    data.writeShort(plMagicTree.getCurrPeas());
                    data.writeInt(plMagicTree.getSecondPea());
                    break;
                }
            }
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("loadMagicTree: " + ex.getMessage(), ex);
        }
    }

    public void sendHideNpcInArea(Entity entity, Npc npc) {
        try (Message message = new Message(ConstsCmd.NPC_ADD_REMOVE)) {
            DataOutputStream writer = message.writer();
            writer.writeByte(npc.getTempId());
            writer.writeByte(npc.isHide() ? 0 : 1);
            entity.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendHideNpcInArea: " + ex.getMessage() + " for npc id: " + npc.getTempId(), ex);
        }
    }

}
