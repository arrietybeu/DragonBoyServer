package nro.service;

import lombok.Getter;
import nro.model.npc.Npc;
import nro.model.player.Player;
import nro.model.player.PlayerMagicTree;
import nro.server.manager.MagicTreeManager;
import nro.server.manager.MapManager;
import nro.server.network.Message;
import nro.server.LogServer;

import java.io.DataOutputStream;

public class NpcService {

    @Getter
    private static final NpcService instance = new NpcService();

    public void openMenuNpc(Player player, int npcId) {
        Npc npc = player.getArea().getNpcById(npcId);
        if (npc != null) {
            npc.openMenu(player);
        } else {
            this.sendNpcChatAllPlayerInArea(player, npc, "Xin chào");
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

    public void loadMagicTree(Player player, int type) {
        try (Message message = new Message(-34)) {
            DataOutputStream data = message.writer();
            MagicTreeManager magicTreeManager = MagicTreeManager.getInstance();
            PlayerMagicTree playerMagicTree = player.getPlayerMagicTree();
            Npc npc = player.getArea().getNpcById(4);
            data.writeByte(type);
            switch (type) {
                case 0: {
                    data.writeShort(magicTreeManager.getIconMagicTree(player));
                    data.writeUTF(magicTreeManager.getNameMagicTree(playerMagicTree.getLevel()));
                    data.writeShort(npc.getX());
                    data.writeShort(npc.getY());
                    data.writeByte(playerMagicTree.getLevel());
                    data.writeShort(playerMagicTree.getCurrPeas());
                    data.writeShort(playerMagicTree.getMaxPea());
                    data.writeUTF("");
                    data.writeInt(1111);

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
