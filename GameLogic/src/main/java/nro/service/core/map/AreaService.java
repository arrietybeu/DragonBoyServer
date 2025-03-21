package nro.service.core.map;

import lombok.Getter;
import nro.consts.ConstTypeObject;
import nro.service.core.system.ServerService;
import nro.service.model.LiveObject;
import nro.service.model.map.GameMap;
import nro.service.model.map.Waypoint;
import nro.service.model.map.areas.Area;
import nro.service.model.player.Player;
import nro.service.model.player.PlayerFashion;
import nro.server.network.Message;
import nro.service.core.item.DropItemMap;
import nro.server.LogServer;
import nro.server.manager.CaptionManager;
import nro.server.manager.MapManager;
import nro.service.core.player.PlayerService;
import nro.utils.Util;

import java.io.DataOutputStream;
import java.util.Map;

public class AreaService {

    @Getter
    public static final AreaService instance = new AreaService();

    public void sendInfoAllLiveObjectsTo(Player player) {
        try {
            Map<Integer, LiveObject> objects = player.getArea().getAllPlayerInZone();
            for (LiveObject obj : objects.values()) {
                switch (obj) {
                    case Player plInZone -> {
                        if (plInZone != player) {
                            this.addPlayer(player, plInZone);
                        }
                    }
                    default -> {
                    }
                }
            }
            this.sendLiveObjectInfoToOthers(player);
        } catch (Exception ex) {
            LogServer.LogException("sendInfoAllPlayerInArea: " + ex.getMessage(), ex);
        }
    }

