package nro.service.core.usage;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nro.server.system.LogServer;
import org.reflections.Reflections;

public class ItemHandlerRegistry {

    private static final Map<Integer, IUseItemHandler> handlerMap = new HashMap<>();

    public static void init(String packageName) {
        try {
            Reflections reflections = new Reflections(packageName);
            Set<Class<?>> classes = reflections.getTypesAnnotatedWith(AUseItemHandler.class);

            for (Class<?> cls : classes) {
                if (IUseItemHandler.class.isAssignableFrom(cls)) {
                    try {
                        AUseItemHandler annotation = cls.getAnnotation(AUseItemHandler.class);
                        Constructor<?> constructor = cls.getDeclaredConstructor();
                        IUseItemHandler handler = (IUseItemHandler) constructor.newInstance();

                        for (int type : annotation.value()) {
                            handlerMap.put(type, handler);
                            System.out.println("Registered item handler: " + cls.getName() + " for type: " + type);
                        }
                    } catch (Exception e) {
                        LogServer.LogException("Error registering item handler: " + cls.getName() + " - " + e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            LogServer.LogException("Error initializing ItemHandlerRegistry: " + e.getMessage(), e);
        }
    }

    public static IUseItemHandler getHandler(int type) {
        return handlerMap.get(type);
    }

}