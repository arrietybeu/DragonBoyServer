package nro.model.template.entity;

import lombok.Data;
import nro.model.template.skill.SkillOptionTemplate;
import nro.model.template.skill.SkillTemplate;

@Data
public class SkillInfo {

    private final byte ATT_STAND = 0;
    private final byte ATT_FLY = 1;
    private final byte SKILL_AUTO_USE = 0;
    private final byte SKILL_CLICK_USE_ATTACK = 1;
    private final byte SKILL_CLICK_USE_BUFF = 2;
    private final byte SKILL_CLICK_NPC = 3;
    private final byte SKILL_CLICK_LIVE = 4;

    private SkillTemplate template;
    private short skillId;
    private int point;
    private long powRequire;
    private int coolDown;
    private int dx;
    private int dy;
    private int maxFight;
    private int manaUse;
    private short damage;
    private String moreInfo;
    private short price;

    private short curExp;
    private long lastTimeUseThisSkill;
    private SkillOptionTemplate[] options;
    private boolean paintCanNotUseSkill;


    public SkillInfo() {
    }

}
