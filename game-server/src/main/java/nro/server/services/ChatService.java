package nro.server.services;

import lombok.NoArgsConstructor;
import nro.commons.utils.Rnd;
import nro.server.data_holders.data.ItemData;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.player.InventoryComponent;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.server_packets.handler.SmChatMap;
import nro.server.network.nro.server_packets.handler.SmDialogMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class ChatService {
//
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

    public static void sendChatMessage(NroConnection client, PositionComponent po, String message) throws RuntimeException {
        try {
            var account = client.getAccount();

            if (account == null) {
                LOGGER.warn("Account is null for client: {}", client);
                return;
            }
            if (message == null || message.isEmpty()) {
                LOGGER.warn("Attempted to send an empty chat message.");
                return;
            }

            if (account.isAdmin()) {
                chatVIP(client, message);
                return;
            }

            System.out.println("ChatService.sendChatMessage: " + message);
            // FIXME viet check message ki doan nafy

            AreaService.getInstance().sendPacketForALLPlayerInArea(po.mapId, po.getAreaId(), new SmChatMap(message));

        } catch (RuntimeException e) {
            LOGGER.error("Error sending chat message: {}", e.getMessage(), e);
            throw e;
        }
    }

    private static void chatVIP(NroConnection client, String message) {

        switch (message) {
            case "inventory" -> {

                ItemData itemData = ItemData.getInstance();

                InventoryComponent inventory = client.getEntity().getComponent(InventoryComponent.class);

                StringBuilder sb = new StringBuilder();
                sb.append(" size body: ").append(inventory.itemsBody.size()).append("\n");

                for (var item : inventory.itemsBody) {
                    var tem = itemData.getItemTemplates().get(item.shortValue());
                    sb.append(" item id: ").append(item).append("\n");
                    sb.append(" item name: ").append(tem != null ? tem.name() : "null").append("\n");
                }

                client.sendPacket(new SmDialogMessage(sb.toString()));

            }
        }
    }

    private static long getNumber(String text) {
        try {
            return Long.parseLong(text.substring(2).trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }


}
