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

	public void cascadeOnDelete(Object obj, ObjectContainer db) {
		if(obj != null && db != null) {
			try{
				objContainer = db;
				reflector = db.ext().reflector();
				set = new HashSet<Object>();
				cascadeDelete(obj);
				db.delete(obj);
				db.ext().purge(obj);
				set = null;
			} catch( Exception ex){// logging 
				db.rollback();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void cascadeDelete(Object obj) {
		ReflectClass clazz = ReflectHelper.getReflectClazz(obj);
		if(clazz != null){
			ReflectField [] fields = ReflectHelper.getDeclaredFieldsInHierarchy(clazz);
			for( ReflectField rField : fields) {
				Object value = rField.get(obj);
				if(value != null){
					ReflectClass type = rField.getFieldType();
					String name = type.getName();
					if(type.isPrimitive() || ReflectHelper.isWrapperClass(name))
						continue;
					else if(type.isArray()) {
						
						int length = reflector.array().getLength(value);
						for(int i = 0; i < length; i++) {
							Object indexObj = reflector.array().get(value, i);
							if( indexObj instanceof GenericObject){
								if(set.add(indexObj)){
									cascadeDelete(indexObj);
									objContainer.delete(indexObj);
									objContainer.ext().purge(indexObj);
								}
							}
						}
					} else if(type.isCollection()) {
						if( value instanceof Map){ // known bug for key if it's an Object
							Map<Object, Object> map = (Map)value;
							Iterator<Object> iterator = map.keySet().iterator(); 
							while (iterator.hasNext()) {
								Object key = iterator.next();
								Object valueObj = map.get(key);
								if( valueObj instanceof GenericObject){
									if(set.add(valueObj)){
										cascadeDelete(valueObj);
										objContainer.delete(valueObj);
										objContainer.ext().purge(valueObj);
									}
								}
							}
						}else {
							Collection<Object> collection = (Collection)value;
							Iterator<Object> iterator = collection.iterator();
							while(iterator.hasNext()) {
								Object colObj = iterator.next();
								if( colObj instanceof GenericObject){
									if(set.add(colObj)){
										cascadeDelete(colObj);
										objContainer.delete(colObj);
										objContainer.ext().purge(colObj);
									}
								}
							}
						}
					} else {
						if( value instanceof GenericObject){
							if(set.add(value)){
								cascadeDelete(value);
								objContainer.delete(value);
								objContainer.ext().purge(value);
							}
						}
					}
				}
			}
		}
		
	}

	public Object createNewValue(Object newValue, String fieldType) {
		Object obj = convert.getValue(fieldType, newValue.toString());
		return obj;
	}	
}
