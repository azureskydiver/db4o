/**
 * 
 */
package com.db4o.ibs.engine;

import java.util.*;

import com.db4o.ext.*;

public class UpdateChange extends SlotBasedChange {

	private final List<FieldChange> _fieldChanges;

	public UpdateChange(ObjectInfo object, List<FieldChange> fieldChanges) {
		super(object);
		_fieldChanges = Collections.unmodifiableList(fieldChanges);
	}

	public List<FieldChange> fieldChanges() {
		return _fieldChanges;
	}
}