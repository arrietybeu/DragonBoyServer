package nro.model.skill;

import nro.model.template.skill.SkillTemplate;

import java.util.List;

public class NClass {

    private int classId;

    private String name;

    private List<SkillTemplate> skillTemplates;

    public int getClassId() {
        return classId;
    }

    public String getName() {
        return name;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void addSkillTemplate(SkillTemplate skillTemplate) {
        skillTemplates.add(skillTemplate);
    }

    public void setSkillTemplates(List<SkillTemplate> skillTemplates) {
        this.skillTemplates = skillTemplates;
    }

    public List<SkillTemplate> getSkillTemplates() {
        return skillTemplates;
    }

    @Override
    public String toString() {
        return "NClass{" +
                "classId=" + classId +
                ", name='" + name + '\'' +
                ", skillTemplates=" + skillTemplates +
                '}';
    }
}
