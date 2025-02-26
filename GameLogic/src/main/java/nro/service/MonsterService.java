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

    public void sendMonsterDie(Player playerKill, Monster monster, long dame) {
        try (Message message = new Message(-12)) {
            DataOutputStream data = message.writer();
            data.writeByte(monster.getId());
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
            monster.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendMonsterDie: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void sendMonsterRevice(Monster monster) {
        try (Message message = new Message(-13)) {
            DataOutputStream data = message.writer();
            data.writeByte(monster.getId());
            data.writeByte(monster.getStatus().getSys());
            data.writeByte(monster.getInfo().getLevelBoss());
            data.writeLong(monster.getStats().getHp());
            monster.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendMonsterRevice: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void sendMonsterAttackMe(Monster monster, Player player, long HPShow, long MpShow) {
        int mobIb = monster.getId();
        try (Message message = new Message(-11)) {
            DataOutputStream data = message.writer();
            data.writeByte(monster.getId());
            data.writeLong(HPShow);
            if (MpShow > 0) {
                data.writeLong(MpShow);
            }
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("sendMonsterAttackMe: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void sendMonsterAttackPlayer(Monster monster, Player player, long MpShow) {
        try (Message message = new Message(-10)) {
            DataOutputStream data = message.writer();
            data.writeByte(monster.getId());
            data.writeInt(player.getId());
            data.writeLong(player.getPlayerPoints().getCurrentHP());
            if (MpShow > 0) {
                data.writeLong(player.getPlayerPoints().getCurrentMP());
            }
            monster.getArea().sendMessageToPlayersInArea(message, player);
        } catch (Exception ex) {
            LogServer.LogException("sendMonsterAttackPlayer: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
