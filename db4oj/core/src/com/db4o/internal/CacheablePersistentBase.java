/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;
import com.db4o.internal.caching.*;

/**
 * @exclude
 */
public abstract class CacheablePersistentBase extends PersistentBase{
	
	@Override
	protected ByteArrayBuffer produceReadBuffer(final Transaction trans) {
		ByteArrayBuffer buffer = cache(trans).produce(getID(), readProducer(trans), null);
		buffer.seek(0);
		return buffer;
	}

	private Function4<Integer, ByteArrayBuffer> readProducer(
			final Transaction trans) {
		return new Function4<Integer, ByteArrayBuffer>(){
			public ByteArrayBuffer apply(Integer id) {
				return readBufferById(trans);
			}
		};
	}
	
	@Override
	protected ByteArrayBuffer produceWriteBuffer(final Transaction trans, final int length) {
		ByteArrayBuffer buffer = cache(trans).produce(getID(), writerProducer(length), null);
		buffer.ensureSize(length);
		buffer.seek(0);
		return buffer;
	}

	private Function4<Integer, ByteArrayBuffer> writerProducer(final int length) {
		return new Function4<Integer, ByteArrayBuffer>(){
			public ByteArrayBuffer apply(Integer id) {
				return newWriteBuffer(length);
			}
		};
	}


	private Cache4<Integer, ByteArrayBuffer> cache(Transaction trans){
		return ((LocalTransaction)trans).slotCache();
	}

}
