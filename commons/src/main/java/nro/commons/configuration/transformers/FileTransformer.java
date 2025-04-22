package nro.commons.configuration.transformers;

import nro.commons.configuration.TransformationTypeInfo;

import java.io.File;

/**
 * Transforms string to file by creating new file instance. It's not checked if file exists.
 * 
 * @author Arriety
 */
public class FileTransformer extends PropertyTransformer<File> {

	/**
	 * Shared instance of this transformer. It's thread-safe so no need of multiple instances
	 */
	public static final FileTransformer SHARED_INSTANCE = new FileTransformer();

	@Override
	protected File parseObject(String value, TransformationTypeInfo typeInfo) {
		return new File(value);
	}
}
