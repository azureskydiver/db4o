package com.db4o.marshall;

/**
 * this interface is passed to first class type handlers (non embedded ones).
 */
public interface FirstClassReadContext extends ReadContext {

	Object persistentObject();
}
