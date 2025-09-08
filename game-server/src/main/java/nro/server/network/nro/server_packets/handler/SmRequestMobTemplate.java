package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.data_holders.data.ResourcesData;
import nro.server.model.templates.data.EffectDataTemplate;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

import java.io.IOException;

/**
 * @author Arriety
 */

@ServerPacketCommand(ConstsCmd.REQUEST_MOB_TEMPLATE)
public class SmRequestMobTemplate extends NroServerPacket {
    private final short id;

    public SmRequestMobTemplate(short id) {
        this.id = id;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException, IOException {

        byte zoomlevel = con.getSessionInfo().getClientDeviceInfo().getZoomLevel();

        ResourcesData manager = ResourcesData.getInstance();

        EffectDataTemplate effect = manager.getMonsterData(id, zoomlevel);

        if (effect == null) {
            throw new RuntimeException("Monster not found for id: " + id);
        }

        writeShort(id);
        writeByte(effect.getType());

        if (effect.getType() != 0) {
            //write data monster new
        } else {
            writeInt(effect.getDataEffectMonster().length);
            writeBytes(effect.getDataEffectMonster());
        }

        this.writeInt(effect.getImg().length);
        this.writeBytes(effect.getImg());

        this.writeByte(effect.getTypeData());

        if (effect.getTypeData() == 1 || effect.getTypeData() == 2) {
            this.writeBytes(effect.getDataEffectBigMonster());
        }

    }
}
