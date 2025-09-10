package nro.server.model.templates.skill;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class SkillInfo {

    // current skill id
    private short skillId;
    private byte point; // hiểu nôm na là level của skill
    private long powRequire;// sức mạnh yêu cầu
    private int baseCooldown;// thời gian hồi chiêu
    private long currentCooldown;
    private short dx;
    private short dy;
    private byte maxFight;
    private short manaUse;
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