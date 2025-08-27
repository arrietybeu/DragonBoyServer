package nro.server.services.player;

import lombok.NoArgsConstructor;
import nro.server.network.nro.NroConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Arriety
 */

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);


    public static void openInventoryBox(NroConnection client) {
        try {
            // cmd -35
            // byte 1

        } catch (Exception e) {

        }
    }

}
