package nro.server.service.model.entity.ai.boss;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstTypeObject;
import nro.server.realtime.system.boss.BossAISystem;
import nro.server.service.core.map.AreaService;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.ai.AIState;
import nro.server.service.model.entity.ai.AbstractAI;
import nro.server.service.model.entity.player.Player;

@Setter
@Getter
public abstract class Boss extends AbstractAI {

    private String textChat = "";
    private Player lastPlayerTarget;
    protected BossAIController controller;

    private boolean isAutoDespawn;
    protected boolean isBossInMap = false;

    protected long lastTimeIdle;
    protected long lastTimeAttack;
    protected long lastTimeMove;

    protected long lastAttackTime = 0;
    protected long attackCooldown = 700;

    // thời gian không có người chơi thì biến mất (giây)
    protected int afkTimeout;

    protected byte spawnType;
    protected byte typeLeaveMap;

    // mảng chứa các id map để boss có thể xuất hiện
    protected int[] mapsId;

    public Boss(int id, BossFashion bossFashion) {
        this.setTypeObject(ConstTypeObject.TYPE_BOSS);
        this.setCurrentState(AIState.IDLE);
        this.setId(id);
        this.fashion = bossFashion;
    }

    @Override
    public long handleAttack(Entity entityAttack, int type, long damage) {
        return super.handleAttack(entityAttack, type, damage);
    }

    public boolean isValidBossAfkTimeout() {
//        System.out.println("tickAfkTimeout: " + this.tickAfkTimeout + " afkTimeout: " + this.afkTimeout);
        return this.afkTimeout >= 0 && this.tickAfkTimeout > this.afkTimeout;
    }

    public void setLastPlayerTarget(Player player) {
        if (lastPlayerTarget == null) {
            this.lastPlayerTarget = player;
        }
    }

    @Override
    public abstract void onDie(Entity entity);

    @Override
    public void dispose() {
        BossAISystem.getInstance().unregister(this);
        AreaService.getInstance().playerExitArea(this);
    }

    @Override
    public AIState getState() {
        return this.getCurrentState();
    }
}
