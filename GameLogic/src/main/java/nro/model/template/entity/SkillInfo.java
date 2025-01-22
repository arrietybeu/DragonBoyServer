package nro.model.template.entity;

import nro.model.skill.SkillOption;
import nro.model.template.skill.SkillTemplate;

import java.util.Arrays;

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

    public SkillOption[] options;

    public boolean paintCanNotUseSkill;

    public short damage;

    public String moreInfo;

    public short price;

    public short curExp;

    @Override
    public String toString() {
        return "SkillInfo{" +
                "ATT_STAND=" + ATT_STAND +
                ", ATT_FLY=" + ATT_FLY +
                ", SKILL_AUTO_USE=" + SKILL_AUTO_USE +
                ", SKILL_CLICK_USE_ATTACK=" + SKILL_CLICK_USE_ATTACK +
                ", SKILL_CLICK_USE_BUFF=" + SKILL_CLICK_USE_BUFF +
                ", SKILL_CLICK_NPC=" + SKILL_CLICK_NPC +
                ", SKILL_CLICK_LIVE=" + SKILL_CLICK_LIVE +
                ", template=" + template +
                ", skillId=" + skillId +
                ", point=" + point +
                ", powRequire=" + powRequire +
                ", coolDown=" + coolDown +
                ", lastTimeUseThisSkill=" + lastTimeUseThisSkill +
                ", dx=" + dx +
                ", dy=" + dy +
                ", maxFight=" + maxFight +
                ", manaUse=" + manaUse +
                ", options=" + Arrays.toString(options) +
                ", paintCanNotUseSkill=" + paintCanNotUseSkill +
                ", damage=" + damage +
                ", moreInfo='" + moreInfo + '\'' +
                ", price=" + price +
                ", curExp=" + curExp +
                '}';
    }
}
