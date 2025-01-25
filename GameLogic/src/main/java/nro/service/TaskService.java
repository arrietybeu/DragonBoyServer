package nro.service;

import nro.model.player.Player;
import nro.network.Message;
import nro.network.Session;
import nro.server.LogServer;

public class TaskService {

    private static final class InstanceHolder {
        private static final TaskService instance = new TaskService();
    }

    public static TaskService getInstance() {
        return InstanceHolder.instance;
    }

    public void sendTaskMain(Player player) {
        try (Message msg = new Message(40)) {

            player.sendMessage(msg);
        } catch (Exception e) {
            LogServer.LogException("Error sendTaskMain: " + e.getMessage());
        }
    }
}
