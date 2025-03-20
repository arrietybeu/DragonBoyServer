package nro.service.core.usage.handler;

import nro.consts.ConstItem;
import nro.consts.ConstUseItem;
import nro.server.LogServer;
import nro.service.core.system.ServerService;
import nro.service.core.usage.AUseItemHandler;
import nro.service.core.usage.IUseItemHandler;
import nro.service.core.usage.UseItemService;
import nro.service.model.item.Item;
import nro.service.model.player.Player;
import nro.service.model.template.entity.SkillInfo;

@AUseItemHandler({ConstItem.TYPE_LEARN_SKILL})
public class LearnSkillUseItemHandler implements IUseItemHandler {

    @Override
    public void use(Player player, int type, int index, Item item, int... id) {
        try {
            if (type == ConstUseItem.USE_ITEM) {
                String info = "Bạn có chắc muốn dùng\n" + item.getTemplate().name() + "?";
                UseItemService.getInstance().eventUseItem(player, type, 1, index, info);
                return;
            }


            ServerService serverService = ServerService.getInstance();
            int skillId = this.getSkillIdForItem(item);
            if (skillId < 0) {
                serverService.sendChatGlobal(player.getSession(), null, "Sách kỹ năng không hợp lệ!", false);
                return;
            }

            SkillInfo currentSkill = player.getPlayerSkill().getSkillById(skillId);

            if (currentSkill == null) {
                if (this.getSkillLevelForItem(item) == 1) {
//                    player.getPlayerSkill().learnSkill(skillId, 1); // Học skill level 1
                    serverService.sendChatGlobal(player.getSession(), null, "Bạn đã học kỹ năng cấp 1!", false);
                } else {
                    serverService.sendChatGlobal(player.getSession(), null, "Bạn cần học cấp 1 trước!", false);
                }
                return;
            }

            int currentLevel = currentSkill.getPoint();
            int nextLevel = this.getSkillLevelForItem(item);

            if (nextLevel == currentLevel + 1) {
                if (currentLevel >= currentSkill.getTemplate().getMaxPoint()) {
                    serverService.sendChatGlobal(player.getSession(), null, "Bạn đã đạt cấp tối đa của kỹ năng này!", false);
                    return;
                }
//                player.getPlayerSkill().learnSkill(skillId, nextLevel);
                serverService.sendChatGlobal(player.getSession(), null, "Bạn đã nâng cấp kỹ năng lên cấp " + nextLevel + "!", false);
            } else {
                serverService.sendChatGlobal(player.getSession(), null, "Bạn cần học cấp " + (currentLevel + 1) + " trước!", false);
            }
        } catch (Exception ex) {
            LogServer.LogException("LearnSkillUseItemHandler: " + ex.getMessage(), ex);
        }
    }

    private int getSkillIdForItem(Item item) {
        int skillId = item.getTemplate().part();
        return (skillId < 0) ? -1 : skillId;
    }

    private int getSkillLevelForItem(Item item) {
        return item.getTemplate().level();
    }
}
