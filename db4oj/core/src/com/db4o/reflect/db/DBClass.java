package com.db4o.reflect.db;

import com.db4o.ext.*;
import com.db4o.reflect.*;

public class DBClass implements ReflectClass {
    
    private final Reflector _reflector;
	private final ExtObjectContainer _db;
	private final LeanStoredClass _storedClass;
	
	public DBClass(Reflector reflector, ExtObjectContainer db,LeanStoredClass storedClass) {
        _reflector = reflector;
		_db=db;
		_storedClass=storedClass;
	}

	public ReflectClass getComponentType() {
		return this;
	}

	public ReflectConstructor[] getDeclaredConstructors() {
		return null;
	}

	public ReflectField[] getDeclaredFields() {
		LeanStoredField[] fields=_storedClass.storedFields();
		DBField[] reflectFields=new DBField[fields.length];
		for (int idx = 0; idx < fields.length; idx++) {
			DBClass type=new DBClass(reflector(),_db,_db.leanStoredClassByID(fields[idx].typeID()));
			reflectFields[idx]=new DBField(type,fields[idx]);
		}
		return reflectFields;
	}

	public ReflectField getDeclaredField(String name) {
		LeanStoredField[] fields=_storedClass.storedFields();
		for (int idx = 0; idx < fields.length; idx++) {
			if(fields[idx].name().equals(name)) {
				DBClass type=new DBClass(reflector(),_db,_db.leanStoredClassByID(fields[idx].typeID()));
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
		return _storedClass.name();
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

    public Reflector reflector() {
        return _reflector;
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
		return _db.equals(other._db)&&_storedClass.equals(other._storedClass);
	}
	
	public int hashCode() {
		return _db.hashCode()*29+_storedClass.hashCode();
	}

}
