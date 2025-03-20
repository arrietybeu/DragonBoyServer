package nro.service.model.template;

import lombok.Getter;

import java.util.List;

@Getter
public class MagicTreeTemplate {

    private final byte level;
    private final byte gender;
    private final short icon;

    private final List<MagicTreePosition> positions;
    private final MagicTreeTimeUpgrade timeUpgrades;
    private final MagicTreeLevel magicTreeLevel;

    public MagicTreeTemplate(int level, int gender, int icon, List<MagicTreePosition> positions,
                             MagicTreeTimeUpgrade timeUpgrades, MagicTreeLevel magicTreeLevel) {
        this.level = (byte) level;
        this.gender = (byte) gender;
        this.icon = (short) icon;
        this.positions = positions;
        this.timeUpgrades = timeUpgrades;
        this.magicTreeLevel = magicTreeLevel;
    }

    public record MagicTreePosition(int x, int y) {
    }

    public record MagicTreeTimeUpgrade(int day, int hour, int minute, int gold) {
    }

    public record MagicTreeLevel(int itemId, int optionId, int optionParam, int maxPea) {
    }
}

