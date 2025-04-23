package nro.server.service.core.player;

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

    private static final class SingletonHolder {
        private static final SkillService instance = new SkillService();
    }

    public static SkillService getInstance() {
        return SkillService.SingletonHolder.instance;
    }

    public void sendEntityAttackMonster(Entity entity, int mobId) {
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

    public void sendEntityAttackEntity(Entity entityAttack, Entity entityTarget, long damage, boolean isCritical) {
        try (Message message = new Message(ConstsCmd.PLAYER_ATTACK_PLAYER)) {
            DataOutputStream writer = message.writer();
            writer.writeInt(entityAttack.getId());
            // send skill id để client paint skill
            writer.writeByte(entityAttack.getSkills().getSkillSelect().getSkillId());
            writer.writeByte(1);// list id entity target
            writer.writeInt(entityTarget.getId());
            // write is continue attack
            writer.writeByte(0);// continue attack
            entityAttack.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception e) {
            LogServer.LogException("SkillService: sendEntityAttackEntity: " + e.getMessage(), e);
        }
    }

    public void sendHaveAttackPlayer(Entity entityAttack, Entity target, long damage, boolean isCritical, boolean isEffect) {
        try (Message message = new Message(ConstsCmd.HAVE_ATTACK_PLAYER)) {
            DataOutputStream writer = message.writer();
            writer.writeInt(target.getId());
            writer.writeLong(target.getPoints().getCurrentHP());
            writer.writeLong(damage);
            writer.writeBoolean(isCritical);
            writer.writeByte(isEffect ? 37 : -1);
            entityAttack.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception exception) {
            LogServer.LogException("Error sendHaveAttackPlayer: " + exception.getMessage(), exception);
        } finally {
            this.sendEntityAttackEntity(entityAttack, target, damage, isCritical);
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
