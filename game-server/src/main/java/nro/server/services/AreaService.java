package nro.server.services;


import com.artemis.Entity;
import lombok.NoArgsConstructor;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.model.world.World;
import nro.server.model.world.WorldMapInstance;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.handler.SmPlayerAdd;
import nro.server.network.nro.server_packets.handler.SmPlayerRemove;
import nro.server.network.nro.server_packets.handler.SmTeleport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Arriety
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class AreaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AreaService.class);

    public void sendPacketForALLPlayerInArea(int mapID, int areaID, NroServerPacket packet) throws RuntimeException {
        try {

            var area = World.getInstance().getAreaInMap(mapID, areaID);

            var entities = area.getEntities();
            for (int i = 0; i < entities.size(); i++) {
                Entity e = entities.get(i);

                if (e == null) continue;

                var playerComponent = e.getComponent(PlayerComponent.class);

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

    public void sendPacketForALLPlayerInAreaNotMe(int entityId, int mapID, int areaID, NroServerPacket packet) {
        try {

            var area = World.getInstance().getAreaInMap(mapID, areaID);

            var entities = area.getEntities();
            for (int i = 0; i < entities.size(); i++) {
                Entity e = entities.get(i);

                if (e == null) continue;

                if (e.getId() == entityId) continue;

                var playerComponent = e.getComponent(PlayerComponent.class);

                if (playerComponent != null && playerComponent.isOnline()) {
                    var connect = playerComponent.connection;
                    if (connect.getState() != NroConnection.State.IN_GAME) continue;

                    System.out.println("send packet player move to player: " + mapID);
                    connect.sendPacket(packet);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error sending packet to all players in area (mapID: {}, areaID: {}): {}", mapID, areaID, e.getMessage(), e);
        }
    }

    public void sendMyInfoToPlayersInZone(NroConnection client) {
        try {
            var entity = client.getEntity();
            var position = entity.getComponent(PositionComponent.class);

            var area = World.getInstance().getAreaInMap(position.mapId, position.getAreaId());

            for (var playerInZone : area.getPlayersInZone()) {
                if (playerInZone == null || playerInZone.getId() == entity.getId()) continue;

                var playerComponent = playerInZone.getComponent(PlayerComponent.class);

                if (playerComponent != null && playerComponent.isOnline()) {
                    var connect = playerComponent.connection;
                    if (connect.getState() != NroConnection.State.IN_GAME) continue;
                    // send thông tin của client đến playerInZone
                    connect.sendPacket(new SmPlayerAdd(entity));
                }
            }

            this.sendPlayersInfoInZoneToMe(entity, area);

        } catch (Throwable e) {
            LOGGER.error("Error sending player info to others in clinet: {}", client, e);
        }
    }

    private void sendPlayersInfoInZoneToMe(Entity entity, WorldMapInstance area) {

        for (var playerInZone : area.getPlayersInZone()) {

            if (playerInZone == null || playerInZone.getId() == entity.getId()) continue;
            var playerComponent = playerInZone.getComponent(PlayerComponent.class);

            if (playerComponent != null && playerComponent.isOnline()) {
                var client = entity.getComponent(PlayerComponent.class);
                client.connection.sendPacket(new SmPlayerAdd(playerInZone));
            }
        }
    }

    public void sendPacketPlayersInZoneNotMe(NroConnection ss, WorldMapInstance area) {
        var entity = ss.getEntity();
        var meID = ss.getPlayerID();

        for (var playerInZone : area.getPlayersInZone()) {
            if (playerInZone == null || playerInZone.equals(entity)) continue;
            var playerComponent = playerInZone.getComponent(PlayerComponent.class);
            if (playerComponent != null && playerComponent.isOnline()) {
                var client = entity.getComponent(PlayerComponent.class);
                client.connection.sendPacket(new SmTeleport());
                client.connection.sendPacket(new SmPlayerRemove(meID));
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
