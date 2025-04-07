package nro.server.service.model.entity.ai.boss;


import nro.consts.ConstTypeObject;
import nro.server.service.core.map.AreaService;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.map.areas.Area;
import nro.server.system.LogServer;
import nro.utils.Util;

public abstract class BossAIController {

    public void update(Boss boss) {
        if (boss == null) return;
        switch (boss.getState()) {
            case IDLE -> handleIdle(boss);
            case SEARCHING -> handleSearching(boss);
            case CHASING -> handleChasing(boss);
            case ATTACKING -> handleAttacking(boss);
            case GO_TO_MAP -> handleGoToMap(boss);
            case CHAT -> handleChat(boss);
            case DEAD -> handleDeath(boss);
            case LEAVING_MAP -> handleLeavingMap(boss);
            default -> LogServer.LogWarning("Unknown boss state: " + boss.getState());
        }
    }

    public void goToPlayer(Boss boss, Entity target) {
        if (boss == null || target == null) return;
        if (boss.isLockMove()) return;
        try {
            var yTarget = target.getY();
            int direction = boss.getX() - target.getX() < 0 ? 1 : -1;
            int move = Util.nextInt(50, 100);
            boss.setX((short) (boss.getX() + (direction == 1 ? move : -move)));
            boss.setY(yTarget);
            AreaService.getInstance().playerMove(boss);
        } catch (Exception exception) {
            LogServer.LogException("MoveEventHandler.goToPlayer: " + exception.getMessage(), exception);
        }
    }

    public Player findNearestPlayer(Entity boss, Area area) {
        Player nearest = null;
        int minDistance = Integer.MAX_VALUE;

        for (Entity entity : area.getEntitysByType(ConstTypeObject.TYPE_PLAYER)) {
            if (entity instanceof Player player) {
                if (!player.getPoints().isDead() && !player.getPlayerStatus().isInVisible()) {
                    int distance = Math.abs(player.getX() - boss.getX());
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearest = player;
                    }
                }
            }
        }
        return nearest;
    }

    public abstract void handleIdle(Boss boss);

    public abstract void handleChasing(Boss boss);

    public abstract void handleAttacking(Boss boss);

    public abstract void handleGoToMap(Boss boss);

    public abstract void handleChat(Boss boss);

    public abstract void handleDeath(Boss boss);

    public abstract void handleSearching(Boss boss);

    public abstract void handleLeavingMap(Boss boss);

}
