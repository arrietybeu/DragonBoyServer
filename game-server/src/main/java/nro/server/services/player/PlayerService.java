package nro.server.services.player;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nro.commons.database.DatabaseFactory;
import nro.server.dao.PlayerDAO;
import nro.server.model.account.Account;
import nro.server.network.nro.PlayerResponseType;
import nro.server.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

/**
 * @author Arriety
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlayerService {

    private static final Logger log = LoggerFactory.getLogger(PlayerService.class);

    public static PlayerResponseType storeNewPlayer(String name, byte gender, byte hair, Account account) {

        if (name == null || !Utils.NAME_PATTERN.matcher(name).matches()) {
            return PlayerResponseType.CREATION_FAILED_NAME_INVALID;
        }

        if (gender < 0 || gender > 2) {
            return PlayerResponseType.CREATION_FAILED_GENDER_INVALID;
        }

        int[] validHairs = Utils.VALID_HAIR_IDS[gender];

        var isHairValid = false;
        for (int validId : validHairs) {
            if (validId == hair) {
                isHairValid = true;
                break;
            }
        }

        if (!isHairValid) {
            return PlayerResponseType.CREATION_FAILED_HAIR_INVALID;
        }

        try (Connection connection = DatabaseFactory.getConnection()) {

            connection.setAutoCommit(false);

            // kiem tra ten nhan vat
            if (PlayerDAO.isNameTaken(connection, name)) {
                connection.rollback();
                return PlayerResponseType.CREATION_FAILED_NAME_TAKEN;
            }
            // kiem tra account co nhan vat nao chua
            if (PlayerDAO.accountHasCharacter(connection, account.getId())) {
                connection.rollback();
                return PlayerResponseType.CREATION_FAILED_ACCOUNT_HAS_CHAR;
            }

            boolean success = PlayerDAO.saveNewPlayer(connection, account.getId(), name, gender, hair);
            if (success) {
                connection.commit();
                return PlayerResponseType.SUCCESS;
            } else {
                connection.rollback();
                return PlayerResponseType.CREATION_FAILED_SERVER_ERROR;
            }
        } catch (Exception e) {
            log.error("Failed to store new player: {}, account{}. Error: {}", name, e.getMessage(), account, e);
        }
        return PlayerResponseType.CREATION_FAILED_SERVER_ERROR;
    }

}
