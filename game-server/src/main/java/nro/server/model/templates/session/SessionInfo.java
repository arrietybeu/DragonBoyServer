package nro.server.model.templates.session;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionInfo {

    private final ClientDeviceInfo clientDeviceInfo;

    private String version;
    private boolean isUpdateData;
    private boolean isUpdateMap;

    public SessionInfo() {
        clientDeviceInfo = new ClientDeviceInfo();
    }

}
