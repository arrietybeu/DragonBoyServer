package nro.server.services;

/**
 * @author Arriety
 */
public class NpcService {

    private static final class SingletonHolder {
        private static final NpcService instance = new NpcService();
    }

    public static NpcService getInstance() {
        return NpcService.SingletonHolder.instance;
    }

}
