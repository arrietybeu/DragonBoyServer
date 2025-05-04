package nro.server.model.session;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

    @Override
    public String toString() {
        return "ClientDeviceInfo{" +
                "typeClient=" + typeClient +
                ", zoomLevel=" + zoomLevel +
                ", screenWidth=" + screenWidth +
                ", screenHeight=" + screenHeight +
                ", isQwerty=" + isQwerty +
                ", isTouch=" + isTouch +
                ", platformInfo='" + platformInfo + '\'' +
                '}';
    }
}
