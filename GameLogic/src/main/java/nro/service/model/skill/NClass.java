package nro.service.model.skill;

import nro.service.model.template.skill.SkillTemplate;

import java.util.List;

public record NClass(int classId, String name, List<SkillTemplate> skillTemplates) {
}
