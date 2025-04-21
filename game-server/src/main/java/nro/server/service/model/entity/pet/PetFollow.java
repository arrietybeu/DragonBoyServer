package nro.server.service.model.entity.pet;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nro.consts.ConstPetFollow;
import nro.server.service.model.entity.player.Player;

@Getter
@Setter
@ToString
public class PetFollow {

    private final Player player;

    private short smaillId;
    private int himg;
    private int[] frame;

    private boolean isAssistPet;
    private long buffStartTime;
    private byte typeBuff;

    public PetFollow(Player player) {
        this.player = player;
    }

    public void update() {
        while (isAssistPet) {
            switch (typeBuff) {
                case ConstPetFollow.PET_BUFF_MP -> {
                }
                case ConstPetFollow.PET_BUFF_EXP -> {
                }
                case ConstPetFollow.PET_BUFF_HP -> {
                }
                case ConstPetFollow.PET_BUFF_REWARD_GOLD -> {
                }
            }
        }
    }

    public void dispose() {
        this.isAssistPet = false;
    }
}
