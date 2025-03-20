package nro.service.model.skill;

import nro.service.model.template.entity.SkillPaintInfo;

import java.util.List;

public class SkillPaint {

    public int id;
    public int effectHappenOnMob;
    public int numEff;

    public List<SkillPaintInfo> skillStand;
    public List<SkillPaintInfo> skillfly;
}
