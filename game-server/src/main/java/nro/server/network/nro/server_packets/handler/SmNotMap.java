package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.configs.main.ConfigServer;
import nro.server.data_holders.data.*;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.NOT_MAP)
public class SmNotMap extends NroServerPacket {

    public static final byte CREATE_CHARACTER = 2;
    public static final byte ALL_DATA_GAME = 4;
    public static final byte UPDATE_MAP = 6;
    public static final byte UPDATE_SKILL = 7;
    public static final byte UPDATE_ITEM = 8;

    public static final byte ITEM_OPTION = 0;
    public static final byte ITEM_TEMPLATE = 1;
    public static final byte ITEM_TEMPLATE2 = 2;
    public static final byte ITEM_ARR_HEAD_2FR = 100;

    private final byte status;
    private byte type;

    public SmNotMap(int status) {
        this.status = (byte) status;
    }

    /**
     * Packet gửi item
     * @param status
     * @param type
     */
    public SmNotMap(int status, int type) {
        this.status = (byte) status;
        this.type = (byte) type;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        this.writeByte(status);
        switch (status) {
            case ALL_DATA_GAME -> {
                sendAllDataGame();
                con.getSessionInfo().setUpdateData(true);
            }
            case UPDATE_MAP -> {
                sendUpdateMap();
                con.getSessionInfo().setUpdateMap(true);
            }
            case UPDATE_SKILL -> {
                sendUpdateSkill();
                con.getSessionInfo().setUpdateSkill(true);
            }
            case UPDATE_ITEM -> {
                sendUpdateItem(type);
                con.getSessionInfo().setUpdateItem(true);
            }
        }
    }

    private void sendAllDataGame() {
        this.writeByte(ConfigServer.VERSION_DATA);
        this.writeByte(ConfigServer.VERSION_DATA_MAP);
        this.writeByte(ConfigServer.VERSION_DATA_SKILL);
        this.writeByte(ConfigServer.VERSION_DATA_ITEM);
        this.writeByte(0);

        var captionTemplates = CaptionData.getInstance().getCaptionTemplates();
        this.writeByte(captionTemplates.size());
        for (var caption : captionTemplates) {
            this.writeLong(caption.exp());
        }
    }

    private void sendUpdateMap() {
        var mapTemplates = MapData.getInstance().getWorldMaps();
        writeByte(ConfigServer.VERSION_DATA_MAP);
        writeUnsignedByte(mapTemplates.size());

        for (var map : mapTemplates.values()) {
            writeUTF(map.getName());
        }

        var npcTemplates = NpcData.getInstance().getNpcTemplates();
        // write npc
        writeUnsignedByte(npcTemplates.size());
        for (var npc : npcTemplates) {
            this.writeUTF(npc.name());
            this.writeShort(npc.head());
            this.writeShort(npc.body());
            this.writeShort(npc.leg());
            this.writeByte(1);
            this.writeByte(1);
            this.writeUTF("Nói chuyện");
        }

        var monsterTemplates = MonsterData.getInstance().getMonsters();
        writeUnsignedByte(monsterTemplates.size());
        for (var monster : monsterTemplates) {
            this.writeByte(monster.type());
            this.writeUTF(monster.NAME());
            this.writeInt((int) monster.hp());
            this.writeByte(monster.rangeMove());
            this.writeByte(monster.speed());
            this.writeByte(monster.dartType());
        }
    }

    private void sendUpdateSkill() {

        var skillData = SkillData.getInstance();
        var nClasses = skillData.getNClassTemplates();

        this.writeByte(ConfigServer.VERSION_DATA_SKILL);
        this.writeByte(0);

        // write skill nClass
        this.writeByte(nClasses.size());

        for (var classSkill : nClasses) {
            this.writeUTF(classSkill.name());
            this.writeByte(classSkill.skillTemplates().size());
            for (var skillTemplate : classSkill.skillTemplates()) {
                this.writeByte(skillTemplate.getId());
                this.writeUTF(skillTemplate.getName());
                this.writeByte(skillTemplate.getMaxPoint());
                this.writeByte(skillTemplate.getManaUseType());
                this.writeByte(skillTemplate.getType());
                this.writeShort(skillTemplate.getIconId());
                this.writeUTF(skillTemplate.getDamInfo());
                this.writeUTF(skillTemplate.getDescription());
                this.writeByte(skillTemplate.getSkills().size());
                for (var skill : skillTemplate.getSkills()) {
                    this.writeShort(skill.getSkillId());
                    this.writeByte(skill.getPoint());
                    this.writeLong(skill.getPowRequire());
                    this.writeShort(skill.getManaUse());
                    this.writeInt((int) skill.getBaseCooldown());
                    this.writeShort(skill.getDx());
                    this.writeShort(skill.getDy());
                    this.writeByte(skill.getMaxFight());
                    this.writeShort(skill.getDamage());
                    this.writeShort(skill.getPrice());
                    this.writeUTF(skill.getMoreInfo());
                }
            }
        }

    }

    private void sendUpdateItem(int type) {
        this.writeByte(ConfigServer.VERSION_DATA_ITEM);
        switch (type) {
            case ITEM_OPTION -> sendUpdateOption();
            case ITEM_TEMPLATE -> sendUpdateItemTemplate();
            case ITEM_TEMPLATE2 -> sendUpdateItemTemplate2();
            case ITEM_ARR_HEAD_2FR -> sendItemArr_Head_2Fr();
            default -> throw new IllegalArgumentException("Unknown item update type: " + type);
        }
    }

    private void sendUpdateOption() {
        writeBytes(ItemData.getInstance().getDataItemOption());
    }

    private void sendUpdateItemTemplate() {
        writeBytes(ItemData.getInstance().getDataItemTemplate());
    }

    private void sendUpdateItemTemplate2() {
        writeBytes(ItemData.getInstance().getDataItemTemplate2());
    }

    private void sendItemArr_Head_2Fr() {
        writeBytes(ItemData.getInstance().getDataArrHead2Fr());
    }

}
