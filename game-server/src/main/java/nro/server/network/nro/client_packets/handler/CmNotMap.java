package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.consts.ConstMsgNotMap;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.services.PlayerResponseType;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.network.nro.server_packets.handler.*;
import nro.server.services.player.PlayerEnterWorldService;
import nro.server.services.player.PlayerService;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.NOT_MAP, validStates = {
        NroConnection.State.CONNECTED,
        NroConnection.State.AUTHED,
        NroConnection.State.IN_GAME})
public class CmNotMap extends NroClientPacket {

    private byte status;

    private String name;
    private byte gender;
    private byte hair;

    public CmNotMap(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        this.status = readByte();
        switch (status) {
            case ConstMsgNotMap.REQUEST_MAP_TEMPLATE -> {
                if (isValideInGame()) {
                    var mapTemplateID = readByte();
                    // ngày xưa client write cái id dữ liệu byte lên nhưng giờ map nó lên short rồi nên là bỏ thui :3
                    sendPacket(new SmNotMap(status));
                } else {
                    getConnection().close(new SmDialogMessage("Cut ra khoi game anh di em"));
                }
            }
            case SmNotMap.CREATE_CHARACTER -> {
                this.name = this.readUTF().toLowerCase();
                this.gender = this.readByte();
                this.hair = this.readByte();
            }
            case SmNotMap.UPDATE_MAP -> {
                if (!getConnection().getSessionInfo().isUpdateMap()) {
                    sendPacket(new SmNotMap(status));
                    getConnection().getSessionInfo().setUpdateMap(true);
                }
            }
            case SmNotMap.UPDATE_SKILL -> {
                if (!getConnection().getSessionInfo().isUpdateSkill()) {
                    sendPacket(new SmNotMap(status));
                    getConnection().getSessionInfo().setUpdateSkill(true);
                }
            }
            case SmNotMap.UPDATE_ITEM -> {
                if (!getConnection().getSessionInfo().isUpdateItem()) {
                    sendPacket(new SmNotMap(status, SmNotMap.ITEM_OPTION));
                    sendPacket(new SmNotMap(status, SmNotMap.ITEM_ARR_HEAD_2FR));
                    sendPacket(new SmCmdExtraBig());
                    getConnection().getSessionInfo().setUpdateItem(true);
                }
            }
            case 13 -> {
                if (getConnection().getSessionInfo().isClientOk())
                    return;

                sendPacket(new SmItemBackground());
                sendPacket(new SmTileSet());
                sendPacket(new SmSmallImageVersion());
                sendPacket(new SmBackgroundItemVersion());
                getConnection().setState(NroConnection.State.AUTHED);
                getConnection().getSessionInfo().setClientOk(true);
            }
        }
    }

    /**
     * Có những con thuyền không bao giờ cập bến, có những cuộc tình không bao giờ thành đôi.
     */
    @Override
    protected void runImpl() {
        switch (status) {
            case SmNotMap.CREATE_CHARACTER -> {
                var playerResponseType = PlayerService.storeNewPlayer(name, gender, hair, getConnection().getAccount());
                switch (playerResponseType) {
                    case PlayerResponseType.SUCCESS -> PlayerEnterWorldService.enterWorld(getConnection());
                    default -> sendPacket(new SmDialogMessage(playerResponseType.getDefaultMessage()));
                }
            }
        }
    }

}
