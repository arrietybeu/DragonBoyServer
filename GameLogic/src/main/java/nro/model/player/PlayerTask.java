package nro.model.player;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstMap;
import nro.consts.ConstMenu;

import nro.consts.ConstMonster;
import nro.consts.ConstNpc;
import nro.model.monster.Monster;
import nro.model.npc.Npc;
import nro.model.npc.NpcFactory;
import nro.model.task.TaskMain;
import nro.server.LogServer;
import nro.server.manager.MapManager;
import nro.server.manager.TaskManager;
import nro.service.NpcService;
import nro.service.Service;
import nro.service.TaskService;

@Setter
@Getter
public class PlayerTask {

    private final Player player;
    private TaskMain taskMain;

    public PlayerTask(Player player) {
        this.player = player;
    }

    public TaskMain getTaskMainById(int id) {
        TaskManager taskManager = TaskManager.getInstance();
        return taskManager.getTaskMainById(id);
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
        return switch (npc.getTempId()) {
            case ConstNpc.ONG_GOHAN, ConstNpc.ONG_MOORI, ConstNpc.ONG_PARAGUS ->
                    this.doneTask(0, 2) || this.doneTask(0, 5) || this.doneTask(1, 1);
            default -> false;
        };
    }

    public void checkDoneTaskKKillMonster(Monster monster) {
        try {
            switch (monster.getTemplateId()) {
                case ConstMonster.MOC_NHAN: {
                    this.doneTask(1, 0);
                    break;
                }
                case ConstMonster.KHUNG_LONG: {
                    break;
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("PlayerTask checkDoneTaskKKillMonster - " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    public void checkDoneTaskGetItemBox() {
        doneTask(0, 3);
    }

    public void checkDoneTaskConfirmMenuNpc(int npcId) {
        if (npcId == ConstNpc.DAU_THAN) {
            if (player.getPlayerStatus().getIndexMenu() == ConstMenu.MENU_HARVEST_PEA) {
                this.doneTask(0, 4);
            }
        }
    }

    public boolean doneTask(int taskId, int index) {
        try {
            if (!checkTaskInfo(taskId, index)) return false;

            this.addDoneSubTask();
            NpcService npcService = NpcService.getInstance();
            String npcName = ConstNpc.getNameNpcHouseByGender(player.getGender());
            String mapName = MapManager.getInstance().getNameMapHomeByGender(player.getGender());
            String mapNameVillage = MapManager.getInstance().getNameMapVillageByGender(player.getGender());
            switch (taskId) {
                case 0 -> handleTaskZero(index, npcService, npcName, mapName, mapNameVillage);
                case 1 -> handleTaskOne(index, npcService, npcName, mapName);
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
                String content = (index == 2) ? "Con mới đi đâu về thế ? Con hãy đến rương đồ để lấy rađa, sau đó lại thu hoạch những hạt đậu trên cây đậu thần đằng kia!"
                        : String.format("Tốt lắm, Rađa sẽ giúp con biết được HP và KI của mình ở góc trên màn hình\n"
                        + "Đậu thần sẽ giúp con phục hồi HP và KI khi con yếu đi\n"
                        + "Bây giờ, con hãy đi ra %s để tập luyện, hãy đánh ngã 5 mộc nhân, rồi trở về gặp ta, ta sẽ dạy con bay\n"
                        + "Đi đi, và về sớm con nhé!", mapNameVillage);
                npcService.sendNpcTalkUI(player, npc.getTempId(), content, npc.getAvatar());
            }
        }
    }

    private void handleTaskOne(int index, NpcService npcService, String npcName, String mapName) {
        try {
            Service service = Service.getInstance();
            switch (index) {
                case 0 -> {
                    var count = this.taskMain.getSubNameList().get(index).getCount();
                    var maxCount = this.taskMain.getSubNameList().get(index).getMaxCount();
                    service.sendChatGlobal(player.getSession(), null, String.format("Bạn đánh được %d/%d", count, maxCount), false);
                }
                case 1 -> {
                    Npc npc = NpcFactory.getNpc(ConstNpc.GetIdNpcHomeByGender(player.getGender()));
                    String content = "Tốt lắm, con đã biết cách chiến đấu rồi đấy\n" +
                            "Bây giờ, con hãy đi đến đồi hoa cúc, đánh bọn khủng long con mang về cho ta 10 cái đùi gà, chúng ta sẽ để dành ăn dần\n" +
                            "đây là tấm bản đồ của vùng đất này, con có thể xem để tìm đường đi đến đồi hoa cúc\n" +
                            "Con có thể sửa dụng đậu thần khi hết Hp hoặc KI, bằng cách click vào nút có hình trái tim\n" +
                            "Nhanh lên, ta đói lắm rồi!\n" +
                            "để sử dụng bản đồ, hãy mở menu, mục Nhiệm vụ, chọn Bản đồ\n" +
                            "Vị trí đang chớp sáng là nđi bạn làm nhiệm vụ, hãy tìm đường đến đó";
                    npcService.sendNpcTalkUI(player, npc.getTempId(), content, npc.getAvatar());

                    player.getPlayerPoints().addExp(2, 3000);

                    service.sendChatGlobal(player.getSession(), null, "Bạn vừa được thưởng 3 k sức mạnh", false);
                    service.sendChatGlobal(player.getSession(), null, "Bạn vừa được thưởng 3 k tiềm năng nữa", false);
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("PlayerTask handleTaskOne - " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void handleTaskTwo(int index, NpcService npcService, String npcName, String mapName) {
        switch (index) {
        }
    }

    private void addDoneSubTask() {
        var subList = this.taskMain.getSubNameList();
        var currentIndex = this.taskMain.getIndex();

        subList.get(currentIndex).addCount(1);

        var count = subList.get(currentIndex).getCount();
        if (count >= subList.get(currentIndex).getMaxCount()) {
            this.taskMain.setIndex(currentIndex + 1);
        }

        if (this.taskMain.getIndex() >= subList.size()) {
            TaskMain nextTask = this.getTaskMainById(this.taskMain.getId() + 1);
            if (nextTask != null) {
                this.taskMain = nextTask;
                this.taskMain.setIndex(0);
            } else {
                LogServer.LogWarning("Không tìm thấy nhiệm vụ tiếp theo! Giữ nguyên nhiệm vụ hiện tại.");
            }
        }
        this.sendTaskInfo();
    }

    private boolean checkTaskInfo(int taskId, int index) {
        return this.taskMain != null && this.taskMain.getId() == taskId && this.taskMain.getIndex() == index;
    }

    public void sendInfoTaskForNpcTalkByUI(Player player) {
        TaskMain taskMain = player.getPlayerTask().getTaskMain();
        if (taskMain == null) {
            LogServer.LogWarning("Không thể gửi nhiệm vụ: taskMain null!");
            return;
        }

        if (taskMain.getId() == 0 && taskMain.getIndex() == 0) {
            String birdNameNpc = player.getPlayerBirdNames()[0];
            String content = String.format("Chào mừng %s đến với thế giới Ngọc Rồng!\nMình là %s sẽ đồng hành cùng bạn ở thế giới này\n" + "Để di chuyển, hãy click chuột vào nơi muốn đến", player.getName(), birdNameNpc);

            NpcService.getInstance().sendNpcTalkUI(player, 5, content, -1);
        }
    }

    public boolean checkMapCanJoinToTask(int mapId) {
//        return switch (mapId) {
//            case ConstMap.MAP_OFFLINE ->
//        };
        return false;
    }

}
