package nro.service.core.usage;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nro.server.LogServer;
import org.reflections.Reflections;

public class ItemHandlerRegistry {

    private static final Map<Integer, IItemHandler> handlerMap = new HashMap<>();

    public static void init(String packageName) {
        try {
            Reflections reflections = new Reflections(packageName);
            Set<Class<?>> classes = reflections.getTypesAnnotatedWith(AItemHandler.class);

            for (Class<?> cls : classes) {
                if (IItemHandler.class.isAssignableFrom(cls)) {
                    try {
                        AItemHandler annotation = cls.getAnnotation(AItemHandler.class);
                        Constructor<?> constructor = cls.getDeclaredConstructor();
                        IItemHandler handler = (IItemHandler) constructor.newInstance();

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

    public static IItemHandler getHandler(int type) {
        return handlerMap.get(type);
    }

}