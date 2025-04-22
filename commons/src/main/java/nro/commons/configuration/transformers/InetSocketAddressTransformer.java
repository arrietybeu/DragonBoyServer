package nro.commons.configuration.transformers;


import nro.commons.configuration.TransformationTypeInfo;

import java.net.InetSocketAddress;
import java.net.URI;

/**
 * Transforms strings in the format {@code host:port} to an InetSocketAddress, where host can be a hostname or an IP address (IPv6 addresses must be
 * enclosed in square brackets).
 * 
 * @author Arriety
 */
public class InetSocketAddressTransformer extends PropertyTransformer<InetSocketAddress> {

	/**
	 * Shared instance of this transformer. It's thread-safe so no need of multiple instances
	 */
	public static final InetSocketAddressTransformer SHARED_INSTANCE = new InetSocketAddressTransformer();

	@Override
	protected InetSocketAddress parseObject(String value, TransformationTypeInfo typeInfo) throws Exception {
		URI uri = new URI(null, value, null, null, null);
		return new InetSocketAddress(uri.getHost(), uri.getPort());
	}
}
