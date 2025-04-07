package nro.server.service.core.player;

import lombok.Getter;
import nro.consts.ConstMsgSubCommand;
import nro.consts.ConstsCmd;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.template.entity.SkillInfo;
import nro.server.system.LogServer;
import nro.server.network.Message;

import java.io.DataOutputStream;
import java.util.List;

public class SkillService {

    @Getter
    private static final SkillService instance = new SkillService();

    public void sendEntityAttackMonster(Player entity, int mobId) {
        try (Message message = new Message(54)) {
            DataOutputStream writer = message.writer();
            writer.writeInt(entity.getId());
            writer.writeByte(entity.getSkills().getSkillSelect().getSkillId());
            writer.writeByte(mobId);
            entity.getArea().sendMessageToPlayersInArea(message, null);
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

    public void sendUseSkillNotFocus(Entity entity, int type, int skillId, int second) {
        try (Message message = new Message(ConstsCmd.SKILL_NOT_FOCUS)) {
            DataOutputStream writer = message.writer();
            writer.writeByte(type);
            writer.writeInt(entity.getId());
            writer.writeShort(skillId);
            writer.writeShort(second);
            entity.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception e) {
            LogServer.LogException("SkillService: sendUseSkillNotFocus: " + e.getMessage(), e);
        }
    }

    public void sendCooldownSkill(Player player) {
        try (Message message = new Message(ConstsCmd.UPDATE_COOLDOWN)) {
            DataOutputStream writer = message.writer();
            for (SkillInfo skillInfo : player.getSkills().getSkills()) {
                writer.writeShort(skillInfo.getSkillId());
                writer.writeInt((int) skillInfo.getCooldownRemaining());
            }
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("SkillService: sendCooldownSkill: " + e.getMessage(), e);
        }
    }

    public void sendEntityAttackEntity(Entity enttiyAttack, Entity entityTarget, long damage, boolean isCritical) {
        try (Message message = new Message(ConstsCmd.PLAYER_ATTACK_PLAYER)) {
            DataOutputStream writer = message.writer();
            writer.writeInt(enttiyAttack.getId());
            // send skill id để client paint skill
            writer.writeByte(enttiyAttack.getSkills().getSkillSelect().getSkillId());
            writer.writeByte(1);// list id entity target
            writer.writeInt(entityTarget.getId());

            // write is continue attack
            writer.writeByte(1);// continue attack
            writer.writeByte(enttiyAttack.getSkills().getTypSkill());// type skill
            writer.writeLong(damage);

            writer.writeBoolean(entityTarget.getPoints().isDead());
            writer.writeBoolean(isCritical);
            enttiyAttack.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception e) {
            LogServer.LogException("SkillService: sendEntityAttackEntity: " + e.getMessage(), e);
        }
    }

    public void sendSkillCooldown(Player player) {
        try (Message message = new Message(ConstsCmd.UPDATE_COOLDOWN)) {
            DataOutputStream writer = message.writer();
            for (SkillInfo skillInfo : player.getSkills().getSkills()) {
                writer.writeShort(skillInfo.getSkillId());
                writer.writeInt((int) skillInfo.getCooldownRemaining());
            }
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("SkillService: sendSkillCooldown: " + e.getMessage(), e);
        }
    }

}
