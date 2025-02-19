package nro.service;

import lombok.Getter;
import nro.model.npc.Npc;
import nro.model.player.Player;
import nro.server.network.Message;
import nro.server.LogServer;

import java.io.DataOutputStream;

public class NpcService {

    @Getter
    private static final NpcService instance = new NpcService();

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

    public void sendNpcChatAllPlayerInArea(Player player, Npc npc) {
        try (Message message = new Message(124)) {
            message.writer().writeShort(npc.getId());
            message.writer().writeUTF(npc.getName());
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("sendNpcChatAllPlayerInArea: " + ex.getMessage());
        }
    }
}
