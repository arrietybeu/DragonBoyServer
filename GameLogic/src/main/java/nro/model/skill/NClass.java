package nro.model.skill;

import lombok.Data;
import lombok.NoArgsConstructor;
import nro.model.template.skill.SkillTemplate;

import java.util.List;

@Data
@NoArgsConstructor
public class NClass {
    private int classId;
    private String name;
    private List<SkillTemplate> skillTemplates;
}
