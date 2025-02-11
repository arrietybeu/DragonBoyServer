package nro.model.template.entity;

import lombok.Data;

@Data
public class SessionInfo {

    private int id;
    private String ip;
    private boolean connected;
    private final byte[] keys = {0};
    private boolean isUpdateItem;
    private boolean isClientOk;
    private boolean isLogin;

    public byte curR, curW;
    public int recvByteCount, sendByteCount;
    public String strRecvByteCount;

    public int constLogin;
    private long banUntil = 0;

    public boolean getConnect() {
        return this.connected;
    }

}
