package nro.model.npc;

import nro.consts.ConstNpc;
import nro.model.npc.type.Bunma;
import nro.model.npc.type.QuestGiver;
import nro.model.npc.type.RuongDo;
import nro.server.LogServer;

public class NpcFactory {

    public static Npc CreateNpc(int npcId, int status, int mapId, int x, int y, int avatar) {
        Npc npc = null;
        try {
            switch (npcId) {
                case ConstNpc.ONG_MOORI:
                case ConstNpc.ONG_PARAGUS:
                case ConstNpc.ONG_GOHAN: {
                    npc = new QuestGiver(npcId, status, mapId, x, y, avatar);
                    break;
                }
                case ConstNpc.RUONG_DO: {
                    npc = new RuongDo(npcId, status, mapId, x, y, avatar);
                    break;
                }
                case ConstNpc.BUNMA: {
                    npc = new Bunma(npcId, status, mapId, x, y, avatar);
                    break;
                }
                default: {
                    LogServer.LogWarning("Npc Npc Chưa làm: [" + npcId + "] " + ConstNpc.getNpcName(npcId));
                    break;
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("createNpc: " + npcId);
            ex.printStackTrace();
            return null;
        }
        return npc;
    }
}
