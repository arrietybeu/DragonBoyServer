/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.controller;

import nro.server.network.Message;
import nro.server.network.Session;

/**
 * @author Arriety
 */
public interface IMessageProcessor {

    void process(Session session, Message message);

}
