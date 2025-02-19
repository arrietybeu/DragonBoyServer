package nro.model.player;

import lombok.Getter;
import lombok.Setter;
import nro.model.LiveObject;
import nro.model.clan.Clan;
import nro.model.map.areas.Area;
import nro.model.discpile.Disciple;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.service.AreaService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
public class Player extends LiveObject {

    private final PlayerCurrencies playerCurrencies;
    private final PlayerStats playerStats;
    private final PlayerTask playerTask;
    private final PlayerFashion playerFashion;
    private final PlayerSkill playerSkill;
    private final PlayerInventory playerInventory;
    private final PlayerFusion playerFusion;

    private Area area;
    private Clan clan;
    private Disciple disciple;

    private Instant createdAt;
    private Session session;

    private int role;
    private int activePoint;
    private int rank;
    private int teleport = 0;

    public Player() {
        this.setTypeObject(1);
        this.playerCurrencies = new PlayerCurrencies(this);
        this.playerStats = new PlayerStats(this);
        this.playerTask = new PlayerTask(this);
        this.playerFashion = new PlayerFashion(this);
        this.playerSkill = new PlayerSkill(this);
        this.playerInventory = new PlayerInventory(this);
        this.playerFusion = new PlayerFusion(this);
    }

    public void sendMessage(Message message) {
        this.session.sendMessage(message);
    }

    public boolean isNewPlayer() {
        long days = this.getDaysSinceCreation();
//        System.out.println("Player name: " + this.getName() + " create " + days + " day.");
        return days < 30;
    }

    private long getDaysSinceCreation() {
        return ChronoUnit.DAYS.between(createdAt, Instant.now());
    }

    @Override
    public void update() {
    }

    @Override
    public void dispose() {
        AreaService.getInstance().playerExitArea(this);
    }

    @Override
    public String toString() {
        return "Player{" + "session=" + session + ", playerCurrencies=" + playerCurrencies + ", playerStats=" + playerStats + ", playerTask=" + playerTask + ", playerFashion=" + playerFashion + ", playerSkill=" + playerSkill + ", playerInventory=" + playerInventory + ", playerFusion=" + playerFusion + ", createdAt=" + createdAt + ", area=" + area + ", clan=" + clan + ", disciple=" + disciple + ", role=" + role + ", activePoint=" + activePoint + ", rank=" + rank + ", teleport=" + teleport + '}';
    }
}
