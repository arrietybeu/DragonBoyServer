package nro.server.service.model.entity.player;

import lombok.Getter;
import lombok.Setter;
import nro.server.manager.MapManager;
import nro.server.service.core.map.AreaService;
import nro.server.service.core.system.ServerService;
import nro.server.service.model.map.areas.Area;
import nro.server.service.model.template.map.Transport;
import nro.server.system.LogServer;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlayerTransport {

    private final Player owner;
    private List<Transport> transports;
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

            this.transports.add(beforeTransport);
        }

        for (Transport transport : transports) {
            if (transport.getMapIdByGender(gender) == currentMapId) continue;
            this.transports.add(transport);
        }
    }

    public void playerTransport(int index) {
        long ms = System.currentTimeMillis();
        try {
            var lastTimeTransport = owner.getPlayerContext().getLastTimeTransport();
            if (ms - lastTimeTransport < 10000) {
                long remainingTime = (10000 - (ms - lastTimeTransport)) / 1000;
                ServerService.getInstance().sendChatGlobal(owner.getSession(), null, String.format("Vui lòng đợi %d giây để sử dụng lại", remainingTime), false);
                return;
            }
            List<Transport> transports = owner.getPlayerTransport().getTransports();

            if (!isValidIndex(index, transports)) return;// check index

            Transport destination = transports.get(index);
            short mapId = destination.getMapIdByGender(owner.getGender());

            if (beforeTransport == null || destination != beforeTransport) {
                this.saveCurrentLocationAsBefore(owner, destination);
            } else {
                this.beforeTransport = null;
            }

            AreaService.getInstance().changerMapByShip(owner, mapId, destination.getX(), destination.getY(), 1,
                    destination.getAreeBefore());
            transports.clear();

            owner.getPlayerContext().setLastTimeTransport(System.currentTimeMillis());
        } catch (Exception ex) {
            LogServer.LogException("playerTransport: " + ex.getMessage(), ex);
        }
    }

    private boolean isValidIndex(int index, List<Transport> transports) {
        return index >= 0 && index < transports.size();
    }

    private void saveCurrentLocationAsBefore(Player player, Transport transport) {
        Transport before = new Transport();
        Area area = player.getArea();
        String mapName = "Về chỗ cũ: " + area.getMap().getName();
        before.setName(mapName);
        before.setMapIds(new short[]{(short) area.getMap().getId()});
        before.setPlanetName(transport.getPlanetName());
        before.setX(transport.getX());
        before.setY(transport.getY());
        before.setAreeBefore(area);
        player.getPlayerTransport().setBeforeTransport(before);
    }

    public void dispose() {
        this.transports.clear();
        this.transports = null;
        this.beforeTransport = null;
    }


}
