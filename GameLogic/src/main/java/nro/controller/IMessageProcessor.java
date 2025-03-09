
package nro.controller;

import nro.server.network.Message;
import nro.server.network.Session;

/**
 * @author Arriety
 */
public interface IMessageProcessor {
    void process(Session session, Message message);
}
