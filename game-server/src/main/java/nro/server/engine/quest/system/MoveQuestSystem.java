package nro.server.engine.quest.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import nro.server.consts.ConstMap;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.model.ecs.component.player.QuestInstanceComponent;

/**
 * @author Arriety
 */
public class MoveQuestSystem extends IteratingSystem {

    private ComponentMapper<PositionComponent> posMapper;
    private ComponentMapper<QuestInstanceComponent> questMapper;
    private ComponentMapper<PlayerComponent> playerMapper;

    public MoveQuestSystem() {
        super(Aspect.all(PositionComponent.class, QuestInstanceComponent.class, PlayerComponent.class));
    }

    @Override
    protected void process(int entityId) {

        PositionComponent pos = posMapper.get(entityId);
//        if (!pos.isDirty) return;

        QuestInstanceComponent quest = questMapper.get(entityId);
        if (quest.completed) return;

        short mapId = pos.mapId;

        if (quest.questId == 0) {
            switch (mapId) {
                case ConstMap.VACH_NUI_ARU_BASE, ConstMap.VACH_NUI_MOORI_BASE, ConstMap.VUC_PLANT -> {
                    if (pos.x >= 635 && quest.currentStep == 0) {
                        quest.currentCount = 1;
                    }
                }
                case ConstMap.NHA_GOHAN, ConstMap.NHA_MOORI, ConstMap.NHA_BROLY -> {
                    if (quest.currentStep == 1) {
                        quest.currentCount = 1;
                    }
                }
            }
        }

        if (quest.questId == 9 && quest.currentStep == 2 && mapId == ConstMap.THAP_KARIN) {
            quest.currentCount = 1;
        }

        if (mapId == ConstMap.RUNG_KARIN) {
            if (quest.questId == 8 && quest.currentStep == 3) {
                quest.currentCount = 1;
            }

//            var player = playerMapper.get(entityId).player;
//
//            if ((quest.questId == 9 && quest.currentStep >= 1)
//                    || (quest.questId == 10 && quest.currentStep == 0)) {
//                BossFactory.getInstance().trySpawnSpecialBossInAreaToPointsPlayer(
//                        player, player.getArea(), pos.x, 0, ConstBoss.TAU_PAY_PAY);
//                quest.currentCount = 1;
//            }
//
//            if (quest.questId == 10 && quest.currentStep == 1) {
//                BossFactory.getInstance().trySpawnSpecialBossInArea(
//                        player, player.getArea(), pos.x, 0, ConstBoss.TAU_PAY_PAY);
//            }
        }

        if (quest.questId == 11 && quest.currentStep == 0
                && (mapId == ConstMap.DAO_KAME || mapId == ConstMap.DAO_GURU || mapId == ConstMap.VACH_NUI_DEN)) {
            quest.currentCount = 1;
        }
    }
}
