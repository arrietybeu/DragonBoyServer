package nro.commons.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

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
}
