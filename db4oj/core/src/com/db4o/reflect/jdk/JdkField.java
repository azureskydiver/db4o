/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.jdk;

import java.lang.reflect.*;

import com.db4o.*;
import com.db4o.reflect.*;

/**
 * Reflection implementation for Field to map to JDK reflection.
 */
public class JdkField implements ReflectField {

    private final Reflector reflector;
	private final Field field;

    public JdkField(Reflector reflector_, Field field_) {
    	reflector = reflector_;
        field = field_;
    }

    public String getName() {
        return field.getName();
    }

    public ReflectClass getFieldType() {
        return reflector.forClass(field.getType());
    }

    public boolean isPublic() {
        return Modifier.isPublic(field.getModifiers());
    }

    public boolean isStatic() {
        return Modifier.isStatic(field.getModifiers());
    }

    public boolean isTransient() {
        return Modifier.isTransient(field.getModifiers());
    }

    public void setAccessible() {
        Platform4.setAccessible(field);
    }

    public Object get(Object onObject) {
        try {
            return field.get(onObject);
        } catch (Exception e) {
            return null;
        }
    }

    public void set(Object onObject, Object attribute) {
        try {
            field.set(onObject, attribute);
        } catch (Exception e) {
            // FIXME: This doesn't work when in its own package...
//            if(Debug.atHome){
//                e.printStackTrace();
//            }
        }
    }

	public Object indexEntry(Object orig) {
		return orig;
	}

	public ReflectClass indexType() {
		return getFieldType();
	}
}
