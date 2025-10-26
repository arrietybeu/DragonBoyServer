package nro.server.engine;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.IteratingSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nro.server.consts.ConstMonster;
import nro.server.model.ecs.component.HealthComponent;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.StateComponent;
import nro.server.model.ecs.component.StatsComponent;
import nro.server.model.ecs.component.monster.MonsterComponent;
import nro.server.model.ecs.component.monster.StateMonsterComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.services.MonsterService;
import nro.server.utils.MapUtils;

/**
 * @author Arriety
 */
public final class CombatSystem extends IteratingSystem {

    private static final Logger log = LoggerFactory.getLogger(CombatSystem.class);

    private ComponentMapper<HealthComponent> healthMapper;
    private ComponentMapper<PositionComponent> positionMapper;
    private ComponentMapper<StatsComponent> statsMapper;
    private ComponentMapper<StateComponent> stateMapper;

    /**
     * Tôi yêu cầu system này tìm những entity có Health, Position, Stats, State Componet
     */
    public CombatSystem() {
        super(Aspect.all(HealthComponent.class,
                PositionComponent.class,
                StatsComponent.class,
                StateComponent.class));
    }

    @Override
    protected void process(int entityId) {
        StateComponent state = stateMapper.get(entityId);
        if (state.state != EntityState.ATTACKING) return;
        try {
            handler(entityId, state);
        } catch (Throwable e) {
            log.error("CombatSystem error for entityId={}", entityId, e);
        }
    }

    private void handler(int entityId, StateComponent state) {
        var positionComponent = positionMapper.get(entityId);
        log.debug("CombatSystem attack monster: {}", state.targetId);
        // var entityTarget = world.getEntity(state.targetId);
        Entity entityTarget = null;
        for (var monster : MapUtils.getMonsters(MapUtils.findZone(positionComponent.mapId, positionComponent.getAreaId()))) {
            if (monster.getId() == state.targetId) {
                entityTarget = monster;
                break;
            }
        }

        if (entityTarget == null) {
            log.error("CombatSystem attack monster: entityTarget not found");
            return;
        }

        HealthComponent healthTarget = healthMapper.get(entityTarget.getId());
        StatsComponent statsPlayer = statsMapper.get(entityId);
        PositionComponent positionTarget = positionMapper.get(entityTarget.getId());
        StateMonsterComponent stateMonsterTarget = entityTarget.getComponent(StateMonsterComponent.class);

        var damage = statsPlayer.currentDamage;
        var currentHpEntityTarget = healthTarget.currentHP;

        currentHpEntityTarget = (currentHpEntityTarget - damage) > 0 ? (currentHpEntityTarget - damage) : 0;

        log.debug("CUR : " + (currentHpEntityTarget - damage));

        if (currentHpEntityTarget <= 0) {
            healthTarget.currentHP = 0;
            currentHpEntityTarget = 0;
            stateMonsterTarget.status = ConstMonster.STATUS_DEAD;
        }

        log.debug("CombatSystem attack monster: {} {} {}", entityTarget.getId(), currentHpEntityTarget, damage);

        MonsterService.sendHpMonster(
                positionTarget.mapId, positionTarget.getAreaId(),
                entityTarget.getId(), currentHpEntityTarget,
                damage, true, true);

        state.state = EntityState.IDLE;
    }

}
