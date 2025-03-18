package nro.model.template.entity;

import lombok.Getter;
import lombok.Setter;
import nro.model.template.skill.SkillOptionTemplate;
import nro.model.template.skill.SkillTemplate;

@Getter
@Setter
public class SkillInfo {

    private short skillId;
    private int point;
    private long powRequire;
    private int coolDown;
    private int dx;
    private int dy;
    private int maxFight;
    private int manaUse;
    private short damage;
    private short price;
    private short curExp;
    private long lastTimeUseThisSkill;
    private boolean paintCanNotUseSkill;

    private SkillTemplate template;
    private SkillOptionTemplate[] options;
    private String moreInfo;

    public SkillInfo() {
    }

}
