package nro.server.service.model.entity.ai.boss.types.than_meo;

import nro.consts.ConstNpc;
import nro.server.manager.MapManager;
import nro.server.service.core.map.AreaService;
import nro.server.service.core.social.ChatService;
import nro.server.service.model.entity.ai.AIState;
import nro.server.service.model.entity.ai.boss.Boss;
import nro.server.service.model.entity.ai.boss.BossAIController;
import nro.server.service.model.entity.npc.Npc;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.map.GameMap;
import nro.server.service.model.map.areas.Area;
import nro.server.system.LogServer;
import nro.commons.utils.Rnd;

public class ThanMeoKarinController extends BossAIController {

    private static final String[] THACH_THUC = new String[]{"Tuổi lồn", "Tuổi con cặc", "Tuổi buồi"};


    @Override
    public void handleIdle(Boss boss) {
        super.handleIdle(boss);
    }

    @Override
    public void handleChasing(Boss boss) {
        try {
            // lấy player mà boss target
            Player target = boss.getEntityTargetAsPlayer();

            // set player vừa nãy được boss target
            boss.setLastPlayerTarget(target);

            if (target == null) {
                // nếu không có player nào target thì boss sẽ chuyển sang trạng thái tìm kiếm
                if (boss.getLastPlayerTarget() != null) {
                    boss.getLastPlayerTarget().changeTypePlayerKill(0);
                    boss.setLastPlayerTarget(null);
                }
                boss.changeTypePlayerKill(0);
                if (boss.isAutoDespawn()) {
                    boss.dispose();
                    return;
                }
                return;
            }
            boss.changeTypePlayerKill(3);
            target.changeTypePlayerKill(3);
            int distance = Math.abs(boss.getX() - target.getX());

            // neu đủ gần → chuyển sang đánh
            if (distance <= 50) {
                boss.setState(AIState.ATTACKING);
            } else {
                // di chuyển về phía target
                this.goToPlayer(boss, target);
            }
        } catch (Exception e) {
            LogServer.LogException("ChasingEventHandler.handle: " + e.getMessage(), e);
        }
    }

    @Override
    public void handleAttacking(Boss boss) {
        try {
            if (boss == null) return;
            if (boss.getPoints().isDead()) return;

            Player target = boss.getEntityTargetAsPlayer();

            if (target == null) {
                if (boss.getLastPlayerTarget() != null) {
                    boss.getLastPlayerTarget().changeTypePlayerKill(0);
                    boss.setLastPlayerTarget(null);
                }
                if (boss.isAutoDespawn()) {
                    boss.dispose();
                    return;
                }
            }
            // neu target mất hoặc chết hoặc khác map → về SEARCHING
            if (boss.isValidTarget(target)) {
                boss.setState(AIState.SEARCHING);
                return;
            }

            int distance = Math.abs(boss.getX() - target.getX());

            // neu target quá xa → quay lại chase
            if (distance > 50) {
                boss.setState(AIState.CHASING);
                return;
            }

            // Gọi kỹ năng đang chọn (hoặc bạn có thể random skill nếu muốn)
            if (boss.getSkills() == null) return;

            boss.getSkills().selectRandomSkill();

            if (boss.getSkills().getSkillSelect() == null) return;

            long now = System.currentTimeMillis();
            if (now - boss.getLastAttackTime() < boss.getSkills().getSkillSelect().getBaseCooldown()) return;
            boss.setLastAttackTime(now);

            boss.getSkills().useSkill(target);

            // Sau khi đánh, vẫn giữ ATTACKING hoặc chuyển về SEARCHING tùy ý bạn
            // ai.setStateIfNot(AIState.SEARCHING);
        } catch (Exception e) {
            LogServer.LogException("AttackingEventHandler.handle: " + e.getMessage(), e);
        }
    }

