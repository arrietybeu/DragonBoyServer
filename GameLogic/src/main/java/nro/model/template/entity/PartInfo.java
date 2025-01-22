package nro.model.template.entity;

import nro.model.template.PartImageTemplate;

public class PartInfo {

    private short id;

    private byte type;

    private PartImageTemplate[] pi;

    public short getIcon(int index) {
        return pi[index].icon();
    }

    public short getId() {
        return id;
    }

    public byte getType() {
        return type;
    }

    public PartImageTemplate[] getPi() {
        return pi;
    }

    public void setId(short id) {
        this.id = id;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public void setPi(PartImageTemplate[] pi) {
        this.pi = pi;
    }

    @Override
    public String toString() {
        return "PartInfo{id=" + id + ", type=" + type + ", pi=" + pi.length + "}";
    }

}
