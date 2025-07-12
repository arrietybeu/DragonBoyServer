package nro.server.services.player;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import nro.commons.consts.ConstsCmd;
import nro.server.dao.PlayerDAO;
import nro.server.engine.GameWorld;
import nro.server.model.ecs.component.HealthComponent;
import nro.server.model.ecs.component.StatsComponent;
import nro.server.model.ecs.component.player.PlayerComponent;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.server_packets.PacketHelper;
import nro.server.network.nro.server_packets.handler.SmMeLoadPoint;
import nro.server.network.nro.server_packets.handler.SmSpecialSkill;
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
            client.sendPacket(PacketHelper.empty(ConstsCmd.CLIENT_INFO));
        } else {
            var playerEntity = PlayerDAO.loadPlayerEntity(playerId, accountId);
            if (playerEntity == null) {
                log.error("Failed to load player entity for player ID: {}. Cannot enter world.", playerId);
                client.close(PacketHelper.empty(ConstsCmd.CLIENT_INFO));
                return;
            }
            client.attachPlayerEntity(playerEntity.getId());

            if (!enteringWorld.contains(playerEntity.getId()) && enteringWorld.add(playerEntity.getId())) {
                try {
                    playerEntity.edit().add(new PlayerComponent(client));

//                    this.sendSelectSkillShortCut(player, "KSkill");
//                    this.sendSelectSkillShortCut(player, "OSkill");

                    client.sendPacket(new SmSpecialSkill());
                    client.sendPacket(new SmMeLoadPoint(playerId));
                    // send task
                    client.sendPacket(PacketHelper.empty(ConstsCmd.MAP_CLEAR));

                    // send info player -30
                    // send clan info  -53
                    // send flag bag -64
                    // send player body -90
                    // send map info  -24
                    // send current hp mp -30

                    //this.sendThongBaoInfoTask(player, serverService);
                    // send max stamina -69
                    // send stamina -68
                    // send actiove point -97
                    // send player is pet -107
                    // send player ranks -119
                    // send skill shortcut -113
                    // send game noti 50
                    // send caption -41
                    // player.getPlayerTask().sendInfoTaskForNpcTalkByUI(player);
                    // SkillService.getInstance().sendSkillCooldown(player);
                } catch (Throwable e) {
                    log.error("Error during enter world of {}", playerEntity.getId(), e);
                } finally {
                    enteringWorld.remove(playerEntity.getId());
                }
            }
        }
    }
}
