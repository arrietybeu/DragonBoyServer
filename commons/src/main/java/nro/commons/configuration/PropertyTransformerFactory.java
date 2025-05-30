package nro.commons.configuration;

import nro.commons.configuration.transformers.*;
import org.quartz.CronExpression;

import java.io.File;
import java.net.InetSocketAddress;
import java.time.ZoneId;
import java.util.Collection;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class PropertyTransformerFactory {

    /**
     * @param targetType The value type that strings should be transformed into.
     * @return A shared instance of the {@link PropertyTransformer} that can transform strings into given targetType.
     * @throws IllegalArgumentException If there's no transformer for given target class.
     */
    @SuppressWarnings("unchecked")
    public static <T> PropertyTransformer<T> getTransformer(Class<T> targetType) {
        if (Number.class.isAssignableFrom(NumberTransformer.toWrapper(targetType)))
            return (PropertyTransformer<T>) NumberTransformer.SHARED_INSTANCE;
        if (targetType == Boolean.class || targetType == Boolean.TYPE)
            return (PropertyTransformer<T>) BooleanTransformer.SHARED_INSTANCE;
        if (targetType == Character.class || targetType == Character.TYPE)
            return (PropertyTransformer<T>) CharTransformer.SHARED_INSTANCE;
        if (targetType == String.class)
            return (PropertyTransformer<T>) StringTransformer.SHARED_INSTANCE;
        if (targetType.isEnum())
            return (PropertyTransformer<T>) EnumTransformer.SHARED_INSTANCE;
        if (Collection.class.isAssignableFrom(targetType))
            return (PropertyTransformer<T>) CollectionTransformer.SHARED_INSTANCE;
        if (targetType.isArray())
            return (PropertyTransformer<T>) ArrayTransformer.SHARED_INSTANCE;
        if (targetType == File.class)
            return (PropertyTransformer<T>) FileTransformer.SHARED_INSTANCE;
        if (InetSocketAddress.class.isAssignableFrom(targetType))
            return (PropertyTransformer<T>) InetSocketAddressTransformer.SHARED_INSTANCE;
        if (targetType == Pattern.class)
            return (PropertyTransformer<T>) PatternTransformer.SHARED_INSTANCE;
        if (targetType == Class.class)
            return (PropertyTransformer<T>) ClassTransformer.SHARED_INSTANCE;
        if (TimeZone.class.isAssignableFrom(targetType))
            return (PropertyTransformer<T>) TimeZoneTransformer.SHARED_INSTANCE;
        if (ZoneId.class.isAssignableFrom(targetType))
            return (PropertyTransformer<T>) ZoneIdTransformer.SHARED_INSTANCE;
        if (targetType == CronExpression.class)
            return (PropertyTransformer<T>) CronExpressionTransformer.SHARED_INSTANCE;
        throw new IllegalArgumentException("Transformer for " + targetType.getSimpleName() + " is not implemented.");
    }
}
