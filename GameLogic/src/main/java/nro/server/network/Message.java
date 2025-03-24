/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.server.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import lombok.Getter;
import nro.server.system.LogServer;

/**
 * @author Arriety
 */
public class Message implements AutoCloseable {

    @Getter
    private byte command;

    private ByteArrayOutputStream os;
    private DataOutputStream dos;
    private ByteArrayInputStream is;
    private DataInputStream dis;

    public Message(int command) {
        this((byte) command);
    }

    /**
     * Phương thức này đang set byte command cho message
     * constructor này sẽ là private để chuyển int sang byte ở constructor public
     *
     * @param command the command identifier for the message, as a byte.
     */
    private Message(byte command) {
        this.command = command;
        this.os = new ByteArrayOutputStream();
        this.dos = new DataOutputStream(os);
    }

    public Message() {
        this.os = new ByteArrayOutputStream();
        this.dos = new DataOutputStream(os);
    }

    public Message(byte command, byte[] data) {
        this.command = command;
        this.is = new ByteArrayInputStream(data);
        this.dis = new DataInputStream(is);
    }

    public DataInputStream reader() {
        return dis;
    }

    public DataOutputStream writer() {
        return this.dos;
    }

    public byte[] getData() {
        return this.os.toByteArray();
    }

    public int available() {
        try {
            return (this.is != null) ? this.is.available() : 0;
        } catch (Exception e) {
            LogServer.LogException("Lỗi trong available(): " + e.getMessage());
            return 0;
        }
    }

    /**
     * Cleans up all streams associated with the message.
     * Ensures that no resources are leaked by closing all streams.
     */

    public void cleanup() {
        try {
            if (this.os != null) {
                this.os.close();
            }
            if (this.dos != null) {
                this.dos.close();
            }
            if (this.is != null) {
                this.is.close();
            }
            if (this.dis != null) {
                this.dis.close();
            }
        } catch (Exception e) {
            LogServer.LogException("Error in cleanup: " + e.getMessage(), e);
        }
    }

    /**
     * Ghi đè phương thức {@code close()} của interface {@code AutoCloseable}.
     * Phương thức này được sửa dụng khi khởi tạo một {@code Message} trong một
     * block {@code try-with-resources}.
     */

    @Override
    public void close() {
        this.cleanup();
    }

    public void logPendingData() {
        if (this.os != null && this.os.size() > 0) {
            LogServer.DebugLogic(" OutputStream rò rỉ " + this.os.size() + " bytes of data.");
        }
        if (this.is != null && this.is.available() > 0) {
            LogServer.DebugLogic(" InputStream rò rỉ " + this.is.available() + " bytes of data.");
        }
    }
}
