package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.model.session.SessionInfo;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;

import java.util.Set;

@AClientPacketHandler(command = ConstsCmd.NOT_LOGIN, validStates = {NroConnection.State.CONNECTED})
public class CMNotLogin extends NroClientPacket {

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
        byte command = readByte();
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

    @Override
    protected void runImpl() {
        final SessionInfo session = getConnection().getSessionInfo();
        session.getClientDeviceInfo().setTypeClient(typeClient);
        session.getClientDeviceInfo().setZoomLevel(zoomLevel);
        session.getClientDeviceInfo().setScreenWidth(screenWidth);
        session.getClientDeviceInfo().setScreenHeight(screenHeight);
        session.getClientDeviceInfo().setQwerty(isQwerty);
        session.getClientDeviceInfo().setTouch(isTouch);
        session.getClientDeviceInfo().setPlatformInfo(platformInfo);
        session.getClientDeviceInfo().setExtraInfo(extraInfo);

        // log info all
        System.out.println("Client Device Info:");
        System.out.println("Type Client: " + typeClient);
        System.out.println("Zoom Level: " + zoomLevel);
        System.out.println("Screen Width: " + screenWidth);
        System.out.println("Screen Height: " + screenHeight);
        System.out.println("Is Qwerty: " + isQwerty);
        System.out.println("Is Touch: " + isTouch);
        System.out.println("Platform Info: " + platformInfo);
        System.out.println("Extra Info: " + extraInfo.length);
        System.out.println("Client Device Info End");

    }
}
