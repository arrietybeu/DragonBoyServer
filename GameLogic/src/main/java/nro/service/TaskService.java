package nro.service;

import nro.model.player.Player;
import nro.model.player.PlayerTask;
import nro.model.task.TaskMain;
import nro.network.Message;
import nro.server.LogServer;

import java.io.DataOutput;
import java.io.DataOutputStream;

public class TaskService {

    private static final class InstanceHolder {
        private static final TaskService instance = new TaskService();
    }

    public static TaskService getInstance() {
        return InstanceHolder.instance;
    }

    public void sendTaskMain(Player player) {
        try (Message message = new Message(40);
             DataOutputStream output = message.writer()) {

            var taskMain = player.getPlayerTask().getTaskMain();
            var subNames = taskMain.getSubNameList();
            int index = taskMain.getIndex();

            output.writeShort(taskMain.getId());
            output.writeByte(index);
            output.writeUTF(taskMain.getName());
            output.writeUTF(taskMain.getDetail());
            output.writeByte(subNames.size());

            for (var sub : subNames) {
                output.writeUTF(sub.getName());
                output.writeByte(sub.getNpcId());
                output.writeShort(sub.getMapId());
                output.writeUTF(sub.getContentInfo());
            }

            output.writeShort(subNames.get(index).getCount());

            for (var sub : subNames) {
                output.writeShort(sub.getMax());
            }

            player.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
            LogServer.LogException("Error sendTaskMain: " + e.getMessage());
        }
    }
}
