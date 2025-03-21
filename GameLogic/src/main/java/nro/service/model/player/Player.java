package nro.service.model.player;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstTypeObject;
import nro.service.model.LiveObject;
import nro.service.model.clan.Clan;
import nro.service.model.map.areas.Area;
import nro.service.model.pet.PetFollow;
import nro.service.model.discpile.Disciple;
import nro.server.LogServer;
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
public class Player extends LiveObject {

    private final PlayerCurrencies playerCurrencies;
    private final PlayerTask playerTask;
    private final PlayerInventory playerInventory;
    private final PlayerMagicTree playerMagicTree;

    private final Session session;

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
        this.playerPoints = new PlayerPoints(this);
        this.playerTask = new PlayerTask(this);
        this.playerFashion = new PlayerFashion(this);
        this.playerSkill = new PlayerSkill(this);
        this.playerInventory = new PlayerInventory(this);
        this.playerFusion = new PlayerFusion(this);
        this.playerMagicTree = new PlayerMagicTree(this);
        this.playerStatus = new PlayerStatus(this);
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

                    this.playerFashion.setFlagPk((byte) index);
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
    }

    @Override
    public long handleAttack(Player player, int type, long damage) {
        if (this.playerPoints.isDead()) {
            this.playerPoints.setDie();
            return 0;
        }

        this.playerPoints.subCurrentHp(damage);

        if (this.playerPoints.isDead()) {
            this.playerPoints.setDie();
        }

        System.out.println("hp: " + this.playerPoints.getCurrentHP());
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
                + playerPoints + ", playerTask=" + playerTask + ", playerFashion=" + playerFashion + ", playerSkill="
                + playerSkill + ", playerInventory=" + playerInventory + ", playerFusion=" + playerFusion
                + ", createdAt=" + createdAt + ", area=" + area + ", clan=" + clan + ", disciple=" + disciple
                + ", role=" + role + ", activePoint=" + activePoint + ", rank=" + rank + '}';
    }
}
