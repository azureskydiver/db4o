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
public class BigIntegerTypeHandler implements EmbeddedTypeHandler, QueryableTypeHandler {

	public void defragment(DefragmentContext context) {
		skip(context);
	}

	public void delete(DeleteContext context) throws Db4oIOException {
		skip(context);
	}

	public Object read(ReadContext context) {
		return unmarshall(context);
	}

	private BigInteger unmarshall(final ReadBuffer buffer) {
	    byte[] data = new byte[buffer.readInt()];
		buffer.readBytes(data);
		return new BigInteger(data);
    }

	public void write(WriteContext context, Object obj) {
		byte[] data = ((BigInteger)obj).toByteArray();
		context.writeInt(data.length);
		context.writeBytes(data);
	}

	public PreparedComparison prepareComparison(Context context, Object obj) {
		final BigInteger value = obj instanceof TransactionContext
			? bigIntegerFrom(((TransactionContext)obj)._object)
			: bigIntegerFrom(obj);
//		if (value == null) {
//			return new PreparedComparison() {
//				public int compareTo(Object obj) {
//					if (obj == null) {
//						return 0;
//					}
//					return -1;
//                }
//			};
//		}
		return new PreparedComparison() {
			public int compareTo(Object obj) {
				BigInteger valueToCompareAgainst = (BigInteger) obj;
				if (valueToCompareAgainst == null) {
					return 1;
				}
			    return value.compareTo(valueToCompareAgainst);
			}
		};
	}

	private BigInteger bigIntegerFrom(Object obj) {
	    return obj instanceof BigInteger
			? (BigInteger)obj
			: unmarshall((ReadBuffer)obj);
    }
	
	private void skip(ReadBuffer context) {
		int numBytes = context.readInt();
		context.seek(context.offset() + numBytes);
	}

	public boolean canHold(ReflectClass type) {
		return ReflectClasses.areEqual(BigInteger.class, type);
    }

	public boolean isSimple() {
		return true;
    }

	public int linkLength() {
	    // TODO Auto-generated method stub
	    return 0;
    }

}
