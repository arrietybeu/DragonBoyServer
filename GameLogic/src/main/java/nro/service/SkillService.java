package nro.service;

import lombok.Getter;
import nro.model.player.Player;
import nro.server.LogServer;
import nro.server.network.Message;

import java.io.DataOutputStream;

public class SkillService {

    @Getter
    private static final SkillService instance = new SkillService();

    public void sendPlayerAttackMonster(Player player, int mobId) {
        try (Message message = new Message(54)) {
            DataOutputStream writer = message.writer();
            writer.writeInt(player.getId());
            writer.writeByte(player.getPlayerSkill().getSkillSelect().getSkillId());
            writer.writeByte(mobId);
            player.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception e) {
            LogServer.LogException("SkillService: sendPlayerAttackMonster: " + e.getMessage());
        }
    }
}
