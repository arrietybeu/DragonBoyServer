package nro.server.model.templates.skill;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Arriety
 */
@Getter
@Setter
@ToString
public class SkillInfo {

    // current skill id
    private short skillId;

    private int point; // hiểu nôm na là level của skill

    private long powRequire;// sức mạnh yêu cầu

    private long baseCooldown;// thời gian hồi chiêu

    private long currentCooldown;

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

    private String moreInfo;

    public SkillInfo copy() {
        SkillInfo clone = new SkillInfo();
        clone.skillId = this.skillId;
        clone.point = this.point;
        clone.powRequire = this.powRequire;
        clone.baseCooldown = this.baseCooldown;
        clone.currentCooldown = this.currentCooldown;
        clone.dx = this.dx;
        clone.dy = this.dy;
        clone.maxFight = this.maxFight;
        clone.manaUse = this.manaUse;
        clone.damage = this.damage;
        clone.price = this.price;
        clone.curExp = this.curExp;
        clone.lastTimeUseThisSkill = this.lastTimeUseThisSkill;
        clone.paintCanNotUseSkill = this.paintCanNotUseSkill;
        clone.template = this.template;
        clone.moreInfo = this.moreInfo;
        return clone;
    }

}
