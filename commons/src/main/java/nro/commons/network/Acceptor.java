package nro.commons.network;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor {

    private final ConnectionFactory factory;

    private final NioServer nioServer;

    public Acceptor(ConnectionFactory factory, NioServer nioServer) {
        this.factory = factory;
        this.nioServer = nioServer;
    }

    public final void accept(SelectionKey key) throws IOException {
        // For an accept to be pending the channel must be a server socket channel
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        // Accept the connection and make it non-blocking
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.socket().setSoLinger(true, 10);
        socketChannel.socket().setTcpNoDelay(true);

        String ip = socketChannel.socket().getInetAddress().getHostAddress();
        Dispatcher dispatcher = nioServer.getReadWriteDispatcher();
        AConnection<?> con = factory.create(socketChannel, dispatcher);

        if (con == null) {
            socketChannel.close();
            return;
        }

        // register
        dispatcher.register(socketChannel, SelectionKey.OP_READ, con);
        // notify initialized :)
        con.initialized();
    }

}
