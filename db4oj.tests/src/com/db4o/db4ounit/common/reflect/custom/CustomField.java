package com.db4o.db4ounit.common.reflect.custom;

import com.db4o.foundation.*;
import com.db4o.reflect.*;

public class CustomField implements ReflectField {

	// fields must be public so test works on less capable runtimes
	public CustomClassRepository _repository;
	public int _index;
	public String _name;
	public Class _type;

	public CustomField(CustomClassRepository repository, int index, String name, Class type) {
		_repository = repository;
		_index = index;
		_name = name;
		_type = type;
	}

	public Object get(Object onObject) {
		logMethodCall("get", onObject);
		return fieldValues(onObject)[_index];
	}

	private Object[] fieldValues(Object onObject) {
		return ((PersistentEntry)onObject).fieldValues;
	}

	public ReflectClass getFieldType() {
		logMethodCall("getFieldType");
		return _repository.reflectClass(_type);
	}

	public String getName() {
		return _name;
	}

	public Object indexEntry(Object orig) {
		logMethodCall("indexEntry", orig);
		throw new NotImplementedException();
	}

	public ReflectClass indexType() {
		logMethodCall("indexType");
		throw new NotImplementedException();
	}

	public boolean isPublic() {
		return true;
	}

	public boolean isStatic() {
		return false;
	}

	public boolean isTransient() {
		return false;
	}

	public void set(Object onObject, Object value) {
		logMethodCall("set", onObject, value);
		fieldValues(onObject)[_index] = value;
	}

	public void setAccessible() {
	}
	
	public String toString() {
		return "CustomField(" + _index + ", " + _name + ", " + _type.getName() + ")";
	}
	
	private void logMethodCall(String methodName) {
		Logger.logMethodCall(this, methodName);
	}
	
	private void logMethodCall(String methodName, Object arg) {
		Logger.logMethodCall(this, methodName, arg);
	}
	
	private void logMethodCall(String methodName, Object arg1, Object arg2) {
		Logger.logMethodCall(this, methodName, arg1, arg2);
	}
}
