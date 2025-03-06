package nro.service;

import lombok.Getter;
import nro.consts.ConstTypeObject;
import nro.model.map.GameMap;
import nro.model.map.Waypoint;
import nro.model.map.areas.Area;
import nro.model.player.Player;
import nro.model.player.PlayerFashion;
import nro.server.network.Message;
import nro.server.LogServer;
import nro.server.manager.CaptionManager;
import nro.server.manager.MapManager;

import java.io.DataOutputStream;
import java.util.Map;

public class AreaService {

    @Getter
    public static final AreaService instance = new AreaService();

    public void sendInfoAllPlayerInArea(Player player) {
        try {
            Map<Integer, Player> players = player.getArea().getAllPlayerInZone();
            for (Player plInZone : players.values()) {
                if (plInZone != player) {
                    this.addPlayer(player, plInZone);
                }
            }
            this.sendPlayerInfoToAllInArea(player);
        } catch (Exception ex) {
            LogServer.LogException("sendInfoAllPlayerInArea: " + ex.getMessage(), ex);
        }
    }

    private void sendPlayerInfoToAllInArea(Player player) {
        try {
            Map<Integer, Player> players = player.getArea().getAllPlayerInZone();
            for (Player plInZone : players.values()) {
                if (plInZone != player) {
                    this.addPlayer(plInZone, player);
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("sendPlayerInfoToAllInArea: " + ex.getMessage(), ex);
        }
    }

    private void addPlayer(Player isMe, Player playerInfo) {
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
            ex.printStackTrace();
        }
    }

    private boolean writePlayerInfo(Player player, DataOutputStream data, PlayerFashion playerFashion)
            throws Exception {
        byte level = (byte) CaptionManager.getInstance().getLevel(player);
        data.writeByte(level);
        data.writeBoolean(false);// write isInvisiblez
        data.writeByte(player.getTypePk()); // write type Pk
        data.writeByte(player.getGender());
        data.writeByte(player.getGender());
        data.writeShort(playerFashion.getHead());
        data.writeUTF(player.getName());
        data.writeLong(player.getPlayerPoints().getCurrentHP());
        data.writeLong(player.getPlayerPoints().getMaxHP());
        data.writeShort(playerFashion.getBody());
        data.writeShort(playerFashion.getLeg());
        data.writeShort(playerFashion.getFlagBag());
        data.writeByte(0);
        data.writeShort(player.getX());
        data.writeShort(player.getY());
        data.writeShort(player.getPlayerPoints().getEff5BuffHp());
        data.writeShort(player.getPlayerPoints().getEff5BuffMp());
        data.writeByte(0);
        return true;
    }

    public void playerMove(Player player) {
        try (Message message = new Message(-7)) {
            DataOutputStream data = message.writer();
            data.writeInt(player.getId());
            data.writeShort(player.getX());
            data.writeShort(player.getY());
            player.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("playerMove: " + ex.getMessage() + " player:  " + player.getId(), ex);
        }
    }

    public void playerChangerMapByWayPoint(Player player) {
        try {
            Area currentArea = player.getArea();
            GameMap currentMap = currentArea.getMap();
            Waypoint waypoint = currentMap.getWayPointInMap(player);
            Service service = Service.getInstance();

            if (waypoint == null) {
                this.keepPlayerInSafeZone(player, null);
                service.sendChatGlobal(player.getSession(), null, "Không tìm thấy Waypoint", false);
                return;
            }

            GameMap newMap = MapManager.getInstance().findMapById(waypoint.getGoMap());

            if (newMap == null) {
                this.keepPlayerInSafeZone(player, waypoint);
                service.sendChatGlobal(player.getSession(), null, "Map không tồn tại", false);
                return;
            }

            if (player.getPlayerTask().checkMapCanJoinToTask(newMap.getId())) {
                this.keepPlayerInSafeZone(player, waypoint);
                service.sendChatGlobal(player.getSession(), null, "Bạn chưa thể đến khu vực này", false);
                return;
            }

            this.gotoMap(player, newMap, waypoint.getGoX(), waypoint.getGoY());

        } catch (Exception ex) {
            LogServer.LogException("playerChangerMap: " + ex.getMessage(), ex);
        }
    }

    public void changeArea(Player player, Area newArea) {
        this.transferPlayer(player, newArea, player.getX(), player.getY());
    }

    public void gotoMap(Player player, GameMap goMap, short goX, short goY) {
        Area newArea = goMap.getArea();
        this.transferPlayer(player, newArea, goX, goY);
        player.getPlayerTask().checkDoneTaskGoMap();
    }

    private void transferPlayer(Player player, Area newArea, short x, short y) {
        Service service = Service.getInstance();

        if (newArea == null) {
            this.keepPlayerInSafeZone(player, null);
            service.sendChatGlobal(player.getSession(), null, "Không có Area để vào", false);
            return;
        }

        if (newArea.getPlayersByType(ConstTypeObject.TYPE_PLAYER).size() >= newArea.getMaxPlayers()) {
            this.keepPlayerInSafeZone(player, null);
            service.sendChatGlobal(player.getSession(), null, "Khu vực đầy", false);
            return;
        }

        this.playerExitArea(player);

        newArea.addPlayer(player);
        player.setArea(newArea);
        player.setX(x);
        player.setY(y);
        this.sendMessageChangerMap(player);
        this.sendInfoAllPlayerInArea(player);
        this.sendPlayerInfoToAllInArea(player);
        player.getPlayerStatus().setTeleport(0);
    }

    public void playerExitArea(Player player) {
        try {
            Area area = player.getArea();
            if (area.getAllPlayerInZone().containsKey(player.getId())) {
                this.sendTeleport(player);
                this.sendRemovePlayerExitArea(player);
                area.removePlayer(player);
            }
        } catch (Exception ex) {
            LogServer.LogException("playerExitArea: " + ex.getMessage(), ex);
        }
    }

    private void sendRemovePlayerExitArea(Player player) {
        try (Message message = new Message(-6)) {
            message.writer().writeInt(player.getId());
            player.getArea().sendMessageToPlayersInArea(message, player);
        } catch (Exception ex) {
            LogServer.LogException("sendRemovePlayerExitArea: " + ex.getMessage() + " player:  " + player.getId(), ex);
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
                if (safeX < 0)
                    safeX = (short) (waypoint.getMinX() + 50);
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
            LogServer.LogException("Error send Message Changer Map: " + ex.getMessage() + " player:  " + player.getId(),
                    ex);
        }
    }

    public void sendTeleport(Player player) {
        try (Message message = new Message(-65)) {
            DataOutputStream data = message.writer();
            data.writeInt(player.getId());
            data.writeByte(player.getPlayerStatus().getTeleport());
            player.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendTeleport: " + ex.getMessage() + " player:  " + player.getId(), ex);
        }
    }

}
