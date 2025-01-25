package nro.service;

import nro.model.player.Player;
import nro.network.Message;
import nro.network.Session;

public class MapService {

    public static final class InstanceHolder {
        public static final MapService instance = new MapService();
    }

    public static MapService getInstance() {
        return InstanceHolder.instance;
    }

    public void sendMapInfo(Session session) {
    }

    /**
     *  {@link #clearMap}
     *
     * <pre>
     *     {@code
     *      GameCanvas.debug("SA65", 2);
     *      Char.isLockKey = true;
     *      Char.ischangingMap = true;
     *      GameScr.gI().timeStartMap = 0;
     *      GameScr.gI().timeLengthMap = 0;
     *      Char.myCharz().mobFocus = null;
     *      Char.myCharz().npcFocus = null;
     *      Char.myCharz().charFocus = null;
     *      Char.myCharz().itemFocus = null;
     *      Char.myCharz().focus.removeAllElements();
     *      Char.myCharz().testCharId = -9999;
     *      Char.myCharz().killCharId = -9999;
     *      GameCanvas.resetBg();
     *      GameScr.gI().resetButton();
     *      GameScr.gI().center = null;
     *      if (Effect.vEffData.size() > 15) {
     *          for (int num111 = 0; num111 < 5; num111++) {
     *              Effect.vEffData.removeElementAt(0);
     *          }
     *      }
     * </pre>
     */
    public static void clearMap(Player player) {
        try (Message message = new Message(-22)) {
            player.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
