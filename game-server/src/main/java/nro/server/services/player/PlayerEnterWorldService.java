package nro.server.services.player;

import com.artemis.World;
import nro.commons.consts.ConstsCmd;
import nro.server.consts.ConstMsgSubCommand;
import nro.server.dao.PlayerDAO;
import nro.server.engine.GameWorld;
import nro.server.model.ecs.component.AppearanceComponent;
import nro.server.model.ecs.component.InfoComponent;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.PlayerResponseType;
import nro.server.network.nro.server_packets.PacketHelper;
import nro.server.network.nro.server_packets.handler.*;
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
        if (client == null)
            throw new NullPointerException("Client EnterWorldService cannot be null");

        int accountId = client.getAccount().getId();

        int playerId = PlayerDAO.findPlayerIdByAccountId(accountId);

        if (playerId == -1) {
            // show UI create chảr
            client.sendPacket(PacketHelper.empty(ConstsCmd.CLIENT_INFO));
        } else {

            int playerEntityID = client.getPlayerEntityId();
            if (playerEntityID <= 0) {
                playerEntityID = PlayerDAO.loadPlayerEntity(playerId, accountId);
                if (playerEntityID == -1) {
                    log.error("Failed to load player entity for player ID: {}. Cannot enter world.", playerId);
                    client.close(
                            new SmDialogMessage(PlayerResponseType.LOGIN_FAILED_DATA_LOAD_ERROR.getDefaultMessage()));
                    return;
                }
                client.attachPlayerEntity(playerEntityID);
                client.setPlayerID(playerId);
            }

            if (!enteringWorld.contains(playerEntityID) && enteringWorld.add(playerEntityID)) {
                try {
                    World world = GameWorld.getInstance().getWorld();
                    var playerEntity = world.getEntity(playerEntityID);
                    playerEntity.edit().add(new PlayerComponent(client));

                    client.sendPacket(
                            new SmSubCommand(ConstMsgSubCommand.UPDATE_SKILL_SHORTCUT, "KSkill", playerEntityID));
                    client.sendPacket(
                            new SmSubCommand(ConstMsgSubCommand.UPDATE_SKILL_SHORTCUT, "OSkill", playerEntityID));

                    client.sendPacket(new SmSpecialSkill());
                    client.sendPacket(new SmMeLoadPoint(playerEntityID));
                    // client.sendPacket(new SmTaskInfo());
                    client.sendPacket(PacketHelper.empty(ConstsCmd.MAP_CLEAR));

                    client.sendPacket(new SmSubCommand(ConstMsgSubCommand.INIT_MY_CHARACTER, playerEntityID));
                    client.sendPacket(new SmClanInfo());
                    client.sendPacket(
                            new SmUpdateBag(playerEntityID, playerEntity.getComponent(AppearanceComponent.class)));
                    client.sendPacket(new SmUpdateBody(playerId, playerEntity.getComponent(AppearanceComponent.class)));
                    client.sendPacket(new SmMapInfo(playerEntity.getComponent(PositionComponent.class)));
                    client.sendPacket(new SmSubCommand(ConstMsgSubCommand.UPDATE_MY_CURRENCY_HPMP, playerEntityID));

                    // this.sendThongBaoInfoTask(player, serverService);

                    client.sendPacket(new SmMaxStamina());
                    client.sendPacket(new SmStamina());
                    client.sendPacket(new SmActivePoint());
                    client.sendPacket(new SmPetInfo());
                    client.sendPacket(new SmRank());
                    client.sendPacket(new SmChangeOnSkill());
                    client.sendPacket(new SmGameInfo());
                    client.sendPacket(new SmUpdateCaption(playerEntity.getComponent(InfoComponent.class).gender));

                    // player.getPlayerTask().sendInfoTaskForNpcTalkByUI(player);
                    // SkillService.getInstance().sendSkillCooldown(player);
                } catch (Throwable e) {
                    log.error("Error during enter world of {}", playerEntityID, e);
                } finally {
                    enteringWorld.remove(playerEntityID);
                }
            }
        }
    }
}
