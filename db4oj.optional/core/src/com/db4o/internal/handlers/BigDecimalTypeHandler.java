package com.db4o.internal.handlers;

import java.math.*;

import com.db4o.internal.reflect.*;
import com.db4o.reflect.*;

/**
 * @sharpen.ignore
 * @exclude
 */
public class BigDecimalTypeHandler extends BigNumberTypeHandler<BigDecimal> {
	
	public boolean canHold(ReflectClass type) {
		return ReflectClasses.areEqual(BigDecimal.class, type);
    }

	@Override
	protected BigDecimal fromByteArray(byte[] data) {
	    return new BigDecimal(new String(data));
    }
	
	@Override
	protected byte[] toByteArray(BigDecimal value) {
		return value.toString().getBytes();
	}

	@Override
    protected int compare(BigDecimal x, BigDecimal y) {
		return x.compareTo(y);
    }
}
