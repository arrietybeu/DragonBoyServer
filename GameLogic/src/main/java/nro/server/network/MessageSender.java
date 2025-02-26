/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.server.network;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

import nro.server.config.ConfigServer;
import nro.server.manager.SessionManager;
import nro.server.LogServer;

/**
 * @author Arriety
 */
@SuppressWarnings("All")
public final class MessageSender {

    private DataOutputStream dos;
    private final Session session;

    public MessageSender(Session session, OutputStream os) {
        this.session = session;
        this.session.list_msg = new ConcurrentLinkedQueue<>();
        this.dos = new DataOutputStream(os);
    }

    public void startSend() {
        this.sendKeys();
        this.createSendThread();
    }

    public void sendKeys() {
        try (Message msg = new Message(-27)) {
            final byte[] keys = this.session.getSessionInfo().getKeys();

            msg.writer().writeByte(keys.length);
            for (int i = 0; i < keys.length; i++) {
                msg.writer().writeByte(keys[i]);
            }

            msg.writer().writeUTF(ConfigServer.IP);// write ip
            msg.writer().writeInt(ConfigServer.PORT);// write port
//            msg.writer().writeBoolean(false);// cai này vô cùng quan trọng nếu gửi true thì sẽ không vô được game =))
            msg.writer().writeByte(0);
            this.session.getClientInfo().setSendKeyComplete(true);
            this.doSendMessage(msg);
        } catch (Exception e) {
            LogServer.LogException("Error sendKeys: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createSendThread() {
        try {
            if (this.session.isExecutorServiceRunning()) {
                this.session.executorService.execute(() -> {
                    while (this.session.getSessionInfo().getConnect()) {
                        try (Message message = this.session.getListMessage()) {
                            if (message != null) {
                                this.doSendMessage(message);
                            } else {
                                try {
                                    Thread.sleep(10L);
                                } catch (InterruptedException ignored) {
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogServer.LogException("Error in send thread: " + e.getMessage());
                            SessionManager.getInstance().kickSession(this.session);
                        }
                    }
                });
            }
        } catch (Exception e) {
            LogServer.LogException("Error createSendThread: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void doSendMessage(Message msg) throws Exception {
        byte[] data = msg.getData();
        if (this.session.getClientInfo().isSendKeyComplete()) {
            byte b = writeKey(msg.getCommand());
            dos.writeByte(b);
        } else {
            dos.writeByte(msg.getCommand());
        }
        if (data != null) {
            int size = data.length;
//            if (size >= 65_535) {
//                LogServer.DebugLogic("BigData: " + msg.getCommand() + " size: " + size + " bytes");
//            }
            if (msg.getCommand() == -32// BACKGROUND_TEMPLATE
                    || msg.getCommand() == -66// GET_EFFDATA
                    || msg.getCommand() == 11// REQUEST_MOB_TEMPLATE
                    || msg.getCommand() == -67// REQUEST_ICON
                    || msg.getCommand() == -74// GET_IMAGE_SOURCE
                    || msg.getCommand() == -87// UPDATE_DATA
                    || msg.getCommand() == 66// GET_IMG_BY_NAME
                    || msg.getCommand() == 12// CMD_EXTRA_BIG
            ) {
                byte b = writeKey((byte) (size));
                dos.writeByte(b - 128);
                byte b2 = writeKey((byte) (size >> 8));
                dos.writeByte(b2 - 128);
                byte b3 = writeKey((byte) (size >> 16));
                dos.writeByte(b3 - 128);
            } else if (this.session.getClientInfo().isSendKeyComplete()) {
                int byte1 = writeKey((byte) (size >> 8));
                dos.writeByte(byte1);
                int byte2 = writeKey((byte) (size & 255));
                dos.writeByte(byte2);
            } else {
                dos.writeShort(size);
            }
            if (this.session.getClientInfo().isSendKeyComplete()) {
                for (int i = 0; i < data.length; i++) {
                    data[i] = writeKey(data[i]);
                }
            }
            dos.write(data);
        } else {
            dos.writeShort(0);
        }
        dos.flush();
    }

    private byte writeKey(byte b) {
//        byte i = (byte) ((this.session.getSessionInfo().getKeys()[session.getSessionInfo().curW++] & 255) ^ (b & 255));
//        if (session.getSessionInfo().curW >= this.session.getSessionInfo().getKeys().length) {
//            session.getSessionInfo().curW %= (byte) this.session.getSessionInfo().getKeys().length;
//        }
//        return i;
        var curW = session.getSessionInfo().curW;
        var keys = session.getSessionInfo().getKeys();
        final byte i = (byte) ((keys[curW++] & 0xFF) ^ (b & 0xFF));
        if (curW >= keys.length) {
            curW %= keys.length;
        }
        return i;
    }

    public void close() {
        try {
            if (this.dos != null) {
                this.dos.close();
            }
            this.dos = null;
        } catch (Exception e) {
            LogServer.LogException("Error close: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // phương thức này dùng để gửi dữ liệu cho client
//    public void doSendMessage(Message msg) throws Exception {
//        byte[] data = msg.getData();
//        if (this.session.getSendKey()) {
//            byte value = this.writeKey(msg.getCommand());
//            this.dos.write(value);
//        } else {
//            this.dos.write(msg.getCommand());
//        }
//        if (data != null) {
//            int num = data.length;
//            if (msg.getCommand() == -32
//                    || msg.getCommand() == -66
//                    || msg.getCommand() == 11
//                    || msg.getCommand() == -67
//                    || msg.getCommand() == -74
//                    || msg.getCommand() == -87
//                    || msg.getCommand() == 66
//                    || msg.getCommand() == 12) {
//                this.dos.writeByte(this.writeKey((byte) (num)) - 128);
//                this.dos.writeByte(writeKey((byte) (num >> 8)) - 128);
//                this.dos.writeByte(writeKey((byte) (num >> 16)) - 128);
//            }
//
//            if (this.session.getSendKey()) {
//                int num2 = writeKey((byte) (num >> 8));
//                this.dos.write((byte) num2);
//                int num3 = writeKey((byte) (num & 0xFF));// 0xFF = 255
//                this.dos.write((byte) num3);
//            } else {
//                this.dos.write((short) num);
//            }
//            if (this.session.getSendKey()) {
//                for (int i = 0; i < data.length; i++) {
//                    byte value2 = writeKey(data[i]);
//                    this.dos.write(value2);
//                }
//            } else {
//                this.dos.write((short) 0);
//            }
//        } else {
//            if (this.session.getSendKey()) {
//                int num4 = 0;
//                int num5 = writeKey((byte) (num4 >> 8));
//                dos.write((byte) num5);
//                int num6 = writeKey((byte) (num4 & 0xFF));
//                dos.write((byte) num6);
//            } else {
//                dos.write((short) 0);
//            }
//        }
//        System.out.println("send msg: " + msg.getCommand() + " " + data.length);
//        this.dos.write(data);
//        this.dos.flush();
//    }


}
