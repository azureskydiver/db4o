/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.qlin;

import com.db4o.foundation.*;
import com.db4o.reflect.*;


/**
 * creates a network of connected prototype objects, with
 * one prototype for each class, a singleton. 
 * Allows analyzing expressions called on prototype objects
 * to find the underlying field that delivers the return 
 * value. Passed expressions should not have side effects
 * on objects, otherwise the "singleton world" will change.<br><br> 
 *  
 * We were inspired for this approach when we saw that 
 * Thomas Mueller managed to map expressions to fields for 
 * his JaQu query interface, Kudos!
 * http://www.h2database.com/html/jaqu.html<br><br>
 * 
 * We took the idea a bit further and made it work for all
 * primitives except for boolean and we plan to also get 
 * deeper expressions, collections and interfaces working 
 * nicely.
 */
public class Prototypes {
	
	private final Reflector _reflector;
	
	private final Hashtable4 _prototypes = new Hashtable4();

	private final boolean _ignoreTransient;
	
	private final int _recursionDepth;
	
	public Prototypes(Reflector reflector, int recursionDepth, boolean ignoreTransient){
		_reflector = reflector;
		_recursionDepth = recursionDepth;
		_ignoreTransient = ignoreTransient;
	}
	
	/**
	 * returns a prototype object for a specific class.
	 */
	public <T> T forClass(Class<T> clazz){
		if(clazz == null){
			throw new PrototypesException("Class can not be null");
		}
		final String className = clazz.getName();
		Prototype<T> prototype = (Prototype) _prototypes.get(className);
		if(prototype != null){
			return prototype.object();
		}
		ReflectClass claxx = _reflector.forClass(clazz);
		if(claxx == null){
			throw new PrototypesException("Not found in the reflector: " + clazz);
		}
		prototype = new Prototype(claxx);
		_prototypes.put(className, prototype);
		return prototype.object();
	}
	
	/**
	 * analyzes the passed expression and tries to find the path to the 
	 * backing field that is accessed.
	 */
	public <T> Iterator4<String> backingFieldPath(Class<T> clazz, Object expression){
		Prototype prototype = (Prototype) _prototypes.get(clazz.getName());
		if(prototype == null){
			return null;
		}
		return prototype.backingFieldPath(_reflector, expression);
	}
	
	private class Prototype <T> {
		
		private final IdentityHashtable4 _fieldsByIdentity = new IdentityHashtable4();
		
		private final Hashtable4 _fieldsByIntId = new Hashtable4();
		
		private final T _object;
		
		private int intIdGenerator;
		
		public Prototype(final ReflectClass claxx){
			_object = (T) claxx.newInstance();
			if(_object == null){
				throw new PrototypesException("Prototype could not be created for class " + claxx.getName());
			}
			analyze(_object, claxx, _recursionDepth, new Object[]{});
		}

		private void analyze(final Object object, final ReflectClass claxx, final int depth, final Object[] parentPath) {
			if(depth < 0){
				return;
			}
			Reflections.forEachField(claxx, new Procedure4<ReflectField>() {
				public void apply(ReflectField field) {
					if(field.isStatic()){
						return;
					}
					if(_ignoreTransient && field.isTransient()){
						return;
					}
					ReflectClass fieldType = field.getFieldType();
					Object[] path = Arrays4.append(parentPath, field.getName());
					IntegerConverter converter = integerConverterforClassName(claxx.reflector(), fieldType.getName());
					if(converter != null){
						int id = ++intIdGenerator;
						Object integerRepresentation = converter.fromInteger(id);
						try{
							field.set(object, integerRepresentation);
						} catch (Exception e){
							return;
						}
						_fieldsByIntId.put(id, new Pair(integerRepresentation, path));
						return;
					}
					if(! fieldType.isPrimitive()){
						Object identityInstance = fieldType.newInstance();
						if(identityInstance == null){
							return;
						}
						try{
							field.set(object, identityInstance);
						} catch (Exception e){
							return;
						}
						_fieldsByIdentity.put(identityInstance, path);
						analyze(identityInstance, claxx, depth - 1, path);
					}
				}
			});
		}

		public T object(){
			return _object;
		}
		
		public Iterator4 <String> backingFieldPath(Reflector reflector, Object expression) {
			if(expression == null){
				return null;
			}
			ReflectClass claxx = reflector.forObject(expression);
			if(claxx == null){
				return null;
			}
			IntegerConverter converter = integerConverterforClassName(reflector, claxx.getName());
			if(converter != null){
				Pair entry = (Pair)_fieldsByIntId.get(converter.toInteger(expression));
				if(entry == null){
					return null;
				}
				if(entry.first.equals(expression)){
					
					// can't inline the following otherwise sharpen 
					// will create another array around it for varargs
					// Object[] path_Dont_Inline_Me = (Object[]) entry.second;
					
					return Iterators.iterate((Object[]) entry.second);
				}
				return null;
			}
			if(claxx.isPrimitive()){
				return null;
			}
			Object[] path = (Object[]) _fieldsByIdentity.get(expression);
			if(path != null){
				return Iterators.iterate(path);
			}
			return null;
		}

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
						if(str.indexOf(STRING_IDENTIFIER) != 0){
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
