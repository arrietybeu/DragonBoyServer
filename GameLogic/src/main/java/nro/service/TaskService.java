package nro.service;

import lombok.Getter;
import nro.model.player.Player;
import nro.model.task.TaskMain;
import nro.server.network.Message;
import nro.server.LogServer;
import nro.server.manager.TaskManager;

import java.io.DataOutputStream;

public class TaskService {

    @Getter
    private static final TaskService instance = new TaskService();

    public void sendTaskMain(Player player) {

        try (Message message = new Message(40);
             DataOutputStream output = message.writer()) {

            var taskMain = player.getPlayerTask().getTaskMain();
            var subNames = taskMain.getSubNameList();
            int index = taskMain.getIndex();
            output.writeShort(taskMain.getId());
//            output.writeShort(15);
            output.writeByte(index);
            output.writeUTF(taskMain.getName());
            output.writeUTF(taskMain.getDetail());
            output.writeByte(subNames.size());

            for (int i = 0; i < subNames.size(); i++) {
                var sub = subNames.get(i);
                String nameToSend = (i <= index) ? sub.getName() : "...";
                output.writeUTF(nameToSend);
                output.writeByte(sub.getNpcIdByGender(player.getGender()));
                output.writeShort(sub.getMapId());
                output.writeUTF(sub.getContentInfo());
            }

            output.writeShort(subNames.get(index).getCount());

            for (var sub : subNames) {
                output.writeShort(sub.getMaxCount());
            }

            player.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
            LogServer.LogException("Error sendTaskMain: " + e.getMessage());
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
            e.printStackTrace();
            LogServer.LogException("Error sendTaskMainUpdate: " + e.getMessage());
        }
    }
}
