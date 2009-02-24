package com.db4o.internal.handlers;

import java.math.*;

import com.db4o.internal.reflect.*;
import com.db4o.reflect.*;

/**
 * @sharpen.ignore
 * @exclude
 */
public class BigIntegerTypeHandler extends BigNumberTypeHandler {

	@Override
	protected Comparable fromByteArray(byte[] data) {
	    return new BigInteger(data);
    }

	@Override
	protected byte[] toByteArray(Object obj) {
	    return ((BigInteger)obj).toByteArray();
    }

	public boolean canHold(ReflectClass type) {
		return ReflectClasses.areEqual(BigInteger.class, type);
    }
}
