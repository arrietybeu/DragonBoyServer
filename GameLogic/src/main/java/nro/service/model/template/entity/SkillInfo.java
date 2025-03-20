package nro.service.model.template.entity;

import lombok.Getter;
import lombok.Setter;
import nro.service.model.template.skill.SkillOptionTemplate;
import nro.service.model.template.skill.SkillTemplate;

@Getter
@Setter
public class SkillInfo {

    // current skill id
    private short skillId;

    private int point; // hiểu nôm na là level của skill

    private long powRequire;// sức mạnh yêu cầu

    private int coolDown;// thời gian hồi chiêu

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
