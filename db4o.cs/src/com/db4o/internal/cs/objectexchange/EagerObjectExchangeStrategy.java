/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal.cs.objectexchange;

import com.db4o.foundation.*;
import com.db4o.internal.*;

public class EagerObjectExchangeStrategy implements ObjectExchangeStrategy {

	// TODO: ...
//	private final int _depth;

	public EagerObjectExchangeStrategy(int prefetchDepth) {
//		_depth = prefetchDepth;
    }

	public ByteArrayBuffer marshall(LocalTransaction transaction, IntIterator4 ids, int maxCount) {
	   return new EagerObjectWriter(transaction).write(ids, maxCount);
    }

	public FixedSizeIntIterator4 unmarshall(Transaction transaction, ByteArrayBuffer reader) {
		return new EagerObjectReader(transaction, reader).iterator();
    }

}
