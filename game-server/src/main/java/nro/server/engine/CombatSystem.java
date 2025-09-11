package nro.server.engine;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.IteratingSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nro.server.model.ecs.component.HealthComponent;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.StateComponent;
import nro.server.model.ecs.component.StatsComponent;
import nro.server.model.ecs.component.monster.MonsterComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.services.MonsterService;
import nro.server.utils.MapUtils;

public class CombatSystem extends IteratingSystem {

    private static final Logger log = LoggerFactory.getLogger(CombatSystem.class);

    private ComponentMapper<HealthComponent> healthMapper;
    private ComponentMapper<MonsterComponent> monsterMapper;
    private ComponentMapper<PlayerComponent> playerMapper;
    private ComponentMapper<PositionComponent> positionMapper;

    public CombatSystem() {
        super(Aspect.all(HealthComponent.class, PlayerComponent.class, PositionComponent.class));
    }

    @Override
    protected void process(int entityId) {
        PlayerComponent playercComponent = playerMapper.get(entityId);
        PositionComponent positionComponent = positionMapper.get(entityId);
        var player = playercComponent.connection.getEntity();
        var state = player.getComponent(StateComponent.class);
        try {
            handler(entityId, state, positionComponent, player);
        } catch (Throwable e) {
            log.error("CombatSystem error for entityId={}", entityId, e);
        } finally {
            state.state = EntityState.IDLE;
        }
    }

    private void handler(int entityId, StateComponent state, PositionComponent positionComponent, Entity player) {
        if (state.state == EntityState.ATTACKING) {
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

            var healthTarget = entityTarget.getComponent(HealthComponent.class);

            healthTarget.currentHP -= player.getComponent(StatsComponent.class).currentDamage;

            if (healthTarget.currentHP <= 0) {
                // entityTarget.getComponent(StateComponent.class).state = EntityState.DEAD;
            }

            log.debug("CombatSystem attack monster: {} {} {}", entityTarget.getId(), healthTarget.currentHP,
                    player.getComponent(StatsComponent.class).currentDamage);
            MonsterService.sendHpMonster(
                    positionComponent.mapId, positionComponent.getAreaId(),
                    entityTarget.getId(), healthTarget.currentHP,
                    player.getComponent(StatsComponent.class).currentDamage, false, true);
        }
    }

}
