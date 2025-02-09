/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.network;

import java.io.DataInputStream;
import java.io.IOException;

import nro.server.config.ConfigServer;
import nro.server.manager.SessionManager;
import nro.server.LogServer;

/**
 * @author Arriety
 */
@SuppressWarnings("ALL")
public final class MessageReceiver {

    private final Session session;
    private DataInputStream dis;
    private long lastMessageTimestamp;
    private int messageCount = 0;


    public MessageReceiver(Session session, DataInputStream dis) {
        this.session = session;
        this.dis = dis;
    }

    /**
     * trách rò rỉ tài nguyên thì nên check message != null
     * chứ đừng check message == null rồi return
     * vì nếu message == null rồi continue dữ liệu còn sót lại trong input stream
     * vẫn sẽ không thoát khỏi khối try catch này dẫn đến try with resource không thể close được
     */

    public void startReadMessage() {
        try {
            this.session.executorService.execute(() -> {
                while (this.session.getSessionInfo() != null && this.session.getSessionInfo().getConnect()) {
                    try (Message message = this.readMessage()) {
                        switch (message.getCommand()) {
                            case -27:
                                this.session.sendSessionKey();
                                break;
                            default:
                                this.session.getController().onMessage(this.session, message);
                                this.session.getClientInfo().updateLastActiveTime();
                                break;
                        }
                    } catch (Exception e) {
                        SessionManager.getInstance().kickSession(session);
                        return;
                    }
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException ex) {
                    }
                }
                SessionManager.getInstance().kickSession(session);
            });
        } catch (Exception e) {
            LogServer.LogException("Error startReadMessage: " + e.getMessage());
        }
    }

    private Message readMessage() throws IOException {
        int num;
        byte cmd = this.dis.readByte();
        if (this.session.getClientInfo().isSendKeyComplete()) {
            cmd = this.readKey(cmd);
        }

        if (!checkMessageRateLimit(cmd)) {
            return null;
        }

        if (this.session.getClientInfo().isSendKeyComplete()) {
            byte b2 = this.dis.readByte();
            byte b3 = this.dis.readByte();
            num = ((readKey(b2) & 0xFF) << 8) | (readKey(b3) & 0xFF);
        } else {
            num = this.dis.readUnsignedShort();
        }

        if (num > 1024) {
            throw new IOException("Data too big");
        }

        byte[] array = new byte[num];
        byte[] src = new byte[num];

        this.dis.readFully(src);
        System.arraycopy(src, 0, array, 0, num);
        this.session.getSessionInfo().recvByteCount += 5 + num;

        int num4 = this.session.getSessionInfo().recvByteCount + this.session.getSessionInfo().sendByteCount;
        this.session.getSessionInfo().strRecvByteCount = (num4 / 1024) + "." + (num4 % 1024 / 102) + "Kb";
//        LogServer.DebugLogic(this.session.getSessionInfo().strRecvByteCount);

        if (this.session.getClientInfo().isSendKeyComplete()) {
            for (int i = 0; i < array.length; i++) {
                array[i] = this.readKey(array[i]);
            }
        }
        return new Message(cmd, array);
    }

    private byte readKey(byte b) {
        byte[] array = this.session.getSessionInfo().getKeys();
        byte num = this.session.getSessionInfo().curR;
        this.session.getSessionInfo().curR = (byte) (num + 1);
        byte result = (byte) ((array[num] & 0xFF) ^ (b & 0xFF));
        if (this.session.getSessionInfo().curR >= this.session.getSessionInfo().getKeys().length) {
            this.session.getSessionInfo().curR %= (byte) this.session.getSessionInfo().getKeys().length;
        }
        return result;
    }

    private boolean checkMessageRateLimit(int msg) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastMessageTimestamp > 5000) {
            this.messageCount = 0;
            this.lastMessageTimestamp = currentTime;
        }
        this.messageCount++;
        if (this.messageCount > ConfigServer.MAX_MESSAGES_PER_5_SECONDS) {
            LogServer.LogException("Session id: " + session.getSessionInfo().getId() + " | Message count: " + this.messageCount + " | Command: " + msg);
            SessionManager.getInstance().kickSession(session);
            return false;
        }
        return true;
    }

    public void close() {
        try {
            if (this.dis != null) {
                this.dis.close();
            }
            this.dis = null;
        } catch (Exception e) {
            LogServer.LogException("Error close: " + e.getMessage());
        }
    }

}
