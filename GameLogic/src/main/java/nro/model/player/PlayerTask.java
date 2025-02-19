package nro.model.player;

import lombok.Getter;
import lombok.Setter;
import nro.model.task.TaskMain;

@Setter
@Getter
public class PlayerTask {

    private final Player player;

    private TaskMain taskMain;

    public PlayerTask(Player player) {
        this.player = player;
    }

    @Override
    public String toString() {
        return "PlayerTask{" +
                "player=" + player +
                ", taskMain=" + taskMain +
                '}';
    }
}
