/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.server.service.core.system;

import nro.consts.ConstsCmd;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.entity.Fashion;
import nro.server.service.model.template.entity.PartInfo;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.system.LogServer;
import nro.server.config.ConfigServer;
import nro.server.manager.GameNotifyManager;
import nro.server.manager.resources.PartManager;

import java.io.DataOutputStream;

/**
 * @author Arriety
 */

public class ServerService {

    private static final class InstanceHolder {
        private static final ServerService instance = new ServerService();
    }

    public static ServerService getInstance() {
        return InstanceHolder.instance;
    }

    public void sendNotLoginResponse(Session session) {
        try (Message msg = new Message(-29)) {
            msg.writer().writeByte(2);
            msg.writer().writeUTF(ConfigServer.LINK_IP_PORT + ":0,0,0");
            msg.writer().writeByte(1);
            session.doSendMessage(msg);
        } catch (Exception e) {
            LogServer.LogException("Error sending NotLogin response: " + e.getMessage(), e);
        }
    }

    public static void dialogMessage(Session session, String info) {
        try (Message message = new Message(-26)) {
            message.writer().writeUTF(info);
            session.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error dialogMessage: " + e.getMessage() + " - " + info, e);
        }
    }

    public static void sendLoginDe(Session session, short delay) {
        try (Message message = new Message(122)) {
            message.writer().writeShort(delay);
            session.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("Error sendLoginDe: " + ex.getMessage(), ex);
        }
    }

    public static void sendLoginFail(Session session) {
        try (Message message = new Message(-102)) {
            message.writer().writeByte(0);
            session.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendLoginFail: " + e.getMessage(), e);
        }
    }

    public static void initSelectChar(Session session) {
        try (Message msg = new Message(2)) {
            session.sendMessage(msg);
        } catch (Exception e) {
            LogServer.LogException("Error initSelectChar: " + e.getMessage(), e);
        }
    }

    public void createUserAo(Session session) {
        try (Message message = new Message(-101)) {
            // TODO load database id user

        } catch (Exception e) {
            LogServer.LogException("Error create User Ao: " + e.getMessage(), e);
        }
    }

    public void switchToRegisterScr(Session session) {
        try (Message message = new Message(42)) {
            message.writer().writeByte(0);
            session.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error switchToRegisterScr: " + e.getMessage(), e);
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
                Fashion fashion = player.getFashion();
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
            LogServer.LogException("Error in sendChatGlobal: " + e.getMessage(), e);
        }
    }

    public void sendGameNotify(Player player) {
        try (Message message = new Message(50)) {
            byte[] data = GameNotifyManager.getInstance().getDataNotify();
            message.writer().write(data);
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("Error in sendGameNotify: " + ex.getMessage(), ex);
        }
    }

    public void sendHideWaitDialog(Player player) {
        try (Message message = new Message(-99)) {
            message.writer().writeByte(-1);
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("Error in sendHideWaitDialog: " + ex.getMessage(), ex);
        }
    }

    public void sendPetFollow(Player player, int type, int typeSend) {
        try (Message message = new Message(31)) {
            DataOutputStream write = message.writer();
            write.writeInt(player.getId());
            switch (type) {
                case 0 -> write.writeByte(type);// stop follow
                case 1 -> {
                    write.writeByte(type);
                    write.writeShort(5000);// smallId
                    write.writeByte(0);// fimg
                }
            }
            if (typeSend == 0) {
                player.getArea().sendMessageToPlayersInArea(message, null);
            } else {
                player.sendMessage(message);
            }
        } catch (Exception ex) {
            LogServer.LogException("Error in sendStatusPet: " + ex.getMessage(), ex);
        }
    }

    public void showListTop(Player player, boolean isThachDau) {
        try (Message message = new Message(ConstsCmd.TOP)) {
            DataOutputStream write = message.writer();

            write.writeByte(isThachDau ? 1 : 0);
            write.writeUTF("Top 10");

        } catch (Exception ex) {
            LogServer.LogException("Error in showListTop: " + ex.getMessage(), ex);
        }
    }

    // message này dùng để send text ở dưới đáy game
    public void sendChatVip(Player player, String text) {
        try (Message message = new Message(ConstsCmd.CHAT_VIP)) {
            message.writer().writeUTF(text);
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("Error in sendChatVip: " + ex.getMessage(), ex);
        }
    }

}
