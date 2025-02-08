package nro.model.task;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
public class TaskMain {

    private final int id;

    private final String name;
    private final String detail;
    private final List<SubName> subNameList;

    private int index;

    public TaskMain(int id, String name, String detail, List<SubName> subNameList) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.subNameList = subNameList;
    }

    @Override
    public String toString() {
        return "TaskMain{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", detail='" + detail + '\'' +
                ", subNameList size: " + subNameList.size() +
                ", index=" + index +
                '}';
    }

    @Data
    public static class SubName {

        private byte npcId;
        private short count;
        private short mapId;
        private int max;

        private String name;
        private String contentInfo;
    }
}
