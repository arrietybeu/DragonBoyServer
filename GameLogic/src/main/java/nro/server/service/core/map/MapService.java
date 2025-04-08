package nro.server.service.core.map;

import nro.consts.ConstMsgNotMap;
import nro.consts.ConstPlayer;
import nro.consts.ConstTypeObject;
import nro.consts.ConstsCmd;
import nro.server.service.core.player.PlayerTransport;
import nro.server.service.core.system.ServerService;
import nro.server.service.model.map.GameMap;
import nro.server.service.model.item.ItemMap;
import nro.server.service.model.map.Waypoint;
import nro.server.service.model.map.areas.Area;
import nro.server.service.model.map.decorates.BackgroudEffect;
import nro.server.service.model.map.decorates.BgItem;
import nro.server.service.model.entity.monster.Monster;
import nro.server.service.model.entity.npc.Npc;
import nro.server.service.model.entity.player.Player;
import nro.server.network.Message;
import nro.server.service.model.template.map.Transport;
import nro.server.system.LogServer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MapService {

    private static final Message CLEAR_MAP_MESSAGE = new Message(-22);

    public static final class InstanceHolder {
        public static final MapService instance = new MapService();
    }

    public static MapService getInstance() {
        return InstanceHolder.instance;
    }

    public void sendListUIArea(Player player) {

        try (Message message = new Message(29)) {
            DataOutputStream data = message.writer();

            List<Area> areas = player.getArea().getMap().getAreas();
            data.writeByte(areas.size());
            for (Area area : areas) {
                int slPlayer = area.getEntitysByType(ConstTypeObject.TYPE_PLAYER).size();
                data.writeByte(area.getId());
                data.writeByte(slPlayer < 5 ? 0 : slPlayer < 8 ? 1 : 2);// 0 blue || 1 yellow || 2 red
                data.writeByte(slPlayer);
                data.writeByte(area.getMaxPlayers());
                data.writeByte(1);
                data.writeUTF("arriety dep zai");
                data.writeInt(1);
                data.writeUTF("nguyen ngu");
                data.writeInt(100);
            }
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("sendListUIArea: " + ex.getMessage(), ex);
        }
    }

    public void requestMaptemplate(Player player) {
        try (Message message = new Message(-28)) {
            DataOutputStream data = message.writer();
            GameMap map = player.getArea().getMap();
            data.writeByte(ConstMsgNotMap.REQUEST_MAP_TEMPLATE);
            data.writeByte(map.getTileMap().width());
            data.writeByte(map.getTileMap().height());

            for (int i = 0; i < map.getTileMap().tiles().length; i++) {
                data.writeByte(map.getTileMap().tiles()[i]);
            }

            this.loadInfoMap(player, data);
            data.writeByte(map.getIsMapDouble());
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("Error request Map Template: " + ex.getMessage(), ex);
        }
    }

    public void sendMapInfo(Player player) {
        Area area = player.getArea();
        try (Message message = new Message(-24)) {
            DataOutputStream output = message.writer();
            output.writeByte(area.getMap().getId());
            output.writeByte(area.getMap().getPlanetId());
            output.writeByte(area.getMap().getTileId());
            output.writeByte(area.getMap().getBgId());
//            output.writeByte(area.getMap().getTypeMap());
            output.writeByte(0);
            output.writeUTF(area.getMap().getName());
            output.writeByte(area.getId());
            this.loadInfoMap(player, output);
            output.writeByte(area.getMap().getIsMapDouble());
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendMapInfo: " + e.getMessage(), e);
        }
    }

    private void loadInfoMap(Player player, DataOutputStream output) throws IOException {

        Area area = player.getArea();
        GameMap map = area.getMap();

        List<BgItem> bgItems = map.getBgItems();
        List<Waypoint> wayPoints = map.getWaypoints();
        List<BackgroudEffect> backgroudEffects = map.getBackgroudEffects();

        Map<Integer, Monster> monsters = area.getMonsters();
        List<Npc> npcs = area.getNpcList();
        var itemMaps = area.getItemsMap().values();

        // send location
        output.writeShort(player.getX());
        output.writeShort(player.getY());

        // send waypoint
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

        // send mob
        output.writeByte(monsters.size());
        for (Monster monster : monsters.values()) {
            output.writeBoolean(monster.getStatus().isDisable());
            output.writeBoolean(monster.getStatus().isDontMove());
            output.writeBoolean(monster.getStatus().isFire());
            output.writeBoolean(monster.getStatus().isIce());
            output.writeBoolean(monster.getStatus().isWind());
            output.writeShort(monster.getTemplateId());
            output.writeByte(monster.getStatus().getSys());
            output.writeLong(monster.getPoint().getHp());
            output.writeByte(monster.getPoint().getLevel());
            output.writeLong(monster.getPoint().getMaxHp());
            output.writeShort(monster.getX());
            output.writeShort(monster.getY());
            output.writeByte(monster.getStatus().getStatus());
            output.writeByte(monster.getInfo().getLevelBoss());
            output.writeBoolean(monster.getInfo().isBoss());
        }

        output.writeByte(0);

        // send npc
        output.writeByte(npcs.size());
        for (Npc npc : npcs) {
            output.writeByte(npc.getStatus());
            output.writeShort(npc.getX());
            output.writeShort(npc.getY());
            output.writeByte(npc.getTempId());
            output.writeShort(npc.getAvatar());
        }

        // send item in Map

        output.writeByte(itemMaps.size());
        for (ItemMap item : itemMaps) {
            output.writeShort(item.getId());
            output.writeShort(item.getItem().getTemplate().id());
            output.writeShort(item.getX());
            output.writeShort(item.getY());
            output.writeInt(item.getPlayerId());
            if (item.getPlayerId() == -2) {
                output.writeShort(item.getRange());
            }
        }

        // send background in Map
        output.writeShort(bgItems.size());
        for (BgItem bgItem : bgItems) {
            output.writeShort(bgItem.getId());
            output.writeShort(bgItem.getX());
            output.writeShort(bgItem.getY());
        }

        // send effect in Map
        output.writeShort(backgroudEffects.size());//write effect
        for (BackgroudEffect backgroudEffect : backgroudEffects) {
            output.writeUTF(backgroudEffect.key());
            output.writeUTF(backgroudEffect.value());
        }

        output.writeByte(map.getBgType());
        output.writeByte(player.getTeleport()); // is teleport
        player.setTeleport(ConstPlayer.TELEPORT_DEFAULT);
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

    public void sendMapTransport(Player player) {
        PlayerTransport playerTransport = player.getPlayerTransport();

        playerTransport.initCapsuleTransport();

        try (Message message = new Message(ConstsCmd.MAP_TRASPORT)) {
            DataOutputStream writer = message.writer();
            writer.writeByte(playerTransport.getTransports().size());

            for (Transport transport : playerTransport.getTransports()) {
                writer.writeUTF(transport.getName());
                writer.writeUTF(transport.getPlanetNameByGender(player.getGender()));
            }

            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendMapTransport: " + e.getMessage());
        }
    }

}
