/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.controller.interfaces;

import nro.network.Message;
import nro.network.Session;

/**
 *
 * @author Arriety
 */
public interface IMessageProcessor {

    void process(Session session, Message message);

}
