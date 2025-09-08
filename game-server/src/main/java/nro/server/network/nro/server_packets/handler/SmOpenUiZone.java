package nro.server.network.nro.server_packets.handler;

import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import lombok.extern.slf4j.Slf4j;
import nro.commons.consts.ConstsCmd;
import nro.server.model.ecs.component.InfoComponent;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.map.zone.ZoneType;
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
            boolean isOffline = zone.type() == ZoneType.OFFLINE;
            final ImmutableBag<Entity> playersInZone = MapUtils.getPlayers(zone);

            final int numPlayers = playersInZone.size();

            writeByte(zone.zoneId()); // id zone
            writeByte(numPlayers < 5 ? 0 : numPlayers < 8 ? 1 : 2); // 0 blue || 1 yellow || 2 red
            writeByte(numPlayers); // số người chơi trong zone
            writeByte(zone.maxPlayers()); // max người chơi trong zone

            if (isOffline) {
                var info = playersInZone.size() > 0 ? playersInZone.get(0).getComponent(InfoComponent.class) : null;
                writeByte(1);
                writeUTF(zone.groupName());
                writeInt(0);
                writeUTF(info != null ? info.name : "thằng này ra khỏi khu rồi");
                writeInt(info != null ? info.id : 0);
            } else {
                writeByte(0);
            }
        }
    }

    /**
     * mô phỏng code client
     * public void openUIZone(Message message)
     * {
     * InfoDlg.hide();
     * try
     * {
     * zones = new int[message.reader().readByte()];
     * pts = new int[zones.Length];
     * numPlayer = new int[zones.Length];
     * maxPlayer = new int[zones.Length];
     * rank1 = new int[zones.Length];
     * rankName1 = new string[zones.Length];
     * rank2 = new int[zones.Length];
     * rankName2 = new string[zones.Length];
     * for (int i = 0; i < zones.Length; i++)
     * {
     * zones[i] = message.reader().readByte();
     * pts[i] = message.reader().readByte();
     * numPlayer[i] = message.reader().readByte();
     * maxPlayer[i] = message.reader().readByte();
     * sbyte b = message.reader().readByte();
     * if (b == 1)
     * {
     * rankName1[i] = message.reader().readUTF();
     * rank1[i] = message.reader().readInt();
     * rankName2[i] = message.reader().readUTF();
     * rank2[i] = message.reader().readInt();
     * }
     * }
     * }
     * catch (Exception ex)
     * {
     * Cout.LogError("Loi ham OPEN UIZONE " + ex.ToString());
     * }
     * GameCanvas.panel.setTypeZone();
     * GameCanvas.panel.show();
     * }
     */

}
