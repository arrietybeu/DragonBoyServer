package nro.model.template.entity;

import lombok.Data;
import nro.model.template.skill.SkillOptionTemplate;
import nro.model.template.skill.SkillTemplate;

@Data
public class SkillInfo {

    public final byte ATT_STAND = 0;
    public final byte ATT_FLY = 1;
    public final byte SKILL_AUTO_USE = 0;
    public final byte SKILL_CLICK_USE_ATTACK = 1;
    public final byte SKILL_CLICK_USE_BUFF = 2;
    public final byte SKILL_CLICK_NPC = 3;
    public final byte SKILL_CLICK_LIVE = 4;

    public SkillTemplate template;
    public short skillId;
    public int point;
    public long powRequire;
    public int coolDown;
    public long lastTimeUseThisSkill;
    public int dx;
    public int dy;
    public int maxFight;
    public int manaUse;
    public SkillOptionTemplate[] options;
    public boolean paintCanNotUseSkill;
    public short damage;
    public String moreInfo;
    public short price;
    public short curExp;

}
