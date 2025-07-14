package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.controllers.AccountController;
import nro.server.network.nro.NroAuthResponse;
import nro.server.model.session.SessionInfo;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.network.nro.server_packets.handler.*;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.NOT_LOGIN, validStates = {
        NroConnection.State.CONNECTED,
        NroConnection.State.AUTHED,
        NroConnection.State.IN_GAME})
public class CMNotLogin extends NroClientPacket {

    private String username;
    private String password;
    private String version;

    private byte command;
    private byte typeClient;
    private byte zoomLevel;
    private int screenWidth;
    private int screenHeight;
    private boolean isQwerty;
    private boolean isTouch;
    private String platformInfo;
    private byte[] extraInfo;

    public CMNotLogin(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        this.command = readByte();
        switch (command) {
            case 0 -> {
                this.username = readUTF().toLowerCase();
                this.password = readUTF().toLowerCase();
                this.version = readUTF();
                var type = readByte();
            }
            case 1 -> {
            }
            case 2 -> {
                typeClient = readByte();
                zoomLevel = readByte();
                readBoolean();
                screenWidth = readInt();
                screenHeight = readInt();
                isQwerty = readBoolean();
                isTouch = readBoolean();
                platformInfo = readUTF();
                if (getRemainingBytes() > 0) {
                    int size = readShort();
                    extraInfo = new byte[size];
                    for (int i = 0; i < size; i++) {
                        extraInfo[i] = readByte();
                    }
                }
            }
        }
    }

    @Override
    protected void runImpl() {
        switch (command) {
            case 0 -> {
                NroConnection connection = getConnection();
                if (connection == null) {
                    throw new RuntimeException("Connection is null in CMNotLogin command 1");
                }
                var zoom = connection.getSessionInfo().getClientDeviceInfo().getZoomLevel();
                if (zoom <= 0 || zoom > 4) {
                    connection.close(new SmLoginFail(SmLoginFail.RELOGIN_ALLOWED));
                    return;
                }
                connection.getSessionInfo().setVersion(version);
                NroAuthResponse response = AccountController.Login(username, password, connection);
                if (response == null) return;

                if (response == NroAuthResponse.SUCCESS) {
                    sendPacket(new SmSmallImageVersion());
                    sendPacket(new SmBackgroundItemVersion());
//                        if (!connection.getSessionInfo().isUpdateData())
                    sendPacket(new SmNotMap(SmNotMap.ALL_DATA_GAME));
                } else {
                    connection.sendPacket(new SmDialogMessage(NroAuthResponse.ACCOUNT_NOT_FOUND.getCode()));
                }
            }
            case 2 -> {
                final SessionInfo session = getConnection().getSessionInfo();
                var deviceInfo = session.getClientDeviceInfo();
                deviceInfo.setTypeClient(typeClient);
                deviceInfo.setZoomLevel(zoomLevel);
                deviceInfo.setScreenWidth(screenWidth);
                deviceInfo.setScreenHeight(screenHeight);
                deviceInfo.setQwerty(isQwerty);
                deviceInfo.setTouch(isTouch);
                deviceInfo.setPlatformInfo(platformInfo);
                deviceInfo.setExtraInfo(extraInfo);
                sendPacket(new SMGetImageSources2());
                sendPacket(new SMNotLogin());
            }
        }
    }
}
