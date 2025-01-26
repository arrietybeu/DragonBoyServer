package nro.model.player;

import lombok.Data;
import nro.model.task.TaskMain;

@Data
public class PlayerTask {

    private final Player player;

    private TaskMain taskMain;

    public PlayerTask(Player player) {
        this.player = player;
    }
}
