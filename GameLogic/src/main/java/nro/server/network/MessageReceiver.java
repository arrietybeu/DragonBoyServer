/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.server.network;

import java.io.DataInputStream;
import java.io.IOException;

import nro.server.config.ConfigServer;
import nro.server.manager.SessionManager;
import nro.server.system.LogServer;

/**
 * @author Arriety
 */
@SuppressWarnings("ALL")
public final class MessageReceiver {

    private static final int MAX_MESSAGE_SIZE = 1024;
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
                                this.session.getController().handleMessage(this.session, message);
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
        final boolean sendKeyComplete = session.getClientInfo().isSendKeyComplete();
        byte cmd = dis.readByte();
        if (sendKeyComplete) {
            cmd = readKey(cmd);
        }
        if (!checkMessageRateLimit(cmd)) {
            return null;
        }

        int payloadLength;
        if (sendKeyComplete) {
            byte b2 = dis.readByte();
            byte b3 = dis.readByte();
            payloadLength = ((readKey(b2) & 0xFF) << 8) | (readKey(b3) & 0xFF);
        } else {
            payloadLength = dis.readUnsignedShort();
        }

        if (payloadLength > MAX_MESSAGE_SIZE) {
            LogServer.LogException("Data too big cmd: " + cmd);
        }

        byte[] payload = new byte[payloadLength];
        dis.readFully(payload);

        session.getSessionInfo().recvByteCount += 5 + payloadLength;
        int totalBytes = session.getSessionInfo().recvByteCount + session.getSessionInfo().sendByteCount;
        session.getSessionInfo().strRecvByteCount = (totalBytes / 1024) + "." + ((totalBytes % 1024) / 102) + "Kb";

        if (sendKeyComplete) {
            for (int i = 0; i < payload.length; i++) {
                payload[i] = readKey(payload[i]);
            }
        }
        return new Message(cmd, payload);
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
