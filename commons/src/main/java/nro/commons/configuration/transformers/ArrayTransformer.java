package nro.commons.configuration.transformers;

import nro.commons.configuration.PropertyTransformerFactory;
import nro.commons.configuration.TransformationTypeInfo;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Creates an <code>Array</code> containing the comma separated items.
 * 
 * @author Arriety
 */
public class ArrayTransformer extends CommaSeparatedValueTransformer<Object> {

	public static final ArrayTransformer SHARED_INSTANCE = new ArrayTransformer();

	@Override
	protected Object parseObject(List<String> values, TransformationTypeInfo typeInfo) {
		Class<?> arrayType = typeInfo.getType().getComponentType();

		Object array = Array.newInstance(arrayType, values.size());

		if (!values.isEmpty()) {
			PropertyTransformer<?> pt = PropertyTransformerFactory.getTransformer(arrayType);
			for (int i = 0; i < values.size(); i++)
				Array.set(array, i, pt.transform(values.get(i), arrayType));
		}

		return array;
	}
}
