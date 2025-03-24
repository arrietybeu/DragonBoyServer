package nro.controller;

import nro.server.network.Message;
import nro.server.network.Session;
import nro.consts.ConstsCmd;
import nro.server.system.LogServer;
import nro.service.core.system.ServerService;

/**
 * @author Arriety
 */
public class Controller {

    public void handleMessage(Session session, Message message) {
        long st = System.currentTimeMillis();
        byte command = message.getCommand();
        try {
            IMessageProcessor processor = MessageProcessorRegistry.getProcessor(command);
            if (processor != null) {
                processor.process(session, message);
            } else {
                var info = "Unknow command: [" + command + "] " + ConstsCmd.getMessageName(command);
                ServerService.dialogMessage(session, info);
                LogServer.LogException(info);
            }
        } catch (Exception e) {
            LogServer.LogException("Error Message Processor: [" + command + "] " + e.getMessage(), e);
        } finally {
            this.checkTimeDelay(session, command, st);
        }
    }

    private void checkTimeDelay(Session session, byte cmd, long time) {
        long executionTime = System.currentTimeMillis() - time;
//        LogServer.DebugLogic("session " + session.getSessionInfo().getId() + " get message [" + cmd + "] - " + executionTime + " ms");
        if (executionTime > 1000) {
            LogServer.LogWarning("Session IP: " + session.getSessionInfo().getIp() + " time delay: [" + cmd + "] - " + executionTime + " ms");
        }
    }
}
