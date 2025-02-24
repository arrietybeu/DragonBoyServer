package nro.model.player;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstMap;
import nro.consts.ConstNpc;
import nro.model.task.TaskMain;
import nro.server.LogServer;
import nro.server.manager.MapManager;
import nro.service.NpcService;
import nro.service.TaskService;

@Setter
@Getter
public class PlayerTask {

    private final Player player;

    private TaskMain taskMain;

    public PlayerTask(Player player) {
        this.player = player;
    }

    public void checkDoneTaskGoMap() {
        switch (this.player.getArea().getMap().getId()) {
            case ConstMap.VACH_NUI_ARU:
            case ConstMap.VACH_NUI_MOORI:
            case ConstMap.VUC_PLANT: {
                if (this.player.getX() >= 635) {
                    this.doneTask(0, 0);
                }
                break;
            }
            case ConstMap.NHA_GOHAN:
            case ConstMap.NHA_MOORI:
            case ConstMap.NHA_BROLY: {
                this.doneTask(0, 1);
                break;
            }
        }
    }

    public void doneTask(int taskId, int index) {
        if (!checkTaskInfo(taskId, index)) {
            return;
        }
        this.addDoneSubTask();
        NpcService npcService = NpcService.getInstance();
        String npcName = ConstNpc.getNameNpcHouseByGender(player.getGender());
        String mapName = MapManager.getInstance().getNameMapHomeByGender(player.getGender());
        LogServer.DebugLogic("player name: " + this.player.getName() + " task " + this.taskMain.toString());
        switch (taskId) {
            case 0: {
                switch (index) {
                    case 0: {
                        String content = String.format("Hãy di chuyển đến %s, %s đang chờ bạn ở đằng kia!", mapName, npcName);
                        npcService.sendNpcTalkUI(player, 5, content, -1);
                        break;
                    }
                    case 1: {
                        String content = String.format("%s đang chờ. Bạn hãy đi đến gần và click đôi vào ông để trò chuyện", npcName);
                        npcService.sendNpcTalkUI(player, 5, content, -1);
                    }
                }
                break;
            }
        }
    }

    private void addDoneSubTask() {
        this.taskMain.getSubNameList().get(this.taskMain.getIndex()).addCount(1);

        var count = this.taskMain.getSubNameList().get(this.taskMain.getIndex()).getCount();
        if (count >= this.taskMain.getSubNameList().get(this.taskMain.getIndex()).getMaxCount()) {
            this.taskMain.setIndex(this.taskMain.getIndex() + 1);
        }

        if (this.taskMain.getIndex() >= this.taskMain.getSubNameList().size()) {
            this.taskMain.setIndex(0);
            this.taskMain.setId(this.taskMain.getId() + 1);
        }

        TaskService taskService = TaskService.getInstance();
        taskService.sendTaskMain(this.player);
        taskService.sendTaskMainUpdate(this.player);
    }

    private boolean checkTaskInfo(int taskId, int index) {
        return this.taskMain.getId() == taskId && this.taskMain.getIndex() == index;
    }

    public void sendInfoTaskForNpcTalkByUI(Player player) {
        TaskMain taskMain = player.getPlayerTask().getTaskMain();
        NpcService npcService = NpcService.getInstance();
        String BirdNameNpc = player.getPlayerBirdNames()[0];
        if (taskMain.getId() == 0) {
            if (taskMain.getIndex() == 0) {
                String wellCome = String.format("Chào mừng %s đến với thế giới Ngọc Rồng!\n", player.getName());
                String content = wellCome
                        + String.format("Mình là %s sẽ đồng hành cùng bạn ở thế giới này\n", BirdNameNpc)
                        + "Để di chuyển, hãy click chuột vào nơi muốn đến";
                npcService.sendNpcTalkUI(player, 5, content, -1);
            }
        }
    }
}
