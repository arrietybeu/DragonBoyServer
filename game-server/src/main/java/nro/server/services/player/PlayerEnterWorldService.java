package nro.server.services.player;

import nro.commons.consts.ConstsCmd;
import nro.commons.database.DatabaseFactory;
import nro.server.dao.PlayerDAO;
import nro.server.model.ecs.component.player.SessionComponent;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.server_packets.PacketHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Arriety
 */
public class PlayerEnterWorldService {

    private static final Logger log = LoggerFactory.getLogger(PlayerEnterWorldService.class);

    private static final ConcurrentLinkedQueue<Integer> enteringWorld = new ConcurrentLinkedQueue<>();

    public static void enterWorld(final NroConnection client) {
        if (client == null)
            throw new NullPointerException("Client EnterWorldService cannot be null");

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

            if (!enteringWorld.contains(playerEntity.getId()) && enteringWorld.add(playerEntity.getId())) {
                try {
                    playerEntity.edit().add(new SessionComponent(client));

                } catch (Throwable e) {
                    log.error("Error during enter world of {}", playerEntity.getId(), e);
                } finally {
                    enteringWorld.remove(playerEntity.getId());
                }
            }
        }
    }


}
