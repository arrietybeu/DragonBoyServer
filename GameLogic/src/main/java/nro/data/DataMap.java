package nro.data;

import nro.network.Message;
import nro.network.Session;
import nro.server.config.ConfigServer;
import nro.consts.ConstMsgNotMap;
import nro.server.manager.MapManager;
import nro.server.manager.MonsterManager;
import nro.server.manager.NpcManager;
import nro.model.map.GameMap;
import nro.model.template.NpcTemplate;
import nro.server.LogServer;

import java.io.DataOutputStream;

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

            DataOutputStream data = message.writer();
            data.writeByte(ConstMsgNotMap.UPDATE_MAP);
            // send version map (check neu local client != server client se thi update)
            data.writeByte(ConfigServer.VERSION_MAP);// 1

            data.writeShort(sizeMap);// TODO version < 2.4.3 write byte

            for (GameMap gameMap : mapManager.getGameMaps().values()) {
                data.writeUTF(gameMap.getName());
            }

            data.writeByte(sizeNpc);
            for (NpcTemplate npc : npcManager.getNpcTemplates()) {
                data.writeUTF(npc.name());
                data.writeShort(npc.head());
                data.writeShort(npc.body());
                data.writeShort(npc.leg());
                data.writeByte(1);
                data.writeByte(1);
                data.writeUTF("Nói chuyện");
            }

            data.writeShort(sizeMonster);
            for (var monster : monsterManager.getMonsterTemplates()) {
                data.writeByte(monster.type());
                data.writeUTF(monster.name());
                data.writeLong(monster.hp());
                data.writeByte(monster.rangeMove());
                data.writeByte(monster.speed());
                data.writeByte(monster.dartType());
            }
            session.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error updateMapData: " + e.getMessage());
        }
    }
}
