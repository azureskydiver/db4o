/*
 * Created on Feb 8, 2005
 */
package com.db4o.binding.converter;

/**
 * Interface IConverter. An interface for objects that can convert from one data
 * type to another.
 */
public interface IConverter {
	/**
     * Method convert.  Convert the value in 'source' to some other type
     * and return it.
     * 
	 * @param source The value to convert
	 * @return The converted type.
	 */
	public Object convert(Object source);
}
