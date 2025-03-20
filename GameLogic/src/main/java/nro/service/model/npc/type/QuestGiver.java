package nro.service.model.npc.type;

import nro.consts.ConstNpc;
import nro.service.model.npc.ANpcHandler;
import nro.service.model.npc.Npc;
import nro.service.model.player.Player;
import nro.service.core.npc.NpcService;

@ANpcHandler({ConstNpc.ONG_GOHAN, ConstNpc.ONG_MOORI, ConstNpc.ONG_PARAGUS})
public class QuestGiver extends Npc {

    public QuestGiver(int tempId, int status, int mapId, int cx, int cy, int avatar) {
        super(tempId, status, mapId, cx, cy, avatar);
    }

    @Override
    public void openMenu(Player player) {
        if (player.getPlayerTask().checkDoneTaskTalkNpc(this)) return;
        String npcName = ConstNpc.getNameNpcHomeByGender(player.getGender());
        var taskMain = player.getPlayerTask().getTaskMain();
        String text = "";
        switch (taskMain.getId()) {
            case 0 -> {
                switch (taskMain.getIndex()) {
                    case 3 ->
                            text = "Con mới đi đâu về thế ? Con hãy đến rương đồ để lấy rađa, sau đó lại thu hoạch những hạt đậu trên cây đậu thần đằng kia!";
                    case 4 -> text = "Thu hoạch những hạt đậu trên cây đậu thần đằng kia!";
                }
            }
            case 1 -> text = "Nhanh lên, ra ngoài Làng Aru đánh ngã 5 mộc nhân!";
            case 2 -> text = "Ta đói lắm rồi, con mau đi thu thập đùi gà";
            case 4 -> text = "Con đi mau lên dân làng đang gặp nguy hiểm";
            case 7, 8 -> text = "Con đã tìm thấy ngọc rồng chưa?";
            default -> text = "Con cố gắng theo " + npcName + " học thành tài, đừng lo lắng cho ta.";
        }
        NpcService.getInstance().sendNpcTalkUI(player, this.getTempId(), text, this.getAvatar());
    }


}