package nro.server.model.templates.skill;

import java.util.List;

/**
 * @author Arriety
 */
public record NClassTemplate(int classId, String name, List<SkillTemplate> skillTemplates) {
}
