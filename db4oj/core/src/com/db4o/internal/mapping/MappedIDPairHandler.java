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
	
	public MappedIDPairHandler(ObjectContainerBase stream) {
		_origHandler=new IntHandler(stream);
		_mappedHandler=new IntHandler(stream);
	}

	public void defragIndexEntry(ReaderPair readers) {
        throw new NotImplementedException();
	}

	public int linkLength() {
		return _origHandler.linkLength()+_mappedHandler.linkLength();
	}

	public Object readIndexEntry(Buffer reader) {
		int origID=readID(reader);
		int mappedID=readID(reader);
        return new MappedIDPair(origID,mappedID);
	}

	public void writeIndexEntry(Buffer reader, Object obj) {
		MappedIDPair mappedIDs=(MappedIDPair)obj;
		_origHandler.writeIndexEntry(reader, new Integer(mappedIDs.orig()));
		_mappedHandler.writeIndexEntry(reader, new Integer(mappedIDs.mapped()));
	}

	public int compareTo(Object obj) {
        return _origHandler.compareTo(((MappedIDPair)obj).orig());
	}

	public Object current() {
		return new MappedIDPair(_origHandler.currentInt(),_mappedHandler.currentInt());
	}

	public Comparable4 prepareComparison(Object obj) {
        MappedIDPair mappedIDs = (MappedIDPair)obj;
        _origHandler.prepareComparison(mappedIDs.orig());
        _mappedHandler.prepareComparison(mappedIDs.mapped());
        return this;
	}
	
	private int readID(Buffer a_reader) {
		return ((Integer)_origHandler.readIndexEntry(a_reader)).intValue();
	}
}
