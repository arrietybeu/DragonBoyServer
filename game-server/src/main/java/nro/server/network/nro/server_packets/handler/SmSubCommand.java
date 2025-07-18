package nro.server.network.nro.server_packets.handler;

import com.artemis.Entity;
import com.artemis.World;
import nro.commons.consts.ConstsCmd;
import nro.server.GameServer;
import nro.server.consts.ConstMsgSubCommand;
import nro.server.data_holders.data.ItemData;
import nro.server.engine.GameWorld;
import nro.server.model.ecs.component.*;
import nro.server.model.ecs.component.player.CurrencyComponent;
import nro.server.model.ecs.component.player.InventoryComponent;
import nro.server.model.ecs.component.player.TaskComponent;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;
import nro.server.utils.Utils;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.SUB_COMMAND)
public class SmSubCommand extends NroServerPacket {

    private final byte subCommand;
    private String text;
    private int playerID = -1;
    private final World world = GameWorld.getInstance().getWorld();

    public SmSubCommand(int subCommand, String text, int playerID) {
        this.subCommand = (byte) subCommand;
        this.text = text;
        this.playerID = playerID;
    }

    public SmSubCommand(int subCommand) {
        this.subCommand = (byte) subCommand;
    }

    public SmSubCommand(int subCommand, int playerID) {
        this.subCommand = (byte) subCommand;
        this.playerID = playerID;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        writeByte(subCommand);
        switch (subCommand) {
            case ConstMsgSubCommand.UPDATE_SKILL_SHORTCUT -> {
                writeUTF(text);
                var playerEntity = getEntity();
                var skillComponent = playerEntity.getComponent(SkillComponent.class);

                if (skillComponent == null) {
                    throw new RuntimeException("SkillComponent not found for player entity ID: " + playerID);
                }

                writeInt(skillComponent.skillShortCut.length);
                writeBytes(skillComponent.skillShortCut);
            }
            case ConstMsgSubCommand.INIT_MY_CHARACTER -> sendInfoCharacter();

            case ConstMsgSubCommand.UPDATE_MY_CURRENCY_HPMP -> {
                var playerEntity = getEntity();
                var currency = playerEntity.getComponent(CurrencyComponent.class);
                if (currency == null)
                    throw new RuntimeException("Currency not found for player entity ID: " + playerID);
                var health = playerEntity.getComponent(HealthComponent.class);
                if (health == null)
                    throw new RuntimeException("Health not found for player entity ID: " + playerID);
                writeLong(currency.gold);
                writeInt(currency.gem);
                writeLong(health.currentHP);
                writeLong(health.currentMP);
                writeInt(currency.ruby);
            }
        }
    }

    private void sendInfoCharacter() {
        if (playerID == -1) {
            throw new RuntimeException("Player ID is not set for sub command: " + subCommand);
        }

        var playerEntity = getEntity();

        var taskComponent = playerEntity.getComponent(TaskComponent.class);
        var infoComponent = playerEntity.getComponent(InfoComponent.class);
        var appearanceComponent = playerEntity.getComponent(AppearanceComponent.class);
        var stateComponent = playerEntity.getComponent(StateComponent.class);
        var statsComponent = playerEntity.getComponent(StatsComponent.class);
        var buffComponent = playerEntity.getComponent(BuffComponent.class);
        var skillComponent = playerEntity.getComponent(SkillComponent.class);
        var currencyComponent = playerEntity.getComponent(CurrencyComponent.class);
        var inventoryComponent = playerEntity.getComponent(InventoryComponent.class);
        var fusionComponent = playerEntity.getComponent(FusionComponent.class);

        writeInt(playerEntity.getId());
        writeByte(taskComponent.taskId);
        writeByte(infoComponent.gender);
        writeShort(appearanceComponent.head);
        writeUTF(infoComponent.name);
        writeByte(0); // write type pk
        writeByte(stateComponent.typePk);
        writeLong(statsComponent.power);
        writeShort(buffComponent.eff5BuffHp);
        writeShort(buffComponent.eff5BuffMp);
        writeByte(infoComponent.gender);

        // ============ Send Skills ============

//        writeBytes(skillComponent.skills.size());
        writeByte(0);

        // ============ Send Curren ============

        writeLong(currencyComponent.gold);
        writeInt(currencyComponent.ruby);
        writeInt(currencyComponent.gem);

        // ============ Send Equipment To Body ============
        sendInventoryForPlayer();
        // ============ Send Equipment To Bag ============
        sendInventoryForPlayer();

        // ============ Send Equipment To Box ============
        sendInventoryForPlayer();

        // ============ Send Data Item Head ============
        writeBytes(ItemData.getInstance().getDataItemHead());

        sendPlayerBirdFrames(infoComponent.gender);
        writeByte(fusionComponent.fusionType != 0 ? 1 : 0);
        writeInt(GameServer.START_TIME_SECONDS);
        writeShort(appearanceComponent.aura);
        writeShort(appearanceComponent.effSetItem);
        writeShort(appearanceComponent.idHat);
    }

    private void sendInventoryForPlayer() {
        writeByte(0);
    }

    private void sendPlayerBirdFrames(int gender) {
        short[] frames = Utils.getPlayerBirdFrames(gender);
        this.writeShort(frames[0]); // frame1
        this.writeShort(frames[1]); // frame2
        this.writeShort(frames[2]); // avatar
    }

    private Entity getEntity() {
        var playerEntity = world.getEntity(playerID);
        if (playerEntity == null) {
            throw new RuntimeException("Player entity not found for ID: " + playerID);
        }
        return playerEntity;
    }

}
