package com.db4o.reflect.db;

import com.db4o.ext.*;
import com.db4o.internal.io.*;
import com.db4o.reflect.*;

public class DBField implements ReflectField {
	private DBClass type;
	private FieldReader field;

	public DBField(DBClass type,FieldReader field) {
		this.type=type;
		this.field=field;
	}

	public Object get(Object onObject) {
		return ((DBObject)onObject).get(field.name());
	}

	public String getName() {
		return field.name();
	}

	public ReflectClass getType() {
		return type;
	}

	public boolean isPublic() {
		return false;
	}

	public boolean isStatic() {
		return false;
	}

	public boolean isTransient() {
		return false;
	}

	public void set(Object onObject, Object value) {
		((DBObject)onObject).set(field.name(),value);
	}

	public void setAccessible() {
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		DBField other = (DBField) obj;
		return type.equals(other.type)&&field.equals(other.field);
	}
	
	public int hashCode() {
		return type.hashCode()*29+field.hashCode();
	}
}
