/* Copyright (C) 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.qlin;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;


/**
 * static import support class for {@link QLin} queries.
 * @since 8.0
 */
public class QLinSupport {
	
	/**
	 * returns a prototype object for a specific class
	 * to be passed to the where expression of a QLin 
	 * query. 
	 * @see QLin#where(Object)
	 */
	public static <T> T prototype(Class<T> clazz){
		try{
			return _prototypes.forClass(clazz);
		} catch(PrototypesException ex){
			throw new QLinException(ex);
		}
	}

	/**
	 * sets the context for the next query on this thread.
	 * This method should never have to be called manually.
	 * The framework should set the context up. 
	 */
	public static <T> QLinContext context(Class<T> clazz) {
		QLinContext context = _context.value();
		if(context == null){
			// get the standalone emergency Reflector
			context = new QLinContext(com.db4o.internal.Platform4.reflectorForType(clazz), clazz);
		}
		return context(context.createNewFor(clazz));
	}
	
	/**
	 * sets the context for the next query on this thread.
	 * This method should never have to be called manually.
	 * The framework should set the context up. 
	 */
	public static <T> QLinContext context(QLinContext context){
		_context.value(context);
		return context;
	}
	
	/**
	 * shortcut for the {@link #prototype(Class)} method.
	 */
	public static <T> T p(Class<T> clazz){
		return prototype(clazz);
	}
	
	/**
	 * parameter for {@link QLin#orderBy(Object, QLinOrderByDirection)}
	 */
	public static QLinOrderByDirection ascending(){
		return QLinOrderByDirection.ASCENDING;
	}
	
	/**
	 * parameter for {@link QLin#orderBy(Object, QLinOrderByDirection)}
	 */
	public static QLinOrderByDirection descending(){
		return QLinOrderByDirection.DESCENDING;
	}
	
	/**
	 * public for implementors, do not use directly 
	 */
	public static Iterator4<String> backingFieldPath(Object expression){
		checkForNull(expression);
		if(expression instanceof ReflectField){
			return Iterators.iterate( ((ReflectField)expression).getName());
		}
		Iterator4 path = _prototypes.backingFieldPath(_context.value().clazz, expression);
		if(path != null){
			return path;
		}
		return Iterators.iterate(fieldByFieldName(expression).getName());
	}

	
	/**
	 * converts an expression to a single field. 
	 */
	public static ReflectField field(Object expression){
		checkForNull(expression);
		if(expression instanceof ReflectField){
			return (ReflectField)expression;
		}
		Iterator4 path = _prototypes.backingFieldPath(_context.value().clazz, expression);
		if(path != null){
			if(path.moveNext()){
				expression = path.current();
			}
			if(path.moveNext()){
				path.reset();
				throw new QLinException("expression can not be converted to a single field. It evaluates to: " + 
						Iterators.join(path, "[", "]", ", "));
			}
		}
		return fieldByFieldName(expression);
	}

	private static ReflectField fieldByFieldName(Object expression) {
		if(expression instanceof String){
			ReflectField field = Reflections.field(_context.value().classReflector(), (String)expression);
			if(field != null){
				return field;
			}
		}
		throw new QLinException("expression can not be mapped to a field");
	}
	
	private static void checkForNull(Object expression) {
		warnOnce();
		if(expression == null){
			throw new QLinException("expression can not be null");
		}
	}
	
	private static volatile boolean warned = false;
	
	private static void warnOnce() {
		if(!warned){
			System.err.println("\nQLin is a new experimental query interface, that we wrote for the ");
			System.err.println("db4o replication system (dRS).\n");
			System.err.println("It is not yet certain that QLin will go into the db4o 8.0 final release.");
			System.err.println("\nWe would love to have real LINQ for Java instead.\n");
			System.err.println("Kudos to Thomas Mueller for the inspiration that it is possible to map");
			System.err.println("expressions to fields: http://www.h2database.com/html/jaqu.html\n");
			warned = true;
		}
	}
	
	private static final boolean IGNORE_TRANSIENT_FIELDS = true;
	
	private static final int RECURSION_DEPTH = 4;
	
	private static final Prototypes _prototypes = 
		new Prototypes(new GenericReflector(Platform4.reflectorForType(QLinSupport.class)), 
		RECURSION_DEPTH, 
		IGNORE_TRANSIENT_FIELDS);
	
	private static final DynamicVariable<QLinContext> _context = DynamicVariable.newInstance();
	
}
