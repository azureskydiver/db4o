/**
 * 
 */
package com.db4o.ibs.engine;

import com.db4o.internal.*;

public class FieldChange {

	private final FieldMetadata _field;
	
	private final Object _currentValue;

	public FieldChange(FieldMetadata field, Object currentValue) {
		_field = field;
		_currentValue = currentValue;
	}
	
	public FieldMetadata field() {
		return _field;
	}
	
	public Object currentValue() {
		return _currentValue;
	}
}