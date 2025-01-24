package nro.data;

import nro.network.Message;
import nro.network.Session;
import nro.server.config.ConfigServer;
import nro.consts.ConstMsgNotMap;
import nro.server.manager.skill.SkillManager;
import nro.server.LogServer;

public class DataSkill {

    public static void SendDataSkill(Session session) {
        System.out.println("SendDataSkill");
        SkillManager skillManager = SkillManager.getInstance();
        var nClasses = skillManager.getNClasses();
        var skillOptions = skillManager.getSkillOptions();

        try (Message message = new Message(-28)) {
            message.writer().writeByte(ConstMsgNotMap.UPDATE_SKILL);
            message.writer().writeByte(ConfigServer.VERSION_SKILL);
            message.writer().writeByte(skillOptions.size());// send skill options
            for (var option : skillOptions) {
                message.writer().writeUTF(option.getName());
            }
            message.writer().writeByte(nClasses.size());// 23
            for (var classSkill : nClasses) {
                message.writer().writeUTF(classSkill.getName());
                message.writer().writeByte(classSkill.getSkillTemplates().size());
                for (var skillTemplate : classSkill.getSkillTemplates()) {
                    message.writer().writeByte(skillTemplate.getId());
                    message.writer().writeUTF(skillTemplate.getName());
                    message.writer().writeByte(skillTemplate.getMaxPoint());
                    message.writer().writeByte(skillTemplate.getManaUseType());
                    message.writer().writeByte(skillTemplate.getType());
                    message.writer().writeShort(skillTemplate.getIconId());
                    message.writer().writeUTF(skillTemplate.getDamInfo());
                    message.writer().writeUTF(skillTemplate.getDescription());
                    message.writer().writeByte(skillTemplate.getSkills().size());
                    for (var skill : skillTemplate.getSkills()) {
                        message.writer().writeShort(skill.getSkillId());
                        message.writer().writeByte(skill.getPoint());
                        message.writer().writeLong(skill.getPowRequire());
                        message.writer().writeShort(skill.getManaUse());
                        message.writer().writeInt(skill.getCoolDown());
                        message.writer().writeShort(skill.getDx());
                        message.writer().writeShort(skill.getDy());
                        message.writer().writeByte(skill.getMaxFight());
                        message.writer().writeShort(skill.getDamage());
                        message.writer().writeShort(skill.getPrice());
                        message.writer().writeUTF(skill.getMoreInfo());
                    }
                }
            }
            session.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendDataSkill: " + e.getMessage());
        }
    }
}
