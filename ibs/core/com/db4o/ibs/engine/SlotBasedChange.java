/**
 * 
 */
package com.db4o.ibs.engine;

import com.db4o.ext.*;

public class SlotBasedChange {
	
	private final ObjectInfo _object;
	
	public SlotBasedChange(ObjectInfo object) {
		_object = object;
	}
	
	public ObjectInfo object() {
		return _object;
	}

	public Db4oUUID uuid() {
		return _object.getUUID();
	}
}