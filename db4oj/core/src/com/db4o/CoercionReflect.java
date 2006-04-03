package com.db4o;

import com.db4o.foundation.No4;

/**
 * @exclude
 */
public class CoercionReflect implements Coercion {
	public Object toSByte(Object obj) {
        if(obj instanceof Byte){
            return obj;
        }
        if(obj instanceof Number){
        	Number number=(Number)obj;
        	if(number.byteValue()==number.doubleValue()) {
                return new Byte((number).byteValue());
        	}
        }
        return No4.INSTANCE;
	}

	public Object toShort(Object obj) {
        if(obj instanceof Short){
            return obj;
        }
        if(obj instanceof Number){
        	Number number=(Number)obj;
        	if(number.shortValue()==number.doubleValue()) {
                return new Short((number).shortValue());
        	}
        }
        return No4.INSTANCE;
	}

	public Object toInt(Object obj) {
        if(obj instanceof Integer){
            return obj;
        }
        if(obj instanceof Number){
        	Number number=(Number)obj;
        	if(number.intValue()==number.doubleValue()) {
                return new Integer((number).intValue());
        	}
        }
        return No4.INSTANCE;
	}

	public Object toLong(Object obj) {
        if(obj instanceof Long){
            return obj;
        }
        if(obj instanceof Number){
        	Number number=(Number)obj;
        	if(number.longValue()==number.doubleValue()) {
                return new Long((number).longValue());
        	}
        }
        return No4.INSTANCE;
	}

	public Object toFloat(Object obj) {
        if(obj instanceof Float){
            return obj;
        }
        if(obj instanceof Number){
        	Number number=(Number)obj;
        	if(number.floatValue()==number.doubleValue()) {
                return new Float((number).floatValue());
        	}
        }
        return No4.INSTANCE;
	}

	public Object toDouble(Object obj) {
        if(obj instanceof Double){
            return obj;
        }
        if(obj instanceof Number){
        	Number number=(Number)obj;
            return new Double((number).doubleValue());
        }
        return No4.INSTANCE;
	}
}
