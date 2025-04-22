package nro.commons.configuration.transformers;

import nro.commons.configuration.TransformationTypeInfo;

public class EnumTransformer extends PropertyTransformer<Enum<?>> {

	/**
	 * Shared instance of this transformer. It's thread-safe so no need of multiple instances
	 */
	public static final EnumTransformer SHARED_INSTANCE = new EnumTransformer();

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Enum<?> parseObject(String value, TransformationTypeInfo typeInfo) {
		return value.isEmpty() ? null : Enum.valueOf((Class<? extends Enum>) typeInfo.getType(), value);
	}
}
