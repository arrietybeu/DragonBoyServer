package nro.service.core.usage;

import lombok.Getter;
import nro.consts.ConstMsgSubCommand;
import nro.consts.ConstsCmd;
import nro.service.model.player.Player;
import nro.server.LogServer;
import nro.server.network.Message;
import nro.service.model.template.entity.SkillInfo;

import java.io.DataOutputStream;

public class UseItemService {

    @Getter
    private static final UseItemService instance = new UseItemService();

    public void eventUseItem(Player player, int itemAction, int where, int index, String info) {
        try (Message message = new Message(ConstsCmd.USE_ITEM)) {
            DataOutputStream data = message.writer();
            data.writeByte(itemAction);
            data.writeByte(where);
            data.writeByte(index);
            data.writeUTF(info);
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("eventUseItem: " + ex.getMessage(), ex);
        }
    }

    // học skill = sách type = -1
    // chưa biết khi vô game nếu có skill mới thì gửi type = 0 và id skill mới
    public void sendPlayerLearnSkill(Player player, SkillInfo skillInfo, int type) {
        try (Message message = new Message(ConstsCmd.SUB_COMMAND)) {
            DataOutputStream writer = message.writer();
            writer.writeByte(ConstMsgSubCommand.UPDATE_MY_SKILLS);
            writer.writeShort(skillInfo.getSkillId());

            switch (type) {
                case 0 -> writer.writeShort(skillInfo.getPoint());
                case 1 -> {
                }
            }

            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("SkillService: sendPlayerLearnSkill: " + e.getMessage());
        }
    }
}
