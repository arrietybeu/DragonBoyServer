package nro.server.services.player;

import nro.server.model.account.Account;
import nro.server.model.entity.player.Player;
import nro.server.network.nro.NroConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Arriety
 */
public class PlayerEnterWorldService {

    private static final Logger log = LoggerFactory.getLogger(PlayerEnterWorldService.class);

    private static final ConcurrentLinkedQueue<Integer> enteringWorld = new ConcurrentLinkedQueue<>();

    public static void enterWorld(final NroConnection client) {
//        Account account = client.getAccount();
//        if (account == null)
//            throw new RuntimeException("Account is null for client: " + client);
//
//        int playerId = account.getPlayerId();
//
//        Player player = PlayerService.getPlayer(account);
//
//        var playerId = player.getId();
//
//        if (!enteringWorld.contains(playerId) && enteringWorld.add(playerId)) {
//            try {
//
//            } catch (Throwable ex) {
//                log.error("Error during enter world of {}", player, ex);
//                enteringWorld.remove(playerId);
//            }
//        }

    }

    private static void enterWorld(final NroConnection client, final Player player) {

    }

}
