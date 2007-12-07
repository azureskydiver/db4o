/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ObjectContainer;
import com.db4o.diagnostic.DefragmentRecommendation.DefragmentRecommendationReason;
import com.db4o.internal.diagnostic.DiagnosticProcessor;
import com.db4o.internal.marshall.MarshallerFamily;
import com.db4o.internal.slots.Slot;

/**
 * @exclude
 */
public class DeleteContextImpl implements DeleteContext {
	
	private final MarshallerFamily _family;

	private final StatefulBuffer _buffer;

	public DeleteContextImpl(MarshallerFamily family, StatefulBuffer buffer){
		_family = family;
		_buffer = buffer;
	}

	public MarshallerFamily family() {
		return _family;
	}

	public StatefulBuffer buffer() {
		return _buffer;
	}

	public Transaction transaction() {
		return _buffer.getTransaction();
	}

	public Object getByID(int id) {
		return container().getByID2(transaction(), id);
	}
	
	private ObjectContainerBase container(){
		return _buffer.getStream();
	}

	public ObjectContainer objectContainer() {
		return (ObjectContainer) container();
	}

	public void cascadeDeleteDepth(int depth) {
		_buffer.setCascadeDeletes(depth);
	}

	public int cascadeDeleteDepth() {
		return _buffer.cascadeDeletes();
	}

	public void delete(ObjectReference ref, Object obj, int cascadeDeleteDepth) {
		container().delete2(transaction(), ref, obj,cascadeDeleteDepth, false);
	}

	public int readInt() {
		return _buffer.readInt();
	}

	public boolean isLegacyHandlerVersion() {
		return _family.isLegacyVersion();
	}

	public void incrementOffset(int length) {
		_buffer.incrementOffset(length);
	}

	public void defragmentRecommended() {
        DiagnosticProcessor dp = container()._handlers._diagnosticProcessor;
        if(dp.enabled()){
            dp.defragmentRecommended(DefragmentRecommendationReason.DELETE_EMBEDED);
        }
	}

	public Slot readSlot() {
		return _buffer.readSlot();
	}

	public int offset() {
		return _buffer.offset();
	}

	public void seek(int offset) {
		_buffer.seek(offset);
	}

	public byte readByte() {
		return _buffer.readByte();
	}

	public void readBytes(byte[] bytes) {
		_buffer.readBytes(bytes);
	}

	public long readLong() {
		return _buffer.readLong();
	}

}
