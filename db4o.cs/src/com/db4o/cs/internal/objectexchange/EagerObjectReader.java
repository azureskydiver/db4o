/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal.objectexchange;

import static com.db4o.foundation.Environments.*;

import com.db4o.cs.caching.*;
import com.db4o.cs.internal.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

public class EagerObjectReader {

	private final ByteArrayBuffer _reader;
	private final ClientTransaction _transaction;

	public EagerObjectReader(ClientTransaction transaction, ByteArrayBuffer reader) {
		_reader = reader;
		_transaction = transaction;
    }

	public FixedSizeIntIterator4 iterator() {
		
		readChildSlots();
		
		return iterateRootSlots();
    }

	private FixedSizeIntIterator4 iterateRootSlots() {
	    final int size = _reader.readInt();
		return new FixedSizeIntIterator4Base(size) {
			@Override
			protected int nextInt() {
				return readNext();
			}
		};
    }

	private void readChildSlots() {
	    final int childSlots = _reader.readInt();
		for (int i=0; i<childSlots; ++i) {
			readNext();
		}
    }

	protected void contributeCachedSlot(int id, int length) {
		final ByteArrayBuffer slot = _reader.readPayloadReader(_reader.offset(), length);
		my(ClientSlotCache.class).add(_transaction, id, slot);
		_reader.skip(length);
    }

	private int readNext() {
	    int id = _reader.readInt();
	    int length = _reader.readInt(); // slot length
	    if (length > 0) {
	    	contributeCachedSlot(id, length);
	    }
	    return id;
    }

}
