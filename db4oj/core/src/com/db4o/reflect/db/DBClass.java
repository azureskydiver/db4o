package com.db4o.reflect.db;

import com.db4o.ext.*;
import com.db4o.reflect.*;

public class DBClass implements ReflectClass {
	private ExtObjectContainer db;
	private LeanStoredClass storedClass;
	
	public DBClass(ExtObjectContainer db,LeanStoredClass storedClass) {
		this.db=db;
		this.storedClass=storedClass;
	}

	public ReflectClass getComponentType() {
		return this;
	}

	public ReflectConstructor[] getDeclaredConstructors() {
		return null;
	}

	public ReflectField[] getDeclaredFields() {
		LeanStoredField[] fields=storedClass.storedFields();
		DBField[] reflectFields=new DBField[fields.length];
		for (int idx = 0; idx < fields.length; idx++) {
			DBClass type=new DBClass(db,db.leanStoredClassByID(fields[idx].typeID()));
			reflectFields[idx]=new DBField(type,fields[idx]);
		}
		return reflectFields;
	}

	public ReflectField getDeclaredField(String name) {
		LeanStoredField[] fields=storedClass.storedFields();
		for (int idx = 0; idx < fields.length; idx++) {
			if(fields[idx].name().equals(name)) {
				DBClass type=new DBClass(db,db.leanStoredClassByID(fields[idx].typeID()));
				return new DBField(type,fields[idx]);
			}
		}
		return null;
	}

	public ReflectMethod getMethod(String methodName,
			ReflectClass[] paramClasses) {
		return null;
	}

	public String getName() {
		return storedClass.name();
	}

	public ReflectClass getSuperclass() {
		return null;
	}

	public boolean isAbstract() {
		return false;
	}

	public boolean isArray() {
		return false;
	}

	public boolean isAssignableFrom(ReflectClass type) {
		return false;
	}

	public boolean isInstance(Object obj) {
		if(!(obj instanceof DBObject)) {
			return false;
		}
		return this.equals(((DBObject)obj).type());
	}

	public boolean isInterface() {
		return false;
	}

	public boolean isPrimitive() {
		return false;
	}

	public Object newInstance() {
		return new DBObject(this);
	}

	public boolean skipConstructor(boolean flag) {
		return false;
	}

	public void useConstructor(ReflectConstructor constructor, Object[] params) {
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		DBClass other = (DBClass) obj;
		return db.equals(other.db)&&storedClass.equals(other.storedClass);
	}
	
	public int hashCode() {
		return db.hashCode()*29+storedClass.hashCode();
	}
}
