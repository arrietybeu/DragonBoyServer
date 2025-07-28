package nro.server.services.player;

import nro.commons.consts.ConstsCmd;
import nro.server.consts.ConstMsgSubCommand;
import nro.server.dao.PlayerDAO;
import nro.server.engine.GameWorld;
import nro.server.model.ecs.component.AppearanceComponent;
import nro.server.model.ecs.component.InfoComponent;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.StatsComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.PlayerResponseType;
import nro.server.network.nro.server_packets.PacketHelper;
import nro.server.network.nro.server_packets.handler.*;
import nro.server.world.World;
import nro.server.world.WorldMap;
import nro.server.world.WorldMapInstance;
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

            var entity = client.getEntity();
            if (entity == null) {
                entity = PlayerDAO.loadPlayerEntity(playerId, accountId);
                if (entity == null) {
                    log.error("Failed to load player entity for player ID: {}. Cannot enter world.", playerId);
                    client.close(
                            new SmDialogMessage(PlayerResponseType.LOGIN_FAILED_DATA_LOAD_ERROR.getDefaultMessage()));
                    return;
                }
                client.attachPlayerEntity(entity);
                client.setPlayerID(playerId);
            }

            if (!enteringWorld.contains(playerId) && enteringWorld.add(playerId)) {
                try {
                    entity.edit().add(new PlayerComponent(client));

                    PositionComponent positionComponent = entity.getComponent(PositionComponent.class);

                    WorldMapInstance instance = World.getInstance().getAvailableInstance(positionComponent.mapId, playerId, positionComponent.areaId);
                    if (instance == null) {
                        log.error("No available instance for player: {}", playerId);
                        client.close(
                                new SmDialogMessage(PlayerResponseType.LOGIN_FAILED_SERVER_FULL.getDefaultMessage()));
                        return;
                    }
                    instance.addEntity(playerId);
                    positionComponent.areaId = instance.getInstanceId();

                    client.sendPacket(
                            new SmSubCommand(ConstMsgSubCommand.UPDATE_SKILL_SHORTCUT, "KSkill"));
                    client.sendPacket(
                            new SmSubCommand(ConstMsgSubCommand.UPDATE_SKILL_SHORTCUT, "OSkill"));

                    client.sendPacket(new SmSpecialSkill());
                    client.sendPacket(new SmMeLoadPoint());
                    // client.sendPacket(new SmTaskInfo());
                    client.sendPacket(PacketHelper.empty(ConstsCmd.MAP_CLEAR));

                    client.sendPacket(new SmSubCommand(ConstMsgSubCommand.INIT_MY_CHARACTER));
                    client.sendPacket(new SmClanInfo());
                    client.sendPacket(new SmUpdateBag(playerId, entity.getComponent(AppearanceComponent.class)));
                    client.sendPacket(new SmUpdateBody(playerId, entity.getComponent(AppearanceComponent.class)));
                    client.sendPacket(new SmMapInfo(positionComponent));
                    client.sendPacket(new SmSubCommand(ConstMsgSubCommand.UPDATE_MY_CURRENCY_HPMP));

                    // this.sendThongBaoInfoTask(player, serverService);

                    client.sendPacket(new SmMaxStamina());
                    client.sendPacket(new SmStamina());
                    client.sendPacket(new SmActivePoint(entity.getComponent(StatsComponent.class).activePoint));
                    client.sendPacket(new SmPetInfo());
                    client.sendPacket(new SmRank());
                    client.sendPacket(new SmChangeOnSkill());
                    client.sendPacket(new SmGameInfo());
                    client.sendPacket(new SmUpdateCaption(entity.getComponent(InfoComponent.class).gender));

                    // player.getPlayerTask().sendInfoTaskForNpcTalkByUI(player);
                    // SkillService.getInstance().sendSkillCooldown(player);

                } catch (Throwable e) {
                    log.error("Error during enter world of {}", entity, e);
                } finally {
                    enteringWorld.remove(playerId);
                }
            } else {
                log.warn("Player with ID {} is already entering the world or is in the process of entering.", playerId);
            }
        }
    }

}
