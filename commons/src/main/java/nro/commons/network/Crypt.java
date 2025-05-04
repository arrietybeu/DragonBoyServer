package nro.commons.network;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Crypt {

    /**
     * check xem client đã write key chưa?
     * nếu chưa có key thì packet client-server không bị mã hóa.
     * -- GETTER --
     * Check xem đã có key mã hóa chưa
     */
    private boolean isSendKey = false;

    /**
     * day key XOR do client gui len tu packet -27
     */
//    public static final byte[] sessionKey = "beo".getBytes();
    public static final byte[] sessionKey = {0};
    private byte curReadIndex;
    private byte curWriteIndex;

    private int corruptPackets = 0;

    // encrypt một byte
    public byte encryptByte(byte b) {
        byte result = (byte) ((b & 0xFF) ^ (sessionKey[curWriteIndex] & 0xFF));
        curWriteIndex++;
        if (curWriteIndex >= sessionKey.length) {
            curWriteIndex = 0;
        }
        return result;
    }

    public byte decryptByte(byte b) {
        byte num = curReadIndex;

        curReadIndex = (byte) (num + 1);

        byte result = (byte) ((sessionKey[num] & 0xFF) ^ (b & 0xFF));

        if (curReadIndex >= sessionKey.length) {
            curReadIndex %= (byte) sessionKey.length;
        }
//        System.out.println("Decrypt byte, curReadIndex=" + curReadIndex);
        return result;
    }

    public final void encrypt() {
        if (!isSendKey) {
            isSendKey = true;
        }
        // TODO packet encrypt
    }

    /**
     * call khi server nhận được key từ packet -27 của client.
     * Server lưu key để mã hóa/giải mã từ giờ về sau.
     */
    public void resetKeyIndex() {
        this.curReadIndex = 0;
        this.curWriteIndex = 0;
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
