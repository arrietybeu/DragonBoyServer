package nro.service.model.entity;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstItem;
import nro.service.model.entity.player.Player;
import nro.service.model.entity.player.PlayerInventory;
import nro.service.model.item.Item;
import nro.server.system.LogServer;

import java.util.List;

@Setter
@Getter
public abstract class Fashion {

    protected byte flagPk = 0;
    protected short head = -1;

    public abstract short getHead();

    public abstract short getBody();

    public abstract short getLeg();

    public abstract short getMount();

    public abstract short getFlagBag();

    public abstract short getAura();

    public abstract byte getEffSetItem();

    public abstract short getIdHat();
}
