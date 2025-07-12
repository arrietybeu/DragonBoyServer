package nro.server.network.nro.server_packets.handler;

import com.artemis.Entity;
import nro.commons.consts.ConstsCmd;
import nro.server.consts.ConstMsgSubCommand;
import nro.server.engine.GameWorld;
import nro.server.model.ecs.component.*;
import nro.server.model.ecs.component.player.TaskComponent;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.SUB_COMMAND)
public class SmSubCommand extends NroServerPacket {

    private final byte subCommand;
    private String text;
    private int playerID = -1;

    public SmSubCommand(int subCommand, String text) {
        this.subCommand = (byte) subCommand;
        this.text = text;
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
                // send skill shortcut
            }
            case ConstMsgSubCommand.INIT_MY_CHARACTER -> sendInfoCharacter();
        }
    }

    private void sendInfoCharacter() {
        if (playerID == -1) {
            throw new RuntimeException("Player ID is not set for sub command: " + subCommand);
        }

        Entity playerEntity = GameWorld.getInstance().getWorld().getEntity(playerID);

        if (playerEntity == null) {
            throw new RuntimeException("Player entity not found for ID: " + playerID);
        }

        var taskComponent = playerEntity.getComponent(TaskComponent.class);
        var infoComponent = playerEntity.getComponent(InfoComponent.class);
        var appearanceComponent = playerEntity.getComponent(AppearanceComponent.class);
        var stateComponent = playerEntity.getComponent(StateComponent.class);
        var statsComponent = playerEntity.getComponent(StatsComponent.class);
        var buffComponent = playerEntity.getComponent(BuffComponent.class);

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

    }

}
