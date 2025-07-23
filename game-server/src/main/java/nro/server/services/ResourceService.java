package nro.server.services;


/**
 * @author Arriety
 */
public class ResourceService {

    private static ResourceService instance;

    private static class SingletonHolder {
        private static final ResourceService INSTANCE = new ResourceService();
    }

    public static ResourceService getInstance() {
        return ResourceService.SingletonHolder.INSTANCE;
    }
}
