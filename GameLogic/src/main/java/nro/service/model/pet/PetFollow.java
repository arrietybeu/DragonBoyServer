package nro.service.model.pet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PetFollow {

    private short smaillId;
    private int wing;
    private int himg;
    private int[] frame;
}
