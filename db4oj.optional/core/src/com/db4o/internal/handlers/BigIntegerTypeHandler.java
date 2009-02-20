package com.db4o.internal.handlers;

import java.math.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.reflect.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;

/**
 * @sharpen.ignore
 */
public class BigIntegerTypeHandler implements EmbeddedTypeHandler {

	public void defragment(DefragmentContext context) {
		skip(context);
	}

	public void delete(DeleteContext context) throws Db4oIOException {
		skip(context);
	}

	public Object read(ReadContext context) {
		byte[] data = new byte[context.readInt()];
		context.readBytes(data);
		return new BigInteger(data);
	}

	public void write(WriteContext context, Object obj) {
		byte[] data = ((BigInteger)obj).toByteArray();
		context.writeInt(data.length);
		context.writeBytes(data);
	}

	public PreparedComparison prepareComparison(Context context, Object obj) {
		return null;
	}

	private void skip(ReadBuffer context) {
		int numBytes = context.readInt();
		context.seek(context.offset() + numBytes);
	}

	public boolean canHold(ReflectClass type) {
		return ReflectClasses.areEqual(BigInteger.class, type);
    }

}
