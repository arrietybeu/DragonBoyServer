package nro.server.services;

import lombok.NoArgsConstructor;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.server_packets.handler.SmChatTheGioi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class NotifyService {

    private static final Logger log = LoggerFactory.getLogger(NotifyService.class);

    public static void SendNotifyPlayer(NroConnection client, String reason) {
        try {
            if (client == null) {
                log.error("client is null");
                return;
            }
            if (reason == null || reason.isEmpty()) {
                log.error("reason is null or empty");
                return;
            }
            client.sendPacket(new SmChatTheGioi((byte) 0, reason));
        } catch (Throwable e) {
            log.error("Error send notify for client: {} reason: {}", client, reason, e);
        }
    }

}
