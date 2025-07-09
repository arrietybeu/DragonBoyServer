package nro.server.model.ecs.component;

import com.artemis.Component;
import nro.server.model.templates.skill.SkillInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arriety
 */
public class SkillsetComponent extends Component {

    public List<SkillInfo> skills = new ArrayList<>();

    public byte[] skillShortCut = new byte[10];

    public int selectedSkillId = -1;
}