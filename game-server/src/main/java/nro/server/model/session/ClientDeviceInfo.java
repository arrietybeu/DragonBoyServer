package nro.server.model.session;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientDeviceInfo {

    private byte typeClient;
    private byte zoomLevel;
    private int screenWidth;
    private int screenHeight;
    private boolean isQwerty;
    private boolean isTouch;
    private String platformInfo;
    private byte[] extraInfo;

}
