/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.jdk;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.db4o.Platform;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.Reflector;

/**
 * Reflection implementation for Field to map to JDK reflection.
 */
public class JdkField implements ReflectField {

    private final Reflector reflector;
	private final Field field;

    public JdkField(Reflector reflector, Field field) {
    	this.reflector = reflector;
        this.field = field;
    }

    public String getName() {
        return field.getName();
    }

    public ReflectClass getType() {
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
        Platform.setAccessible(field);
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
}
