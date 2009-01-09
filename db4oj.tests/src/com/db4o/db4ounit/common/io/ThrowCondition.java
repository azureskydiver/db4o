package com.db4o.db4ounit.common.io;


/**
 */
@decaf.Ignore
public interface ThrowCondition {
	boolean shallThrow(long pos, int numBytes);
}
