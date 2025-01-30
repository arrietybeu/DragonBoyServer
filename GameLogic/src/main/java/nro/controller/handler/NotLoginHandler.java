package nro.controller.handler;

import nro.data.DataMap;
import nro.network.Message;
import nro.network.Session;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.model.template.entity.UserInfo;
import nro.server.manager.UserManager;
import nro.repositories.account.AccountRepository;
import nro.server.Maintenance;
import nro.service.ResourceService;
import nro.service.Service;
import nro.server.manager.SessionManager;
import nro.server.LogServer;

@APacketHandler(-29)
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
                    ResourceService.getInstance().sendDataImageVersion(session);
                    Service.getInstance().sendNotLoginResponse(session);
                    break;
                default:
                    LogServer.LogException("Unknow command NotLoginHandler: [" + cmd + "]");
                    SessionManager.gI().kickSession(session);
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
            System.out.println("username: " + username + " password: " + password);
            var version = message.reader().readUTF();
            var type = message.reader().readByte();// type login

            session.getClientInfo().setVersion(version);
            UserInfo userInfo = new UserInfo(session, username, password);
            AccountRepository accountRepository = AccountRepository.getInstance();

            if (!accountRepository.checkAccount(userInfo)) {
                Service.sendLoginFail(userInfo.getSession());
                SessionManager.gI().kickSession(userInfo.getSession());
                // checkAccount return false => account not exist
                return;
            }

            ResourceService resourcesService = ResourceService.getInstance();
            resourcesService.sendDataBackgroundMapTemplate(session);// -31
            resourcesService.sendTileSetInfo(session); //-82
            resourcesService.sendSmallVersion(session);// -77
            resourcesService.sendBackgroundVersion(session);// -93
            DataMap.updateMapData(session);
            resourcesService.sendVersionDataGame(session);

            System.out.println("Login success");
            UserManager.getInstance().add(userInfo);
        } catch (Exception e) {
            LogServer.DebugLogic("Error login: " + e.getMessage());
            SessionManager.gI().kickSession(session);
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
        if (session.getClientInfo().isSetClientType()) {
//            LogServer.LogException("Error setClientType: isSetClientType");
            return;
        }
        try {
            this.readAndSetClientType(session, message);
        } catch (Exception e) {
            LogServer.LogException("Error setClientType: " + e.getMessage());
        }
    }

    private void readAndSetClientType(Session session, Message message) throws Exception {
        var clientInfo = session.getClientInfo();
        var typeClient = message.reader().readByte();
        var zoomLevel = message.reader().readByte();

        if (zoomLevel < 0 || zoomLevel > 4) {
            SessionManager.gI().kickSession(session);
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
//        LogServer.DebugLogic(platform);
        clientInfo.setPlatform(platform);
        clientInfo.setSetClientType(true);
    }

    private boolean activeLogin(Session session) {
        Maintenance maintenance = Maintenance.getInstance();

        if (maintenance.isMaintenance()) {
            Service.dialogMessage(session, "Server đang trong thời gian bảo trì, vui lòng quay lại sau");
            return false;
        }

        // TODO check message rate limit, if message count > 5,000 then kick session v..vvv
        return true;
    }

    private boolean isVersionValid(Session session) {
        String requiredVersion = "2.4.3";
        boolean isValid = requiredVersion.equals(session.getClientInfo().getVersion());
        if (!isValid) {
            Service.dialogMessage(session, "Phiên bản không tương thích. Vui lòng cập nhật phiên bản mới nhất.");
        }
        return isValid;
    }
}
