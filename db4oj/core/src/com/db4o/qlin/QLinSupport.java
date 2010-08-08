/* Copyright (C) 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.qlin;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.reflect.*;


/**
 * static import support class for {@link QLin} queries.
 * @since 8.0
 */
public class QLinSupport {
	
	private static final Prototypes _prototypes = 
		new Prototypes(Platform4.reflectorForType(QLinSupport.class), true);
	
	private static final DynamicVariable<QLinContext> _context = DynamicVariable.newInstance();
	
	private static volatile boolean warned = false;
	
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
	
	
	public static ReflectField field(Object expression){
		warnOnce();
		
		if(expression == null){
			throw new QLinException("expression can not be null");
		}
		if(expression instanceof ReflectField){
			return (ReflectField)expression;
		}
		
		final QLinContext context = _context.value();
		
		
		String[] path = _prototypes.backingFieldPath(context.clazz, expression);
		if(path != null){
			expression = path[0];
		}
		
		if(expression instanceof String){
			ReflectField field = Reflections.field(context.classReflector(), (String)expression);
			if(field != null){
				return field;
			}
		}
		throw new QLinException("expression can not be mapped to a field");
	}

	private static void warnOnce() {
		if(!warned){
			System.err.println("\n*** Warning ****** Warning ****** Warning ****** Warning ****** Warning ***\n");
			System.err.println("QLin (\"Coolin\") is an experimental new query interface for db4o and dRS.");
			System.err.println("We would love to have real LINQ for Java instead.\n");
			System.err.println("Kudos to Thomas Mueller for the inspiration that it is possible to map");
			System.err.println("expressions to fields: http://www.h2database.com/html/jaqu.html\n");
			System.err.println("QLin computes underlying field names from methods by doing wild magic.");
			System.err.println("Side effects upon calling methods from within queries may be potentially dangerous.");
			System.err.println("\nThere is no guarantee that QLin will be in the final db4o 8.0 release.");
			System.err.println("\n*** Warning ****** Warning ****** Warning ****** Warning ****** Warning ***\n");

			warned = true;
		}
	}
	
	public static QLinOrderByDirection ascending(){
		return QLinOrderByDirection.ASCENDING;
	}
	
	public static QLinOrderByDirection descending(){
		return QLinOrderByDirection.DESCENDING;
	}
	
	
}
