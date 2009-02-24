package com.db4o.internal.handlers;

import java.math.*;

import com.db4o.internal.reflect.*;
import com.db4o.reflect.*;

/**
 * @sharpen.ignore
 * @exclude
 */
public class BigDecimalTypeHandler extends BigNumberTypeHandler {
	
	public boolean canHold(ReflectClass type) {
		return ReflectClasses.areEqual(BigDecimal.class, type);
    }

	@Override
	protected Comparable fromByteArray(byte[] data) {
	    return new BigDecimal(new String(data));
    }
	
	@Override
	protected byte[] toByteArray(Object obj) {
		return ((BigDecimal)obj).toString().getBytes();
	}
}
