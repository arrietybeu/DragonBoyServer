package nro.server.services;

import lombok.NoArgsConstructor;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.server_packets.handler.SmChatTheGioi;
import nro.server.network.nro.server_packets.handler.SmDialogMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class NotifyService {

    private static final Logger log = LoggerFactory.getLogger(NotifyService.class);

    public static void SendNotifyPlayer(NroConnection client, NotifyType type, String reason) {
        try {
            if (client == null) {
                log.error("client is null");
                return;
            }
            if (reason == null || reason.isEmpty()) {
                log.error("reason is null or empty");
                return;
            }
            switch (type) {
                case NotifyType.UI_FORM -> client.sendPacket(new SmDialogMessage(reason));
                case NotifyType.FLYING_CAT -> client.sendPacket(new SmChatTheGioi((byte) 0, reason));
                default -> throw new IllegalArgumentException("Unexpected value: " + type);
            }
        } catch (Throwable e) {
            log.error("Error send notify for client: {} reason: {}", client, reason, e);
        }
    }

}
