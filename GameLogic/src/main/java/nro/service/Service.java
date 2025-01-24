/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.service;

import nro.network.Message;
import nro.network.Session;
import nro.server.LogServer;
import nro.server.config.ConfigServer;

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
}
