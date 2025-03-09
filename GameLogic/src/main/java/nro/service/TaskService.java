package nro.service;

import lombok.Getter;
import nro.model.player.Player;
import nro.server.network.Message;
import nro.server.LogServer;

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
            var gender = player.getGender();
            output.writeShort(taskMain.getId());
            // output.writeShort(15);
            output.writeByte(index);
            output.writeUTF(taskMain.getName());
            output.writeUTF(taskMain.getDetailByGender(gender));
            output.writeByte(subNames.size());

            for (int i = 0; i < subNames.size(); i++) {
                var sub = subNames.get(i);
                String nameToSend = (i <= index) ? sub.getNameMapByGender(gender) : "...";
                output.writeUTF(nameToSend);
                output.writeByte(sub.getNpcIdByGender(gender));
                output.writeShort(sub.getMapIdByGender(gender));
                output.writeUTF(sub.getContentInfo(gender));
            }

            output.writeShort(subNames.get(index).getCount());

            for (var sub : subNames) {
                output.writeShort(sub.getMaxCount());
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
            e.printStackTrace();
            LogServer.LogException("Error sendTaskMainUpdate: " + e.getMessage());
        }
    }
}
