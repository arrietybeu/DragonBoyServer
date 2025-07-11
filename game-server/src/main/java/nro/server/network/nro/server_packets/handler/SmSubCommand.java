package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.consts.ConstMsgSubCommand;
import nro.server.data_holders.data.ItemData;
import nro.server.data_holders.data.SkillData;
import nro.server.model.ecs.component.*;
import nro.server.model.ecs.component.player.*;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

import java.util.List;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.SUB_COMMAND)
public class SmSubCommand extends NroServerPacket {

    private final byte subCommand;

    private String text;

    private InfoComponent info;
    private StatsComponent stats;
    private TaskComponent task;
    private AppearanceComponent appearance;
    private StateComponent state;
    private BuffComponent buff;
    private FusionComponent fusion;
    private List<SkillData> skills;
    private CurrencyComponent currency;
    private List<ItemData> itemsBody, itemsBag, itemsBox;
    private int serverTimestamp;

    public SmSubCommand(int subCommand, InfoComponent info, StatsComponent stats, TaskComponent task, AppearanceComponent appearance, StateComponent state, BuffComponent buff, FusionComponent fusion, List<SkillData> skills, CurrencyComponent currency, List<ItemData> itemsBody, List<ItemData> itemsBag, List<ItemData> itemsBox, int serverTimestamp) {
        this.subCommand = (byte) subCommand;
        this.info = info;
        this.stats = stats;
        this.task = task;
        this.appearance = appearance;
        this.state = state;
        this.buff = buff;
        this.fusion = fusion;
        this.skills = skills;
        this.currency = currency;
        this.itemsBody = itemsBody;
        this.itemsBag = itemsBag;
        this.itemsBox = itemsBox;
        this.serverTimestamp = serverTimestamp;
    }

    public SmSubCommand(int subCommand, String text) {
        this.subCommand = (byte) subCommand;
        this.text = text;
    }

    public SmSubCommand(int subCommand) {
        this.subCommand = (byte) subCommand;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        writeByte(subCommand);
        switch (subCommand) {
            case ConstMsgSubCommand.UPDATE_SKILL_SHORTCUT -> {
                writeUTF(text);
                // send skill shortcut
            }
            case ConstMsgSubCommand.INIT_MY_CHARACTER -> {
            }
        }
    }

}
