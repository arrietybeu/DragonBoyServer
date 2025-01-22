package nro.service;

import nro.network.Session;

public class MapService {

    public static final class InstanceHolder {
        public static final MapService instance = new MapService();
    }

    public static MapService getInstance() {
        return InstanceHolder.instance;
    }

    public void sendMapInfo(Session session) {
    }

}
