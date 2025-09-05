package nro.server.network.nro.server_packets.handler;

import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import lombok.extern.slf4j.Slf4j;
import nro.commons.consts.ConstsCmd;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;
import nro.server.utils.MapUtils;

import java.io.IOException;

/**
 * @author Arriety
 */
@Slf4j
@ServerPacketCommand(ConstsCmd.OPEN_UI_ZONE)
public class SmOpenUiZone extends NroServerPacket {

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException, IOException {

        final var entity = con.getEntity();

        final var pos = entity.getComponent(PositionComponent.class);

        if (pos == null) {
            writeByte(0);
            throw new RuntimeException("Position is null for client: " + con);
        }

        final var zones = MapUtils.getAllZoneForMapID(pos.mapId);

        // dùng để đọc số lượng zone trong map
        if (zones.isEmpty()) {
            writeByte(0);
            throw new RuntimeException("Zones is empty for client: " + con + " PositionComponent: " + pos);
        }

        writeByte(zones.size());

        for (final var zone : zones) {
            if (zone == null) {
                writeByte(0);
                log.warn(" Zone is null for client: {} PositionComponent: {}", con, pos);
                continue;
            }
            final ImmutableBag<Entity> playersInZone = MapUtils.getEntities(zone);

            final int numPlayers = playersInZone.size();

            writeByte(zone.zoneId()); // id zone
            writeByte(numPlayers < 5 ? 0 : numPlayers < 8 ? 1 : 2); // 0 blue || 1 yellow || 2 red
            writeByte(numPlayers); // số người chơi trong zone
            writeByte(zone.maxPlayers()); // max người chơi trong zone
            writeByte(1); // hardcode 1
            writeUTF("cho huy"); // tên zone
            writeInt(1); // hardcode 1
            writeUTF("cho jake"); // tên map
            writeInt(100); // id map
        }
    }

}
