package nro.commons.configuration.transformers;


import nro.commons.configuration.TransformationTypeInfo;

public class StringTransformer extends PropertyTransformer<String> {

	/**
	 * Shared instance of this transformer. It's thread-safe so no need of multiple instances
	 */
	public static final StringTransformer SHARED_INSTANCE = new StringTransformer();

	@Override
	protected String parseObject(String value, TransformationTypeInfo typeInfo) {
		return value;
	}
}
