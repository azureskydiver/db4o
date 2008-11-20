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
		Cache4<Integer, ByteArrayBuffer> cache = cache(trans);
		if(cache == null){
			return readBufferById(trans);
		}
		ByteArrayBuffer buffer = cache.produce(getID(), new Function4<Integer, ByteArrayBuffer>(){
			public ByteArrayBuffer apply(Integer id) {
				return readBufferById(trans);
			}
		}, null);
		buffer.seek(0);
		return buffer;
	}
	
	@Override
	protected ByteArrayBuffer produceWriteBuffer(final Transaction trans, final int length) {
		Cache4<Integer, ByteArrayBuffer> cache = cache(trans);
		if(cache == null){
			return newWriteBuffer(length);
		}
		ByteArrayBuffer buffer = cache.produce(getID(), new Function4<Integer, ByteArrayBuffer>(){
			public ByteArrayBuffer apply(Integer id) {
				return newWriteBuffer(length);
			}
		}, null);
		buffer.ensureSize(length);
		buffer.seek(0);
		return buffer;
	}


	private Cache4<Integer, ByteArrayBuffer> cache(Transaction trans){
		return ((LocalTransaction)trans).slotCache();
	}

}
