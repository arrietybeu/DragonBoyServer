package nro.server.model.session;


import lombok.Getter;

public class SessionInfo {

    @Getter
    private final ClientDeviceInfo clientDeviceInfo;

    public SessionInfo(){
        clientDeviceInfo = new ClientDeviceInfo();
    }

}
