package nro.controller;

import nro.network.Message;
import nro.network.Session;
import nro.consts.ConstsCmd;
import nro.server.LogServer;
import nro.service.Service;

/**
 * @author Arriety
 */
public class Controller {

    private final MessageProcessorRegistry factory = new MessageProcessorRegistry();

    public Controller() {
        //scan package
        this.factory.init("nro.controller.handler");
    }

    public void onMessage(Session session, Message msg) {
        long st = System.currentTimeMillis();
        byte cmd = msg.getCommand();
        try {
            IMessageProcessor processor = this.factory.getProcessor(cmd);
            if (processor != null) {
                processor.process(session, msg);
            } else {
                var info = "Unknow command: [" + cmd + "] " + ConstsCmd.getMessageName(cmd);
                Service.dialogMessage(session, info);
                LogServer.LogException(info);
            }
        } catch (Exception e) {
            LogServer.LogException("Error Message Processor: [" + cmd + "] " + e.getMessage());
        } finally {
            this.checkTimeDelay(session, cmd, st);
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
