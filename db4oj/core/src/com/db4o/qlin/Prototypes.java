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
	
	public Prototypes(Reflector reflector, boolean ignoreTransient){
		_reflector = reflector;
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
		prototype = Prototype.forClass(claxx, _ignoreTransient);
		if(prototype == null){
			throw new PrototypesException("Prototype could not be created for " + clazz);
		}
		_prototypes.put(className, prototype);
		return prototype.object();
	}
	
	/**
	 * analyzes the passed expression and tries to find the path to the 
	 * backing field that is accessed.
	 */
	public <T> String[] backingFieldPath(Class<T> clazz, Object expression){
		Prototype prototype = (Prototype) _prototypes.get(clazz.getName());
		if(prototype == null){
			return null;
		}
		String fieldName = prototype.backingFieldPath(_reflector, expression);
		if(fieldName == null){
			return null;
		}
		return new String[]{fieldName};
	}

}
