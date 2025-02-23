package nro.service;

import lombok.Getter;
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
            LogServer.LogException("sendInfoAllPlayerInArea: " + ex.getMessage());
            ex.printStackTrace();
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
            LogServer.LogException("sendPlayerInfoToAllInArea: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void addPlayer(Player isMe, Player playerInfo) {
        try (Message message = new Message(-5)) {
            DataOutputStream data = message.writer();
            PlayerFashion playerFashion = playerInfo.getPlayerFashion();
            data.writeInt(playerInfo.getId());
            data.writeInt(playerInfo.getClan() != null ? playerInfo.getClan().getId() : -1);
            if (this.writePlayerInfo(playerInfo, data, playerFashion)) {
                data.writeByte(playerInfo.getTeleport());
                data.writeByte(playerInfo.getPlayerSkill().isMonkey() ? 1 : 0);
                data.writeShort(playerInfo.getMount());
            }

            data.writeByte(playerFashion.getFlag());
            data.writeByte(playerInfo.getPlayerFusion().getTypeFusion() != 0 ? 1 : 0);
            data.writeShort(playerInfo.getAura());
            data.writeByte(playerInfo.getEffSetItem());
            data.writeShort(playerInfo.getIdHat());
            isMe.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("addPlayer: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private boolean writePlayerInfo(Player player, DataOutputStream data, PlayerFashion playerFashion) throws Exception {
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
            LogServer.LogException("playerMove: " + ex.getMessage() + " player:  " + player.getId());
            ex.printStackTrace();
        }
    }

    public void playerChangerMapByWayPoint(Player player) {
        var ms = System.currentTimeMillis();
        try {
            Area currentArea = player.getArea();
            GameMap currentMap = currentArea.getMap();
            Waypoint waypoint = currentMap.getWayPointInMap(player);
            Service service = Service.getInstance();

            if (waypoint == null) {
                this.keepPlayerInSafeZone(player);
                service.sendChatGlobal(player.getSession(), null, "Không tìm thấy Waypoint", false);
                return;
            }

            GameMap newMap = MapManager.getInstance().findMapById(waypoint.getGoMap());
            if (newMap == null) {
                this.keepPlayerInSafeZone(player);
                service.sendChatGlobal(player.getSession(), null, "Map không tồn tại", false);
                return;
            }

            this.gotoMap(player, newMap, waypoint.getGoX(), waypoint.getGoY());

        } catch (Exception ex) {
            LogServer.LogException("playerChangerMap: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void changeArea(Player player, Area newArea) {
        this.transferPlayer(player, newArea, player.getX(), player.getY());
    }

    public void gotoMap(Player player, GameMap goMap, short goX, short goY) {
        Area newArea = goMap.getArea();
        this.transferPlayer(player, newArea, goX, goY);
    }

    private void transferPlayer(Player player, Area newArea, short x, short y) {
        if (newArea == null) {
            this.keepPlayerInSafeZone(player);
            Service.getInstance().sendChatGlobal(player.getSession(), null, "Không có Area để vào", false);
            return;
        }

        if (newArea.getPlayers().size() >= newArea.getMaxPlayers()) {
            this.keepPlayerInSafeZone(player);
            Service.getInstance().sendChatGlobal(player.getSession(), null, "Khu vực đầy", false);
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
    }

    public void playerExitArea(Player player) {
        try {
            Area area = player.getArea();
            if (area.getAllPlayerInZone().containsKey(player.getId())) {
                this.sendRemovePlayerExitArea(player);
                area.removePlayer(player);
            }
        } catch (Exception ex) {
            LogServer.LogException("playerExitArea: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void sendRemovePlayerExitArea(Player player) {
        try (Message message = new Message(-6)) {
            message.writer().writeInt(player.getId());

            player.getArea().sendMessageToPlayersInArea(message, player);
        } catch (Exception ex) {
            LogServer.LogException("sendRemovePlayerExitArea: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void keepPlayerInSafeZone(Player player) {
        try {
            GameMap map = player.getArea().getMap();
            int safeX = player.getX();

            if (safeX >= map.getTileMap().tmh() - 60) {
                safeX = map.getTileMap().tmw() - 60;
            } else if (safeX <= 60) {
                safeX = 60;
            }
            player.setX((short) safeX);
            this.sendResetPoint(player);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // khi player qua map ma khong the qua duoc thi send messsage nay
    private void sendResetPoint(Player player) {
        try (Message message = new Message(46)) {
            message.writer().writeShort(player.getX());
            message.writer().writeShort(player.getY());
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("sendResetPoint: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void sendMessageChangerMap(Player player) {
        try {
            var playerService = PlayerService.getInstance();
            MapService.clearMap(player);
            playerService.sendStamina(player);
            playerService.sendCurrencyHpMp(player);
            MapService.getInstance().sendMapInfo(player);// -24

        } catch (Exception ex) {
            LogServer.LogException("Error send Message Changer Map: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


}
