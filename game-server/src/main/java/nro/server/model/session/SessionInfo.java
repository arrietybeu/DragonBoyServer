package nro.server.model.session;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionInfo {

    private final ClientDeviceInfo clientDeviceInfo;

    private String version;
    private boolean isUpdateData;
    private boolean isUpdateMap;
    private boolean isUpdateSkill;
    private boolean isUpdateItem;

    private boolean isLogin;
    private boolean isEnterWorld;
    private boolean isSendVersion;

    private boolean isClientOk;
    public SessionInfo() {
        clientDeviceInfo = new ClientDeviceInfo();
    }

}
