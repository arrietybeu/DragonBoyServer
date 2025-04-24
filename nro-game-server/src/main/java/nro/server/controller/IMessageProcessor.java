
package nro.server.controller;

import nro.server.network.Message;
import nro.server.network.Session;

/**
 * @author Arriety
 */
@FunctionalInterface
public interface IMessageProcessor {
    void process(Session session, Message message);
}
