/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.config;

/**
 * Interface to configure the cache configurations.
 */
public interface CacheConfiguration {
	
    /**
     * configures the size of the slot cache to hold a number of
     * slots in the cache.
     * @param size the number of slots
     * @sharpen.property
     */
    public void slotCacheSize(int size);

}
