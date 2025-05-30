package nro.commons.configuration.transformers;

import nro.commons.configuration.TransformationTypeInfo;

import java.util.TimeZone;

public class TimeZoneTransformer extends PropertyTransformer<TimeZone> {

	/**
	 * Shared instance of this transformer, it's thread safe so no need to create multiple instances
	 */
	public static final TimeZoneTransformer SHARED_INSTANCE = new TimeZoneTransformer();

	@Override
	protected TimeZone parseObject(String value, TransformationTypeInfo typeInfo) {
		return value.isEmpty() ? TimeZone.getDefault() : TimeZone.getTimeZone(value);
	}
}
