/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.qlin;

import com.db4o.foundation.*;
import com.db4o.reflect.*;

/**
 * Internal implementation for ExpressionMapper and QLinSupport 
 * mapping of expressions to fields.
 * @exclude
 */
public class Prototype <T> {
	
	private final IdentityHashtable4 _fieldsByIdentity = new IdentityHashtable4();
	
	private final Hashtable4 _fieldsByIntId = new Hashtable4();
	
	private final T _object;
	
	private int intIdGenerator;
	
	private Prototype(T obj){
		_object = obj;
	}
	
	public T object(){
		return _object;
	}

	public static <T> Prototype<T> forClass(ReflectClass claxx, boolean ignoreTransient){
		T obj = (T) claxx.newInstance();
		final Prototype<T> prototype = new Prototype<T>(obj);
		prototype.init(claxx, ignoreTransient);
		return prototype;
	}

	private void init(final ReflectClass claxx, final boolean ignoreTransient) {
		Reflections.forEachField(claxx, new Procedure4<ReflectField>() {
			public void apply(ReflectField field) {
				if(field.isStatic()){
					return;
				}
				if(ignoreTransient && field.isTransient()){
					return;
				}
				ReflectClass fieldType = field.getFieldType();
				
				IntegerConverter converter = integerConverterforClassName(claxx.reflector(), fieldType.getName());
				if(converter != null){
					int id = ++intIdGenerator;
					Object integerRepresentation = converter.fromInteger(id);
					field.set(_object, integerRepresentation);
					_fieldsByIntId.put(id, Pair.of(integerRepresentation, field.getName()));
					return;
				}
				if(! fieldType.isPrimitive()){
					Object identityInstance = fieldType.newInstance();
					if(identityInstance == null){
						return;
					}
					field.set(_object, identityInstance);
					_fieldsByIdentity.put(identityInstance, field.getName());
					return;
				}
			}
		});
	}
	
	public String backingFieldPath(Reflector reflector, Object expression) {
		if(expression == null){
			return null;
		}
		ReflectClass claxx = reflector.forObject(expression);
		if(claxx == null){
			return null;
		}
		IntegerConverter converter = integerConverterforClassName(reflector, claxx.getName());
		if(converter != null){
			final Pair<?, String> mappedPair = (Pair)_fieldsByIntId.get(converter.toInteger(expression));
			if(mappedPair == null){
				return null;
			}
			Object mappedRepresenation = mappedPair.first;
			if(! mappedRepresenation.equals(expression)){
				return null;
			}
			return mappedPair.second;
		}
		if(claxx.isPrimitive()){
			return null;
		}
		return (String)_fieldsByIdentity.get(expression);
	}
	
	private static IntegerConverter integerConverterforClassName(Reflector reflector, String className){
		if(_integerConverters == null){
			_integerConverters = new Hashtable4();
			IntegerConverter[] converters = new IntegerConverter[]{
				new IntegerConverter(){
					public String primitiveName() {return int.class.getName();}
					public Object fromInteger(int i) {return new Integer(i);}
				},
				new IntegerConverter(){
					public String primitiveName() {return long.class.getName();}
					public Object fromInteger(int i) {return new Long(i);}
				},
				new IntegerConverter(){
					public String primitiveName() {return double.class.getName();}
					public Object fromInteger(int i) {return new Double(i);}
				},
				new IntegerConverter(){
					public String primitiveName() {return float.class.getName();}
					public Object fromInteger(int i) {return new Float(i);}
				},
				new IntegerConverter(){
					public String primitiveName() {return byte.class.getName();}
					public Object fromInteger(int i) {return new Byte((byte)i);}
				},
				new IntegerConverter(){
					public String primitiveName() {return char.class.getName();}
					public Object fromInteger(int i) {return new Character((char)i);}
				},
				new IntegerConverter(){
					public String primitiveName() {return short.class.getName();}
					public Object fromInteger(int i) {return new Short((short)i);}
				},
				new IntegerConverter(){
					public String primitiveName() {return String.class.getName();}
					public Object fromInteger(int i) {return STRING_IDENTIFIER + i;}
					@Override
					public int toInteger(Object obj) {
						if(! (obj instanceof String)){
							return -1;
						}
						String str = (String)obj;
						if(str.length() < STRING_IDENTIFIER.length()){
							return -1;
						}
						return Integer.parseInt(str.substring(STRING_IDENTIFIER.length()));
					}
				},
			};
			for (IntegerConverter converter : converters) {
				_integerConverters.put(converter.primitiveName(), converter);
				if(! converter.primitiveName().equals(converter.wrapperName(reflector))){
					_integerConverters.put(converter.wrapperName(reflector), converter);
				}
			}
		}
		return (IntegerConverter) _integerConverters.get(className);
	}
	
	private static Hashtable4 _integerConverters;
	
	private static abstract class IntegerConverter <T> {
		
		public String wrapperName(Reflector reflector){
			return reflector.forObject(fromInteger(1)).getName();
		}
		
		public abstract String primitiveName();
		
		public abstract T fromInteger(int i);

		public int toInteger(T obj){
			return Integer.parseInt(obj.toString());
		}
		
	}
	
	private static final String STRING_IDENTIFIER = "QLinIdentity"; 

}
