/* Copyright (C) 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.qlin;

import com.db4o.foundation.*;
import com.db4o.reflect.*;


/**
 * static import support class for {@link QLin} queries.
 * @since 8.0
 */
public class QLinSupport {
	
	private static final DynamicVariable<QLinContext> _context = DynamicVariable.newInstance();
	
	private static final Hashtable4 _prototypes = new Hashtable4();
	
	private static volatile boolean warned = false;
	
	/**
	 * returns a prototype object for a specific class
	 * to be passed to the where expression of a QLin 
	 * query. 
	 * @see QLin#where(Object)
	 */
	public static <T> T prototype (Class<T> clazz){
		final String className = clazz.getName();
		Prototype<T> prototype = (Prototype) _prototypes.get(className);
		if(prototype != null){
			return prototype.object();
		}
		prototype = Prototype.forContext(adjustContext(clazz));
		if(prototype == null){
			throw new QLinException("Prototype could not be created for " + clazz);
		}
		_prototypes.put(className, prototype);
		return prototype.object();
	}

	private static <T> QLinContext adjustContext(Class<T> clazz) {
		QLinContext context = _context.value();
		if(context == null){
			throw new QLinException("Context not set for the current thread. context() should be called automatically by the framework.");
		}
		context = context.createNewFor(clazz);
		_context.value(context);
		return context;
	}
	
	/**
	 * sets the context for the next query on this thread.
	 * This method should never have to be called manually.
	 * The framework should set the context up. 
	 */
	public static void context(QLinContext context){
		_context.value(context);
	}
	
	/**
	 * shortcut for the {@link #prototype(Class)} method.
	 */
	public static <T> T   p (Class<T> clazz){
		return prototype(clazz);
	}
	
	
	/**
	 * maps expressions to fields.
	 * This method is intended to be used by the framework only.
	 * Other frameworks might also find our approach nifty to try. 
	 * We were inspired when we saw how Thomas Mueller mapped
	 * expressions to fields for his JaQu query interface, Kudos!
	 * http://www.h2database.com/html/jaqu.html
	 * We took the idea a bit further and made it work for all
	 * primitives except for boolean and we plan to also get 
	 * deeper expressions and collections working nicely.
	 */
	public static ReflectField field(Object expression){
		warnOnce();
		
		if(expression == null){
			throw new QLinException("expression can not be null");
		}
		if(expression instanceof ReflectField){
			return (ReflectField)expression;
		}
		final QLinContext context = _context.value();
		Prototype prototype = (Prototype) _prototypes.get(context.clazz.getName());
		if(prototype != null){
			String fieldName = prototype.matchToFieldName(context, expression);
			if(fieldName != null){
				expression = fieldName;
			}
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
			System.err.println("We would love to have real LINQ for Java. For now this is the best we can do.\n");
			System.err.println("Kudos to Thomas Mueller for the inspiration that it is possible to map");
			System.err.println("expressions to fields: http://www.h2database.com/html/jaqu.html\n");
			System.err.println("QLin computes underlying field names from methods by doing wild magic.");
			System.err.println("Side effects upon calling methods from within queries may be potentially dangerous.");
			System.err.println("\nThere is no guarantee yet that QLin will be in the final db4o 8.0 release.");
			System.err.println("\n*** Warning ****** Warning ****** Warning ****** Warning ****** Warning ***\n");

			warned = true;
		}
	}
	
}
