package nro.service.model.template.skill;

import java.util.List;

public record NClassTemplate(int classId, String name, List<SkillTemplate> skillTemplates) {
}
