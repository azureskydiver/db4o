/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal.objectexchange;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

public class EagerObjectWriter {

	private LocalTransaction _transaction;
	private ObjectExchangeConfiguration _config;

	public EagerObjectWriter(ObjectExchangeConfiguration config, LocalTransaction transaction) {
		_config = config;
		_transaction = transaction;
    }
	
	public ByteArrayBuffer write(IntIterator4 idIterator, int maxCount) {
		
		List<Pair<Integer, Slot>> rootSlots = readSlots(idIterator, maxCount);
		List<Pair<Integer, Slot>> childSlots = childSlotsFor(rootSlots);
		
		int marshalledSize = marshalledSizeFor(rootSlots) + marshalledSizeFor(childSlots);
		
		ByteArrayBuffer buffer = new ByteArrayBuffer(marshalledSize);
		writeIdSlotPairsTo(childSlots, buffer);
		writeIdSlotPairsTo(rootSlots, buffer);
		
		return buffer;
	}

	private List<Pair<Integer, Slot>> childSlotsFor(List<Pair<Integer, Slot>> rootSlots) {
	    final Iterator4 ids = Iterators.map(Iterators.iterator(rootSlots), new Function4<Pair<Integer, Slot>, Integer>() {
			public Integer apply(Pair<Integer, Slot> arg) {
				return arg.first;
           }
		});
		return new ChildSlotCollector(_config, new StandardReferenceCollector(_transaction), new StandardSlotAccessor(_transaction)).collect(ids);
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
	    return Pair.of(id, slot);
    }

	private int configuredPrefetchObjectCount() {
	    return _config.prefetchCount;
    }
	

}
