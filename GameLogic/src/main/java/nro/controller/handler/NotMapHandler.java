package nro.controller.handler;

import nro.network.Message;
import nro.network.Session;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.data.DataItem;
import nro.data.DataMap;
import nro.data.DataSkill;
import nro.service.PlayerService;
import nro.service.ResourceService;
import nro.service.Service;
import nro.server.LogServer;

@APacketHandler(-28)
public class NotMapHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            byte cmd = message.reader().readByte();
            switch (cmd) {
                case 2:
                    this.createChar(session, message);
                    break;
                case 6:
                    DataMap.updateMapData(session);
                    break;
                case 7:
                    DataSkill.SendDataSkill(session);
                    break;
                case 8:
                    DataItem.getInstance().sendDataItem(session);
                    break;
                case 10:
                    // TODO requestMaptemplate (Send tile map)
                    break;
                case 13:
                    ResourceService.getInstance().clientOk(session);
                    break;
                case 16:
                    // TODO sendCardInfo ( nạp cặc 2 chục ngàn bát môn độn giáp )
                    break;
                case 18:
                    // TODO changeName (Nhân vật cần đổi tên)
                    break;
                case 37:
                    // TODO activeAccProtect (kieu dang mat khau cap 2 ay)
                    break;
                case 39:
                    // TODO openLockAccProtect (mo khoa mat khau cap 2)
                    break;
                case 41:
                    // TODO clearAccProtect (xoas mat khau cap 2)
                    break;
                case 38:// case này không sửa dụng
                    // TODO updateActive
                    break;
                case 33:// case này không sử dụng
                    // TODO doConvertUpgrade
                    break;
                case 34:// case này không sửa dụng
                    // TODO inviteClanDun
                    break;
                case 40:// case này không sửa dụng
                    // TODO inputNumSplit
                    break;
                case 17:// case này không sửa dụng
                    // TODO clearTask
                    break;
                case 9:// case này không sửa dụng
                    // TODO requestSkill
                    break;
                default:
                    LogServer.LogException("Unknow command NotMapHandler: [" + cmd + "]");
                    break;
            }
        } catch (Exception e) {
            LogServer.LogException("Error NotMapHandler: " + e.getMessage());
        }
    }

    private void createChar(Session session, Message message) {
        try {
            var name = message.reader().readUTF().toLowerCase();
            var gender = message.reader().readByte();
            var hair = message.reader().readByte();

            PlayerService playerService = PlayerService.getInstance();

            String validationError = playerService.validateCharacterData(name, gender);
            if (validationError != null) {
                Service.dialogMessage(session, validationError);
                return;
            }

            var isCreated = playerService.handleCharacterCreation(session, name, gender, hair);

            if (isCreated) {

                PlayerService.getInstance().finishUpdateHandler(session);

                LogServer.DebugLogic("Tạo nhân vật thành công: " + name
                        + " (gender=" + gender
                        + ", hair=" + hair + ")");
            }
        } catch (Exception e) {
            LogServer.LogException("Error createChar: " + e.getMessage());
        }
    }

}
