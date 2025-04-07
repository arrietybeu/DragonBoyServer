package nro.server.service.model.entity.ai.boss.types.taupaypay;

import nro.server.manager.MapManager;
import nro.server.service.core.map.AreaService;
import nro.server.service.core.social.ChatService;
import nro.server.service.model.entity.ai.AIState;
import nro.server.service.model.entity.ai.boss.Boss;
import nro.server.service.model.entity.ai.boss.BossAIController;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.map.GameMap;
import nro.server.service.model.map.areas.Area;
import nro.server.system.LogServer;
import nro.utils.Util;

public class TauPayPayController extends BossAIController {

    private static final int ATTACK_RANGE = 60;
    private static final long CHAT_DURATION = 10_000;
    private static final String[] THACH_THUC = new String[]{"Tuổi lồn", "Tuổi con cặc", "Tuổi buồi"};

    @Override
    public void handleIdle(Boss boss) {
        try {
            if (boss.getArea() == null || !boss.isBossInMap()) {
                boss.setState(AIState.GO_TO_MAP);
                return;
            }

            boss.tickAfkTimeout++;
            if (boss.isValidBossAfkTimeout()) {
                boss.dispose();
            }

            if (boss.getEntityTarget() == null) {
                if (boss.getNextState() != AIState.SEARCHING) {
                    boss.onEnterStateWithDelay(AIState.IDLE, 5000, AIState.SEARCHING);
                    return;
                }
                boss.trySwitchToNextState();
            }
        } catch (Exception e) {
            LogServer.LogException("IdleEventHandler.handle: " + e.getMessage(), e);
        }
    }

    @Override
    public void handleChasing(Boss boss) {
        try {
            Player target = boss.getEntityTargetAsPlayer();

            boss.setLastPlayerTarget(target);

            // target không còn hoặc không hợp lệ
            if (target == null) {
                if (boss.isAutoDespawn()) {
                    boss.dispose();
                    return;
                }
                if (boss.getLastPlayerTarget() != null) {
                    boss.getLastPlayerTarget().changeTypePlayerKill(0);
                    boss.setLastPlayerTarget(null);
                }
                if (boss.isValidTarget(target)) {
                    boss.setEntityTarget(null);
                    boss.setState(AIState.SEARCHING);
                    boss.changeTypePlayerKill(0);
                    return;
                }
                return;
            }
            boss.changeTypePlayerKill(3);
            target.changeTypePlayerKill(3);
            int distance = Math.abs(boss.getX() - target.getX());

            // neu đủ gần → chuyển sang đánh
            if (distance <= ATTACK_RANGE) {
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
//                boss.setEntityTarget(null);
                boss.setState(AIState.SEARCHING);
                return;
            }

            int distance = Math.abs(boss.getX() - target.getX());

            // neu target quá xa → quay lại chase
            if (distance > ATTACK_RANGE) {
                boss.setState(AIState.CHASING);
                return;
            }

            // Gọi kỹ năng đang chọn (hoặc bạn có thể random skill nếu muốn)
            if (boss.getSkills() == null) {
                return;
            }

            long now = System.currentTimeMillis();
            if (now - boss.getLastAttackTime() < boss.getAttackCooldown()) {
                return;
            }

            boss.setLastAttackTime(now);

            boss.getSkills().selectRandomSkill();

            if (boss.getSkills().getSkillSelect() == null) {
                return;
            }

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
            if (boss.isInState(AIState.GO_TO_MAP) || boss.getArea() == null && !boss.isBossInMap()) {
                int mapId = boss.getMapsId()[Util.nextInt(0, boss.getMapsId().length)];
                GameMap mapNew = MapManager.getInstance().findMapById(mapId);
                if (mapNew == null) return;
                Area newArea = mapNew.getArea(-1, boss);
                AreaService.getInstance().changerMapByShip(boss, mapNew.getId(), boss.getX(), boss.getY(), 1, newArea);
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
//                    if (boss.getTextChat().isEmpty()) {
//                        boss.setState(AIState.SEARCHING);
//                    } else {
//                        ChatService.getInstance().chatMap(ai, boss.getTextChat());
//                    }

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
                ChatService.getInstance().chatMap(boss, "đù má chúng mầy tau cho chúng mầy 10s để chạy đi");
                boss.onEnterStateWithDelay(AIState.CHAT, CHAT_DURATION, AIState.SEARCHING);
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
            //111
            ChatService.getInstance().chatMap(boss, "Khá lắm hẹn ngày tái ngộ");
            boss.setState(AIState.LEAVING_MAP);
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
                String chat = THACH_THUC[Util.nextInt(0, THACH_THUC.length - 1)];
                ChatService.getInstance().chatMap(boss, chat);
                boss.setState(AIState.IDLE);
            }
        } catch (Exception e) {
            LogServer.LogException("SearchingEventHandler.handle: " + e.getMessage(), e);
        }
    }

    @Override
    public void handleLeavingMap(Boss boss) {
        try {
            boss.setTeleport(1);
            boss.dispose();
        } catch (Exception exception) {
            LogServer.LogException("MoveEventHandler.handleLeavingMap: " + exception.getMessage(), exception);
        }
    }

}
