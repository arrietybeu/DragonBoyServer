package nro.model.template;

import lombok.Getter;

@Getter
public class MagicTreeTemplate {

    private byte level;

    private byte gender;

    private short icon;

    public MagicTreeTemplate(int level, int gender, int icon) {
        this.level = (byte) level;
        this.gender = (byte) gender;
        this.icon = (short) icon;
    }

}

