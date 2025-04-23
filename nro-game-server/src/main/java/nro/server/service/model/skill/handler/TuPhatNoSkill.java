package nro.server.service.model.skill.handler;

import nro.consts.ConstSkill;
import nro.server.realtime.system.player.SkillSystem;
import nro.server.service.core.player.SkillService;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.skill.SafeCallback;
import nro.server.service.model.entity.monster.Monster;
import nro.server.service.model.skill.behavior.ASkillHandler;
import nro.server.service.model.skill.behavior.SkillBehavior;

import java.util.HashMap;
import java.util.Map;

@ASkillHandler({ConstSkill.TU_PHAT_NO})
public class TuPhatNoSkill implements SkillBehavior {

    @Override
    public void use(Entity caster, Entity ignored) {
        SkillService.getInstance().sendUseSkillNotFocus(caster, 7, caster.getSkills().getSkillSelect().getSkillId(),
                3000);

        SkillSystem.getInstance().scheduleDelaySkill(3000, () -> SafeCallback.run(caster, () -> {
            Map<Integer, Monster> monstersCopy = new HashMap<>(caster.getArea().getMonsters());
            long damage = caster.getPoints().getMaxHP();
            for (Monster monster : monstersCopy.values()) {
                monster.handleAttack(caster, 1, damage);
                SkillService.getInstance().sendEntityAttackMonster(caster, monster.getId());
            }
            caster.getPoints().setDie(caster);
        }));
    }
}