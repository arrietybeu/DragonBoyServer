package nro.server.model.npc;

import nro.server.network.nro.NroConnection;

import java.util.Objects;

/**
 * @author Arriety
 */
public abstract class Npc {

    private final int id;
    private final int status;
    private final int mapID;
    private final int x;
    private final int y;
    private final int avatarId;

    public Npc(int id, int status, int mapID, int x, int y, int avatarId) {
        this.id = id;
        this.status = status;
        this.mapID = mapID;
        this.x = x;
        this.y = y;
        this.avatarId = avatarId;
    }

    public Npc cloneNpc(int npcId, int status, int mapId, int x, int y, int avatar) {
        try {
            return this.getClass().getDeclaredConstructor(new Class<?>[]{int.class, int.class, int.class, int.class, int.class, int.class}).newInstance(npcId, status, mapId, x, y, avatar);
        } catch (Exception e) {
            throw new RuntimeException("Error cloning NPC: " + e.getMessage(), e);
        }
    }

    public int id() {
        return id;
    }

    public int status() {
        return status;
    }

    public int mapID() {
        return mapID;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int avatarId() {
        return avatarId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Npc) obj;
        return this.id == that.id && this.status == that.status && this.mapID == that.mapID && this.x == that.x && this.y == that.y && this.avatarId == that.avatarId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, mapID, x, y, avatarId);
    }

    @Override
    public String toString() {
        return "Npc[" + "id=" + id + ", " + "status=" + status + ", " + "mapID=" + mapID + ", " + "x=" + x + ", " + "y=" + y + ", " + "avatarId=" + avatarId + ']';
    }

    public abstract void openUIMenu(NroConnection client);

    public abstract void openUIConfirm(NroConnection client, int select);

}