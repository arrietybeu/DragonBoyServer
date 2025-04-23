package nro.commons.network.packet;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class BasePacket {

    private int command;

    /**
     * Constructs a new packet.<br>
     * If this constructor is used, then setOpcode() must be used just after it.
     */
    protected BasePacket() {
    }

    /**
     * Constructs a new packet with specified id.
     *
     * @param command Id of packet
     */
    protected BasePacket(int command) {
        this.command = command;
    }

    /**
     * Returns packet name.
     * <p/>
     * Actually packet name is a simple name of the underlying class.
     *
     * @return packet name
     * @see Class#getSimpleName()
     */
    public final String getPacketName() {
        return getClass().getSimpleName();
    }

    protected int getOpCodeZeroPadding() {
        return 3;
    }

    public String toFormattedPacketNameString() {
        return toFormattedPacketNameString(getOpCodeZeroPadding(), getCommand(), getPacketName());
    }

    public static String toFormattedPacketNameString(int zeroPadding, int opcode, String packetName) {
        return String.format("[%0" + zeroPadding + "d] %s", opcode, packetName);
    }

    /**
     * Returns string representation of this packet based on opCode and name.
     *
     * @return packet type string
     * @see #toFormattedPacketNameString
     */
    @Override
    public String toString() {
        return toFormattedPacketNameString();
    }
}
