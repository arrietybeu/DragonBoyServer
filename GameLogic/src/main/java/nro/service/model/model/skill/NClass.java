package nro.service.model.model.skill;

import nro.service.model.model.template.skill.SkillTemplate;

import java.util.List;

public record NClass(int classId, String name, List<SkillTemplate> skillTemplates) {
}
