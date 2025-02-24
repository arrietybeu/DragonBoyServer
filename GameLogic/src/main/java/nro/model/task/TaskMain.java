package nro.model.task;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class TaskMain {

    private int id;
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
        return "TaskMain{" + "id=" + id + ", name='" + name + '\'' + ", detail='" + detail + '\'' + ", subNameList=" + subNameList.size() + ", index=" + index + '}';
    }

    @Getter
    @Setter
    public static class SubName {

        public short[] npcList;
        private short count;
        private short mapId;
        private int maxCount;

        private String name;
        private String contentInfo;

        public void addCount(int count) {
            this.count += (short) count;
        }

        public short getNpcIdByGender(int gender) {
            if (npcList.length == 0 || npcList.length <= 2) {
                return -1;
            }
            return npcList[gender];
        }
    }

}
