package nro.server.service.model.entity.player;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstItem;
import nro.consts.ConstTypeObject;
import nro.server.manager.MapManager;
import nro.server.realtime.system.player.PlayerSystem;
import nro.server.service.core.item.ItemFactory;
import nro.server.service.core.player.PlayerTransport;
import nro.server.service.model.entity.Entity;

import nro.server.service.model.clan.Clan;
import nro.server.service.model.entity.pet.PetFollow;

import nro.server.service.model.item.Item;
import nro.server.service.model.item.ItemMap;
import nro.server.service.model.template.map.Transport;
import nro.server.system.LogServer;
import nro.server.manager.ItemManager;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.service.core.map.AreaService;
import nro.server.service.core.item.ItemService;
import nro.server.service.core.system.ServerService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@Setter
public class Player extends Entity {

    private final Session session;
    private PlayerCurrencies playerCurrencies;
    private PlayerTask playerTask;
    private PlayerInventory playerInventory;
    private PlayerMagicTree playerMagicTree;
    private PlayerStatus playerStatus;
    private PlayerTransport playerTransport;

    private Clan clan;
    private PetFollow petFollow;
    private Instant createdAt;
    private PlayerAdministrator playerAdministrator;

    private int role;
    private int activePoint;
    private int rank;

    public Player(Session session) {
        this.setTypeObject(ConstTypeObject.TYPE_PLAYER);
        this.session = session;
        this.playerCurrencies = new PlayerCurrencies(this);
        this.playerTask = new PlayerTask(this);
        this.playerInventory = new PlayerInventory(this);
        this.playerMagicTree = new PlayerMagicTree(this);
        this.playerStatus = new PlayerStatus(this);
        this.points = new PlayerPoints(this);
        this.fashion = new PlayerFashion(this);
        this.skills = new PlayerSkills(this);
        this.fusion = new PlayerFusion(this);
        this.playerTransport = new PlayerTransport(this);
        this.createAdministrator();
    }

    public void sendMessage(Message message) throws Exception {
        this.session.sendMessage(message);
    }

    public boolean isNewPlayer() {
        long days = this.getDaysSinceCreation();
        return days < 30;
    }

    private long getDaysSinceCreation() {
        return ChronoUnit.DAYS.between(createdAt, Instant.now());
    }

    public void changeFlag(int action, int index) {
        try {
            ItemService itemService = ItemService.getInstance();
            long currentTime = System.currentTimeMillis();
            long lastChangeTime = this.getPlayerStatus().getLastTimeChangeFlag();
            boolean isInvalidIndex = index < 0 || index >= ItemManager.getInstance().getFlags().size();

            switch (action) {
                case 0 -> itemService.sendShowListFlag(this);
                case 1 -> {
                    if (index != 0 && lastChangeTime + 60000 > currentTime) {
                        long remainingTime = (lastChangeTime + 60000 - currentTime) / 1000;
                        ServerService.getInstance().sendChatGlobal(this.getSession(), null,
                                String.format("Chỉ được đổi cờ sau %d giây nữa", remainingTime),
                                false);
                        return;
                    }

                    if (isInvalidIndex) {
                        ServerService.getInstance().sendChatGlobal(this.getSession(), null,
                                "Đã xảy ra lỗi\nvui lòng thao tác lại", false);
                        return;
                    }

                    if (index != 0) {
                        this.getPlayerStatus().setLastTimeChangeFlag(currentTime);
                    }

                    this.fashion.setFlagPk((byte) index);
                    itemService.sendChangeFlag(this, index);
                    itemService.sendImageFlag(this, index, ItemManager.getInstance().findFlagId(index).icon());
                }
                case 2 -> {
                    if (isInvalidIndex) {
                        ServerService.getInstance().sendChatGlobal(this.getSession(), null,
                                "Đã xảy ra lỗi\nvui lòng thao tác lại", false);
                        return;
                    }
                    if (index == 0 || lastChangeTime + 60000 <= currentTime) {
                        itemService.sendImageFlag(this, index, ItemManager.getInstance().findFlagId(index).icon());
                    }
                }
            }
        } catch (Exception e) {
            LogServer.LogException("Error changeFlag: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized long handleAttack(Entity entityAttack, int type, long damage) {
        return super.handleAttack(entityAttack, type, damage);
    }

    // Override this method to handle the player's death
    @Override
    protected void onDie(Entity killer) {
        int goldLost = Math.min((int) (this.playerCurrencies.getGold() * 0.10), 32000);
        if (goldLost <= 0) return;
        this.playerCurrencies.subGold(goldLost);

        int goldReceived = (int) (goldLost * 0.9); // hé hé hé trừ 10% đi

        Item item = ItemFactory.getInstance().createItemNotOptionsBase(ConstItem.VANG_1, goldReceived);
        ItemMap itemMap = new ItemMap(this.getArea(), this.getArea().increaseItemMapID(), this.getId(), item, this.getX(), this.getY(),
                -1, true);
        ItemService.getInstance().sendDropItemMap(this, itemMap, false);
    }

    public boolean isAdministrator() {
        return this.session.getUserInfo().isAdmin();
    }

    private void createAdministrator() {
        if (this.isAdministrator()) this.playerAdministrator = new PlayerAdministrator(this);
    }

    private void unregisterFromEntityComponentSystem() {
        PlayerSystem.getInstance().unregister(this);
    }

    @Override
    public void dispose() {
        try {
            if (this.session != null) {
                this.session.setPlayer(null);
            }
            if (this.playerAdministrator != null) {
                this.playerAdministrator = null;
            }
            if (this.playerMagicTree != null) {
                this.playerMagicTree = null;
            }
            if (this.playerInventory != null) {
                this.playerInventory.dispose();
                this.playerInventory = null;
            }
            if (this.playerTask != null) {
                this.playerTask.dispose();
                this.playerTask = null;
            }
            if (this.playerStatus != null) {
                this.playerStatus.dispose();
                this.playerStatus = null;
            }
            if (this.playerTransport != null) {
                this.playerTransport.dispose();
                this.playerTransport = null;
            }
            if (this.playerCurrencies != null) {
                this.playerCurrencies = null;
            }
            if (this.fashion != null) {
                this.fashion = null;
            }
            if (this.skills != null) {
                this.skills.dispose();
                this.skills = null;
            }
            if (this.fusion != null) {
                this.fusion = null;
            }
            if (this.petFollow != null) {
                this.petFollow.dispose();
                this.petFollow = null;
            }

            MapManager.getInstance().removeMapOffline(this);
            AreaService.getInstance().playerExitArea(this);
            this.unregisterFromEntityComponentSystem();
        } catch (Exception exception) {
            LogServer.LogException("Error dispose player: " + exception.getMessage(), exception);
        }
    }

    @Override
    public String toString() {
        return "Player{" + "session=" + session + ", playerCurrencies=" + playerCurrencies + ", playerPoints="
                + points + ", playerTask=" + playerTask + ", fashion=" + fashion + ", playerSkill="
                + skills + ", playerInventory=" + playerInventory + ", fusion=" + fusion
                + ", createdAt=" + createdAt + ", area=" + area + ", clan=" + clan +
                ", role=" + role + ", activePoint=" + activePoint + ", rank=" + rank + '}';
    }
}
