package nro.server.service.core.map;

import lombok.Getter;
import nro.consts.ConstTypeObject;
import nro.server.service.core.player.PlayerTransport;
import nro.server.service.core.system.ServerService;
import nro.server.service.model.clan.Clan;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.discpile.Disciple;
import nro.server.service.model.map.GameMap;
import nro.server.service.model.map.Waypoint;
import nro.server.service.model.map.areas.Area;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.entity.Fashion;
import nro.server.network.Message;
import nro.server.service.core.item.DropItemMap;
import nro.server.service.model.template.map.Transport;
import nro.server.system.LogServer;
import nro.server.manager.CaptionManager;
import nro.server.manager.MapManager;
import nro.server.service.core.player.PlayerService;
import nro.utils.Util;

import java.io.DataOutputStream;
import java.util.List;
import java.util.Map;

public class AreaService {

    @Getter
    public static final AreaService instance = new AreaService();

    public void sendInfoAllLiveObjectsTo(Entity entity) {
        try {
            Map<Integer, Entity> entities = entity.getArea().getAllEntityInArea();

            for (Entity plInZone : entities.values()) {
                // Hiện tại chỉ xử lý Player
                if (plInZone != entity) {
                    // Gửi thông tin Player đang có mặt cho entity (người mới vào)
                    this.addPlayer(entity, plInZone);
                }
            }

            this.sendLiveObjectInfoToOthers(entity);

        } catch (Exception ex) {
            LogServer.LogException("sendInfoAllPlayerInArea: " + ex.getMessage(), ex);
        }
    }

    /**
     * Gửi thông tin của entity vừa vào zone đến tất cả các thực thể khác trong zone.
     * <p>
     * - entity: người/đối tượng mới vào zone.
     * - Mục tiêu: những người khác trong zone sẽ thấy entity này xuất hiện.
     */
    private void sendLiveObjectInfoToOthers(Entity entity) {
        try {
            Map<Integer, Entity> players = entity.getArea().getAllEntityInArea();

            for (Entity obj : players.values()) {
                switch (obj) {
                    case Player plInZone -> {
                        if (plInZone != entity) {
                            // Gửi thông tin entity cho player đang ở trong zone
                            this.addPlayer(plInZone, entity);
                        }
                    }
                    default -> LogServer.LogException("sendLiveObjectInfoToOthers: Not Object: " + entity);
                }
            }

        } catch (Exception ex) {
            LogServer.LogException("sendPlayerInfoToAllInArea: " + ex.getMessage(), ex);
        }
    }


    private void addPlayer(Entity entity, Entity isMe) {
        Player me = (Player) isMe;
        try (Message message = new Message(-5)) {
            DataOutputStream data = message.writer();
            Fashion fashion = entity.getFashion();
            data.writeInt(entity.getId());

            Clan clan = entity.getArea().getAllEntityInArea().get(entity.getId()) != null ? ((Player) entity).getClan() : null;
            data.writeInt(clan != null ? clan.getId() : -1);
            if (this.writePlayerInfo(entity, data, fashion)) {
                data.writeByte(entity.getTeleport());
                data.writeByte(entity.getSkills().isMonkey() ? 1 : 0);
                data.writeShort(fashion.getMount());
            }

            data.writeByte(fashion.getFlagPk());
            data.writeByte(entity.getFusion().getTypeFusion() != 0 ? 1 : 0);
            data.writeShort(entity.getFashion().getAura());
            data.writeByte(entity.getFashion().getEffSetItem());
            data.writeShort(entity.getFashion().getIdHat());
            me.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("addPlayer: " + ex.getMessage(), ex);
        }
    }

    private boolean writePlayerInfo(Entity entity, DataOutputStream data, Fashion fashion) throws Exception {
        byte level = (byte) CaptionManager.getInstance().getLevel(entity);
        data.writeByte(level);
        data.writeBoolean(false);// write isInvisiblez
        data.writeByte(entity.getTypePk()); // write status Pk
        data.writeByte(entity.getGender());
        data.writeByte(entity.getGender());
        data.writeShort(fashion.getHead());
        data.writeUTF(entity.getName());
        data.writeLong(entity.getPoints().getCurrentHP());
        data.writeLong(entity.getPoints().getMaxHP());
        data.writeShort(fashion.getBody());
        data.writeShort(fashion.getLeg());
        data.writeShort(fashion.getFlagBag());
        data.writeByte(0);
        data.writeShort(entity.getX());
        data.writeShort(entity.getY());
        data.writeShort(entity.getPoints().getEff5BuffHp());
        data.writeShort(entity.getPoints().getEff5BuffMp());
        data.writeByte(0);
        return true;
    }

    public void playerMove(Entity entity) {
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
//        var ms = System.currentTimeMillis();
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

    public void gotoMap(Entity object, GameMap goMap, int goX, int goY) {
        switch (object) {
            case Player player -> {
                Area newArea = goMap.getArea();
                if (this.transferPlayer(player, newArea, (short) goX, (short) goY)) {
                    player.getPlayerTask().checkDoneTaskGoMap();
                    DropItemMap.dropMissionItems(player);
                }
            }
            case Disciple disciple -> {
                Area newArea = goMap.getArea();
            }
            default -> LogServer.LogException("Not Entity :" + object);
        }
    }

    private boolean transferPlayer(Entity entity, Area newArea, short x, short y) {
        try {
            switch (entity) {
                case Player player -> {
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

                    this.entityEnterArea(player, newArea, x, y);
                    this.sendMessageChangerMap(player);// no entity
                    return true;
                }
                case Disciple disciple -> {
                    if (newArea == null) {
                        return false;
                    }

                    this.entityEnterArea(disciple, newArea, x, y);
                    return true;
                }
                default -> {
                    LogServer.LogException("transferPlayer: Not Entity :" + entity);
                    return false;
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("transferPlayer: " + ex.getMessage(), ex);
            return false;
        }
    }

    private void entityEnterArea(Entity entity, Area newArea, short x, short y) {
        try {
            // xoa entity khỏi area cũ
            this.playerExitArea(entity);

            // add entity vào area mới
            newArea.addPlayer(entity);
            entity.setArea(newArea);
            entity.setX(x);
            entity.setY(y);
            this.sendInfoAllLiveObjectsTo(entity);

        } catch (Exception ex) {
            LogServer.LogException("entityEnterArea: " + ex.getMessage(), ex);
        }
    }

    public void playerExitArea(Entity entity) {
        try {
            Area area = entity.getArea();
            if (area.getAllEntityInArea().containsKey(entity.getId())) {
                this.sendTeleport(entity);
                this.sendRemovePlayerExitArea(entity);
                area.removePlayer(entity);
            }
        } catch (Exception ex) {
            LogServer.LogException("playerExitArea: " + ex.getMessage(), ex);
        }
    }

    private void sendRemovePlayerExitArea(Entity entity) {
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
//                System.out.println("safeX: " + safeX + " safeY: " + safeY);
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

    public void sendTeleport(Entity entity) {
        try (Message message = new Message(-65)) {
            DataOutputStream data = message.writer();
            data.writeInt(entity.getId());
            data.writeByte(entity.getTeleport());
            entity.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendTeleport: " + ex.getMessage() + " player:  " + entity.getId(), ex);
        }
    }

    public void changerMapByShip(Player player, int mapId, int x, int y, int typeTele) {
        try {
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
            player.setTeleport(typeTele);
            AreaService.getInstance().gotoMap(player, newMap, x, y);
        } catch (Exception ex) {
            LogServer.LogException("changerMapByShip: " + ex.getMessage(), ex);
        }
    }




}
