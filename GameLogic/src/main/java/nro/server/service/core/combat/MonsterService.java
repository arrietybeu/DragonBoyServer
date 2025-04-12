package nro.server.service.core.combat;

import nro.server.service.model.item.ItemMap;
import nro.server.service.model.entity.monster.Monster;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.server.network.Message;

import java.io.DataOutputStream;
import java.util.List;

public class MonsterService {

    private static final class SingletonHolder {
        private static final MonsterService instance = new MonsterService();
    }

    public static MonsterService getInstance() {
        return MonsterService.SingletonHolder.instance;
    }

    public void sendMonsterDie(Monster monster, long dame, boolean crit, List<ItemMap> itemMaps) {
        try (Message message = new Message(-12)) {
            DataOutputStream data = message.writer();
            data.writeByte(monster.getId());
            data.writeLong(dame);
            data.writeBoolean(crit);
            data.writeByte(itemMaps.size());
            for (ItemMap itemMap : itemMaps) {
                data.writeShort(itemMap.getId());
                data.writeShort(itemMap.getItem().getTemplate().id());
                data.writeShort(itemMap.getX());// vat pham dat o vi tri nao
                data.writeShort(itemMap.getY());// vx = xEnd - x >> 2; thì vật phẩm rơi xuống
                data.writeInt(itemMap.getPlayerId());
            }
            monster.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendMonsterDie: " + ex.getMessage(), ex);
        }
    }

    public void sendMonsterRevice(Monster monster) {
        try (Message message = new Message(-13)) {
            DataOutputStream data = message.writer();
            data.writeByte(monster.getId());
            data.writeByte(monster.getStatus().getSys());
            data.writeByte(monster.getInfo().getLevelBoss());
            data.writeLong(monster.getPoint().getHp());
            monster.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendMonsterRevice: " + ex.getMessage(), ex);
        }
    }

    public void sendMonsterAttackMe(Monster monster, Player player, long HPShow, long MpShow) {
        int mobIb = monster.getId();
        try (Message message = new Message(-11)) {
            DataOutputStream data = message.writer();
            data.writeByte(mobIb);
            data.writeLong(HPShow);
            if (MpShow > 0) {
                data.writeLong(MpShow);
            }
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("sendMonsterAttackMe: " + ex.getMessage(), ex);
        }
    }

    public void sendMonsterAttackPlayer(Monster monster, Player player, long MpShow) {
        try (Message message = new Message(-10)) {
            DataOutputStream data = message.writer();
            data.writeByte(monster.getId());
            data.writeInt(player.getId());
            data.writeLong(player.getPoints().getCurrentHP());
            if (MpShow > 0) {
                data.writeLong(player.getPoints().getCurrentMP());
            }
            monster.getArea().sendMessageToPlayersInArea(message, player);
        } catch (Exception ex) {
            LogServer.LogException("sendMonsterAttackPlayer: " + ex.getMessage(), ex);
        }
    }

    public void sendHpMonster(Player player, Monster monster, long dame, boolean isCrit, boolean isHut) {
        try (Message message = new Message(-9)) {
            DataOutputStream writer = message.writer();
            writer.writeByte(monster.getId());
            writer.writeLong(monster.getPoint().getHp());
            writer.writeLong(dame);
            writer.writeBoolean(isCrit);
            writer.writeByte(isHut ? 37 : -1);
            player.getArea().sendMessageToPlayersInArea(message, null);
        } catch (Exception ex) {
            LogServer.LogException("sendHpMonster: " + ex.getMessage(), ex);
        }
    }

}
