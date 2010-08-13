package com.db4o.omplus.datalayer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import com.db4o.ObjectContainer;
import com.db4o.omplus.datalayer.queryBuilder.QueryBuilderConstants;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.Reflector;
import com.db4o.reflect.generic.GenericObject;

public class ModifyObject {
	
	private final String VALUE = "value";
	private final String COUNT = "count";

	private ObjectContainer objContainer;
	private Reflector reflector;
	private HashSet<Object> set;
	private Converter convert = new Converter();
	
	// TODO: how to handle enums?
	public void updateValue(Object prev, Object value, String fieldType)
	{
		int type = convert.getType(fieldType);
		ReflectClass clz = ReflectHelper.getReflectClazz(prev);
		ReflectField field = ReflectHelper.getReflectField(clz, VALUE);
		Object obj = convert.getValue(fieldType, value.toString());
		switch (type){
		case QueryBuilderConstants.DATATYPE_STRING:
			char []charArr = ((String)obj).toCharArray();
			field.set(prev, charArr);
			ReflectField countF = ReflectHelper.getReflectField(clz,COUNT);
			countF.set(prev, charArr.length);
			break;
		case QueryBuilderConstants.DATATYPE_BYTE :
			field.set(prev, ((Byte)obj).byteValue());
			break;
		case QueryBuilderConstants.DATATYPE_SHORT:
			field.set(prev, ((Short)obj).shortValue());
			break;
		case QueryBuilderConstants.DATATYPE_CHARACTER:
			field.set(prev, ((Character)obj).charValue());
			break;
		case QueryBuilderConstants.DATATYPE_INT:
			field.set(prev, ((Integer)obj).intValue());
			break;
		case QueryBuilderConstants.DATATYPE_LONG:
			field.set(prev, ((Long)obj).longValue());
			break;
		case QueryBuilderConstants.DATATYPE_DOUBLE:
			field.set(prev, ((Double)obj).doubleValue());
			break;
		case QueryBuilderConstants.DATATYPE_FLOAT:
			field.set(prev, ((Float)obj).floatValue());
			break;
		case QueryBuilderConstants.DATATYPE_BOOLEAN:
			field.set(prev, ((Boolean)obj).booleanValue());
			break;
//		case QueryBuilderConstants.DATATYPE_DATE_TIME:

		}
	}
	
	public Object createNewValue(Object newValue, String fieldType) {
		Object obj = convert.getValue(fieldType, newValue.toString());
		return obj;
	}	
}
