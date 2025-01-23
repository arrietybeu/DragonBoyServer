package nro.controller;

import nro.network.Message;
import nro.network.Session;
import nro.consts.ConstsCmd;
import nro.controller.interfaces.IMessageProcessor;
import nro.server.LogServer;

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
                LogServer.LogException("Unknow command: [" + cmd + "] " + ConstsCmd.getMessageName(cmd));
            }
        } catch (Exception e) {
            LogServer.LogException("Error Message Processor: [" + cmd + "] " + e.getMessage());
        } finally {
            this.checkTimeDelay(session, cmd, st);
        }
    }

    private void checkTimeDelay(Session session, byte cmd, long time) {
        long executionTime = System.currentTimeMillis() - time;
        LogServer.DebugLogic("session " + session.getSessionInfo().getId() + " get message [" + cmd + "] - " + executionTime + " ms");
        if (executionTime > 5000) {
            LogServer.LogException("Session IP: " + session.getSessionInfo().getIp() + " time delay: [" + cmd + "] - " + executionTime + " ms");
        }
    }
}