    private void sendLiveObjectInfoToOthers(Player player) {
        try {
            Map<Integer, LiveObject> players = player.getArea().getAllPlayerInZone();
            for (LiveObject obj : players.values()) {
                switch (obj) {
                    case Player plInZone -> {
                        if (plInZone != player) {
                            this.addPlayer(plInZone, player);
                        }
                    }
                    default -> {
                    }
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("sendPlayerInfoToAllInArea: " + ex.getMessage(), ex);
        }
    }

    private void addPlayer(Player isMe, LiveObject entityInfo) {
        switch (entityInfo) {
            case Player playerInfo -> {
                try (Message message = new Message(-5)) {
                    DataOutputStream data = message.writer();
                    PlayerFashion playerFashion = playerInfo.getPlayerFashion();
                    data.writeInt(playerInfo.getId());
                    data.writeInt(playerInfo.getClan() != null ? playerInfo.getClan().getId() : -1);
                    if (this.writePlayerInfo(playerInfo, data, playerFashion)) {
                        data.writeByte(playerInfo.getPlayerStatus().getTeleport());
                        data.writeByte(playerInfo.getPlayerSkill().isMonkey() ? 1 : 0);
                        data.writeShort(playerFashion.getMount());
                    }

                    data.writeByte(playerFashion.getFlagPk());
                    data.writeByte(playerInfo.getPlayerFusion().getTypeFusion() != 0 ? 1 : 0);
                    data.writeShort(playerInfo.getAura());
                    data.writeByte(playerInfo.getEffSetItem());
                    data.writeShort(playerInfo.getIdHat());
                    isMe.sendMessage(message);
                } catch (Exception ex) {
                    LogServer.LogException("addPlayer: " + ex.getMessage(), ex);
                }
            }
            default -> {
            }
        }
    }

    private boolean writePlayerInfo(LiveObject entity, DataOutputStream data, PlayerFashion playerFashion) throws Exception {
        byte level = (byte) CaptionManager.getInstance().getLevel(entity);
        data.writeByte(level);
        data.writeBoolean(false);// write isInvisiblez
        data.writeByte(entity.getTypePk()); // write status Pk
        data.writeByte(entity.getGender());
        data.writeByte(entity.getGender());
        data.writeShort(playerFashion.getHead());
        data.writeUTF(entity.getName());
        data.writeLong(entity.getPlayerPoints().getCurrentHP());
        data.writeLong(entity.getPlayerPoints().getMaxHP());
        data.writeShort(playerFashion.getBody());
        data.writeShort(playerFashion.getLeg());
        data.writeShort(playerFashion.getFlagBag());
        data.writeByte(0);
        data.writeShort(entity.getX());
        data.writeShort(entity.getY());
        data.writeShort(entity.getPlayerPoints().getEff5BuffHp());
        data.writeShort(entity.getPlayerPoints().getEff5BuffMp());
        data.writeByte(0);
        return true;
    }

    public void playerMove(LiveObject entity) {
        try (Message message = new Message(-7)) {
            DataOutputStream data = message.writer();
            data.writeInt(entity.getId());
            data.writeShort(entity.getX());
            data.writeShort(entity.getY());
            entity.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("playerMove: " + ex.getMessage() + " player:  " + entity.getId(), ex);
        }
    }

    public void playerChangerMapByWayPoint(Player player) {
        var ms = System.currentTimeMillis();
        try {
            Area currentArea = player.getArea();
            GameMap currentMap = currentArea.getMap();
            Waypoint waypoint = currentMap.getWayPointInMap(player);
            ServerService serverService = ServerService.getInstance();

            if (waypoint == null) {
                this.keepPlayerInSafeZone(player, null);
                serverService.sendChatGlobal(player.getSession(), null, "Không tìm thấy Waypoint", false);
                return;
            }

            GameMap newMap = MapManager.getInstance().findMapById(waypoint.getGoMap());

            if (newMap == null) {
                this.keepPlayerInSafeZone(player, waypoint);
                serverService.sendChatGlobal(player.getSession(), null, "Map không tồn tại", false);
                return;
            }

            if (player.getPlayerTask().checkMapCanJoinToTask(newMap.getId())) {
                this.keepPlayerInSafeZone(player, waypoint);
                serverService.sendChatGlobal(player.getSession(), null, "Bạn chưa thể đến khu vực này", false);
                return;
            }

//            if (player.getPlayerStatus().getLastTimeChangeMap() + 5000 > System.currentTimeMillis()) {
//                this.keepPlayerInSafeZone(player, waypoint);
//                serverService.sendChatGlobal(player.getSession(), null, "Vui lòng chờ 5 giây để chuyển map", false);
//                return;
//            }

            this.gotoMap(player, newMap, waypoint.getGoX(), waypoint.getGoY());
//            player.getPlayerStatus().setLastTimeChangeMap(System.currentTimeMillis());
        } catch (Exception ex) {
            LogServer.LogException("playerChangerMap: " + ex.getMessage(), ex);
        }
    }

    public void changeArea(Player player, Area newArea) {
        this.transferPlayer(player, newArea, player.getX(), player.getY());
    }

    public void gotoMap(LiveObject object, GameMap goMap, int goX, int goY) {
        switch (object) {
            case Player player -> {
                Area newArea = goMap.getArea();
                if (this.transferPlayer(player, newArea, (short) goX, (short) goY)) {
                    player.getPlayerTask().checkDoneTaskGoMap();
                    DropItemMap.dropMissionItems(player);
                }
            }
            default -> LogServer.LogException("");
        }
    }

    private boolean transferPlayer(Player player, Area newArea, short x, short y) {
        try {
            ServerService serverService = ServerService.getInstance();

            if (newArea == null) {
                this.keepPlayerInSafeZone(player, null);
                serverService.sendChatGlobal(player.getSession(), null, "Không có Area để vào", false);
                return false;
            }

            if (newArea.getPlayersByType(ConstTypeObject.TYPE_PLAYER).size() >= newArea.getMaxPlayers()) {
                this.keepPlayerInSafeZone(player, null);
                serverService.sendChatGlobal(player.getSession(), null, "Khu vực đầy", false);
                return false;
            }

            this.playerExitArea(player);

            newArea.addPlayer(player);
            player.setArea(newArea);
            player.setX(x);
            player.setY(y);
            this.sendMessageChangerMap(player);
            this.sendInfoAllLiveObjectsTo(player);
            this.sendLiveObjectInfoToOthers(player);
            player.getPlayerStatus().setTeleport(0);
            return true;
        } catch (Exception ex) {
            LogServer.LogException("transferPlayer: " + ex.getMessage(), ex);
            return false;
        }
    }

    public void playerExitArea(LiveObject entity) {
        try {
            Area area = entity.getArea();
            if (area.getAllPlayerInZone().containsKey(entity.getId())) {
                this.sendTeleport(entity);
                this.sendRemovePlayerExitArea(entity);
                area.removePlayer(entity);
            }
        } catch (Exception ex) {
            LogServer.LogException("playerExitArea: " + ex.getMessage(), ex);
        }
    }

    private void sendRemovePlayerExitArea(LiveObject entity) {
        try (Message message = new Message(-6)) {
            message.writer().writeInt(entity.getId());
            entity.getArea().sendMessageToPlayersInArea(message, entity);
        } catch (Exception ex) {
            LogServer.LogException("sendRemovePlayerExitArea: " + ex.getMessage() + " player:  " + entity.getId(), ex);
        }
    }

    private void keepPlayerInSafeZone(Player player, Waypoint waypoint) {
        try {
            short safeX = player.getX();
            short safeY = player.getY();

            if (waypoint == null) {
                safeX = 120;
                System.out.println("khong tim thay waypoint");
                safeY = 336;
            } else {
                safeX = (short) (waypoint.getMinX() - 40);
                if (safeX < 0) safeX = (short) (waypoint.getMinX() + 50);
                System.out.println("safeX: " + safeX + " safeY: " + safeY);
            }

            player.setX(safeX);
            player.setY(safeY);
            this.sendResetPoint(player);
        } catch (Exception ex) {
            LogServer.LogException("keepPlayerInSafeZone: " + ex.getMessage(), ex);
        }
    }

    // khi player qua map ma khong the qua duoc thi send messsage nay
    private void sendResetPoint(Player player) {
        try (Message message = new Message(46)) {
            message.writer().writeShort(player.getX());
            message.writer().writeShort(player.getY());
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("sendResetPoint: " + ex.getMessage() + " player:  " + player.getId(), ex);
        }
    }

    public void sendMessageChangerMap(Player player) {
        try {
            var playerService = PlayerService.getInstance();
            MapService.clearMap(player);
            playerService.sendStamina(player);
            playerService.sendCurrencyHpMp(player);
            MapService.getInstance().sendMapInfo(player);// -24
        } catch (Exception ex) {
            LogServer.LogException("Error send Message Changer Map: " + ex.getMessage() + " player:  " + player.getId(), ex);
        }
    }

    public void sendTeleport(LiveObject entity) {
        try (Message message = new Message(-65)) {
            DataOutputStream data = message.writer();
            data.writeInt(entity.getId());
            data.writeByte(entity.getPlayerStatus().getTeleport());
            entity.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendTeleport: " + ex.getMessage() + " player:  " + entity.getId(), ex);
        }
    }

    public void changerMapByShip(Player player, int mapId, int typeTele) {
        GameMap newMap = MapManager.getInstance().findMapById(mapId);
        ServerService serverService = ServerService.getInstance();
        if (newMap == null) {
            serverService.sendChatGlobal(player.getSession(), null, "Map không tồn tại: " + mapId, false);
            return;
        }

        Area newArea = newMap.getArea();
        if (newArea == null) {
            serverService.sendChatGlobal(player.getSession(), null, "Không có khu vực trống trong map: " + mapId, false);
            return;
        }
        player.getPlayerStatus().setTeleport(typeTele);
        AreaService.getInstance().gotoMap(player, newMap, Util.nextInt(400, 444), 5);
    }

}
