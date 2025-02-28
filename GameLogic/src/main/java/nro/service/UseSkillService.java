package nro.service;

import lombok.Getter;
import nro.model.monster.Monster;
import nro.model.player.Player;

public class UseSkillService {

    @Getter
    private static final UseSkillService instance = new UseSkillService();

    public void useSkillToAttackMonster(Player plAttack, Monster monster) {
    }

    public void useSkillNotForcus(Player player, int status) {
    }

    public void useSkillNotForcusNew(Player player, int skillId, short cx, short cy, byte dir, short x, short y) {
    }

    private boolean useSkill() {
        return true;
    }
}
