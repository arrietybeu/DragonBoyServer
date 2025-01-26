package nro.model.player;

import lombok.Data;

@Data
public class PlayerStats {

    private final Player player;

    private int cHPGoc;
    private int cMPGoc;
    private int cDamGoc;
    private long cHPFull;
    private long cMPFull;
    private long cHP;
    private long cMP;
    private byte cspeed;
    private byte hpFrom1000TiemNang;
    private byte mpFrom1000TiemNang;
    private byte damFrom1000TiemNang;
    private long cDamFull;
    private long cDefull;
    private byte cCriticalFull;
    private long cTiemNang;
    private short expForOneAdd;
    private int cDefGoc;
    private byte cCriticalGoc;

    private long power;

    public PlayerStats(Player player) {
        this.player = player;
    }
}
