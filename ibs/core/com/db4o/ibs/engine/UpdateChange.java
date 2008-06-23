/**
 * 
 */
package com.db4o.ibs.engine;

import java.util.*;

import com.db4o.ext.*;

public class UpdateChange extends SlotBasedChange {

	private final List<FieldChange> _fields;

	public UpdateChange(ObjectInfo object, List<FieldChange> fields) {
		super(object);
		_fields = Collections.unmodifiableList(fields);
	}

	public List<FieldChange> fields() {
		return _fields;
	}
}