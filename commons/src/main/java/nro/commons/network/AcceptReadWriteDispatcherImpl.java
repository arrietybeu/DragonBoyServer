package nro.commons.network;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Bộ điều phối chấp nhận, đọc và ghi dữ liệu (triển khai cụ thể)
 *
 * @author Arriety
 */
public class AcceptReadWriteDispatcherImpl extends Dispatcher {

    private final List<AConnection<?>> pendingClose = new ArrayList<>();

    public AcceptReadWriteDispatcherImpl(String name, Executor dcExecutor) throws IOException {
        super(name, dcExecutor);
    }

    @Override
    protected void closeConnection(AConnection<?> con) {
        synchronized (pendingClose) {
            pendingClose.add(con);
        }
    }

    @Override
    public void dispatch() throws IOException {
        int selected = selector.select();
        if (selected != 0) {
            for (Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator(); selectedKeys.hasNext(); ) {
                SelectionKey key = selectedKeys.next();
                selectedKeys.remove();

                if (!key.isValid()) continue;

                // Check what event is available and deal with it
                switch (key.readyOps()) {
                    case SelectionKey.OP_ACCEPT -> accept(key);
                    case SelectionKey.OP_READ -> read(key);
                    case SelectionKey.OP_WRITE -> write(key);
                    case SelectionKey.OP_READ | SelectionKey.OP_WRITE -> {
                        read(key);
                        if (key.isValid()) write(key);
                    }
                }
            }
        }
        processPendingClose();
    }

    private void processPendingClose() {
        if (pendingClose.isEmpty()) return;
        synchronized (pendingClose) {
            long nowMillis = System.currentTimeMillis();
            for (Iterator<AConnection<?>> iterator = pendingClose.iterator();
                 iterator.hasNext(); ) {
                AConnection<?> connection = iterator.next();
                if (connection.getSendMsgQueue().isEmpty() || !connection.isConnected() || nowMillis > connection.pendingCloseUntilMillis) {
                    closeConnectionImpl(connection);
                    iterator.remove();
                }
            }
        }
    }
}
