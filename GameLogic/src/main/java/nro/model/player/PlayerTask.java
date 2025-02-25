package nro.model.player;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstMap;
import nro.consts.ConstMenu;
import nro.consts.ConstNpc;
import nro.model.npc.Npc;
import nro.model.npc.NpcFactory;
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

    public boolean checkDoneTaskTalkNpc(Npc npc) {
        switch (npc.getTempId()) {
            case ConstNpc.ONG_GOHAN:
            case ConstNpc.ONG_MOORI:
            case ConstNpc.ONG_PARAGUS: {
                return this.doneTask(0, 2) || this.doneTask(0, 5);
            }
        }
        return false;
    }

    public void checkDoneTaskGetItemBox() {
        doneTask(0, 3);
    }

    public void checkDoneTaskConfirmMenuNpc(int npcId) {
        switch (npcId) {
            case ConstNpc.DAU_THAN: {
                if (player.getPlayerStatus().getIndexMenu() == ConstMenu.MENU_HARVEST_PEA) {
                    this.doneTask(0, 4);
                }
                break;
            }
        }
    }

    public boolean doneTask(int taskId, int index) {
        try {
//            System.out.printf("Check task: %d %d %s%n", taskId, index, this.taskMain);
            if (!checkTaskInfo(taskId, index)) {
                return false;
            }
            addDoneSubTask();

            NpcService npcService = NpcService.getInstance();
            String npcName = ConstNpc.getNameNpcHouseByGender(player.getGender());
            String mapName = MapManager.getInstance().getNameMapHomeByGender(player.getGender());
            String mapNameVillage = MapManager.getInstance().getNameMapVillageByGender(player.getGender());
            LogServer.DebugLogic(String.format("Done task - Player: %s, Task: %s", player.getName(), taskMain));
            switch (taskId) {
                case 0 -> handleTaskZero(index, npcService, npcName, mapName, mapNameVillage);
                case 1 -> {
                }
            }

        } catch (Exception ex) {
            LogServer.LogException("PlayerTask doneTask - " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public void sendTaskInfo() {
        TaskService taskService = TaskService.getInstance();
        taskService.sendTaskMain(player);
        taskService.sendTaskMainUpdate(player);
    }

    private void handleTaskZero(int index, NpcService npcService, String npcName, String mapName, String mapNameVillage) {
        switch (index) {
            case 0 ->
                    npcService.sendNpcTalkUI(player, 5, String.format("Hãy di chuyển đến %s, %s đang chờ bạn ở đằng kia!", mapName, npcName), -1);
            case 1 ->
                    npcService.sendNpcTalkUI(player, 5, String.format("%s đang chờ. Bạn hãy đi đến gần và click đôi vào ông để trò chuyện", npcName), -1);
            case 2, 5 -> {
                Npc npc = NpcFactory.getNpc(ConstNpc.GetIdNpcHomeByGender(player.getGender()));
                String content = (index == 2)
                        ? "Con mới đi đâu về thế ? Con hãy đến rương đồ để lấy rađa, sau đó lại thu hoạch những hạt đậu trên cây đậu thần đằng kia!"
                        : String.format(
                        "Tốt lắm, Rađa sẽ giúp con biết được HP và KI của mình ở góc trên màn hình\n" +
                                "Đậu thần sẽ giúp con phục hồi HP và KI khi con yếu đi\n" +
                                "Bây giờ, con hãy đi ra %s để tập luyện, hãy đánh ngã 5 mộc nhân, rồi trở về gặp ta, ta sẽ dạy con bay\n" +
                                "Đi đi, và về sớm con nhé!", mapNameVillage);
                npcService.sendNpcTalkUI(player, npc.getTempId(), content, npc.getAvatar());
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

        this.sendTaskInfo();
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
