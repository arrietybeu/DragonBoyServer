package nro.server.model.ecs.component;

import com.artemis.Component;
import nro.server.model.templates.skill.SkillInfo;

import java.util.List;

/**
 * @author Arriety
 */
public class SkillComponent extends Component {

    protected List<SkillInfo> skills;
    protected byte[] skillShortCut;
    protected SkillInfo skillSelect;

    public SkillComponent() {
        // Default constructor for Artemis
    }



}