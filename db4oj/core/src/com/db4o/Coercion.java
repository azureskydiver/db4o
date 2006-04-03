package com.db4o;

/**
 * Convert number objects to the requested type, return No4.INSTANCE
 * if coercion cannot be accomplished.
 * 
 * @exclude
 */
public interface Coercion {
	Object toSByte(Object obj);

	Object toShort(Object obj);

	Object toInt(Object obj);

	Object toLong(Object obj);

	Object toFloat(Object obj);

	Object toDouble(Object obj);
}
