package com.db4o.db4ounit.common.io;


/**
 * @decaf.ignore
 */
public interface ThrowCondition {
	boolean shallThrow(long pos, int numBytes);
}
