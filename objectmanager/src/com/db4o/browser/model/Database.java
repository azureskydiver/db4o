package com.db4o.browser.model;

import com.db4o.ObjectSet;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.Reflector;



public interface Database {

	/**
	 * Method open.  Open a YAP file.
	 * 
	 * @param path The os-dependent path and file name for the YAP file.
	 */
	public abstract void open(String path);

	/**
	 * Method close.  Close the current YAP file if one is open.
	 */
	public abstract void close();

	public abstract DatabaseGraphIterator graphIterator();

	public abstract DatabaseGraphIterator graphIterator(String name);

	public abstract ObjectSet instances(ReflectClass clazz);
	
	public abstract long getId(Object object);
	
	public abstract void activate(Object object);

	public abstract void setInitialActivationDepth(int initialActivationDepth);

    public abstract Reflector reflector();
}