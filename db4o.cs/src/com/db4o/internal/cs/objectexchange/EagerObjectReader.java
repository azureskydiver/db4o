/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal.cs.objectexchange;

import com.db4o.foundation.*;
import com.db4o.internal.*;

public class EagerObjectReader {

	private final Transaction _transaction;
	private final ByteArrayBuffer _reader;

	public EagerObjectReader(Transaction transaction, ByteArrayBuffer reader) {
		_transaction = transaction;
		_reader = reader;
    }

	public FixedSizeIntIterator4 iterator() {
		
		final int size = _reader.readInt();
		
		return new FixedSizeIntIterator4() {

			int _current;
			int _available = size;
			
			public int size() {
				return size;
			}
			
			public int currentInt() {
				return _current;
			}
			
			public Object current() {
				return _current;
            }

			public boolean moveNext() {
				if (_available > 0) {
					int id = _reader.readInt();
					int length = _reader.readInt(); // slot length
					
					ensureObjectIsActive(id, length);
			    	
					_reader.skip(length);
			    	
					--_available;
					_current = id;
					
					return true;
				}
				return false;
            }

			public void reset() {
	            throw new com.db4o.foundation.NotImplementedException();
            }
		};
    }

	protected void createActiveReference(int id, int length) {
		activate(new ObjectReference(id), length, true);
    }

	private void ensureObjectIsActive(int id, int length) {
	    final ObjectReference reference = _transaction.referenceForId(id);
	    if (reference == null) {
	    	createActiveReference(id, length);
	    	return;
	    }
	    
	    if (reference.isActive()) {
	    	return;
	    }
	    
	    boolean addToReferenceSystem = false;
	    activate(reference, length, addToReferenceSystem);
    }

	private void activate(final ObjectReference reference, int length, boolean addToReferenceSystem) {
		final ByteArrayBuffer slotBuffer = _reader.readPayloadReader(_reader._offset, length);
	    reference.readPrefetch(_transaction, slotBuffer, addToReferenceSystem ? Const4.ADD_TO_ID_TREE : Const4.TRANSIENT);
    }

}
