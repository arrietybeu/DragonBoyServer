package nro.server.services.player;

import lombok.NoArgsConstructor;
import nro.server.engine.InventoryState;
import nro.server.model.ecs.component.player.InventoryComponent;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.server_packets.handler.SmBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    public static void openInventoryBox(NroConnection client) {
        try {
            InventoryComponent inventoryComponent = client.getEntity().getComponent(InventoryComponent.class);
            if (inventoryComponent.state != InventoryState.IDLE) {
                logger.warn("Inventory box is dirty for client {}. Items may not be up to date.", client);
            }
            client.sendPacket(new SmBox(inventoryComponent.itemsBox, 0));
            client.sendPacket(new SmBox(inventoryComponent.itemsBox, 1));
        } catch (Exception e) {
            logger.error("Error while opening inventory box for client {}: {}", client, e.getMessage(), e);
        }
    }

}
