package nro.server.service.core.player;

import lombok.Getter;
import lombok.Setter;
import nro.server.manager.MapManager;
import nro.server.service.core.map.AreaService;
import nro.server.service.core.system.ServerService;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.template.map.Transport;
import nro.server.system.LogServer;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlayerTransport {

    private final Player owner;
    private final List<Transport> transports;
    private Transport beforeTransport;

    public PlayerTransport(Player owner) {
        this.owner = owner;
        this.transports = new ArrayList<>();
    }

    public void initCapsuleTransport() {
        this.transports.clear();
        List<Transport> transports = MapManager.getInstance().getTransports();
        int currentMapId = owner.getArea().getMap().getId();
        int gender = owner.getGender();

        if (beforeTransport != null) {
            beforeTransport.setName("Về chỗ cũ: " + beforeTransport.getName());
            this.transports.add(beforeTransport);
        }
        for (Transport transport : transports) {
            if (transport.getMapIdByGender(gender) == currentMapId) continue;
            this.transports.add(transport);
        }
    }

    public void playerTransport(Player player, int index) {
        long ms = System.currentTimeMillis();
        try {
            var lastTimeTransport = player.getPlayerStatus().getLastTimeTransport();
            if (ms - lastTimeTransport < 10000) {
                long remainingTime = (10000 - (ms - lastTimeTransport)) / 1000;
                ServerService.getInstance().sendChatGlobal(
                        player.getSession(), null,
                        String.format("Vui lòng đợi %d giây để sử dụng lại", remainingTime),
                        false
                );
                return;
            }
            List<Transport> transports = player.getPlayerTransport().getTransports();

            if (!isValidIndex(index, transports)) return;// check index

            Transport destination = transports.get(index);
            this.saveCurrentLocationAsBefore(player, destination);

            short mapId = destination.getMapIdByGender(player.getGender());
            AreaService.getInstance().changerMapByShip(player, mapId, destination.getX(), destination.getY(), 1);
            transports.clear();
            player.getPlayerStatus().setLastTimeTransport(System.currentTimeMillis());
        } catch (Exception ex) {
            LogServer.LogException("playerTransport: " + ex.getMessage(), ex);
        }
    }

    private boolean isValidIndex(int index, List<Transport> transports) {
        return index >= 0 && index < transports.size();
    }

    private void saveCurrentLocationAsBefore(Player player, Transport transport) {
        Transport before = new Transport();
        String mapName = player.getArea().getMap().getName();
        before.setName(mapName);
        before.setMapIds(new short[]{(short) player.getArea().getMap().getId()});
        before.setPlanetName(transport.getPlanetName());
        before.setX(transport.getX());
        before.setY(transport.getY());
        player.getPlayerTransport().setBeforeTransport(before);
    }


}
