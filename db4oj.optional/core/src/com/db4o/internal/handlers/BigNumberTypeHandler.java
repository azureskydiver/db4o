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
public abstract class BigNumberTypeHandler<TBigNumber> implements EmbeddedTypeHandler, VariableLengthTypeHandler, QueryableTypeHandler {

	public void defragment(DefragmentContext context) {
		skip(context);
	}

	public void delete(DeleteContext context) throws Db4oIOException {
		skip(context);
	}

	public Object read(ReadContext context) {
		return unmarshall(context);
	}

	private TBigNumber unmarshall(final ReadBuffer buffer) {
	    byte[] data = new byte[buffer.readInt()];
		buffer.readBytes(data);
		return fromByteArray(data);
    }
	
	protected abstract TBigNumber fromByteArray(byte[] data);
	
	protected abstract byte[] toByteArray(TBigNumber obj);
	
	protected abstract int compare(TBigNumber x, TBigNumber y);

	public void write(WriteContext context, Object obj) {
		byte[] data = toByteArray((TBigNumber)obj);
		context.writeInt(data.length);
		context.writeBytes(data);
	}

	public PreparedComparison<TBigNumber> prepareComparison(Context context, Object obj) {
		final TBigNumber value = obj instanceof TransactionContext
			? bigNumberFrom(((TransactionContext)obj)._object)
			: bigNumberFrom(obj);
			
		return new PreparedComparison<TBigNumber>() {
			public int compareTo(TBigNumber other) {
				if (other == null) {
					return 1;
				}
			    return compare(value, other);
			}
		};
	}

	private TBigNumber bigNumberFrom(Object obj) {
		if (true) return (TBigNumber)obj;
		return obj instanceof ReadBuffer
			? unmarshall((ReadBuffer)obj)
			: (TBigNumber)obj;
    }
	
	private void skip(ReadBuffer context) {
		int numBytes = context.readInt();
		context.seek(context.offset() + numBytes);
	}

	public boolean isSimple() {
		return true;
    }
}
