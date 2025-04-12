package nro.server.service.core.player;

import lombok.Getter;
import nro.server.service.core.npc.NpcService;
import nro.server.system.LogServer;
import nro.server.service.core.system.ServerService;
import nro.server.service.model.entity.pet.PetFollow;
import nro.server.service.model.entity.player.Player;
import nro.utils.Util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PetFollowService {

    private static final class SingletonHolder {
        private static final PetFollowService instance = new PetFollowService();
    }

    public static PetFollowService getInstance() {
        return PetFollowService.SingletonHolder.instance;
    }

    private static final long CHECK_INTERVAL_SECONDS = 600;// 10 minutes

    private static final double BUFF_CHANCE = 0.5;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void startPetBuffCycle(Player player) {
        scheduler.scheduleAtFixedRate(() -> tryBuffPet(player), 0, CHECK_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    private void tryBuffPet(Player player) {
        try {
            if (player == null) return;

//            if (Math.random() < BUFF_CHANCE) {
                PetFollow pet = new PetFollow(player);
                pet.setAssistPet(true);
                pet.setBuffStartTime(System.currentTimeMillis());

                byte typeBuff = (byte) Util.nextInt(0, 3);

                pet.setTypeBuff(typeBuff);
                player.setPetFollow(pet);

                ServerService.getInstance().sendPetFollow(player, 1, -1);

                String notify = "✨ Một Pet linh thú vừa xuất hiện hỗ trợ bạn trong trận chiến!";
                ServerService.getInstance().sendChatGlobal(player.getSession(), null, notify, false);
//            }
        } catch (Exception e) {
            LogServer.LogException("Error tryBuffPet: " + e.getMessage(), e);
        }
    }

}

