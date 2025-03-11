package nro.model.clan;

import lombok.Getter;

@Getter
public class ClanMember {

    private int id;
    private String name;

    private short headICON = -1;

    private short head;
    private short leg;
    private short body;

    private byte role;
    private int donate;
    private int receive_donate;
    private int curClanPoint;
    private int clanPoint;
    private int lastRequest;

    private String powerPoint;
    private int joinTime;

}
