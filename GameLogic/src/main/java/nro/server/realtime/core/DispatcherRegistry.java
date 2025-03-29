package nro.server.realtime.core;

import nro.server.system.LogServer;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Set;

public class DispatcherRegistry {

    public static void startAllDispatchers(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> dispatcherClasses = reflections.getTypesAnnotatedWith(GameDispatcher.class);

        for (Class<?> clazz : dispatcherClasses) {
            try {
                Method getInstance = clazz.getDeclaredMethod("getInstance");
                Object instance = getInstance.invoke(null);

                Method startMethod = clazz.getMethod("start");
                startMethod.invoke(instance);

//                LogServer.LogInit("Started dispatcher: " + clazz.getSimpleName());
            } catch (Exception e) {
                LogServer.LogException("Error starting dispatcher: " + clazz.getSimpleName(), e);
            }
        }
    }
}
