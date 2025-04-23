package nro.utils.test.network;

import nro.commons.network.AConnection;
import nro.commons.network.ConnectionFactory;
import nro.commons.network.Dispatcher;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class TestConnectionFactoryImpl implements ConnectionFactory {

    public TestConnectionFactoryImpl() {
    }

    @Override
    public AConnection<?> create(SocketChannel socket, Dispatcher dispatcher) throws IOException {
        return new TestConnection(socket, dispatcher);
    }
}
