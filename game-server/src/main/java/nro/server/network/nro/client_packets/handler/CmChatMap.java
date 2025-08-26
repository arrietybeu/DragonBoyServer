package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.network.nro.server_packets.handler.SmChatTheGioi;
import nro.server.services.ChatService;
import nro.server.utils.PacketSendUtility;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.CHAT_MAP, validStates = {NroConnection.State.IN_GAME})
public class CmChatMap extends NroClientPacket {

    private String message;

    public CmChatMap(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        this.message = this.readUTF();
    }

    @Override
    protected void runImpl() {
        if (this.message.isEmpty()) return;

        var entity = this.getConnection().getEntity();
        var playerComponents =entity.getComponent(PlayerComponent.class);
        var po = entity.getComponent(PositionComponent.class);
        if (po == null) {
            throw new NullPointerException("PositionComponent is null for entity ID: " + this.getConnection().getEntity().getId());
        }
        if (playerComponents == null) {
            throw new NullPointerException("PlayerComponent is null for entity ID: " + this.getConnection().getEntity().getId());
        }
        ChatService.sendChatMessage(po, this.message);
    }
}
