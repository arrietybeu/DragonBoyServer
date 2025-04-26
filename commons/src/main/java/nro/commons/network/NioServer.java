package nro.commons.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.*;
import java.util.concurrent.Executor;

public class NioServer {

    private static final Logger log = LoggerFactory.getLogger(NioServer.class.getName());

    private final List<SelectionKey> serverChannelKeys = new ArrayList<>();

    private Dispatcher acceptDispatcher;

    private int currentReadWriteDispatcher;
    private Dispatcher[] readWriteDispatchers;

    private final int readWriteThreads;
    private final ServerCfg[] cfgs;

    public NioServer(int readWriteThreads, ServerCfg... cfgs) {
        this.readWriteThreads = readWriteThreads;
        this.cfgs = cfgs;
    }

    public void connect(Executor dcExecutor) {
        try {
            initDispatchers(readWriteThreads, dcExecutor);

            for (ServerCfg cfg : cfgs) {
                ServerSocketChannel serverChannel = ServerSocketChannel.open();
                serverChannel.configureBlocking(false);

                serverChannel.socket().bind(cfg.address());
                log.info("Listening on {} for {}", cfg.getAddressInfo(), cfg.clientDescription());

                SelectionKey acceptKey = acceptDispatcher.register(
                        serverChannel,
                        SelectionKey.OP_ACCEPT,
                        new Acceptor(cfg.connectionFactory(), this)
                );
                System.out.println("✅ Acceptor registered for: " + cfg.getAddressInfo());
                serverChannelKeys.add(acceptKey);
            }
        } catch (Exception e) {
            throw new Error("Could not open server socket: " + e.getMessage(), e);
        }
    }

    private void initDispatchers(int readWriteThreads, Executor dcExecutor) throws IOException {
        if (readWriteThreads < 1) {
            acceptDispatcher = new AcceptReadWriteDispatcher("AcceptReadWrite Dispatcher", dcExecutor);
            acceptDispatcher.start();
        } else {
            acceptDispatcher = new AcceptDispatcher("Accept Dispatcher", dcExecutor);
            acceptDispatcher.start();

            readWriteDispatchers = new Dispatcher[readWriteThreads];
            for (int i = 0; i < readWriteDispatchers.length; i++) {
                readWriteDispatchers[i] = new AcceptReadWriteDispatcher("ReadWrite-" + i + " Dispatcher", dcExecutor);
                readWriteDispatchers[i].start();
            }
        }
    }

    public final Dispatcher getReadWriteDispatcher() {
        if (readWriteDispatchers == null)
            return acceptDispatcher;

        if (readWriteDispatchers.length == 1)
            return readWriteDispatchers[0];

        if (currentReadWriteDispatcher >= readWriteDispatchers.length)
            currentReadWriteDispatcher = 0;
        return readWriteDispatchers[currentReadWriteDispatcher++];
    }

    private Set<AConnection<?>> findAllConnections() {
        Set<AConnection<?>> activeConnections = new HashSet<>();
        if (readWriteDispatchers != null) {
            for (Dispatcher d : readWriteDispatchers)
                for (SelectionKey key : d.selector().keys()) {
                    if (key.attachment() instanceof AConnection<?> connection) {
                        activeConnections.add(connection);
                    }
                }
        }
        for (SelectionKey key : acceptDispatcher.selector().keys()) {
            if (key.attachment() instanceof AConnection<?> connection) {
                activeConnections.add(connection);
            }
        }
        return activeConnections;
    }

    private boolean isAnyConnectionClosePending(Collection<AConnection<?>> connections) {
        return connections.stream().anyMatch(AConnection::isPendingClose);
    }

    public final void shutdown() {
        log.info("Closing ServerChannels...");
        serverChannelKeys.forEach(SelectionKey::cancel);
        log.info("ServerChannels closed.");

        // find active connections once, at this point new ones cannot be added anymore
        Set<AConnection<?>> activeConnections = findAllConnections();
        if (!activeConnections.isEmpty()) {
            log.info("\tClosing " + activeConnections.size() + " connections...");

            activeConnections.forEach(AConnection::onServerClose);

            long timeout = System.currentTimeMillis() + 5000;
            while (isAnyConnectionClosePending(activeConnections)) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
                if (System.currentTimeMillis() > timeout) {
                    activeConnections.removeIf(AConnection::isClosed);
                    log.info("\tForcing " + activeConnections.size() + " connections to disconnect...");
                    activeConnections.forEach(AConnection::close);
                    break;
                }
            }
            activeConnections.removeIf(AConnection::isClosed);
            log.info("\tActive connections left: " + activeConnections.size());
        }
    }
}
