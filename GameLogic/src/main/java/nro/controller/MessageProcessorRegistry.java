/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.controller;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nro.server.LogServer;
import org.reflections.Reflections;

/**
 * @author Arriety
 */
public class MessageProcessorRegistry {

    private final Map<Byte, IMessageProcessor> processorMap = new HashMap<>();

    public void init(String packageName) {
        // khoi tao doi tuong Reflections de scan cac class trong package
        try {
            Reflections rf = new Reflections(packageName);

            // tim all class co gan annotation @APacketHandler
            Set<Class<?>> classes = rf.getTypesAnnotatedWith(APacketHandler.class);

            for (Class<?> cls : classes) {
                if (IMessageProcessor.class.isAssignableFrom(cls)) {
                    APacketHandler annotation = cls.getAnnotation(APacketHandler.class);
                    if (annotation != null) {
                        try {

                            Constructor<?> constructor = cls.getDeclaredConstructor();
                            IMessageProcessor processor = (IMessageProcessor) constructor.newInstance();

                            this.registerProcessor(annotation.value(), processor);

                        } catch (Exception e) {
                            LogServer.LogException("Loi ham init" + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogServer.LogException("Loi ham initialize" + e.getMessage(), e);
        }
    }

    private void registerProcessor(byte code, IMessageProcessor processor) {
        this.processorMap.put(code, processor);
    }

    public IMessageProcessor getProcessor(byte code) {
        return this.processorMap.get(code);
    }

}
