package nro.model.template.skill;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class SpeacialSkillTemplate {

    private int id;
    private String name;
    private short icon;
    private byte gender;

    private int paramFrom1;
    private int paramTo1;

    private int paramFrom2;
    private int paramTo2;

}
