package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.data_holders.data.ResourcesData;
import nro.server.model.templates.data.EffectDataTemplate;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

import java.io.IOException;

@ServerPacketCommand(ConstsCmd.GET_EFFDATA)
public class SmRequestEffect extends NroServerPacket {

    private final short id;

    public SmRequestEffect(short id) {
        this.id = id;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException, IOException {

        byte zoomlevel = con.getSessionInfo().getClientDeviceInfo().getZoomLevel();
        ResourcesData manager = ResourcesData.getInstance();
        EffectDataTemplate effect = manager.getEffectData(id, zoomlevel);

        if (effect == null) {
            throw new RuntimeException("Effect not found for id: " + id);
        }

        writeShort(id);

        if (effect.getType() == 0) {
            this.writeInt(effect.getDataEffect().length);
            this.writeBytes(effect.getDataEffect());
        } else {
            // data new boss
        }
        this.writeByte(effect.getType());
        this.writeInt(effect.getImg().length);
        this.writeBytes(effect.getImg());
    }

}
