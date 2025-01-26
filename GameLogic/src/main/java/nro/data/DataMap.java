package nro.data;

import nro.network.Message;
import nro.network.Session;
import nro.server.config.ConfigServer;
import nro.consts.ConstMsgNotMap;
import nro.server.manager.MapManager;
import nro.server.manager.MonsterManager;
import nro.server.manager.NpcManager;
import nro.model.template.map.MapTemplate;
import nro.model.template.NpcTemplate;
import nro.server.LogServer;

public class DataMap {

    public static void updateMapData(Session session) {
        MapManager mapManager = MapManager.getInstance();
        NpcManager npcManager = NpcManager.getInstance();
        MonsterManager monsterManager = MonsterManager.getInstance();
        var sizeMap = mapManager.sizeMap();
        var sizeNpc = npcManager.sizeNpc();
        var sizeMonster = monsterManager.sizeMonster();

        try (Message message = new Message(-28)) {
            // send type msg
            message.writer().writeByte(ConstMsgNotMap.UPDATE_MAP);
            // send version map (check neu local client != server client se thi update)
            message.writer().writeByte(ConfigServer.VERSION_MAP);// 1

            message.writer().writeShort(sizeMap);// TODO version < 2.4.3 write byte

            for (MapTemplate mapTemplate : mapManager.getMapTemplates()) {
                message.writer().writeUTF(mapTemplate.getName());
            }

            message.writer().writeByte(sizeNpc);
            for (NpcTemplate npc : npcManager.getNpcTemplates()) {
                message.writer().writeUTF(npc.name());
                message.writer().writeShort(npc.head());
                message.writer().writeShort(npc.body());
                message.writer().writeShort(npc.leg());
                message.writer().writeByte(0);
//                message.writer().writeUTF(npc.menu());
            }

            message.writer().writeShort(sizeMonster);
            for (var monster : monsterManager.getMonsterTemplates()) {
                message.writer().writeByte(monster.type());
                message.writer().writeUTF(monster.name());
                message.writer().writeLong(monster.hp());
                message.writer().writeByte(monster.rangeMove());
                message.writer().writeByte(monster.speed());
                message.writer().writeByte(monster.dartType());
            }
            session.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error updateMapData: " + e.getMessage());
        }
    }
}
