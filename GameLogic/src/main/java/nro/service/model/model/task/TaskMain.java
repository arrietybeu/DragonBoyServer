package nro.service.model.model.task;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaskMain {

    private int id;
    private final String[] name;
    private final String[] detail;
    private List<SubName> subNameList;
    private int index;

    public TaskMain(int id, String[] name, String[] detail, List<SubName> subNameList) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.subNameList = subNameList;
    }

    public String getNameByGender(int gender) {
        if (name.length == 0) {
            return "";
        }
        if (name.length == 1) {
            return name[0];
        }
        return name[gender];
    }

    public String getDetailByGender(int gender) {
        if (detail.length == 0) {
            return "";
        }
        if (detail.length == 1) {
            return detail[0];
        }
        return detail[gender];
    }

    @Override
    public String toString() {
        return "TaskMain{" + "id=" + id + ", name='" + name + '\'' + ", detail='" + detail + '\'' + ", subNameList="
                + subNameList.size() + ", index=" + index + '}';
    }

    @Getter
    @Setter
    public static class SubName {

        public short[] npcList;
        public short[] mapList;

        private short count;
        private int maxCount;

        public String[] nameList;
        public String[] contentInfo;

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

        public String getNameMapByGender(int gender) {
            if (nameList.length == 0) {
                return "";
            }
            if (nameList.length == 1) {
                return nameList[0];
            }
            return nameList[gender];
        }

        public String getContentInfo(int gender) {
            if (contentInfo.length == 0) {
                return "";
            }
            if (contentInfo.length == 1) {
                return contentInfo[0];
            }
            return contentInfo[gender];
        }
    }

}
