package com.db4o.db4ounit.common.reflect.custom;

import com.db4o.foundation.*;
import com.db4o.reflect.*;

public class CustomClass implements ReflectClass {

	// fields must be public so test works on less capable runtimes
	public CustomClassRepository _repository;
	public String _name;
	public CustomField[] _fields;

	public CustomClass(CustomClassRepository repository, String name, String[] fieldNames,
			Class[] fieldTypes) {
		_repository = repository;
		_name = name;
		_fields = createFields(fieldNames, fieldTypes);
	}

	private CustomField[] createFields(String[] fieldNames, Class[] fieldTypes) {
		CustomField[] fields = new CustomField[fieldNames.length];
		for (int i=0; i<fieldNames.length; ++i) {
			fields[i] = new CustomField(_repository, i, fieldNames[i], fieldTypes[i]);
		}
		return fields;
	}

	public ReflectClass getComponentType() {
		throw new NotImplementedException();
	}

	public ReflectConstructor[] getDeclaredConstructors() {
		throw new NotImplementedException();
	}

	public ReflectField getDeclaredField(String name) {
		throw new NotImplementedException();
	}

	public ReflectField[] getDeclaredFields() {
		return _fields;
	}

	public ReflectClass getDelegate() {
		return this;
	}

	public ReflectMethod getMethod(String methodName,
			ReflectClass[] paramClasses) {
		return null;
	}

	public String getName() {
		return _name;
	}

	public ReflectClass getSuperclass() {
		return null;
//		return _repository.reflectClass(java.lang.Object.class);
	}

	public boolean isAbstract() {
		return false;
	}

	public boolean isArray() {
		return false;
	}

	public boolean isAssignableFrom(ReflectClass type) {
		return equals(type);
	}

	public boolean isCollection() {
		return false;
	}

	public boolean isInstance(Object obj) {
		throw new NotImplementedException();
	}

	public boolean isInterface() {
		return false;
	}

	public boolean isPrimitive() {
		return false;
	}

	public boolean isSecondClass() {
		return false;
	}

	public Object newInstance() {
		return new PersistentEntry(_name, null, new Object[_fields.length]);
	}

	public Reflector reflector() {
		throw new NotImplementedException();
	}

	public boolean skipConstructor(boolean flag, boolean testConstructor) {
		return false;
	}

	public Object[] toArray(Object obj) {
		throw new NotImplementedException();
	}

	public void useConstructor(ReflectConstructor constructor, Object[] params) {
		throw new NotImplementedException();
	}

}
