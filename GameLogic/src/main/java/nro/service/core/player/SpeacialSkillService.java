package nro.service.core.player;

import lombok.Getter;
import nro.service.model.player.Player;
import nro.server.network.Message;
import nro.server.LogServer;

public class SpeacialSkillService {

    @Getter
    private static SpeacialSkillService instance = new SpeacialSkillService();

    public void sendSpeacialSkill(Player player) {
        // SkillManager skillManager = SkillManager.getInstance();

        try (Message message = new Message(112)) {
            message.writer().writeByte(0);
            message.writer().writeShort(8584);
            message.writer().writeUTF("SpecialSkill");
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error showSpeacialSkill: " + e.getMessage());
        }
    }

    public void showInfoSpecialSkill(Player player) {
        // SkillManager skillManager = SkillManager.getInstance();

        try (Message message = new Message(112)) {
            message.writer().writeByte(1);
            message.writer().writeShort(1);// so luong tab special skill
            message.writer().writeUTF("Nội tại");
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error showInfoSpecialSkill: " + e.getMessage());
        }
    }
}
