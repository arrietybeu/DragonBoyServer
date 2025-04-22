package nro.server.network.nro;

import nro.commons.network.ConnectionFactory;
import nro.commons.network.Dispatcher;
import nro.server.config.network.NetworkConfig;
import nro.server.network.sequrity.FloodManager;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class GameConnectionFactoryImpl implements ConnectionFactory {

    // TODO chua lam xong
    private FloodManager floodAcceptor;

    public GameConnectionFactoryImpl() {
//        if (NetworkConfig.ENABLE_FLOOD_CONNECTIONS) {
//            floodAcceptor = new FloodManager(NetworkConfig.Flood_Tick, new FloodManager.FloodFilter(NetworkConfig.Flood_SWARN, NetworkConfig.Flood_SReject, NetworkConfig.Flood_STick), // short period
//                    new FloodManager.FloodFilter(NetworkConfig.Flood_LWARN, NetworkConfig.Flood_LReject, NetworkConfig.Flood_LTick)); // long period
//        }
    }

    @Override
    public NroConnection create(SocketChannel socket, Dispatcher dispatcher) throws IOException {
//        if (NetworkConfig.ENABLE_FLOOD_CONNECTIONS) {
//            String host = socket.socket().getInetAddress().getHostAddress();
//            final FloodManager.Result isFlooding = floodAcceptor.isFlooding(host, true);
//            switch (isFlooding) {
//                case REJECTED:
//                    log.warn("Rejected connection from " + host);
//                    return null;
//                case WARNED:
//                    log.warn("Connection over warn limit from " + host);
//                    break;
//            }
//        }

        return new NroConnection(socket, dispatcher);
    }

}
