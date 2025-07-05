package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.LOGINFAIL)
public class SmLoginFail extends NroServerPacket {

    public static final byte LOGIN_FAIL = 0;
    public static final byte RELOGIN_ALLOWED = 1;
    private final byte type;

    /**
     * type = 0 - login fail bình thường 1 là do sai mật khẩu, tài khoản không tồn tại, vv.v.v.v.
     * <p>
     * type = 1 - Được phép tự động login lại (có thể do timeout, session hết hạn, mất kết nối tạm thời)
     * @param type
     */
    public SmLoginFail(int type) {
        this.type = (byte) type;
    }

    @Override
    protected void writeImpl(NroConnection con) {
        this.writeByte(type);
    }
}
