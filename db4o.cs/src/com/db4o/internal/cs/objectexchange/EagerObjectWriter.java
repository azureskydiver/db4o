/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal.cs.objectexchange;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;

public class EagerObjectWriter {

	private LocalTransaction _transaction;
	private ObjectExchangeConfiguration _config;

	public EagerObjectWriter(ObjectExchangeConfiguration config, LocalTransaction transaction) {
		_config = config;
		_transaction = transaction;
    }
	
	/**
	 * options:
	 * 
	 * (1) precalculate complete buffer size by iterating through slots
	 * (1') keep the slots in a arraylist first to avoid IO overhead
	 * (2) resize the buffer as needed
	 * (2') allow garbage to be transmitted to avoid reallocation of the complete buffer
	 * (3) stream directly to the output socket
	 * @return 
	 */
	public ByteArrayBuffer write(IntIterator4 idIterator, int maxCount) {
		
		List<Pair<Integer, Slot>> rootSlots = readSlots(idIterator, maxCount);
		List<Pair<Integer, Slot>> childSlots = childSlotsFor(rootSlots);
		
		int marshalledSize = marshalledSizeFor(rootSlots) + marshalledSizeFor(childSlots);
		
		ByteArrayBuffer buffer = new ByteArrayBuffer(marshalledSize);
		writeIdSlotPairsTo(childSlots, buffer);
		writeIdSlotPairsTo(rootSlots, buffer);
		
		return buffer;
	}

	private List<Pair<Integer, Slot>> childSlotsFor(List<Pair<Integer, Slot>> slots) {
		
		final ArrayList<Pair<Integer, Slot>> result = new ArrayList<Pair<Integer, Slot>>();
		if (_config.prefetchDepth < 2) {
			return result;
		}
		
		for (Pair<Integer, Slot> pair : slots) {
			final Slot slot = pair.second;
			if (slot == null) {
				break;
			}
			final int id = pair.first;
			
			final Iterator4 childIds = collectChildIdsFor(id);
			while (childIds.moveNext()) {
				final Integer childId = (Integer)childIds.current();
				result.add(idSlotPairFor(childId));
			}
        }
		
		return result;
    }

	private Iterator4 collectChildIdsFor(final int id) {
	    final CollectIdContext context = CollectIdContext.forID(_transaction, id);
	    final ClassMetadata classMetadata = context.classMetadata();
	    if (null == classMetadata) {
	    	// most probably ClassMetadata reading
	    	return Iterators.EMPTY_ITERATOR;
	    }
	    if (classMetadata.isPrimitive()) {
	    	throw new IllegalStateException(classMetadata.toString());
	    }
	    if (!Handlers4.isCascading(classMetadata.typeHandler())) {
	    	return Iterators.EMPTY_ITERATOR;
	    }
		classMetadata.collectIDs(context);
		return new TreeKeyIterator(context.ids());
    }

	private void writeIdSlotPairsTo(List<Pair<Integer, Slot>> slots, ByteArrayBuffer buffer) {
	    buffer.writeInt(slots.size());
		for (Pair<Integer, Slot> idSlotPair : slots) {
			final int id = idSlotPair.first;
			final Slot slot = idSlotPair.second;
			
			if (slot == null || slot.isNull()) {
				buffer.writeInt(id);
				buffer.writeInt(0);
				continue;
			}
			
			final ByteArrayBuffer slotBuffer = _transaction.file().readSlotBuffer(slot);
			buffer.writeInt(id);
			buffer.writeInt(slot.length());
			buffer.writeBytes(slotBuffer._buffer);
		}
    }

	private int marshalledSizeFor(List<Pair<Integer, Slot>> slots) {
		int total = Const4.INT_LENGTH; // count
		for (Pair<Integer, Slot> idSlotPair : slots) {
			total += Const4.INT_LENGTH; // id
			total += Const4.INT_LENGTH; // length
			
			final Slot slot = idSlotPair.second;
			if (slot != null) {
				total += slot.length();
			}
		}
		return total;
    }

	private List<Pair<Integer, Slot>> readSlots(IntIterator4 idIterator, int maxCount) {
	    
		final int prefetchObjectCount = configuredPrefetchObjectCount();
		final ArrayList slots = new ArrayList();
		
        while(idIterator.moveNext()){
            final int id = idIterator.currentInt();
            
            if (slots.size() < prefetchObjectCount) {
            	slots.add(idSlotPairFor(id));
            } else {
            	slots.add(Pair.of(id, null));
            }
            
            if(slots.size() >= maxCount){
            	break;
            }
        }
		return slots;
    }

	private Pair<Integer, Slot> idSlotPairFor(final int id) {
	    final Slot slot = _transaction.getCurrentSlotOfID(id);
	    final Pair<Integer, Slot> pair = Pair.of(id, slot);
	    return pair;
    }

	private int configuredPrefetchObjectCount() {
	    return _config.prefetchCount;
    }
	

}
