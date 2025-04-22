package nro.commons.network;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface ConnectionFactory {

    AConnection<?> create(SocketChannel socket, Dispatcher dispatcher) throws IOException;

}
