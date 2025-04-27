package nro.server.network.nro;

import lombok.Getter;

import java.nio.ByteBuffer;

public class NroCrypt {

    /**
     * check xem client đã write key chưa?
     * nếu chưa có key thì packet client-server không bị mã hóa.
     */
    private boolean isSendKey = false;

    /**
     * Dãy key XOR do client gửi lên từ packet -27
     */
    @Getter
    public static final byte[] sessionKey = "nKO/WctQ0AVLbpzfBkS6NevDYT8ourG5CRlmdjyJ72aswx4EPq1UgZhFMXH?3iI9".getBytes();
    private byte curReadIndex;
    private byte curWriteIndex;

    private static final int MAX_CORRUPT_PACKETS_BEFORE_DISCONNECT = 4;
    private int corruptPackets = 0;

    /**
     * call khi server nhận được key từ packet -27 của client.
     * Server lưu key để mã hóa/giải mã từ giờ về sau.
     */
    public void init() {
        this.curReadIndex = 0;
        this.curWriteIndex = 0;
        this.isSendKey = true;
    }

    /**
     * Check xem đã có key mã hóa chưa
     */
    public boolean isSendKey() {
        return isSendKey;
    }

    /**
     * Giải mã data nhận được từ client.
     * Chỉ thực thi nếu đã nhận được key.
     */
    public boolean decrypt(ByteBuffer buffer) {
        if (!isSendKey) {
            return true;
        }

        int pos = buffer.position();
        int limit = buffer.limit();

        try {
            for (int i = pos; i < limit; i++) {
                buffer.put(i, (byte) ((buffer.get(i) & 0xFF) ^ (sessionKey[curReadIndex] & 0xFF)));
                curReadIndex++;
                if (curReadIndex >= sessionKey.length) {
                    curReadIndex = 0;
                }
            }
            return true;
        } catch (Exception e) {
            corruptPackets++;
            if (corruptPackets >= MAX_CORRUPT_PACKETS_BEFORE_DISCONNECT) {
                System.out.println("Decrypt failed " + corruptPackets + " times, disconnecting " + this);
                return false;
            }
            System.out.println("[" + corruptPackets + "/" + MAX_CORRUPT_PACKETS_BEFORE_DISCONNECT + "] Decrypt fail, ignore packet");
            return true;
        }
    }


    /**
     * Mã hóa data gửi từ server đến client.
     * Gọi sau khi packet đã được viết đầy đủ vào buffer.
     */
    public void encrypt(ByteBuffer buffer) {
        if (!isSendKey || sessionKey == null) {
            return;
        }

        int pos = buffer.position();
        int limit = buffer.limit();

        for (int i = pos; i < limit; i++) {
            buffer.put(i, (byte) ((buffer.get(i) & 0xFF) ^ (sessionKey[curWriteIndex] & 0xFF)));
            curWriteIndex++;
            if (curWriteIndex >= sessionKey.length) {
                curWriteIndex = 0;
            }
        }
    }

    // encrypt một byte
    public byte encryptByte(byte b) {
        if (!isSendKey) {
            return b;
        }
        byte result = (byte) ((b & 0xFF) ^ (sessionKey[curWriteIndex] & 0xFF));
        curWriteIndex++;
        if (curWriteIndex >= sessionKey.length) {
            curWriteIndex = 0;
        }
        return result;
    }

    // decrypt một byte (nếu cần bên nhận)
    public byte decryptByte(byte b) {
        if (!isSendKey) {
            return b;
        }
        byte result = (byte) ((b & 0xFF) ^ (sessionKey[curReadIndex] & 0xFF));
        curReadIndex++;
        if (curReadIndex >= sessionKey.length) {
            curReadIndex = 0;
        }
        return result;
    }


    public void disable() {
        this.isSendKey = false;
//        this.sessionKey = null;
    }

    public void reset() {
        this.curReadIndex = 0;
        this.curWriteIndex = 0;
    }

}
