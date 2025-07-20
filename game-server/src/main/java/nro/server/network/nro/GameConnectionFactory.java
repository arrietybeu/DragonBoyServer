package nro.server.network.nro;

import nro.commons.network.AConnection;
import nro.commons.network.ConnectionFactory;
import nro.commons.network.Dispatcher;
import nro.server.configs.network.NetworkConfig;
import nro.server.network.sequrity.FloodManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import nro.server.network.sequrity.FloodManager.Result;

public class GameConnectionFactory implements ConnectionFactory {

    private final Logger log = LoggerFactory.getLogger(GameConnectionFactory.class);

    private FloodManager floodAcceptor;

    public GameConnectionFactory() {
        if (NetworkConfig.ENABLE_FLOOD_CONNECTIONS) {
            floodAcceptor = new FloodManager(NetworkConfig.FLOOD_TICK,
                    new FloodManager.FloodFilter(NetworkConfig.FLOOD_SWARN,
                            NetworkConfig.FLOOD_SREJECT, // từ chối
                            NetworkConfig.FLOOD_STICK), // thời gian ngắn
                    new FloodManager.FloodFilter(NetworkConfig.FLOOD_LWARN, NetworkConfig.FLOOD_LREJECT,
                            NetworkConfig.FLOOD_LTICK)); // long period
        }
    }

    @Override
    public AConnection<?> create(SocketChannel socket, Dispatcher dispatcher) throws IOException {
        String host = socket.socket().getInetAddress().getHostAddress();
        if (NetworkConfig.ENABLE_FLOOD_CONNECTIONS) {

            final Result isFlooding = floodAcceptor.isFlooding(host, true);
            switch (isFlooding) {
                case REJECTED:// cho cút luon khỏi game
                    log.warn("Tu choi ket noi from IP: {}", host);
                    return null;
                case WARNED:// cảnh báo
                    log.warn("IP vuot qua gioi han gui packet: {}", host);
                    break;
                default:
                    log.info("IP: {} is not flooding", host);
                    break;
            }
        }

        log.info("Creating new connection for {}", host);
        return new NroConnection(socket, dispatcher);
    }

}
