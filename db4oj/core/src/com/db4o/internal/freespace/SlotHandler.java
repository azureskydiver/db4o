/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public abstract class SlotHandler implements Indexable4{
	
	protected Slot _current;
	
	public void defragIndexEntry(ReaderPair readers) {
		throw new NotImplementedException();
	}

	public int linkLength() {
		return Slot.MARSHALLED_LENGTH;
	}

	public Object readIndexEntry(Buffer reader) {
		return new Slot(reader.readInt(), reader.readInt());
	}

	public void writeIndexEntry(Buffer writer, Object obj) {
		Slot slot = (Slot) obj;
		writer.writeInt(slot.address());
		writer.writeInt(slot.length());
	}

	public Object current() {
		return _current;
	}

	public boolean isEqual(Object obj) {
		throw new NotImplementedException();
	}

	public boolean isGreater(Object obj) {
		throw new NotImplementedException();
	}

	public boolean isSmaller(Object obj) {
		throw new NotImplementedException();
	}

	public Comparable4 prepareComparison(Object obj) {
		_current = (Slot)obj;
		return this;
	}

}
