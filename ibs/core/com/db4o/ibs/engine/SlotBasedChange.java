/**
 * 
 */
package com.db4o.ibs.engine;

import com.db4o.ext.*;

public class SlotBasedChange {
	
	private final Db4oUUID _uuid;
	
	public SlotBasedChange(ObjectInfo objectInfo) {
		_uuid = objectInfo.getUUID();
	}
	
	public Db4oUUID uuid() {
		return _uuid;
	}

}