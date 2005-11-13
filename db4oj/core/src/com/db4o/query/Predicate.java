/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.query;

import java.io.*;
import java.lang.reflect.*;

import com.db4o.*;

/**
 * Base class for native queries.
 * <br><br>Native Queries allow typesafe, compile-time checked and refactorable 
 * querying, following object-oriented principles. Native Queries expressions
 * are written as if one or more lines of code would be run against all
 * instances of a class. A Native Query expression should return true to mark 
 * specific instances as part of the result set. 
 * db4o will  attempt to optimize native query expressions and execute them 
 * against indexes and without instantiating actual objects, where this is 
 * possible.<br><br>
 * The syntax of the enclosing object for the native query expression varies,
 * depending on the language version used. Here are some examples,
 * how a simple native query will look like in some of the programming languages 
 * and dialects that db4o supports:<br><br>
 * 
 * <pre>
 * <b>// C# .NET 2.0</b>
 * IList <Cat> cats = db.Query <Cat> (delegate(Cat cat) {
 *   return cat.Name == "Occam";
 * });
 * 
 *
 * <b>// Java JDK 5</b>
 * List <Cat> cats = db.query(new Predicate<Cat>() {
 *     public boolean match(Cat cat) {
 *         return cat.getName().equals("Occam");
 *     }
 * });
 * 
 * 
 * <b>// Java JDK 1.2 to 1.4</b>
 * List cats = db.query(new Predicate() {
 *     public boolean match(Cat cat) {
 *         return cat.getName().equals("Occam");
 *     }
 * });
 * 
 *     
 * <b>// Java JDK 1.1</b>
 * ObjectSet cats = db.query(new CatOccam());
 * 
 * public static class CatOccam extends Predicate {
 *     public boolean match(Cat cat) {
 *         return cat.getName().equals("Occam");
 *     }
 * });
 *     
 *     
 * <b>// C# .NET 1.1</b>
 * IList cats = db.Query(new CatOccam());
 * 
 * public class CatOccam : Predicate {
 *     public boolean Match(Cat cat) {
 *         return cat.Name == "Occam";
 *     }
 * });
 * </pre>
 * 
 * Summing up the above:<br>
 * In order to run a Native Query, you can<br>
 * - use the delegate notation for .NET 2.0.<br>
 * - extend the Predicate class for all other language dialects<br><br>
 * A class that extends Predicate is required to 
 * implement the #match() / #Match() method, following the native query
 * conventions:<br>
 * - The name of the method is "#match()" (Java) / "#Match()" (.NET).<br>
 * - The method must be public public.<br>
 * - The method returns a boolean.<br>
 * - The method takes one parameter.<br>
 * - The Type (.NET) / Class (Java) of the parameter specifies the extent.<br>
 * - For all instances of the extent that are to be included into the
 * resultset of the query, the match method should return true. For all
 * instances that are not to be included, the match method should return
 * false.<br><br>
 */
public abstract class Predicate implements Serializable {
	
    /**
     * public for implementation reasons, please ignore.
     */
    public final static String PREDICATEMETHOD_NAME="match";
	
	static final Class OBJECT_CLASS = Object.class;
	
	private transient Method cachedFilterMethod=null;
	
	// IMPORTANT: must have package visibility because it is used as
	// internal on the .net side
	Method getFilterMethod() {
		if(cachedFilterMethod!=null) {
			return cachedFilterMethod;
		}
		Method[] methods=getClass().getMethods();
		Method untypedMethod=null;
		for (int methodIdx = 0; methodIdx < methods.length; methodIdx++) {
			Method method=methods[methodIdx];
			if (isFilterMethod(method)) {
				if (!OBJECT_CLASS.equals(method.getParameterTypes()[0])) {
					cachedFilterMethod=method;
					return method;
				}
				else {
					untypedMethod=method;
				}
			}
		}
		if(untypedMethod!=null) {
			cachedFilterMethod=untypedMethod;
			return untypedMethod;
		}
		throw new IllegalArgumentException("Invalid predicate.");
	}

	private boolean isFilterMethod(Method method) {
		if (method.getParameterTypes().length != 1) {
			return false;
		}
		if (Deploy.csharp) {
			return method.getName().equalsIgnoreCase(PREDICATEMETHOD_NAME);
		} else {
			return method.getName().equals(PREDICATEMETHOD_NAME);
		}
	}

    /**
     * public for implementation reasons, please ignore.
     */
	public Class extentType() {
		return getFilterMethod().getParameterTypes()[0];
	}

    /**
     * public for implementation reasons, please ignore.
     */
	public boolean appliesTo(Object candidate) {
		try {
			Method filterMethod=getFilterMethod();
			Platform4.setAccessible(filterMethod);
			Object ret=filterMethod.invoke(this,new Object[]{candidate});
			return ((Boolean)ret).booleanValue();
		} catch (Exception e) {
            
            // FIXME: Exceptions should be logged for app developers,
            // but we can't print them out here.
            
			// e.printStackTrace();
            
            
			return false;
		}
	}
}
