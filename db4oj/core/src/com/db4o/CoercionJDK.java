package com.db4o;

import com.db4o.foundation.No4;

/**
 * @exclude
 */
public class CoercionJDK implements Coercion {

	public Object toSByte(Object obj) {
		return (obj instanceof Byte ? obj : No4.INSTANCE);
	}

	public Object toShort(Object obj) {
		return (obj instanceof Short ? obj : No4.INSTANCE);
	}

	public Object toInt(Object obj) {
		return (obj instanceof Integer ? obj : No4.INSTANCE);
	}

	public Object toLong(Object obj) {
		return (obj instanceof Long ? obj : No4.INSTANCE);
	}

	public Object toFloat(Object obj) {
		return (obj instanceof Float ? obj : No4.INSTANCE);
	}

	public Object toDouble(Object obj) {
		return (obj instanceof Double ? obj : No4.INSTANCE);
	}
}