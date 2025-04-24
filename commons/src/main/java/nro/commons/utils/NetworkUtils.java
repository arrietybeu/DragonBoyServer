package nro.commons.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class NetworkUtils {

    public static InetAddress findLocalIPv4() {
        try {
            return NetworkInterface.networkInterfaces().flatMap(NetworkInterface::inetAddresses).filter(
                            inetAddress -> inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress() && !inetAddress.isMulticastAddress()).findFirst()
                    .orElse(null);
        } catch (SocketException ignored) {
            return null;
        }
    }

    /**
     * @param buffer
     * @param start  position to start read from
     * @param end    end position (exclusive)
     * @return Formatted hex string of the buffers data.
     */
    public static String toHex(ByteBuffer buffer, int start, int end) {
        StringBuilder result = new StringBuilder();
        for (int i = start, bytes = 0; i < end; bytes++) {
            if (bytes % 16 == 0) {
                if (!result.isEmpty())
                    result.append("\n");
                result.append(String.format("%04X: ", bytes));
            }

            int b = buffer.get(i) & 0xff;
            result.append(String.format("%02X ", b));

            int bytesInRow = (bytes % 16) + 1;
            if (++i == buffer.capacity() || bytesInRow == 16) {
                for (int j = bytesInRow; j <= 16; j++)
                    result.append("   ");
                toText(buffer, result, i - bytesInRow, i);
            }
        }
        return result.toString();
    }

    /**
     * Writes bytes from the <tt>buffer</tt>'s startIndex (inclusive) to the endIndex (exclusive) as string representable characters into <tt>result</tt>:
     * <ul>
     * <li>if byte represents char from partition 0x1F to 0x80 (which are normal ascii chars) then it's put into buffer as it is</li>
     * <li>otherwise dot is put into buffer</li>
     * </ul>
     *
     * @param buffer
     * @param result
     * @param startIndex
     * @param endIndex   exclusive
     */
    private static void toText(ByteBuffer buffer, StringBuilder result, int startIndex, int endIndex) {
        for (int charPos = startIndex; charPos < endIndex; charPos++) {
            int c = buffer.get(charPos) & 0xFF; // unsigned byte
            if (c > 0x1f && c < 0x80)
                result.append((char) c);
            else
                result.append('.');
        }
    }
}
