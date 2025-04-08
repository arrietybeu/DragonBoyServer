package nro.server.service.model.entity.npc.handler;

import nro.consts.ConstBoss;
import nro.consts.ConstMap;
import nro.consts.ConstMenu;
import nro.consts.ConstNpc;
import nro.server.service.core.npc.NpcService;
import nro.server.service.model.entity.ai.boss.BossFactory;
import nro.server.service.model.entity.npc.ANpcHandler;
import nro.server.service.model.entity.npc.Npc;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.task.TaskMain;

@ANpcHandler({ConstNpc.THAN_MEO_KARIN})
public class ThanMeoKarin extends Npc {

    public ThanMeoKarin(int tempId, int status, int mapId, int cx, int cy, int avatar) {
        super(tempId, status, mapId, cx, cy, avatar);
    }

    @Override
    public void openMenu(Player player) {
        if (player.getPlayerTask().getTaskMain() == null) return;

        TaskMain taskMain = player.getPlayerTask().getTaskMain();
        if (player.getPlayerTask().checkDoneTaskTalkNpc(this)) return;

        String npcSay;
        if (taskMain.getId() == 10 && taskMain.getIndex() == 0) {
            npcSay = "Muốn chiến thắng Tàu Pảy Pảy phải đánh bại được ta đã";
            NpcService.getInstance().createMenu(
                    player, this.getTempId(), ConstMenu.MENU_CAT_BOSS_TO_TASK, npcSay,
                    "Đăng ký\ntập\ntự động",
                    "Nhiệm vụ",
                    "Tập luyện\nvới\nThần Mèo",
                    "Tập luyện\nvới\nYajirô"
            );
        } else {
            npcSay = "Con hãy bay theo cây Gậy Như Ý trên đỉnh tháp để đến Thần Điện gặp Thượng đế\nCon rất xứng đáng để làm đệ tử ông ấy.";
            NpcService.getInstance().createMenu(
                    player, this.getTempId(), ConstMenu.BASE_MENU, npcSay,
                    "Đăng ký\ntập\ntự động",
                    "Tập luyện\nvới\nThần Mèo",
                    "Tập luyện\nvới\nYajirô"
            );
        }
    }

    @Override
    public void openUIConfirm(Player player, int select) {
        if (player.getArea().getMap().getId() == ConstMap.THAP_KARIN) {
            String npcSay;
            switch (player.getPlayerStatus().getIndexMenu()) {
                case ConstMenu.BASE_MENU -> {
                    switch (select) {
                        case 0 -> {
                            // TODO create menu đăng ký tập tự động
                        }
                        case 1 -> playerSendChallengeCatBoss(player);
                        case 2 -> {
                            // TODO create menu tập luyện với Yajiro
                        }
                    }
                }
                case ConstMenu.MENU_CAT_BOSS_TO_TASK -> {
                    switch (select) {
                        case 0 -> {
                            // TODO crea te menu đăng ký tập tự động
                        }
                        case 1 -> {
                            // TODO create menu nhiệm vụ
                        }
                        case 2 -> {
                            npcSay = "Con có chắc muốn tập luyện?\nTập luyện với ta sẽ tăng 20 sức mạnh mỗi phút";
                            NpcService.getInstance().createMenu(
                                    player, this.getTempId(), ConstMenu.MENU_CHALLENGE_CAT_BOSS, npcSay,
                                    "Đồng ý\nluyện tập",
                                    "Không\nđồng ý"
                            );
                        }
                        case 3 -> {
                            // TODO create menu tập luyện với Yajiro
                        }
                    }
                }
                case ConstMenu.MENU_REGISTER_TRANINING -> {
                    // TODO
                }
                case ConstMenu.MENU_CHALLENGE_CAT_BOSS -> {
                    switch (select) {
                        case 0 -> playerSendChallengeCatBoss(player);
                        case 1 -> {
                            // TODO không làm gì cạ
                        }
                    }
                }
                case ConstMenu.MENU_CHALLENGE_YAJIRO_BOSS -> {
                }
            }
        }
    }

    private void playerSendChallengeCatBoss(Player player) {
        this.turnOnHideNpc(player, true);
        BossFactory.getInstance().trySpawnSpecialBossInArea(player, player.getArea(), this.getX(), this.getY(), ConstBoss.THAN_MEO_KARIN);
    }
}
