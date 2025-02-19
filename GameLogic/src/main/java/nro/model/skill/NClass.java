package nro.model.skill;

import nro.model.template.skill.SkillTemplate;

import java.util.List;

public record NClass(int classId, String name, List<SkillTemplate> skillTemplates) {
}
