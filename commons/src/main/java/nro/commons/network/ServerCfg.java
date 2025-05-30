package nro.commons.network;

import java.net.InetSocketAddress;

public record ServerCfg(InetSocketAddress address, String clientDescription, ConnectionFactory connectionFactory) {

    public boolean isAnyLocalAddress() {
        return address.getAddress().isAnyLocalAddress();
    }

    public String getIP() {
        return address.getAddress().getHostAddress();
    }

    public int getPort() {
        return address.getPort();
    }

    public String getAddressInfo() {
        return (isAnyLocalAddress() ? "all addresses on port " : getIP() + ":") + getPort();
    }

}
