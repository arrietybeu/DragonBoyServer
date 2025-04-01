package nro.server.service.core.dragon;

import lombok.Getter;
import nro.consts.ConstShenronDragon;
import nro.consts.ConstsCmd;
import nro.server.network.Message;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.map.GameMap;
import nro.server.system.LogServer;

import java.io.DataOutputStream;

public class DragonService {

    @Getter
    private static final DragonService instance = new DragonService();

    public void sendShenronDragon(Player player, int type, boolean typeDragon) {
        try (Message message = new Message(ConstsCmd.CALL_DRAGON)) {
            DataOutputStream write = message.writer();
            write.writeByte(type);
            if (type == ConstShenronDragon.APPEAR_DRAGON) {
                this.writeDataAppearDragon(player, write, typeDragon);
            }
            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("sendShenronDragon: " + e.getMessage(), e);
        }
    }

    private void writeDataAppearDragon(Player player, DataOutputStream write, boolean typeDragon) throws Exception {
        GameMap gameMap = player.getArea().getMap();
        write.writeShort(gameMap.getId());
        write.writeShort(gameMap.getBgId());
        write.writeByte(player.getArea().getId());

        write.writeInt(player.getId());
        write.writeUTF(player.getName());
        write.writeShort(player.getX());
        write.writeShort(player.getY());

        var isDragon = typeDragon ? 1 : 0;

        write.writeByte(isDragon);
    }

}
