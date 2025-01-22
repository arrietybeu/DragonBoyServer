package nro.model.template.skill;

import nro.model.template.entity.SkillInfo;

import java.util.List;

public class SkillTemplate {

    private byte id;

    private int classId;

    private String name;

    private int maxPoint;

    private int manaUseType;

    private int type;

    private int iconId;

    private String description;

    private List<SkillInfo> skills;

    private String damInfo;

    public void setSkillInfo(List<SkillInfo> skills) {
        this.skills = skills;
    }

    public List<SkillInfo> getSkillInfo() {
        return skills;
    }

    public void setSkillId(byte id) {
        this.id = id;
    }

    public byte getSkillId() {
        return id;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getClassId() {
        return classId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMaxPoint(int maxPoint) {
        this.maxPoint = maxPoint;
    }

    public int getMaxPoint() {
        return maxPoint;
    }

    public void setManaUseType(int manaUseType) {
        this.manaUseType = manaUseType;
    }

    public int getManaUseType() {
        return manaUseType;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getIconId() {
        return iconId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDamInfo(String damInfo) {
        this.damInfo = damInfo;
    }

    public String getDamInfo() {
        return damInfo;
    }

    @Override
    public String toString() {
        return "SkillTemplate{" +
                "id=" + id +
                ", classId=" + classId +
                ", name='" + name + '\'' +
                ", maxPoint=" + maxPoint +
                ", manaUseType=" + manaUseType +
                ", type=" + type +
                ", iconId=" + iconId +
                ", description='" + description + '\'' +
                ", skills=" + skills +
                ", damInfo='" + damInfo + '\'' +
                '}';
    }
}