    @Override
    public void handleGoToMap(Boss boss) {
        try {
            if (boss == null) return;
            if (boss.getArea() == null && !boss.isBossInMap()) {
                int mapId = boss.getMapsId()[Rnd.nextInt(0, boss.getMapsId().length)];
                GameMap mapNew = MapManager.getInstance().findMapById(mapId);
                if (mapNew == null) return;
                Area newArea = mapNew.getArea(-1, boss);
                AreaService.getInstance().gotoMap(boss, newArea, boss.getX(), boss.getY());
                boss.setBossInMap(true);
                boss.setState(AIState.CHAT);
                return;
            }
            if (boss.getArea() != null && !boss.isBossInMap()) {
                AreaService.getInstance().gotoMap(boss, boss.getArea(), boss.getX(), boss.getY());
                boss.setBossInMap(true);
                boss.setState(AIState.CHAT);
            }
        } catch (Exception ex) {
            LogServer.LogException("GoToMapEventHandler.handle: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void handleChat(Boss boss) {
        try {
            Player target = boss.getEntityTargetAsPlayer();
            if (target == null) {
                if (boss.getLastPlayerTarget() != null) {
                    boss.getLastPlayerTarget().changeTypePlayerKill(0);
                    boss.setLastPlayerTarget(null);
                }
                if (boss.isAutoDespawn()) {
                    boss.dispose();
                    return;
                }
            }

            if (boss.getNextState() == null) {
                ChatService.getInstance().chatMap(boss, "Ta sẽ dậy ngươi vài chiêu");
                boss.onEnterStateWithDelay(AIState.CHAT, 1000, AIState.SEARCHING);
                return;
            }

            boss.trySwitchToNextState();
        } catch (Exception exception) {
            LogServer.LogException("MoveEventHandler.entityChat: " + exception.getMessage(), exception);
        }
    }

    @Override
    public void handleDeath(Boss boss) {
        try {
            if (boss == null) return;
            if (boss.getLastPlayerTarget() != null) {
                boss.getLastPlayerTarget().changeTypePlayerKill(0);
                boss.setLastPlayerTarget(null);
            }
            ChatService.getInstance().chatMap(boss, "Ok ta chịu thua");

            if (boss.getNextState() != AIState.LEAVING_MAP) {
                boss.onEnterStateWithDelay(AIState.DEAD, 2000, AIState.LEAVING_MAP);
                return;
            }

            boss.trySwitchToNextState();

        } catch (Exception e) {
            LogServer.LogException("DeathEventHandler.handle: " + e.getMessage(), e);
        }
    }

    @Override
    public void handleSearching(Boss boss) {
        try {
            Area area = boss.getArea(); // hoặc boss.getArea()
            if (area == null) return;

            // tìm người chơi gần nhất
            Player nearestPlayer = this.findNearestPlayer(boss, area);

            // nếu tìm thấy người chơi thì chuyển trạng thái của boss sang đuổi, còn nếu không tìm thấy là đứng im
            if (nearestPlayer != null) {
                boss.setEntityTarget(nearestPlayer);
                boss.setState(AIState.CHASING);
                nearestPlayer.getPlayerTask().doneTask(9, 1);
            } else {
                boss.changeTypePlayerKill(0);
                if (boss.getLastPlayerTarget() != null) {
                    boss.getLastPlayerTarget().changeTypePlayerKill(0);
                    if (!boss.getLastPlayerTarget().getArea().equals(boss.getArea())) {
                        boss.dispose();
                        return;
                    }
                    boss.setLastPlayerTarget(null);
                }

                boss.setEntityTarget(null);
                String chat = THACH_THUC[Rnd.nextInt(0, THACH_THUC.length - 1)];
                ChatService.getInstance().chatMap(boss, chat);

                if (boss.getEntityTarget() == null) {
                    if (boss.getNextState() != AIState.DEAD) {
                        boss.onEnterStateWithDelay(AIState.SEARCHING, 2000, AIState.DEAD);
                        return;
                    }
                    boss.trySwitchToNextState();
                }
            }
        } catch (Exception e) {
            LogServer.LogException("SearchingEventHandler.handle: " + e.getMessage(), e);
        }
    }

    @Override
    public void handleLeavingMap(Boss boss) {
        // hiển thị npc lên
        Npc npc = boss.getArea().getNpcById(ConstNpc.THAN_MEO_KARIN);
        npc.turnOnHideNpc(boss, false);

        // xóa boss khỏi map
        super.handleLeavingMap(boss);
    }

}
