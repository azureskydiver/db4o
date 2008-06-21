/**
 * 
 */
package com.db4o.ibs.engine;

import java.util.*;

import com.db4o.ext.*;

public class UpdateChange implements SlotBasedChange {

	private final ObjectInfo _object;
	private final List<FieldChange> _fields;

	public UpdateChange(ObjectInfo object, List<FieldChange> fields) {
		_object = object;
		_fields = Collections.unmodifiableList(fields);
	}

	public Db4oUUID uuid() {
		return _object.getUUID();
	}
	
	public List<FieldChange> fields() {
		return _fields;
	}
}