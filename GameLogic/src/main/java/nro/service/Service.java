/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.service;

import nro.model.player.Player;
import nro.model.player.PlayerFashion;
import nro.model.template.entity.PartInfo;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.LogServer;
import nro.server.config.ConfigServer;
import nro.server.manager.GameNotifyManager;
import nro.server.manager.resources.PartManager;

import java.io.DataOutputStream;

/**
 * @author Arriety
 */

public class Service {

    private static final class InstanceHolder {
        private static final Service instance = new Service();
    }

    public static Service getInstance() {
        return InstanceHolder.instance;
    }

    public void sendNotLoginResponse(Session session) {
        try (Message msg = new Message(-29)) {
            msg.writer().writeByte(2);
            msg.writer().writeUTF(ConfigServer.LINK_IP_PORT + ":0,0,0");
            msg.writer().writeByte(1);
            session.doSendMessage(msg);
        } catch (Exception e) {
            LogServer.DebugLogic("Error sending NotLogin response: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void dialogMessage(Session session, String info) {
        try (Message message = new Message(-26)) {
            message.writer().writeUTF(info);
            session.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error dialogMessage: " + e.getMessage() + " - " + info);
            e.printStackTrace();
        }
    }

    public static void sendLoginDe(Session session, short delay) {
        try (Message message = new Message(122)) {
            message.writer().writeShort(delay);
            session.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("Error sendLoginDe: " + ex.getMessage());
        }
    }

    public static void sendLoginFail(Session session) {
        try (Message message = new Message(-102)) {
            message.writer().writeByte(0);
            session.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendLoginFail: " + e.getMessage());
        }
    }

    public static void initSelectChar(Session session) {
        try (Message msg = new Message(2)) {
            session.sendMessage(msg);
        } catch (Exception e) {
            LogServer.LogException("Error initSelectChar: " + e.getMessage());
        }
    }

    public void createUserAo(Session session) {
        try (Message message = new Message(-101)) {
            // TODO load database id user

        } catch (Exception e) {
            LogServer.LogException("Error create User Ao: " + e.getMessage());
        }
    }

    public void switchToRegisterScr(Session session) {
        try (Message message = new Message(42)) {
            message.writer().writeByte(0);
            session.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error switchToRegisterScr: " + e.getMessage());
        }
    }

    public void sendChatGlobal(Session session, Player player, String text, boolean isChatServer) {
        final int MESSAGE_CHAT_GLOBAL = 92;
        try (Message message = new Message(MESSAGE_CHAT_GLOBAL)) {
            DataOutputStream out = message.writer();

            String name = (player != null) ? player.getName() : "";
            out.writeUTF(name);
            out.writeUTF(text);

            if (player != null) {
                PlayerFashion fashion = player.getPlayerFashion();
                PartInfo part = PartManager.getInstance().findPartById(fashion.getHead());

                out.writeInt(player.getId());
                out.writeShort(fashion.getHead());
                out.writeShort(part.getIcon(0));
                out.writeShort(fashion.getBody());
                out.writeShort(fashion.getFlagBag());
                out.writeShort(fashion.getLeg());
                out.writeByte(isChatServer ? 0 : 1);
            }
            session.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error in sendChatGlobal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendGameNotify(Player player) {
        try (Message message = new Message(50)) {
            byte[] data = GameNotifyManager.getInstance().getDataNotify();
            message.writer().write(data);
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("Error in sendGameNotify: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void sendHideWaitDialog(Player player) {
        try (Message message = new Message(-99)) {
            message.writer().writeByte(-1);
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("Error in sendHideWaitDialog: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
