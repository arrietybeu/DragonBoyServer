package nro.service.core.player;

import lombok.Getter;
import nro.consts.ConstMsgSubCommand;
import nro.consts.ConstsCmd;
import nro.service.model.entity.player.Player;
import nro.service.model.template.entity.SkillInfo;
import nro.server.system.LogServer;
import nro.server.network.Message;

import java.io.DataOutputStream;
import java.util.List;

public class SkillService {

    @Getter
    private static final SkillService instance = new SkillService();

    public void sendPlayerAttackMonster(Player player, int mobId) {
        try (Message message = new Message(54)) {
            DataOutputStream writer = message.writer();
            writer.writeInt(player.getId());
            writer.writeByte(player.getSkills().getSkillSelect().getSkillId());
            writer.writeByte(mobId);
            player.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception e) {
            LogServer.LogException("SkillService: sendPlayerAttackMonster: " + e.getMessage(), e);
        }
    }

    public void sendLoadSkillInfoAll(Player player, List<SkillInfo> skillsInfo) {
        try (Message message = new Message(ConstsCmd.SUB_COMMAND)) {
            DataOutputStream writer = message.writer();
            writer.writeByte(ConstMsgSubCommand.LOAD_MY_SKILLS);
            writer.writeByte(skillsInfo.size());
            for (SkillInfo skillInfo : skillsInfo) {
                writer.writeShort(skillInfo.getSkillId());
            }
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("SkillService: sendLoadSkillInfoAll: " + ex.getMessage(), ex);
        }
    }

    public void sendUseSkillNotFocus(Player player, int type, int skillId, int second) {
        try (Message message = new Message(ConstsCmd.SKILL_NOT_FOCUS)) {
            DataOutputStream writer = message.writer();
            writer.writeByte(type);
            writer.writeInt(player.getId());
            writer.writeShort(skillId);
            writer.writeShort(second);
            player.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception e) {
            LogServer.LogException("SkillService: sendUseSkillNotFocus: " + e.getMessage(), e);
        }
    }


}
