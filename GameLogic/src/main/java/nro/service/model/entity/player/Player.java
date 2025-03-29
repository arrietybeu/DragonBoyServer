package nro.service.model.entity.player;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstTypeObject;
import nro.service.core.player.PetFollowService;
import nro.service.model.entity.*;
import nro.service.model.clan.Clan;
import nro.service.model.entity.pet.PetFollow;
import nro.service.model.entity.discpile.Disciple;
import nro.server.system.LogServer;
import nro.server.manager.ItemManager;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.service.core.map.AreaService;
import nro.service.core.item.ItemService;
import nro.service.core.system.ServerService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
public class Player extends BaseModel {

    private final Session session;
    private final PlayerCurrencies playerCurrencies;
    private final PlayerTask playerTask;
    private final PlayerInventory playerInventory;
    private final PlayerMagicTree playerMagicTree;
    private final PlayerStatus playerStatus;

    private Clan clan;
    private Disciple disciple;
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
    public void update() {
        if (this.playerMagicTree != null) {
            this.playerMagicTree.update();
        }
        if (this.petFollow != null) {
            this.petFollow.update();
        }
    }

    @Override
    public long handleAttack(Player player, int type, long damage) {
        if (this.points.isDead()) {
            this.points.setDie();
            return 0;
        }

        this.points.subCurrentHp(damage);

        if (this.points.isDead()) {
            this.points.setDie();
        }

        return damage;
    }

    @Override
    public void dispose() {
        AreaService.getInstance().playerExitArea(this);
    }

    public boolean isAdministrator() {
        return this.session.getUserInfo().isAdmin();
    }

    private void createAdministrator() {
        if (this.isAdministrator()) this.playerAdministrator = new PlayerAdministrator(this);
    }

    @Override
    public String toString() {
        return "Player{" + "session=" + session + ", playerCurrencies=" + playerCurrencies + ", playerPoints="
                + points + ", playerTask=" + playerTask + ", fashion=" + fashion + ", playerSkill="
                + skills + ", playerInventory=" + playerInventory + ", fusion=" + fusion
                + ", createdAt=" + createdAt + ", area=" + area + ", clan=" + clan + ", disciple=" + disciple
                + ", role=" + role + ", activePoint=" + activePoint + ", rank=" + rank + '}';
    }
}
