package nro.service.core.social;

import lombok.Getter;
import nro.service.model.clan.Clan;
import nro.service.model.clan.ClanMessage;
import nro.service.model.player.Player;
import nro.server.network.Message;
import nro.server.LogServer;

import java.io.DataOutputStream;
import java.util.List;

public class ClanService {

    @Getter
    private static final ClanService instance = new ClanService();

    public void sendListClan(Player player) {
        try (Message message = new Message(-47)) {

            // DataOutputStream data = message.writer();

        } catch (Exception ex) {
            LogServer.LogException("Error send list Clan: " + ex.getMessage(), ex);
        }
    }

    public void sendClanInfo(Player player) {
        try (Message message = new Message(-53)) {
            Clan clan = player.getClan();
            DataOutputStream output = message.writer();
            if (clan == null) {
                output.writeInt(-1);
            } else {
                output.writeInt(clan.getId());
                output.writeUTF(clan.getName());
                output.writeUTF(clan.getSlogan());
                output.writeShort(clan.getImgID());
                output.writeUTF(clan.getPowerPoint());
                output.writeUTF(clan.getLeaderName());
                output.writeByte(clan.getCurrMember());
                output.writeByte(clan.getMaxMember());
                output.writeByte(player.getRole());
                output.writeInt(clan.getClanPoint());
                output.writeByte(clan.getLevel());

                for (var member : clan.getClanMembers()) {
                    output.writeInt(member.getId());
                    output.writeShort(member.getHead());
                    output.writeShort(member.getHeadICON());
                    output.writeShort(member.getLeg());
                    output.writeShort(member.getBody());
                    output.writeUTF(member.getName());
                    output.writeByte(member.getRole());
                    output.writeUTF(member.getPowerPoint());
                    output.writeInt(member.getDonate());
                    output.writeInt(member.getReceive_donate());
                    output.writeInt(member.getClanPoint());
                    output.writeInt(member.getCurClanPoint());
                    output.writeInt(member.getJoinTime());
                }
                List<ClanMessage> clanMessages = clan.getCurrClanMessages();
                output.writeByte(clanMessages.size());
                for (var clanMessage : clanMessages) {
                    output.writeByte(clanMessage.getType());
                    output.writeInt(clanMessage.getId());
                    output.writeInt(clanMessage.getPlayerId());
                    output.writeUTF(clanMessage.getPlayerName());
                    output.writeByte(clanMessage.getRole());
                    output.writeInt(clanMessage.getTime());
                    switch (clanMessage.getType()) {
                        case 0:
                            output.writeUTF(clanMessage.getText());
                            output.writeByte(clanMessage.getColor());
                            break;
                        case 1:
                            output.writeByte(clanMessage.getRecieve());
                            output.writeByte(clanMessage.getMaxCap());
                            output.writeByte(clanMessage.isNewClanMessage() ? 1 : 0);
                            break;
                    }
                }
            }
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendClanInfo: " + e.getMessage(), e);
        }
    }

}
