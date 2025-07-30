package nro.server.services.player;

import nro.commons.consts.ConstsCmd;
import nro.server.consts.ConstMsgSubCommand;
import nro.server.dao.PlayerDAO;
import nro.server.model.ecs.component.AppearanceComponent;
import nro.server.model.ecs.component.InfoComponent;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.ecs.component.StatsComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.PlayerResponseType;
import nro.server.network.nro.server_packets.PacketHelper;
import nro.server.network.nro.server_packets.handler.*;
import nro.server.model.world.World;
import nro.server.model.world.WorldMapInstance;
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

                    client.sendPacket(new SmSpecialSkill());// TODO chua xong
                    client.sendPacket(new SmMeLoadPoint());// DONE
                     client.sendPacket(new SmTaskInfo()); // FIXME 1 số cái hơi sai
                    client.sendPacket(PacketHelper.empty(ConstsCmd.MAP_CLEAR));// DONE

                    client.sendPacket(new SmSubCommand(ConstMsgSubCommand.INIT_MY_CHARACTER));// FIXME 1 số cái chưa hoàn thiện
                    client.sendPacket(new SmClanInfo());// FIXME chưa xong
                    client.sendPacket(new SmUpdateBag(playerId, entity.getComponent(AppearanceComponent.class)));// DONE
                    client.sendPacket(new SmUpdateBody(playerId, entity.getComponent(AppearanceComponent.class)));// DONE
                    client.sendPacket(new SmMapInfo(positionComponent));// FIXME 1 số cái chưa xong
                    client.sendPacket(new SmSubCommand(ConstMsgSubCommand.UPDATE_MY_CURRENCY_HPMP));// DONE RỒI

                    // this.sendThongBaoInfoTask(player, serverService);

                    client.sendPacket(new SmMaxStamina());// FIXME chua xong
                    client.sendPacket(new SmStamina());// FIXME chua xong
                    client.sendPacket(new SmActivePoint(entity.getComponent(StatsComponent.class).activePoint));// DONE
                    client.sendPacket(new SmPetInfo());// FIXME chua xong
                    client.sendPacket(new SmRank());// FIXME chua xong
                    client.sendPacket(new SmChangeOnSkill());// FIXME chua xong
                    client.sendPacket(new SmGameInfo());// DONE
                    client.sendPacket(new SmUpdateCaption(entity.getComponent(InfoComponent.class).gender));// DONE

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
