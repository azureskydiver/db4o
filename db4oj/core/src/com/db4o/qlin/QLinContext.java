/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.qlin;

import com.db4o.reflect.*;

/**
 * @exclude
 */
public class QLinContext {
	
	public final Reflector reflector;
	
	public final Class clazz;
	
	public QLinContext(Reflector reflector, Class clazz) {
		this.reflector = reflector;
		this.clazz = clazz;
	}
	
	public QLinContext createNewFor(Class clazz){
		return new QLinContext(reflector, clazz);
	}
	
	public ReflectClass classReflector(){
		return reflector.forClass(clazz);
	}

}
