package nro.service.model.clan;

import lombok.Getter;

import java.util.List;

@Getter
public class Clan {

    private int id;
    private int imgID;
    private String name;
    private String slogan;
    private int date;
    private String powerPoint;
    private int currMember;
    private int maxMember = 50;
    private int leaderID;// khong dung
    private String leaderName;
    private int level;
    private int clanPoint;

    private List<ClanMember> clanMembers;
    private List<ClanMessage> currClanMessages;

    public Clan() {
    }

}
