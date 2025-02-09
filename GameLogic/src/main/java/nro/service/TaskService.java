package nro.service;

import lombok.Getter;
import nro.model.player.Player;
import nro.model.task.TaskMain;
import nro.network.Message;
import nro.server.LogServer;
import nro.server.manager.TaskManager;

import java.io.DataOutputStream;

public class TaskService {

    @Getter
    private static final TaskService instance = new TaskService();

    public TaskMain getTaskMainById(Player player, int idTask) {
        TaskManager taskManager = TaskManager.getInstance();
        return taskManager.getTaskMainById(idTask);
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

    public void sendInfoTaskForNpcTalkByUI(Player player) {
        TaskMain taskMain = player.getPlayerTask().getTaskMain();
        NpcService npcService = NpcService.getInstance();

        short avatarBirdNpc = player.getPlayerBirdFrames()[2];

        String BirdNameNpc = player.getPlayerBirdNames()[0];

        switch (taskMain.getId()) {
            case 0:
                switch (taskMain.getIndex()) {
                    case 0:
                        switch (player.getGender()) {
                            case 0:
                                break;
                            case 1:
                                npcService.sendNpcTalkUI(player, 5, "Hãy di chuyển đến Nhà Moori, ông Moori đang chờ bạn ở đằng kia!", avatarBirdNpc);
                                break;
                            case 2:
                                break;
                        }
                        break;
                    case 1:
                        break;
                }
                break;
        }
    }
}
