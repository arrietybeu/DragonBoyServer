package nro.server.service.core.social;

public class FriendService {

    private static final class SingletonHolder {
        private static final FriendService instance = new FriendService();
    }

    public static FriendService getInstance() {
        return FriendService.SingletonHolder.instance;
    }

}
