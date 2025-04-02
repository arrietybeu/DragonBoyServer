package nro.server.service.model.entity.player;

import lombok.Getter;
import lombok.Setter;
import nro.consts.*;

import nro.server.service.model.item.Item;
import nro.server.service.model.entity.monster.Monster;
import nro.server.service.model.entity.npc.Npc;
import nro.server.service.model.entity.npc.NpcFactory;
import nro.server.service.model.task.TaskMain;
import nro.server.system.LogServer;
import nro.server.manager.MapManager;
import nro.server.manager.TaskManager;
import nro.server.service.core.item.ItemService;
import nro.server.service.core.npc.NpcService;
import nro.server.service.core.system.ServerService;
import nro.server.service.core.player.TaskService;
import nro.server.service.core.item.DropItemMap;

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

    public boolean doneTask(int taskId, int index) {
        if (this.player.getTypeObject() != ConstTypeObject.TYPE_PLAYER) return false;
        try {
            if (!checkTaskInfo(taskId, index)) return false;

            this.addDoneSubTask();

            NpcService npcService = NpcService.getInstance();
            var npcName = ConstNpc.getNameNpcHouseByGender(player.getGender());
            var mapName = MapManager.getInstance().getNameMapHomeByGender(player.getGender());
            var mapNameVillage = MapManager.getInstance().getNameMapVillageByGender(player.getGender());
            var nameMapCliff = MapManager.getInstance().getNameMapCliffByGender(player.getGender());
            switch (taskId) {
                case 0 -> this.handleTaskZero(index, npcService, npcName, mapName, mapNameVillage);
                case 1 -> this.handleTaskOne(index, npcService, npcName, mapName);
                case 2 -> this.handleTaskTwo(index, npcService, nameMapCliff);
                case 3 -> this.handleTaskThree(index, npcService);
                case 4 -> this.handleTaskFour(index, npcService);
                case 7 -> this.handleTaskSeven(index, npcService);
                case 8 -> this.handlerTaskEight(index, npcService);
                case 9 -> this.handlerTaskNine(index, npcService);
            }
        } catch (Exception ex) {
            LogServer.LogException("PlayerTask doneTask - " + ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    private void handleTaskZero(int index, NpcService npcService, String npcName, String mapName, String mapNameVillage) {

        switch (index) {
            case 0 ->
                    npcService.sendNpcTalkUI(player, 5, String.format("Hãy di chuyển đến %s, %s đang chờ bạn ở đằng kia!", mapName, npcName), -1);
            case 1 ->
                    npcService.sendNpcTalkUI(player, 5, String.format("%s đang chờ. Bạn hãy đi đến gần và click đôi vào ông để trò chuyện", npcName), -1);
            case 2, 5 -> {
                Npc npc = NpcFactory.getNpc(ConstNpc.GetIdNpcHomeByGender(player.getGender()));
                String content = (index == 2) ? "Con mới đi đâu về thế ? Con hãy đến rương đồ để lấy rađa, sau đó lại thu hoạch những hạt đậu trên cây đậu thần đằng kia!" : String.format("Tốt lắm, Rađa sẽ giúp con biết được HP và KI của mình ở góc trên màn hình\n" + "Đậu thần sẽ giúp con phục hồi HP và KI khi con yếu đi\n" + "Bây giờ, con hãy đi ra %s để tập luyện, hãy đánh ngã 5 mộc nhân, rồi trở về gặp ta, ta sẽ dạy con bay\n" + "Đi đi, và về sớm con nhé!", mapNameVillage);
                npcService.sendNpcTalkUI(player, npc.getTempId(), content, npc.getAvatar());
            }
        }
    }

    private void handleTaskOne(int index, NpcService npcService, String npcName, String mapName) {
        try {
            ServerService serverService = ServerService.getInstance();
            switch (index) {
                case 0 -> {
                    var count = this.taskMain.getSubNameList().get(index).getCount();
                    var maxCount = this.taskMain.getSubNameList().get(index).getMaxCount();
                    serverService.sendChatGlobal(player.getSession(), null, String.format("Bạn đánh được %d/%d", count, maxCount), false);
                }
                case 1 -> {
                    // TODO can sua lai map va ten quai
                    Npc npc = NpcFactory.getNpc(ConstNpc.GetIdNpcHomeByGender(player.getGender()));
                    serverService.sendChatGlobal(player.getSession(), null, "Bạn vừa được thưởng 3 k sức mạnh", false);
                    serverService.sendChatGlobal(player.getSession(), null, "Bạn vừa được thưởng 3 k tiềm năng nữa", false);
                    String content = "Tốt lắm, con đã biết cách chiến đấu rồi đấy\n"
                            + "Bây giờ, con hãy đi đến đồi hoa cúc, đánh bọn khủng long con mang về cho ta 10 cái đùi gà, chúng ta sẽ để dành ăn dần\n"
                            + "đây là tấm bản đồ của vùng đất này, con có thể xem để tìm đường đi đến đồi hoa cúc\n"
                            + "Con có thể sửa dụng đậu thần khi hết HP hoặc KI, bằng cách click vào nút có hình trái tim\n" + "Nhanh lên, ta đói lắm rồi!";
                    npcService.sendNpcTalkUI(player, npc.getTempId(), content, npc.getAvatar());
                    player.getPoints().addExp(ConstPlayer.ADD_POWER_AND_EXP, 3000);
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("PlayerTask handleTaskOne - " + ex.getMessage(), ex);
        }
    }

    private void handleTaskTwo(int index, NpcService npcService, String mapName) {
        try {
            switch (index) {
                case 0 -> {
                    var countCurrent = this.taskMain.getSubNameList().get(index).getCount();
                    var maxCount = this.taskMain.getSubNameList().get(index).getMaxCount();
                    ServerService.getInstance().sendChatGlobal(player.getSession(), null, String.format("Bạn đã đánh được %d/%d", countCurrent, maxCount), false);
                }
                case 1 -> {
                    Npc npc = NpcFactory.getNpc(ConstNpc.GetIdNpcHomeByGender(player.getGender()));
                    var content = String.format("Đùi gà đây rồi, tối lắm, haha. Ta sẽ nướng tại đống lửa đằng kia, con có thể ăn bất cứ lúc nào nếu muốn\n" + "Ta vừa nghe thấy 1 tiếng động lớn, dường như có 1 ngôi sao rơi tại %s, con hãy đến kiểm tra xem\n" + "Con cũng đã có thể bay được, nhưng nhớ là sẽ mất sức nếu bay nhiều đấy nhé!\n" + "Con cũng có thể dùng tiềm năng bản thân để nâng HP, KI, hoặc Sức đánh", mapName);
                    npcService.sendNpcTalkUI(player, npc.getTempId(), content, npc.getAvatar());
                    player.getPoints().addExp(ConstPlayer.ADD_POWER_AND_EXP, 10_000);
                    Item duiGa = player.getPlayerInventory().findItemInBag(ConstItem.DUI_GA);
                    if (duiGa != null && duiGa.getQuantity() >= 10) {
                        player.getPlayerInventory().subQuantityItemsBag(duiGa, duiGa.getQuantity());
                    }
                    DropItemMap.dropMissionItems(player);
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("PlayerTask handleTaskTwo - " + ex.getMessage(), ex);
        }
    }

    private void handleTaskThree(int index, NpcService npcService) {
        try {
            switch (index) {
                case 0 -> DropItemMap.dropMissionItems(player);
                case 1 -> ItemService.getInstance().sendFlagBag(player);
                case 2 -> {
                    Npc npc = NpcFactory.getNpc(ConstNpc.GetIdNpcHomeByGender(player.getGender()));
                    var content = "Có em bé trong phi thuyền rơi xuống à, ta cứ tưởng là sao băng\n" + "Ta sẽ đặt đặt tên cho nó là Sôn Gô Ku, từ bây giờ nó s là thành viên trong gia đình ta\n" + "Ta mới nhận được tin có bầy mãnh thú xuất hiện tại Trạm phi thuyền\n" + "Bọn chúng vừa đổ bộ xuống Trái Đất để tr thù việc con cướp đùi gà của con chúng\n" + "Hãy dùng phi thuyền đến các hành tinh khác để giúp dân làng tại đó luôn nhé";
                    npcService.sendNpcTalkUI(player, npc.getTempId(), content, npc.getAvatar());

                    Item duaBe = player.getPlayerInventory().findItemInBag(ConstItem.DUA_BE);
                    if (duaBe != null && duaBe.getQuantity() >= 1) {
                        player.getPlayerInventory().subQuantityItemAllInventory(duaBe, duaBe.getQuantity());
                        ItemService.getInstance().sendFlagBag(player);
                    }
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("PlayerTask handleTaskThree - " + ex.getMessage(), ex);
        }
    }

    private void handleTaskFour(int index, NpcService npcService) {
        try {
            if (index == 3) {
                Npc npc = NpcFactory.getNpc(ConstNpc.GetIdNpcHomeByGender(player.getGender()));
                var content = "Con đã thực sự trưởng thành\n"
                        + "Ta cho con cuốn bí kíp này để nâng cao võ hoc.\n"
                        + "Con hãy dùng sức mạnh của mình trừ gian diệt ác bảo vệ dân lành nhé\n"
                        + "Ta vừa mới nhận được tin, người bán hàng của chúng ta, Bumma, vừa bị một bọn mãnh thú bắt đi\n"
                        + "Ta nghĩ chúng cũng chưa đi được xa đâu, con hãy chạy theo cứu Bunma, đi nhanh đi con\n"
                        + "Con phải đạt sức mạnh 78.000 mới có thể đánh lại bọn chúng, hãy siêng năng tập luyện";
                npcService.sendNpcTalkUI(player, npc.getTempId(), content, npc.getAvatar());
            }
        } catch (Exception ex) {
            LogServer.LogException("PlayerTask handleTaskFour - " + ex.getMessage(), ex);
        }
    }

    private void handleTaskSeven(int index, NpcService npcService) {
        try {
            ServerService serverService = ServerService.getInstance();
            switch (index) {
                case 1 -> {
                    var count = this.taskMain.getSubNameList().get(index).getCount();
                    var maxCount = this.taskMain.getSubNameList().get(index).getMaxCount();
                    serverService.sendChatGlobal(player.getSession(), null, String.format("Bạn đánh được %d/%d", count, maxCount), false);
                }
                case 2 -> {
                    Npc npc = NpcFactory.getNpc(ConstNpc.GetRescuedNpcId(player.getGender()));
                    var content = "Cảm ơn bạn đã cứu tôi. Tôi sẽ sẵn sàng phục vụ nếu bạn cần mua vật dụng";
                    npcService.sendNpcTalkUI(player, npc.getTempId(), content, npc.getAvatar());
                }
                case 3 -> {
                    Npc npc = NpcFactory.getNpc(ConstNpc.GetIdNpcHomeByGender(player.getGender()));
                    var content = "Con đã bao giờ nghe về Rồng thần chưa?\n" + "Truyền thuyết kể rằng có 7 viên Ngọc rồng nằm rải rác khắp địa cầu\n" + "Người có 7 viên ngọc rồng này sex có thể triệu hồi Rồng thần\n" + "Khi rồng thần xuất hiện, sẽ có 3 điều ước cho người đó\n" + "Ta được biết tại rừng Karin (Trái đất) có 1 viên ngọc rồng\n" + "Con hãy tìm đem về cho ta nhé";
                    npcService.sendNpcTalkUI(player, npc.getTempId(), content, npc.getAvatar());
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("PlayerTask handleTaskSeven - " + ex.getMessage(), ex);
        }
    }

    private void handlerTaskEight(int index, NpcService npcService) {
        try {
            ServerService serverService = ServerService.getInstance();
            switch (index) {
                case 2 -> {
                    Npc npc = NpcFactory.getNpc(ConstNpc.GetIdNpcHomeByGender(player.getGender()));
                    var content = "hihi quên kéo cái text nhiệm vụ của npc này rồi :>";
                    npcService.sendNpcTalkUI(player, npc.getTempId(), content, npc.getAvatar());
                }
                case 3 -> {
                    var tdAdd = 60_000;
                    player.getPoints().addExp(ConstPlayer.ADD_POWER_AND_EXP, tdAdd);
                    serverService.sendChatGlobal(player.getSession(), null, String.format("Bạn vừa được thưởng %d tiềm năng sức mạnh", tdAdd), false);
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("PlayerTask handleTaskSeven - " + ex.getMessage(), ex);
        }
    }

    private void handlerTaskNine(int index, NpcService npcService) {
        try {
            ServerService serverService = ServerService.getInstance();
            switch (index) {
                case 0 -> {
                    Npc npc = NpcFactory.getNpc(ConstNpc.BO_MONG);
                    String content = "Hắn sắp đến đây, hãy giúp ta tiêu diệt hắn";
                    npcService.sendNpcTalkUI(player, npc.getTempId(), content, npc.getAvatar());
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("PlayerTask handleTaskNine - " + ex.getMessage(), ex);
        }
    }

    private boolean checkTaskInfo(int taskId, int index) {
        return this.taskMain != null && this.taskMain.getId() == taskId && this.taskMain.getIndex() == index;
    }

    public boolean checkMapCanJoinToTask(int mapId) {
        switch (mapId) {
            case ConstMap.DOI_HOA_CUC, ConstMap.DOI_NAM_TIM, ConstMap.DOI_HOANG -> {
                if (this.taskMain.getId() <= 1) {
                    return true;
                }
            }
            case ConstMap.VACH_NUI_ARU, ConstMap.VACH_NUI_MOORI, ConstMap.VAC_NUI_KAKAROT, ConstMap.THI_TRAN_MOORI,
                 ConstMap.LANG_PLANT, ConstMap.THUNG_LUNG_TRE -> {
                if (this.taskMain.getId() <= 2) {
                    return true;
                }
            }
            default -> {
            }
        }
        return false;
    }

    public void checkDoneTaskGoMap() {
        try {
            switch (this.player.getArea().getMap().getId()) {
                case ConstMap.VACH_NUI_ARU_BASE, ConstMap.VACH_NUI_MOORI_BASE, ConstMap.VUC_PLANT -> {
                    if (this.player.getX() >= 635) this.doneTask(0, 0);
                }
                case ConstMap.NHA_GOHAN, ConstMap.NHA_MOORI, ConstMap.NHA_BROLY -> this.doneTask(0, 1);
                case ConstMap.RUNG_KARIN -> this.doneTask(8, 3);
            }
        } catch (Exception e) {
            LogServer.LogException("PlayerTask checkDoneTaskGoMap - " + e.getMessage(), e);
        }
    }

    public boolean checkDoneTaskTalkNpc(Npc npc) {
        return switch (npc.getTempId()) {
            case ConstNpc.ONG_GOHAN, ConstNpc.ONG_MOORI, ConstNpc.ONG_PARAGUS ->
                    this.doneTask(0, 2) || this.doneTask(0, 5) || this.doneTask(1, 1)
                            || this.doneTask(2, 1) || this.doneTask(3, 2) || this.doneTask(4, 3)
                            || this.doneTask(7, 3) || this.doneTask(8, 2);
            case ConstNpc.BUNMA, ConstNpc.DENDE, ConstNpc.APPULE -> this.doneTask(7, 2);
            case ConstNpc.BO_MONG -> this.doneTask(9, 0);
            default -> false;
        };
    }

    public void checkDoneTaskKKillMonster(Monster monster) {
        try {

            var gender = player.getGender();
            switch (monster.getTemplateId()) {
                case ConstMonster.MOC_NHAN -> this.doneTask(1, 0);

                case ConstMonster.KHUNG_LONG_ME -> {
                    switch (gender) {
                        case ConstPlayer.TRAI_DAT -> this.doneTask(4, 0);
                        case ConstPlayer.NAMEC, ConstPlayer.XAYDA -> this.doneTask(4, 1);
                    }
                }
                case ConstMonster.LON_LOI_ME -> {
                    switch (gender) {
                        case ConstPlayer.TRAI_DAT -> this.doneTask(4, 1);
                        case ConstPlayer.NAMEC -> this.doneTask(4, 0);
                        case ConstPlayer.XAYDA -> this.doneTask(4, 2);
                    }
                }
                case ConstMonster.QUY_DAT_ME -> {
                    switch (gender) {
                        case ConstPlayer.TRAI_DAT, ConstPlayer.NAMEC -> this.doneTask(4, 2);
                        case ConstPlayer.XAYDA -> this.doneTask(4, 0);
                    }
                }
                case ConstMonster.THAN_LAN_BAY -> {
                    if (gender == ConstPlayer.TRAI_DAT) {
                        this.doneTask(7, 1);
                    }
                }
                case ConstMonster.PHI_LONG -> {
                    if (gender == ConstPlayer.NAMEC) {
                        this.doneTask(7, 1);
                    }
                }
                case ConstMonster.QUY_BAY -> {
                    if (gender == ConstPlayer.XAYDA) {
                        this.doneTask(7, 1);
                    }
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("PlayerTask checkDoneTaskKKillMonster - " + ex.getMessage(), ex);
        }
    }

    public void checkDoneTask(int id, int index) {
        this.doneTask(id, index);
    }

    public void checkDoneTaskConfirmMenuNpc(int npcId) throws RuntimeException {
        if (npcId == ConstNpc.DAU_THAN) {
            if (player.getPlayerStatus().getIndexMenu() == ConstMenu.MENU_HARVEST_PEA) {
                this.doneTask(0, 4);
            }
        }
    }

    public void checkDoneTaskPickItem(int itemId) {
        switch (itemId) {
            case ConstItem.DUI_GA -> this.doneTask(2, 0);
            case ConstItem.DUA_BE -> this.doneTask(3, 1);
        }
    }

    public void checkDoneTaskUpgradeExp(long exp) {
        switch (this.taskMain.getId()) {
            case 7 -> {
                if (exp >= 78_000) {
                    this.doneTask(7, 0);
                }
            }
            case 8 -> {
                if (exp >= 140_000) {
                    this.doneTask(8, 0);
                }
            }
        }
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

    private void addDoneSubTask() {
        try {
            TaskService taskService = TaskService.getInstance();
            var subList = this.taskMain.getSubNameList();
            var currentIndex = this.taskMain.getIndex();
            subList.get(currentIndex).addCount(1);

            var count = subList.get(currentIndex).getCount();
            if (count >= subList.get(currentIndex).getMaxCount()) {
                this.taskMain.setIndex(currentIndex + 1);
                if (this.taskMain.getIndex() >= subList.size()) {
                    int nextTaskId = (this.taskMain.getId() == 4) ? 7 : this.taskMain.getId() + 1;// nhay coc nhiem vu 4 len 7
                    TaskMain nextTask = this.getTaskMainById(nextTaskId);
                    if (nextTask != null) {
                        this.taskMain = nextTask;
                        this.taskMain.setIndex(0);
                        taskService.sendTaskMain(player);
                    } else {
                        LogServer.LogWarning("Không tìm thấy nhiệm vụ tiếp theo! Giữ nguyên nhiệm vụ hiện tại.");
                    }
                } else {
                    taskService.sendTaskMain(player);
                }
            }

            taskService.sendTaskMainUpdate(player);
        } catch (Exception exception) {
            LogServer.LogException("PlayerTask addDoneSubTask - " + exception.getMessage(), exception);
        }
    }

}
