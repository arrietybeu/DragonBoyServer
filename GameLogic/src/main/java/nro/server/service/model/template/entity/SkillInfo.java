package nro.server.service.model.template.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nro.server.service.model.skill.behavior.SkillBehavior;
import nro.server.service.model.template.skill.SkillOptionTemplate;
import nro.server.service.model.template.skill.SkillTemplate;

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

    private SkillOptionTemplate[] options;

    private String moreInfo;

    private SkillBehavior behavior;


    // kiểm tra dùng đc skill chưa
    public boolean isReady() {
        return System.currentTimeMillis() - lastTimeUseThisSkill >= currentCooldown;
    }

    // còn bao nhiêu ms để có thể dùng skill
    public long getCooldownRemaining() {
        long passed = System.currentTimeMillis() - lastTimeUseThisSkill;
        return Math.max(0, currentCooldown - passed);
    }

    // call SKill
    public void markUsedNow() {
        this.lastTimeUseThisSkill = System.currentTimeMillis();
    }

    // giảm thời gian hồi skill
    public void reduceCooldown(long millis) {
        this.lastTimeUseThisSkill -= millis;
    }

    // hồi ngay lập tức
    public void resetCooldown() {
        this.lastTimeUseThisSkill = 0;
    }

    public void applyCooldownRate(double rate) {
        this.currentCooldown = (long) (this.baseCooldown * rate);
    }

    // cho skill ve mac dinh
    public void restoreBaseCooldown() {
        this.currentCooldown = this.baseCooldown;
    }

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
        clone.options = this.options;
        clone.moreInfo = this.moreInfo;
        clone.behavior = this.behavior;
        return clone;
    }

}
