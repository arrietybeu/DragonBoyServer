package nro.model.player;

import nro.model.template.skill.SpeacialSkillTemplate;
import nro.service.SpeacialSkillService;

public class SpeacialSkill {

    private Player player;

    public byte countOpen;

    public SpeacialSkillTemplate intrinsic;

    public SpeacialSkill(Player player) {
        this.player = player;
//        this.intrinsic = SpeacialSkillService.getInstance().getIntrinsicById(0);
    }

    public void dispose() {
        this.player = null;
        this.intrinsic = null;
    }
}
