package nro.server.network.nro;

import java.nio.ByteBuffer;

public class NroCrypt {

    /**
     * check xem client đã write key chưa?
     * nếu chưa có key thì packet client-server không bị mã hóa.
     */
    private boolean isEnabled = false;

    /**
     * Dãy key XOR do client gửi lên từ packet -27
     */
    private byte[] sessionKey;
    private byte curReadIndex;
    private byte curWriteIndex;

    private static final int MAX_CORRUPT_PACKETS_BEFORE_DISCONNECT = 4;
    private int corruptPackets = 0;

    /**
     * call khi server nhận được key từ packet -27 của client.
     * Server lưu key để mã hóa/giải mã từ giờ về sau.
     */
    public void init(byte[] key) {
        this.sessionKey = key;
        this.curReadIndex = 0;
        this.curWriteIndex = 0;
        this.isEnabled = true;
    }

    /**
     * Check xem đã có key mã hóa chưa
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Giải mã data nhận được từ client.
     * Chỉ thực thi nếu đã nhận được key.
     */
    public boolean decrypt(ByteBuffer buffer) {
        if (!isEnabled || sessionKey == null) {
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
        if (!isEnabled || sessionKey == null) {
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

    public void disable() {
        this.isEnabled = false;
        this.sessionKey = null;
    }

    public void reset() {
        this.curReadIndex = 0;
        this.curWriteIndex = 0;
    }

}
