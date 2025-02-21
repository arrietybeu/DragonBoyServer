package nro.model.npc.type;

import nro.consts.ConstNpc;
import nro.model.npc.ANpcHandler;
import nro.model.npc.Npc;
import nro.model.player.Player;
import nro.service.NpcService;

@ANpcHandler({ConstNpc.ONG_GOHAN, ConstNpc.ONG_MOORI, ConstNpc.ONG_PARAGUS})
public class QuestGiver extends Npc {

    public QuestGiver(int tempId, int status, int mapId, int cx, int cy, int avatar) {
        super(tempId, status, mapId, cx, cy, avatar);
    }

    @Override
    public void openMenu(Player player) {
        String npcName = getNpcNameById(this.getTempId());
        String text = "Con cố gắng theo " + npcName + " học thành tài, đừng lo lắng cho ta.";
        NpcService.getInstance().sendNpcTalkUI(player, this.getTempId(), text, this.getAvatar());
    }

    private String getNpcNameById(int npcId) {
        return switch (npcId) {
            case ConstNpc.ONG_GOHAN -> "Quy lão";
            case ConstNpc.ONG_MOORI -> "Trưởng lão Guru";
            case ConstNpc.ONG_PARAGUS -> "Vua Vegeta";
            default -> "Arriety";
        };
    }
}