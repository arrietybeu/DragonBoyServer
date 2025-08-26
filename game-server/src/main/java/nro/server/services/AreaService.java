package nro.server.services;


import com.artemis.Entity;
import nro.server.engine.entity.GameWorld;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.model.world.World;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Arriety
 */
public class AreaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AreaService.class);

    public void sendPacketForALLPlayerInArea(int mapID, int areaID, NroServerPacket packet) throws RuntimeException {

        var area = World.getInstance().getAreaInMap(mapID, areaID);

        if (area == null)
            throw new RuntimeException("No area found for map ID " + mapID + " and area ID " + areaID);

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
    }

    private static final class SingletonHolder {
        private static final AreaService INSTANCE = new AreaService();
    }

    public static AreaService getInstance() {
        return AreaService.SingletonHolder.INSTANCE;
    }

}
