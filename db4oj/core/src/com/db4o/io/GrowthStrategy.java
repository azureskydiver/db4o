/* Copyright (C) 2009  db4objects Inc.   http://www.db4o.com */

package com.db4o.io;

/**
 * Strategy for file/byte array growth.
 */
public interface GrowthStrategy {
	/**
	 * @param curSize The current size of the entity
	 * @return The new size of the entity, must be bigger than curSize
	 */
	long newSize(long curSize);
}
