package nro.service.model.model.template.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SessionInfo {

    private int id;
    private String ip;
    private final byte[] keys = {0};

    private volatile boolean connected;
    private boolean isUpdateItem;
    private boolean isClientOk;
    private boolean isLogin;

    private boolean isLoadData;
    private boolean isSaveData;

    public byte curR, curW;
    public int recvByteCount, sendByteCount;
    public String strRecvByteCount;

    public int constLogin;
    private long banUntil = 0;

    public boolean getConnect() {
        return this.connected;
    }

}
