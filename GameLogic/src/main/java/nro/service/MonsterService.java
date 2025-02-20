package nro.service;

import lombok.Getter;
import nro.model.monster.Monster;
import nro.model.player.Player;
import nro.server.LogServer;
import nro.server.network.Message;
import nro.utils.Util;

import java.io.DataOutputStream;

public class MonsterService {

    @Getter
    private static final MonsterService instance = new MonsterService();

    public void sendMonsterDie(Player playerKill, int mobIb, long dame) {
        try (Message message = new Message(-12)) {
            DataOutputStream data = message.writer();
            data.writeByte(mobIb);
            data.writeLong(dame);
            data.writeBoolean(true);// true = hiển thị chí mạng
            data.writeByte(100);// so luong item roi ra
            for (int i = 0; i <= 100; i++) {
                data.writeShort(i);
                data.writeShort(Util.nextInt(0, 100));
                data.writeShort(playerKill.getX() + Util.nextInt(-10, 1000));
                data.writeShort(playerKill.getY());
                data.writeInt(playerKill.getId());
            }
            playerKill.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendMonsterDie: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
