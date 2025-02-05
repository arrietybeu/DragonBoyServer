package nro.service;

import nro.model.map.ItemMap;
import nro.model.map.Waypoint;
import nro.model.map.areas.Area;
import nro.model.map.decorates.BgItem;
import nro.model.monster.Monster;
import nro.model.npc.Npc;
import nro.model.player.Player;
import nro.network.Message;
import nro.server.LogServer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class MapService {

    private static final Message CLEAR_MAP_MESSAGE = new Message(-22);

    public static final class InstanceHolder {
        public static final MapService instance = new MapService();
    }

    public static MapService getInstance() {
        return InstanceHolder.instance;
    }

    public void sendMapInfo(Player player) {
        Area area = player.getArea();
        try (Message message = new Message(-24)) {
            DataOutputStream output = message.writer();
            output.writeByte(area.getMap().getId());
            output.writeByte(area.getMap().getPlanetId());
            output.writeByte(area.getMap().getTileId());
            output.writeByte(area.getMap().getBgId());
            output.writeByte(area.getMap().getTypeMap());
            output.writeUTF(area.getMap().getName());
            output.writeByte(area.getId());

            this.loadInfoMap(player, output);

            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendMapInfo: " + e.getMessage());
        }
    }

    private void loadInfoMap(Player player, DataOutputStream output) throws IOException {
        output.writeShort(player.getX());
        output.writeShort(player.getY());
        List<Waypoint> wayPoints = player.getArea().getMap().getWaypoints();
        output.writeByte(wayPoints.size());

        for (Waypoint waypoint : wayPoints) {
            output.writeShort(waypoint.getMinX());
            output.writeShort(waypoint.getMinY());
            output.writeShort(waypoint.getMaxX());
            output.writeShort(waypoint.getMaxY());
            output.writeBoolean(waypoint.isEnter());
            output.writeBoolean(waypoint.isOffline());
            output.writeUTF(waypoint.getName());
        }

        List<Monster> monsters = player.getArea().getMonsters();
        output.writeByte(monsters.size());
        for (Monster monster : monsters) {
            output.writeBoolean(monster.isDisable());
            output.writeBoolean(monster.isDontMove());
            output.writeBoolean(monster.isFire());
            output.writeBoolean(monster.isIce());
            output.writeBoolean(monster.isWind());
            output.writeShort(monster.getTemplateId());
            output.writeByte(monster.getSys());
            output.writeLong(monster.getHp());
            output.writeByte(monster.getLevel());
            output.writeLong(monster.getMaxp());
            output.writeShort(monster.getX());
            output.writeShort(monster.getY());
            output.writeByte(monster.getStatus());
            output.writeByte(monster.getLevelBoss());
            output.writeBoolean(monster.isBoss());
        }
        output.writeByte(0);

        List<Npc> npcs = player.getArea().getNpcs();
        for (Npc npc : npcs) {
            output.writeByte(npc.getStatus());
            output.writeShort(npc.getX());
            output.writeShort(npc.getY());
            output.writeByte(npc.getTemplateId());
            output.writeShort(npc.getAvatar());
        }

        List<ItemMap> items = player.getArea().getItems();
        output.writeByte(items.size());
        for (ItemMap item : items) {
            output.writeShort(item.getItemMapID());
            output.writeShort(item.getItemTemplate().id());
            output.writeShort(item.getX());
            output.writeShort(item.getY());
            output.writeInt(item.getPlayerId());
            if(item.getPlayerId() == -2) {
                output.writeShort(item.getRange());
            }
        }

        List<BgItem> bgItems = player.getArea().getMap().getBgItems();
        output.writeShort(bgItems.size());
        for (BgItem bgItem : bgItems) {
            output.writeShort(bgItem.getId());
            output.writeShort(bgItem.getX());
            output.writeShort(bgItem.getY());
        }

        output.writeShort(0);

        // eff map
    }


    /**
     * {@link #clearMap}
     *
     * <pre>
     *     {@code
     *      GameCanvas.debug("SA65", 2);
     *      Char.isLockKey = true;
     *      Char.ischangingMap = true;
     *      GameScr.gI().timeStartMap = 0;
     *      GameScr.gI().timeLengthMap = 0;
     *      Char.myCharz().mobFocus = null;
     *      Char.myCharz().npcFocus = null;
     *      Char.myCharz().charFocus = null;
     *      Char.myCharz().itemFocus = null;
     *      Char.myCharz().focus.removeAllElements();
     *      Char.myCharz().testCharId = -9999;
     *      Char.myCharz().killCharId = -9999;
     *      GameCanvas.resetBg();
     *      GameScr.gI().resetButton();
     *      GameScr.gI().center = null;
     *      if (Effect.vEffData.size() > 15) {
     *          for (int num111 = 0; num111 < 5; num111++) {
     *              Effect.vEffData.removeElementAt(0);
     *          }
     *      }
     * </pre>
     */
    public static void clearMap(Player player) {
        try {
            player.sendMessage(CLEAR_MAP_MESSAGE);
        } catch (Exception e) {
            LogServer.LogException("Error clearMap: " + e.getMessage());
        }
    }

}
