package com.db4o.internal.handlers;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;

/**
 * @sharpen.ignore
 * @exclude
 */
public abstract class BigNumberTypeHandler implements EmbeddedTypeHandler, QueryableTypeHandler {

	public void defragment(DefragmentContext context) {
		skip(context);
	}

	public void delete(DeleteContext context) throws Db4oIOException {
		skip(context);
	}

	public Object read(ReadContext context) {
		return unmarshall(context);
	}

	private Comparable unmarshall(final ReadBuffer buffer) {
	    byte[] data = new byte[buffer.readInt()];
		buffer.readBytes(data);
		return fromByteArray(data);
    }
	
	protected abstract Comparable fromByteArray(byte[] data);
	
	protected abstract byte[] toByteArray(Object obj);

	public void write(WriteContext context, Object obj) {
		byte[] data = toByteArray(obj);
		context.writeInt(data.length);
		context.writeBytes(data);
	}

	public PreparedComparison prepareComparison(Context context, Object obj) {
		final Comparable value = obj instanceof TransactionContext
			? comparableFrom(((TransactionContext)obj)._object)
			: comparableFrom(obj);
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
				Comparable valueToCompareAgainst = (Comparable) obj;
				if (valueToCompareAgainst == null) {
					return 1;
				}
			    return value.compareTo(valueToCompareAgainst);
			}
		};
	}

	private Comparable comparableFrom(Object obj) {
	    return obj instanceof ReadBuffer
			? unmarshall((ReadBuffer)obj)
			: (Comparable)obj;
    }
	
	private void skip(ReadBuffer context) {
		int numBytes = context.readInt();
		context.seek(context.offset() + numBytes);
	}

	public boolean isSimple() {
		return true;
    }
}
