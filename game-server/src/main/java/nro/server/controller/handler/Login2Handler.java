package nro.server.controller.handler;

import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.system.LogServer;
import nro.server.service.core.system.ServerService;

@APacketHandler(-101)
public class Login2Handler implements IMessageProcessor {

    /**
     * <p>
     * Khi người chơi ấn vào "chơi mới" server sẽ gửi event cho client
     * </p>
     *
     * <pre>
     *     {@code
     *     ServerService.getInstance().switchToRegisterScr(session); // người chơi tự tạo user
     *     }
     * </pre>
     *
     * <pre>
     *     {@code
     *      ServerService.getInstance().createUserAo(session);// máy chủ tự tạo user rồi cho player tạo nhân vật
     *     }
     * </pre>
     */

    @Override
    public void process(Session session, Message message) {
        try {
            var username = message.reader().readUTF();
            LogServer.DebugLogic("user -101: " + username);
            ServerService.getInstance().switchToRegisterScr(session);
//            ServerService.getInstance().createUserAo(session);
        } catch (Exception e) {
            LogServer.LogException("Error Login2Handler: " + e.getMessage());
        }
    }

}
