package nro.controller.handler;

import nro.server.network.Message;
import nro.server.network.Session;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.service.model.template.entity.UserInfo;
import nro.server.manager.UserManager;
import nro.service.repositories.account.AccountRepository;
import nro.server.system.Maintenance;
import nro.service.core.system.ResourceService;
import nro.service.core.system.ServerService;
import nro.server.manager.SessionManager;
import nro.server.system.LogServer;

@APacketHandler(-29)
@SuppressWarnings("unused")
public class NotLoginHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            byte cmd = message.reader().readByte();
            switch (cmd) {
                case 0:// login
                    this.login(session, message);
                    break;
                case 1:// create user ảo
                    this.requestRegister(message);
                    break;
                case 2:
                    this.setClientType(session, message);
                    ResourceService.getInstance().sendDataImageVersion(session);// -111
                    ServerService.getInstance().sendNotLoginResponse(session);// -29
                    break;
                default:
                    LogServer.LogException("Unknow command NotLoginHandler: [" + cmd + "]");
                    SessionManager.getInstance().kickSession(session);
                    break;
            }
        } catch (Exception e) {
            LogServer.LogException("Error NotLoginHandler: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void login(Session session, Message message) {
        if (!this.activeLogin(session)) {
            return;
        }
        try {
            var username = message.reader().readUTF().toLowerCase();
            var password = message.reader().readUTF().toLowerCase();
            var version = message.reader().readUTF();
            var type = message.reader().readByte();// status login

            session.getClientInfo().setVersion(version);
            UserInfo userInfo = new UserInfo(session, username, password);

            AccountRepository accountRepository = AccountRepository.getInstance();

            if (accountRepository.handleCheckUserNameOnline(userInfo)) {
                return;
            }

            if (UserManager.getInstance().checkUserNameLogin(username)) {
                userInfo.getSession().getSessionInfo().constLogin++;
                ServerService.sendLoginFail(userInfo.getSession());
                SessionManager.getInstance().kickSession(session);
                return;
            }

            if (!accountRepository.handleLogin(userInfo)) {
                userInfo.getSession().getSessionInfo().constLogin++;
                ServerService.sendLoginFail(userInfo.getSession());
                return;
            }

            if (accountRepository.checkLogin(userInfo)) {
                userInfo.getSession().getSessionInfo().constLogin++;
                ServerService.sendLoginFail(userInfo.getSession());
                return;
            }

            accountRepository.handleCheckTimeLogout(userInfo, () -> {
                session.getSessionInfo().constLogin = 0;
                UserManager.getInstance().add(userInfo);
                session.getSessionInfo().setLogin(true);
                ResourceService.getInstance().sendResourcesLogin(session);
            });

        } catch (Exception e) {
            LogServer.LogException("Error login: " + e.getMessage());
            SessionManager.getInstance().kickSession(session);
        }
    }

    private void requestRegister(Message message) {
        try {
            var username = message.reader().readUTF();
            var password = message.reader().readUTF();
            System.out.println("username: " + username + " password: " + password);
            // TODO read user aor
            if (!username.isEmpty()) {
                var aor = message.reader().readUTF();
                var password2 = message.reader().readUTF();
                System.out.println("aor: " + aor + " password2: " + password2);
            }

        } catch (Exception e) {
            LogServer.DebugLogic("Error requestRegister: " + e.getMessage());
        }
    }

    /**
     * typeClient ? 4 : isPC
     * typeClient ? 5 : IphoneVersionApp
     * typeClient ? 6 : isWindowsPhone
     */

    private void setClientType(Session session, Message message) {
        try {
            var clientInfo = session.getClientInfo();
            var typeClient = message.reader().readByte();
            var zoomLevel = message.reader().readByte();

            if (zoomLevel <= 0 || zoomLevel > 4) {
                SessionManager.getInstance().kickSession(session);
                throw new Exception("Error zoomLevel: " + zoomLevel);
            }

            clientInfo.setTypeClient(typeClient);
            clientInfo.setZoomLevel(zoomLevel);

            var is = message.reader().readBoolean();
            var w = message.reader().readInt();
            var h = message.reader().readInt();
            var isQwerty = message.reader().readBoolean();
            var isTouch = message.reader().readBoolean();
            String platform = message.reader().readUTF();
            // TODO write data info
            // LogServer.DebugLogic(platform);
            clientInfo.setPlatform(platform);
            clientInfo.setSetClientType(true);
        } catch (Exception e) {
            LogServer.LogException("Error setClientType: " + e.getMessage());
        }
    }

    private boolean activeLogin(Session session) {
        Maintenance maintenance = Maintenance.getInstance();

        if (maintenance.isMaintenance()) {
            ServerService.dialogMessage(session, "Server đang trong thời gian bảo trì, vui lòng quay lại sau");
            return false;
        }

        if (session.getSessionInfo().isLogin()) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        if (session.getSessionInfo().getBanUntil() > currentTime) {
            long remainingSeconds = (session.getSessionInfo().getBanUntil() - currentTime) / 1000;
            ServerService.dialogMessage(session,
                    "Bạn đã đăng nhập sai quá nhiều lần. Vui lòng đợi " + remainingSeconds + " giây để thử lại.");
            return false;
        }

        // TODO check message rate limit, if message count > 5,000 then kick session
        // v..vvv
        return true;
    }

    private boolean isVersionValid(Session session) {
        String requiredVersion = "2.4.3";
        boolean isValid = requiredVersion.equals(session.getClientInfo().getVersion());
        if (!isValid) {
            ServerService.dialogMessage(session, "Phiên bản không tương thích. Vui lòng cập nhật phiên bản mới nhất.");
        }
        return isValid;
    }
}
