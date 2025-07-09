package nro.server.services.player;

import com.artemis.Entity;
import nro.commons.consts.ConstsCmd;
import nro.server.dao.PlayerDAO;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.server_packets.PacketHelper;
import nro.server.network.nro.server_packets.handler.SmDialogMessage;
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

        if (client == null) {
            log.warn("Attempted to enter world with invalid client connection.");
            return;
        }
        int accountId = client.getAccount().getId();

        int playerId = PlayerDAO.findPlayerIdByAccountId(accountId);

        if (playerId == -1) {
            client.sendPacket(PacketHelper.empty(ConstsCmd.CLIENT_INFO));
        } else {
            var playerEntity = PlayerDAO.loadPlayerEntity(playerId, accountId);

            if (playerEntity == null) {
                log.error("Failed to load player entity for player ID: {}. Cannot enter world.", playerId);
                client.close(PacketHelper.empty(ConstsCmd.CLIENT_INFO));
                return;
            }

            client.attachPlayerEntity(playerEntity.getId());
        }
    }

}
