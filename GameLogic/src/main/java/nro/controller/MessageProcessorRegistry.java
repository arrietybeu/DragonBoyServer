/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.controller;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nro.server.system.LogServer;
import org.reflections.Reflections;

/**
 * @author Arriety
 */
public class MessageProcessorRegistry {

    private static final Map<Byte, IMessageProcessor> processorMap = new HashMap<>();

    public static void init(String packageName) {
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

                            registerProcessor(annotation.value(), processor);

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

    private static void registerProcessor(byte code, IMessageProcessor processor) {
        processorMap.put(code, processor);
    }

    public static IMessageProcessor getProcessor(byte code) {
        return processorMap.get(code);
    }

}
