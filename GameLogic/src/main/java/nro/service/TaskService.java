package nro.service;

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

    public void sendTaskMain(Session session) {
        try (Message msg = new Message(40)) {

        } catch (Exception e) {
            LogServer.DebugLogic("");
        }
    }
}
