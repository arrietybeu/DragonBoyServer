package nro.server.services;

import lombok.NoArgsConstructor;
import nro.commons.utils.Rnd;
import nro.server.data_holders.repo.ItemData;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.StatsComponent;
import nro.server.model.ecs.component.player.InventoryComponent;
import nro.server.model.map.MapChangeType;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.server_packets.handler.SmChatMap;
import nro.server.network.nro.server_packets.handler.SmDialogMessage;
import nro.server.services.player.PlayerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class ChatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

    public static void sendChatMessage(NroConnection client, PositionComponent po, String message)
            throws RuntimeException {
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

            if (account.isAdmin() && chatVIP(client, message))
                return;

            AreaService.getInstance().sendPacketForALLPlayerInArea(po.mapId, po.getAreaId(),
                    new SmChatMap(client.getPlayerID(), message));

        } catch (RuntimeException e) {
            LOGGER.error("Error sending chat message: {}", e.getMessage(), e);
            throw e;
        }
    }

    private static boolean chatVIP(NroConnection client, String message) {
        if (message.startsWith("m ")) {
            short mapId = getNumberShort(message);
            short x = (short) Rnd.nextInt(400, 444);
            ChangeMapService.requestChangeMap(client.getEntity(), MapChangeType.SHIP, mapId, -1, x, (short) -1);
            NotifyService.SendNotifyPlayer(client, NotifyType.FLYING_CAT, "Đã dịch chuyển đến map " + mapId);
            return true;
        }
        if (message.startsWith("speed ")) {
            byte speed = (byte) getNumber(message);
            client.getEntity().getComponent(StatsComponent.class).movementSpeed = speed;
            PlayerService.sendPlayerSpeed(client, speed);
            NotifyService.SendNotifyPlayer(client, NotifyType.UI_FORM, "Đã thay đổi tốc độ di chuyển thành " + speed);
            return true;
        }
        return switch (message) {
            case "toado" -> {
                PositionComponent pos = client.getEntity().getComponent(PositionComponent.class);
                String info = "Tọa độ map: " + pos.mapId + "\nx: " + pos.x + " y: " + pos.y;
                NotifyService.SendNotifyPlayer(client, NotifyType.UI_FORM, info);
                yield true;
            }
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

                yield true;
            }
            default -> false;
        };
    }

    private static long getNumberLong(String text) {
        try {
            return Long.parseLong(text.substring(2).trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static byte getNumberByte(String text) {
        try {
            return Byte.parseByte(text.substring(2).trim());
        } catch (NumberFormatException e) {
            return (byte) -1;
        }
    }

    private static short getNumberShort(String text) {
        try {
            return Short.parseShort(text.substring(2).trim());
        } catch (NumberFormatException e) {
            return (short) -1;
        }
    }

    public static int getNumber(String text) {
        String[] parts = text.split(" ");
        if (parts.length < 2) {
            return  -1;
        }
        return Integer.parseInt(parts[1].trim());
    }

}
