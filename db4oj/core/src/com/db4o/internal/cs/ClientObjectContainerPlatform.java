/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.internal.cs;

/**
 * Platform specific defaults for ClientObjectContainer.
 */
public class ClientObjectContainerPlatform {

	/**
	 * The default {@link PrefetchingStrategy} for this platform.
	 * @return
	 */
	public static PrefetchingStrategy prefetchingStrategy() {
		return SingleMessagePrefetchingStrategy.INSTANCE;
	}

}
