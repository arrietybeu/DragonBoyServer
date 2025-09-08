package nro.server.services;


import com.artemis.Entity;
import lombok.NoArgsConstructor;
import nro.server.engine.GameWorld;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.handler.SmPlayerAdd;
import nro.server.network.nro.server_packets.handler.SmPlayerRemove;
import nro.server.network.nro.server_packets.handler.SmTeleport;
import nro.server.utils.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class AreaService {

    private static final EntityQueryService entityQueryService = GameWorld.query();


    // FIXME class này rác lắm đừng đọc (chạy được là được rồi)

    private static final Logger LOGGER = LoggerFactory.getLogger(AreaService.class);

    /**
     * Phương thức này dùng để gửi một gói tin (packet) đến tất cả người chơi trong một khu vực cụ thể trên bản đồ.
     * <p>Ví dụ: Chat gửi packet cho toàn bộ người chơi trong map kể cả người chat</p>
     *
     * @param mapID
     * @param areaID
     * @param packet
     * @throws RuntimeException
     */
    public void sendPacketForALLPlayerInArea(int mapID, int areaID, NroServerPacket packet) throws RuntimeException {
        try {
            var zone = MapUtils.findZone((short) mapID, areaID);

            var entities = MapUtils.getPlayers(zone);

            for (int i = 0; i < entities.size(); i++) {
                Entity e = entities.get(i);

                if (e == null) continue;

                var playerComponent = entityQueryService.getPlayer(e.getId());

                if (playerComponent != null && playerComponent.isOnline()) {
                    var connect = playerComponent.connection;
                    if (connect.getState() != NroConnection.State.IN_GAME) continue;
                    connect.sendPacket(packet);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error sending packet to all players in area (mapID: {}, areaID: {}): {}",
                    mapID, areaID, e.getMessage(), e);
        }
    }

    /**
     * Phương thức này dùng để gửi packet cho tất cả người chơi trong area ngoại trừ người chơi có entityId được chỉ định.
     * <p>Ví dụ: Player A di chuyển thì các người chơi trong area được nhận packet này, nhưng người chơi A sẽ không được nhận packet này</p>
     *
     * @param entityId
     * @param mapID
     * @param areaID
     * @param packet
     */
    public void sendPacketForALLPlayerInAreaNotMe(int entityId, int mapID, int areaID, NroServerPacket packet) {
        try {

            var zone = MapUtils.findZone((short) mapID, areaID);

            var entities = MapUtils.getPlayers(zone);

            for (int i = 0; i < entities.size(); i++) {
                Entity e = entities.get(i);

                if (e == null) continue;
                if (e.getId() == entityId) continue;
                var playerComponent = entityQueryService.getPlayer(e.getId());

                if (playerComponent != null && playerComponent.isOnline()) {
                    var connect = playerComponent.connection;
                    if (connect.getState() != NroConnection.State.IN_GAME) continue;

                    connect.sendPacket(packet);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error sending packet to all players in area (mapID: {}, areaID: {}): {}", mapID, areaID, e.getMessage(), e);
        }
    }

    /**
     * Phương thức này dùng khi (client) vào area thì sẽ gửi thông tin của thằng (client) đến tất cả người chơi trong area
     * <p>Ví dụ: Player A vào map 0 area 0 ngay lập tức toàn bộ những người trong map 0 area 0 sẽ được nhận thông tin về người A </p>
     *
     * @param client
     */

    public void sendMyInfoToPlayersInZone(NroConnection client) {
        try {
            var entity = client.getEntity();
            var position = entityQueryService.getPosition(entity.getId());


            var zone = MapUtils.findZone(position.mapId, position.getAreaId());

            var entities = MapUtils.getPlayers(zone);

            for (var playerInZone : entities) {
                if (playerInZone == null || playerInZone.getId() == entity.getId()) continue;

                var playerComponent = entityQueryService.getPlayer(playerInZone.getId());

                if (playerComponent != null && playerComponent.isOnline()) {
                    var connect = playerComponent.connection;
                    if (connect.getState() != NroConnection.State.IN_GAME) continue;
                    // send thông tin của client đến playerInZone
                    connect.sendPacket(new SmPlayerAdd(entity));
                }
            }

            this.sendPlayersInfoInZoneToMe(entity, entities);

        } catch (Throwable e) {
            LOGGER.error("Error sending player info to others in clinet: {}", client, e);
        }
    }

    /**
     * Đây là phương thức phụ trợ của {@link  #sendMyInfoToPlayersInZone(NroConnection)} dùng để gửi toàn bộ thông tin của người trong khu vực đến Entity (client)
     *
     * @param entity
     * @param entities
     */
    private void sendPlayersInfoInZoneToMe(Entity entity, Iterable<Entity> entities) {

        for (var playerInZone : entities) {

            if (playerInZone == null || playerInZone.getId() == entity.getId()) continue;
            var playerComponent = entityQueryService.getPlayer(playerInZone.getId());

            if (playerComponent != null && playerComponent.isOnline()) {
                var client = entityQueryService.getPlayer(entity.getId());
                client.connection.sendPacket(new SmPlayerAdd(playerInZone));
            }
        }
    }

    public void sendPacketPlayerExitArea(NroConnection ss, Iterable<Entity> entities) {
        var entity = ss.getEntity();
        var meID = ss.getPlayerID();
        byte teleport = entityQueryService.getPosition(ss.getEntity().getId()).teleport;

        for (var playerInZone : entities) {
            if (playerInZone == null || playerInZone.equals(entity)) continue;
            var playerComponent = entityQueryService.getPlayer(playerInZone.getId());
            if (playerComponent != null && playerComponent.isOnline()) {
                playerComponent.connection.sendPacket(new SmTeleport(meID, teleport));
                playerComponent.connection.sendPacket(new SmPlayerRemove(meID));
            }
        }
    }

    private static final class SingletonHolder {
        private static final AreaService INSTANCE = new AreaService();
    }

    public static AreaService getInstance() {
        return AreaService.SingletonHolder.INSTANCE;
    }

}
