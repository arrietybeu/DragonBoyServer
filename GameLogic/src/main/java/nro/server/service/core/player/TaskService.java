package nro.server.service.core.player;

import lombok.Getter;
import nro.consts.ConstsCmd;
import nro.server.service.model.entity.player.Player;
import nro.server.network.Message;
import nro.server.service.model.map.Waypoint;
import nro.server.system.LogServer;

import java.io.DataOutputStream;

public class TaskService {

    private static final class SingletonHolder {
        private static final TaskService instance = new TaskService();
    }

    public static TaskService getInstance() {
        return TaskService.SingletonHolder.instance;
    }

    public void sendTaskMain(Player player) {

        try (Message message = new Message(ConstsCmd.TASK_GET);
             DataOutputStream write = message.writer()) {

            var taskMain = player.getPlayerTask().getTaskMain();
            var subNames = taskMain.getSubNameList();
            int index = taskMain.getIndex();
            var gender = player.getGender();
            var nameTask = taskMain.getNameByGender(gender) + " id:" + taskMain.getId() + " index:" + taskMain.getIndex();
            write.writeShort(taskMain.getId());
            write.writeByte(index);
            write.writeUTF(nameTask);
            write.writeUTF(taskMain.getDetailByGender(gender));
            write.writeByte(subNames.size());

            for (int i = 0; i < subNames.size(); i++) {
                var sub = subNames.get(i);
                String nameToSend = (i <= index) ? sub.getNameMapByGender(gender) : "...";
                int npcToSend = (i <= index) ? sub.getNpcIdByGender(gender) : 5;
                int mapToSend = (i <= index) ? sub.getMapIdByGender(gender) : 0;
                String contentToSend = (i <= index) ? sub.getContentInfo(gender) : "";
                write.writeUTF(nameToSend);
                write.writeByte(npcToSend);
                write.writeShort(mapToSend);
                write.writeUTF(contentToSend);
            }

            write.writeShort(subNames.get(index).getCount());

            for (var sub : subNames) {
                write.writeShort(sub.getMaxCount());
            }

            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendTaskMain: " + e.getMessage(), e);
        }
    }

    public void sendTaskMainUpdate(Player player) {
        try (Message message = new Message(43);
             DataOutputStream output = message.writer()) {
            var taskMain = player.getPlayerTask().getTaskMain();
            var subNames = taskMain.getSubNameList();
            output.writeShort(subNames.get(taskMain.getIndex()).getCount());
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendTaskMainUpdate: " + e.getMessage(), e);
        }
    }

    public void sendSupportTask(Player player) {
        // TODO logic tìm map nữa làm sau
        var taskMain = player.getPlayerTask().getTaskMain();
        if (taskMain == null) return;

        var subNames = taskMain.getSubNameList();
        if (subNames == null) return;

        var subName = subNames.get(taskMain.getIndex());
        if (subName == null) return;
        if (subName.getMapIdByGender(player.getGender()) == -1) return;

        System.out.println("map go: " + subName.getMapIdByGender(player.getGender()));

        Waypoint waypoint = player.getArea().getMap().getWaypointByGoMap(subName.getMapIdByGender(player.getGender()));
        if (waypoint == null) return;

        try (Message message = new Message(43);
             DataOutputStream writer = message.writer()) {
            writer.writeShort(subName.getCount());

            writer.writeShort(waypoint.getMinX());
            writer.writeShort(waypoint.getMinY());
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendSupportTask: " + e.getMessage(), e);
        }
    }
}
