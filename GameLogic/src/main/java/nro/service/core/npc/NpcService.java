package nro.service.core.npc;

import lombok.Getter;
import nro.service.core.system.ServerService;
import nro.service.model.model.npc.Npc;
import nro.service.model.model.npc.NpcFactory;
import nro.service.model.model.player.Player;
import nro.service.model.model.player.PlayerMagicTree;
import nro.service.model.model.template.MagicTreeTemplate;
import nro.server.manager.MagicTreeManager;
import nro.server.network.Message;
import nro.server.LogServer;

import java.io.DataOutputStream;
import java.util.List;

public class NpcService {

    @Getter
    private static final NpcService instance = new NpcService();

    public void openMenuNpc(Player player, int npcId) {
        Npc npc = player.getArea().getNpcById(npcId);

        if (npc == null) {
            if (npcId == 54) {
                Npc lyTieuNuong = NpcFactory.getNpc(54);
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

        npc.openMenu(player);
    }

    public void confirmMenu(Player player, int npcId, int select) {
        Npc npc = player.getArea().getNpcById(npcId);
        if (npc == null) {
            ServerService.getInstance().sendHideWaitDialog(player);
            return;
        }
        npc.openUIConFirm(player, select);
    }

    public void createMenu(Player player, int npcId, int indexMenu, String npcSay, String... menus) {
        try (Message message = new Message(32)) {
            player.getPlayerStatus().setIndexMenu(indexMenu);
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

    public void sendNpcChatForPlayer(Player player) {
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
            LogServer.LogException("loadMagicTree: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
