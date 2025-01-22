package nro.data;

import nro.model.template.entity.SkillInfo;
import nro.network.Message;
import nro.network.Session;
import nro.server.config.ConfigServer;
import nro.consts.ConstMsgNotMap;
import nro.server.manager.skill.SkillManager;
import nro.server.LogServer;

public class DataSkill {

    public static void SendDataSkill() {
        SkillManager skillManager = SkillManager.getInstance();

        var version = skillManager.getSize();

        try (Message message = new Message(-28)) {
            message.writer().writeByte(ConstMsgNotMap.UPDATE_SKILL);

            message.writer().writeByte(ConfigServer.VERSION_SKILL);

            message.writer().writeByte(0);// send skill options

            message.writer().writeByte(version);// 23

            for (var nClass : skillManager.getNClasses().values()) {

                message.writer().writeUTF(nClass.getName());
                message.writer().writeByte(nClass.getSkillTemplates().size());

                for (var skill : nClass.getSkillTemplates()) {
                    message.writer().writeByte(skill.getSkillId());
                    message.writer().writeUTF(skill.getName());
                    message.writer().writeByte(skill.getMaxPoint());
                    message.writer().writeByte(skill.getManaUseType());
                    message.writer().writeByte(skill.getType());
                    message.writer().writeShort(skill.getIconId());
                    message.writer().writeUTF(skill.getDamInfo());
                    message.writer().writeUTF(skill.getDescription());
                    message.writer().writeByte(skill.getSkillInfo().size());
                    for (var skillInfo : skill.getSkillInfo()) {
                        message.writer().writeShort(skillInfo.skillId);
                        message.writer().writeByte(skillInfo.point);
                        message.writer().writeLong(skillInfo.powRequire);
                        message.writer().writeShort(skillInfo.manaUse);
                        message.writer().writeInt(skillInfo.coolDown);
                        message.writer().writeShort(skillInfo.dx);
                        message.writer().writeShort(skillInfo.dy);
                        message.writer().writeByte(skillInfo.maxFight);
                        message.writer().writeShort(skillInfo.damage);
                        message.writer().writeShort(skillInfo.price);
                        message.writer().writeUTF(skillInfo.moreInfo);

                        System.out.println("Skill class: " + skill.getSkillId() + " skill info id: " + skillInfo.skillId + " skill name: " + skill.getName());
                    }
                }
            }
        } catch (Exception e) {
            LogServer.LogException("Error sendDataSkill: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void SendDataSkill(Session session) {
        SkillManager skillManager = SkillManager.getInstance();

        var version = skillManager.getSize();

        try (Message message = new Message(-28)) {
            message.writer().writeByte(ConstMsgNotMap.UPDATE_SKILL);

            message.writer().writeByte(ConfigServer.VERSION_SKILL);

            message.writer().writeByte(0);// send skill options

            message.writer().writeByte(version);// 23

            for (var nClass : skillManager.getNClasses().values()) {

                message.writer().writeUTF(nClass.getName());
                message.writer().writeByte(nClass.getSkillTemplates().size());

                for (var skill : nClass.getSkillTemplates()) {
                    message.writer().writeByte(skill.getSkillId());
                    message.writer().writeUTF(skill.getName());
                    message.writer().writeByte(skill.getMaxPoint());
                    message.writer().writeByte(skill.getManaUseType());
                    message.writer().writeByte(skill.getType());
                    message.writer().writeShort(skill.getIconId());
                    message.writer().writeUTF(skill.getDamInfo());
                    message.writer().writeUTF(skill.getDescription());
                    message.writer().writeByte(skill.getSkillInfo().size());
                    System.out.println("id skill: " + skill.getSkillId() + " size() = " + skill.getSkillInfo().size());
                    for (SkillInfo skillInfo : skill.getSkillInfo()) {
                        message.writer().writeShort(skillInfo.skillId);
                        message.writer().writeByte(skillInfo.point);
                        message.writer().writeLong(skillInfo.powRequire);
                        message.writer().writeShort(skillInfo.manaUse);
                        message.writer().writeInt(skillInfo.coolDown);
                        message.writer().writeShort(skillInfo.dx);
                        message.writer().writeShort(skillInfo.dy);
                        message.writer().writeByte(skillInfo.maxFight);
                        message.writer().writeShort(skillInfo.damage);
                        message.writer().writeShort(skillInfo.price);
                        message.writer().writeUTF(skillInfo.moreInfo);
                    }
                }
            }
            session.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendDataSkill: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
