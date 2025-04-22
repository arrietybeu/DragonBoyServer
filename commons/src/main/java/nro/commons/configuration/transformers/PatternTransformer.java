package nro.commons.configuration.transformers;

import nro.commons.configuration.TransformationTypeInfo;

import java.util.regex.Pattern;

/**
 * Automatic pattern transformer for RegExp resolving
 * 
 * @author Arriety
 */
public class PatternTransformer extends PropertyTransformer<Pattern> {

	/**
	 * Shared instance of this transformer. It's thread-safe so no need of multiple instances
	 */
	public static final PatternTransformer SHARED_INSTANCE = new PatternTransformer();

	@Override
	protected Pattern parseObject(String value, TransformationTypeInfo typeInfo) {
		return value.isEmpty() ? null : Pattern.compile(value);
	}
}
