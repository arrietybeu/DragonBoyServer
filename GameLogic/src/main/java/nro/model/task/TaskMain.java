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
    private List<SubName> subNameList;
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
        public short[] mapList;

        private short count;
        private int maxCount;

        private String name;
        private String contentInfo;

        public void addCount(int count) {
            this.count += (short) count;
        }

        public short getNpcIdByGender(int gender) {
            if (npcList.length == 0) {
                return -1;
            }
            if (npcList.length == 1) {
                return npcList[0];
            }
            return npcList[gender];
        }

        public short getMapIdByGender(int gender) {
            if (mapList.length == 0) {
                return -1;
            }
            if (mapList.length == 1) {
                return mapList[0];
            }
            return mapList[gender];
        }

        @Override
        public String toString() {
            return "SubName{" + "npcList=" + npcList.length + ", count=" + count + ", mapId=" + mapList + ", maxCount=" + maxCount + ", name='" + name + '\'' + ", contentInfo='" + contentInfo + '\'' + '}';
        }
    }

}
