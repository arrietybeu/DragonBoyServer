package nro.commons.network;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.concurrent.Executor;

public class AcceptDispatcher extends Dispatcher {

    public AcceptDispatcher(String name, Executor dcExecutor) throws IOException {
        super(name, dcExecutor);
    }

    @Override
    protected void closeConnection(AConnection<?> con) {
        throw new UnsupportedOperationException("This method should never be called!");
    }

    @Override
    protected void dispatch() throws IOException {
        int selected = selector.select();
        if (selected != 0) {
            Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
            while (selectedKeys.hasNext()) {
                SelectionKey key = selectedKeys.next();
                selectedKeys.remove();
                if (key.isValid()) {
                    accept(key);
                }
            }
        }
    }

}
