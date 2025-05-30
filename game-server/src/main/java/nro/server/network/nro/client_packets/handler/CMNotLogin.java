package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.model.template.session.SessionInfo;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.network.nro.server_packets.handler.SMGetImageSources2;

import java.util.Set;

@AClientPacketHandler(command = ConstsCmd.NOT_LOGIN, validStates = {NroConnection.State.CONNECTED})
public class CMNotLogin extends NroClientPacket {

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
                var username = readUTF().toLowerCase();
                var password = readUTF().toLowerCase();
                var version = readUTF();
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
                int size = readShort();
                extraInfo = new byte[size];
                for (int i = 0; i < size; i++) {
                    extraInfo[i] = readByte();
                }
            }
        }
    }

    @Override
    protected void runImpl() {
        switch (command) {
            case 1 -> {
            }
            case 2 -> {
                final SessionInfo session = getConnection().getSessionInfo();
                session.getClientDeviceInfo().setTypeClient(typeClient);
                session.getClientDeviceInfo().setZoomLevel(zoomLevel);
                session.getClientDeviceInfo().setScreenWidth(screenWidth);
                session.getClientDeviceInfo().setScreenHeight(screenHeight);
                session.getClientDeviceInfo().setQwerty(isQwerty);
                session.getClientDeviceInfo().setTouch(isTouch);
                session.getClientDeviceInfo().setPlatformInfo(platformInfo);
                session.getClientDeviceInfo().setExtraInfo(extraInfo);
                sendPacket(new SMGetImageSources2());
            }
        }
    }
}
