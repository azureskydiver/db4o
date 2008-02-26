/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.mapping;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;

/**
 * @exclude
 */
public class MappedIDPairHandler implements Indexable4 {

	private final IntHandler _origHandler;
	private final IntHandler _mappedHandler;
	
	public MappedIDPairHandler() {
		_origHandler=new IntHandler();
		_mappedHandler=new IntHandler();
	}

	public void defragIndexEntry(DefragmentContextImpl context) {
        throw new NotImplementedException();
	}

	public int linkLength() {
		return _origHandler.linkLength()+_mappedHandler.linkLength();
	}

	public Object readIndexEntry(ByteArrayBuffer reader) {
		int origID=readID(reader);
		int mappedID=readID(reader);
        return new MappedIDPair(origID,mappedID);
	}

	public void writeIndexEntry(ByteArrayBuffer reader, Object obj) {
		MappedIDPair mappedIDs=(MappedIDPair)obj;
		_origHandler.writeIndexEntry(reader, new Integer(mappedIDs.orig()));
		_mappedHandler.writeIndexEntry(reader, new Integer(mappedIDs.mapped()));
	}

	private int readID(ByteArrayBuffer a_reader) {
		return ((Integer)_origHandler.readIndexEntry(a_reader)).intValue();
	}

	public PreparedComparison prepareComparison(Object source) {
		MappedIDPair sourceIDPair = (MappedIDPair)source;
		final int sourceID = sourceIDPair.orig();
		return new PreparedComparison() {
			public int compareTo(Object target) {
				MappedIDPair targetIDPair = (MappedIDPair)target;
				int targetID = targetIDPair.orig();
				return sourceID == targetID ? 0 : (sourceID < targetID ? - 1 : 1); 
			}
		};
	}
}
