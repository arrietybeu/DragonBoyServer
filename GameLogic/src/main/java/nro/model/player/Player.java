package nro.model.player;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstTypeObject;
import nro.model.LiveObject;
import nro.model.clan.Clan;
import nro.model.map.areas.Area;
import nro.model.pet.PetFollow;
import nro.model.discpile.Disciple;
import nro.server.LogServer;
import nro.server.manager.ItemManager;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.service.AreaService;
import nro.service.ItemService;
import nro.service.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
public class Player extends LiveObject {

    private final PlayerCurrencies playerCurrencies;
    private final PlayerPoints playerPoints;
    private final PlayerTask playerTask;
    private final PlayerFashion playerFashion;
    private final PlayerSkill playerSkill;
    private final PlayerInventory playerInventory;
    private final PlayerFusion playerFusion;
    private final PlayerMagicTree playerMagicTree;
    private final PlayerStatus playerStatus;

    private Area area;
    private Clan clan;
    private Disciple disciple;
    private PetFollow petFollow;

    private Instant createdAt;
    private Session session;

    private int role;
    private int activePoint;
    private int rank;

    public Player() {
        this.setTypeObject(ConstTypeObject.TYPE_PLAYER);
        this.playerCurrencies = new PlayerCurrencies(this);
        this.playerPoints = new PlayerPoints(this);
        this.playerTask = new PlayerTask(this);
        this.playerFashion = new PlayerFashion(this);
        this.playerSkill = new PlayerSkill(this);
        this.playerInventory = new PlayerInventory(this);
        this.playerFusion = new PlayerFusion(this);
        this.playerMagicTree = new PlayerMagicTree(this);
        this.playerStatus = new PlayerStatus(this);
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
                case 0 -> itemService.sendShowListFlagBag(this);
                case 1 -> {
                    if (index != 0 && lastChangeTime + 60000 > currentTime) {
                        long remainingTime = (lastChangeTime + 60000 - currentTime) / 1000;
                        Service.getInstance().sendChatGlobal(this.getSession(), null,
                                String.format("Chỉ được đổi cờ sau %d giây nữa", remainingTime),
                                false);
                        return;
                    }
                    if (isInvalidIndex) {
                        Service.getInstance().sendChatGlobal(this.getSession(), null,
                                "Đã xảy ra lỗi\nvui lòng thao tác lại", false);
                        return;
                    }

                    if (index != 0) {
                        this.getPlayerStatus().setLastTimeChangeFlag(currentTime);
                    }

                    this.playerFashion.setFlagPk((byte) index);
                    itemService.sendChangeFlag(this, index);
                    itemService.sendImageFlag(this, index, ItemManager.getInstance().findFlagId(index).icon());
                }
                case 2 -> {
                    if (isInvalidIndex) {
                        Service.getInstance().sendChatGlobal(this.getSession(), null,
                                "Đã xảy ra lỗi\nvui lòng thao tác lại", false);
                        return;
                    }

                    if (index == 0 || lastChangeTime + 60000 <= currentTime) {
                        itemService.sendImageFlag(this, index, ItemManager.getInstance().findFlagId(index).icon());
                    }
                }
            }
        } catch (Exception e) {
            LogServer.LogException("Error changeFlag: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        if (this.playerMagicTree != null) {
            this.playerMagicTree.update();
        }
    }

    @Override
    public long handleAttack(Player player, long damage) {
        System.out.println("Player{" + this.getId() + "} attack Player{" + player.getId() + "} with damage: " + damage);
        if (this.playerPoints.isDead())
            return 0;

        this.playerPoints.subCurrentHp(damage);

        if (this.playerPoints.isDead()) {
            this.playerPoints.setDie();
        }
        return damage;
    }

    @Override
    public void dispose() {
        AreaService.getInstance().playerExitArea(this);
    }

    @Override
    public String toString() {
        return "Player{" + "session=" + session + ", playerCurrencies=" + playerCurrencies + ", playerPoints="
                + playerPoints + ", playerTask=" + playerTask + ", playerFashion=" + playerFashion + ", playerSkill="
                + playerSkill + ", playerInventory=" + playerInventory + ", playerFusion=" + playerFusion
                + ", createdAt=" + createdAt + ", area=" + area + ", clan=" + clan + ", disciple=" + disciple
                + ", role=" + role + ", activePoint=" + activePoint + ", rank=" + rank + '}';
    }
}
