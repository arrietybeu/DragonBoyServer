package nro.data;

import nro.network.Message;
import nro.network.Session;
import nro.server.config.ConfigServer;
import nro.consts.ConstMsgNotMap;
import nro.server.manager.skill.SkillManager;
import nro.server.LogServer;

import javax.xml.crypto.Data;
import java.io.DataOutputStream;

public class DataSkill {

    public static void SendDataSkill(Session session) {
        SkillManager skillManager = SkillManager.getInstance();
        var nClasses = skillManager.getNClasses();
        var skillOptions = skillManager.getSkillOptions();

        try (Message message = new Message(-28)) {

            DataOutputStream data = message.writer();
            data.writeByte(ConstMsgNotMap.UPDATE_SKILL);
            data.writeByte(ConfigServer.VERSION_SKILL);
            data.writeByte(skillOptions.size());// send skill options
            for (var option : skillOptions) {
                data.writeUTF(option.name());
            }
            data.writeByte(nClasses.size());// 23
            for (var classSkill : nClasses) {
                data.writeUTF(classSkill.getName());
                data.writeByte(classSkill.getSkillTemplates().size());
                for (var skillTemplate : classSkill.getSkillTemplates()) {
                    data.writeByte(skillTemplate.getId());
                    data.writeUTF(skillTemplate.getName());
                    data.writeByte(skillTemplate.getMaxPoint());
                    data.writeByte(skillTemplate.getManaUseType());
                    data.writeByte(skillTemplate.getType());
                    data.writeShort(skillTemplate.getIconId());
                    data.writeUTF(skillTemplate.getDamInfo());
                    data.writeUTF(skillTemplate.getDescription());
                    data.writeByte(skillTemplate.getSkills().size());
                    for (var skill : skillTemplate.getSkills()) {
                        data.writeShort(skill.getSkillId());
                        data.writeByte(skill.getPoint());
                        data.writeLong(skill.getPowRequire());
                        data.writeShort(skill.getManaUse());
                        data.writeInt(skill.getCoolDown());
                        data.writeShort(skill.getDx());
                        data.writeShort(skill.getDy());
                        data.writeByte(skill.getMaxFight());
                        data.writeShort(skill.getDamage());
                        data.writeShort(skill.getPrice());
                        data.writeUTF(skill.getMoreInfo());
                    }
                }
            }
            session.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendDataSkill: " + e.getMessage());
        }
    }
}
