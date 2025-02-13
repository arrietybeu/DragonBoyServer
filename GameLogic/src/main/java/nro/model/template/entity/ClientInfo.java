package nro.model.template.entity;

import lombok.Getter;
import lombok.Setter;
import nro.utils.Util;

/**
 * @author Arriety
 */
@Getter
@Setter
public class ClientInfo {

    private String version;
    private String platform;
    private boolean sendKeyComplete;
    private boolean isSetClientType;
    private long lastActiveTime;
    private int typeClient;
    private int zoomLevel;

    public void updateLastActiveTime() {
        Util.getMethodCaller();
        this.lastActiveTime = System.currentTimeMillis();
    }
}