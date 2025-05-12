package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.data_holders.data.VersionImageData;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;
import nro.server.utils.FileNio;

import java.io.File;

@ServerPacketCommand(ConstsCmd.GET_IMAGE_SOURCE)
public class SMGetImageSource extends NroServerPacket {

    private final byte type;
    private short nBig;
    private File file;

    public SMGetImageSource(int type) {
        super();
        this.type = (byte) type;
    }

    public SMGetImageSource(int type, short nBig) {
        this(type);
        this.nBig = nBig;
    }

    public SMGetImageSource(int type, File file) {
        this(type);
        this.file = file;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        int zoomLevel = con.getSessionInfo().getClientDeviceInfo().getZoomLevel();
        this.writeByte(type);
        switch (type) {
            case 0, 3 -> {
                int size = VersionImageData.getInstance().getVersionImage(zoomLevel);
                this.writeInt(size);
            }
            case 1 -> this.writeShort(nBig);
            case 2 -> fileTransfer(file);
        }
    }

    private void fileTransfer(File file) throws RuntimeException {
        String strPath = FileNio.cutPng(file.getPath().replace("\\", "/"));
        this.writeUTF(strPath);
        byte[] ab = FileNio.loadDataFile(file.getPath());
        if (ab == null) {
            writeInt(0);
            throw new RuntimeException(" File not found: " + file.getPath());
        }
        writeInt(ab.length);
        writeBytes(ab, strPath);
    }

}
