package nro.service.model.clan;

import lombok.Getter;

@Getter
public class ClanMessage {
    public int id;

    public int type;

    public int playerId;

    public String playerName;

    public String text;

    public int time;

    public int headId;

    public String[] chat;

    public byte color;

    public byte role;

    private int timeAgo;

    public int recieve;

    public int maxCap;

    public String[] option;

    public boolean isNewClanMessage;
}
