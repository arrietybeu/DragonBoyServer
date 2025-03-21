package nro.service.core.usage.handler;

import nro.consts.ConstItem;
import nro.consts.ConstUseItem;
import nro.server.LogServer;
import nro.server.manager.skill.SkillManager;
import nro.service.core.player.SkillService;
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
            int levelSkill = this.getSkillLevelForItem(item);
            if (skillId == -1) {
                serverService.sendChatGlobal(player.getSession(), null, "Không thể học kỹ năng này", false);
                return;
            }

            SkillInfo skillNew = SkillManager.getInstance().getSkillInfoById(skillId, player.getGender(), levelSkill);
            if (skillNew == null) {
                serverService.sendChatGlobal(player.getSession(), null, "Không thể học kỹ năng này", false);
                return;
            }

            int skillLevel = player.getPlayerSkill().getSkillLevel(skillNew.getTemplate().getId());

            if (skillLevel >= skillNew.getPoint()) {
                serverService.sendChatGlobal(player.getSession(), null, String.format("Bạn đã nâng %s cấp %d", skillNew.getTemplate().getName(), skillNew.getPoint()), false);
                return;
            }

            if ((skillNew.getPoint() != 1 && skillLevel == -1) || (skillLevel != -1 && skillLevel + 1 != skillNew.getPoint())) {
                if (skillLevel == -1) {
                    serverService.sendChatGlobal(player.getSession(), null, String.format("Bạn cần học %s", skillNew.getTemplate().getName()), false);
                    return;
                }
                serverService.sendChatGlobal(player.getSession(), null, String.format("Bạn cần nâng %s cấp %d trước", skillNew.getTemplate().getName(), skillLevel + 1), false);
                return;
            }

            if (skillLevel == -1) {
                player.getPlayerSkill().addSkill(skillNew);
                serverService.sendChatGlobal(player.getSession(), null, String.format("Bạn đã học %s cấp %d", skillNew.getTemplate().getName(), levelSkill), false);
                SkillService.getInstance().sendLoadSkillInfoAll(player, player.getPlayerSkill().getSkills());
            } else {
                for (int i = 0; i < player.getPlayerSkill().getSkills().size(); i++) {
                    SkillInfo skillInfo = player.getPlayerSkill().getSkills().get(i);
                    if (skillInfo.getTemplate().getId() == skillNew.getTemplate().getId()) {

                        // set thời gian sửa dụng skill của kỹ năng cũ cho kỹ năng mới
                        skillNew.setLastTimeUseThisSkill(skillInfo.getLastTimeUseThisSkill());

                        // set skill mới vào vị trí cũ
                        player.getPlayerSkill().getSkills().set(i, skillNew);
                        serverService.sendChatGlobal(player.getSession(), null, String.format("Bạn đã nâng %s cấp %d", skillNew.getTemplate().getName(), levelSkill), false);
                        break;
                    }
                }
            }

            // loại bỏ sách học skill sau khi học hoặc nâng cấp level xong
            player.getPlayerInventory().subQuantityItemsBag(item, 1);

            // tính toán lại thông số của nhân vật
            player.getPlayerPoints().calculateStats();

            // send info skill to client
            UseItemService.getInstance().sendPlayerLearnSkill(player, skillNew, -1);

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
