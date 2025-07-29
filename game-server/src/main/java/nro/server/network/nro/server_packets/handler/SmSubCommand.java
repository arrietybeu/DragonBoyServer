package nro.server.network.nro.server_packets.handler;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import nro.commons.consts.ConstsCmd;
import nro.server.GameServer;
import nro.server.consts.ConstMsgSubCommand;
import nro.server.data_holders.data.ItemData;
import nro.server.engine.entity.GameWorld;
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
    private final World world = GameWorld.getInstance().getWorld();

    public SmSubCommand(int subCommand, String text) {
        this.subCommand = (byte) subCommand;
        this.text = text;
    }

    public SmSubCommand(int subCommand) {
        this.subCommand = (byte) subCommand;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        writeByte(subCommand);
        switch (subCommand) {
            case ConstMsgSubCommand.UPDATE_SKILL_SHORTCUT -> {
                writeUTF(text);

                var skillComponent = con.getEntity().getComponent(SkillComponent.class);

                if (skillComponent == null) {
                    throw new RuntimeException("SkillComponent not found for player ID: " + con.getPlayerID());
                }

                writeInt(skillComponent.skillShortCut.length);
                writeBytes(skillComponent.skillShortCut);
            }
            case ConstMsgSubCommand.INIT_MY_CHARACTER -> sendInfoCharacter(con);

            case ConstMsgSubCommand.LOAD_CHAR_IN_MAP -> sendLoadMapCharInMap(con);
            case ConstMsgSubCommand.UPDATE_MY_CURRENCY_HPMP -> {
                var currency = con.getEntity().getComponent(CurrencyComponent.class);
                if (currency == null)
                    throw new RuntimeException("Currency not found for player entity ID: " + con.getPlayerID());
                var health = con.getEntity().getComponent(HealthComponent.class);
                if (health == null)
                    throw new RuntimeException("Health not found for player entity ID: " + con.getPlayerID());
                writeLong(currency.gold);
                writeInt(currency.gem);
                writeLong(health.currentHP);
                writeLong(health.currentMP);
                writeInt(currency.ruby);
            }
        }
    }

    private <T extends Component> T getRequiredComponent(Entity playerEntity, Class<T> clazz) {
        T component = playerEntity.getComponent(clazz);
        if (component == null) {
            throw new RuntimeException("Missing required component: [" + clazz.getSimpleName() + "] for player ID: " + playerEntity.getId());
        }
        return component;
    }

    private void sendInfoCharacter(NroConnection con) {
        var playerEntity = con.getEntity();

        var taskComponent = getRequiredComponent(playerEntity, TaskComponent.class);
        var infoComponent = getRequiredComponent(playerEntity, InfoComponent.class);
        var appearanceComponent = getRequiredComponent(playerEntity, AppearanceComponent.class);
        var stateComponent = getRequiredComponent(playerEntity, StateComponent.class);
        var statsComponent = getRequiredComponent(playerEntity, StatsComponent.class);
        var buffComponent = getRequiredComponent(playerEntity, BuffComponent.class);
        var skillComponent = getRequiredComponent(playerEntity, SkillComponent.class);
        var currencyComponent = getRequiredComponent(playerEntity, CurrencyComponent.class);
        var inventoryComponent = getRequiredComponent(playerEntity, InventoryComponent.class);
        var fusionComponent = getRequiredComponent(playerEntity, FusionComponent.class);

        writeInt(con.getPlayerID());
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

    private void sendLoadMapCharInMap(NroConnection connection) {
        var entity = connection.getEntity();
        var appearance = entity.getComponent(AppearanceComponent.class);
        writeInt(connection.getPlayerID());
        writeInt(-1);
        writePlayerInfo(entity, appearance);
        writeShort(appearance.aura);
        writeByte(appearance.effSetItem);
        writeShort(appearance.idHat);
    }

    private void writePlayerInfo(Entity entity, AppearanceComponent appearance) {

        var state = entity.getComponent(StateComponent.class);
        var info = entity.getComponent(InfoComponent.class);

        var heal = entity.getComponent(HealthComponent.class);
        var position = entity.getComponent(PositionComponent.class);
        var buff = entity.getComponent(BuffComponent.class);

        writeByte(1);// level
        writeBoolean(false); // write isInvisiblez
        writeByte(state.typePk);
        writeByte(info.gender);
        writeByte(info.gender);
        writeShort(appearance.head);
        writeUTF(info.name);
        writeLong(heal.currentHP);
        writeLong(heal.currentMP);
        writeShort(appearance.body);
        writeShort(appearance.leg);
        writeShort(appearance.flagBag);
        writeByte(0);
        writeShort(position.x);
        writeShort(position.y);
        writeShort(buff.eff5BuffHp);
        writeShort(buff.eff5BuffMp);
        writeByte(0);
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


}
