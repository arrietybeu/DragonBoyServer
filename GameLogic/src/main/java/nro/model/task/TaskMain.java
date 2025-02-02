package nro.model.task;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TaskMain {

    private int id;
    private int index;

    private String name;
    private String detail;

    private List<SubName> subNameList;

    public TaskMain() {
        this.subNameList = new ArrayList<>();
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
