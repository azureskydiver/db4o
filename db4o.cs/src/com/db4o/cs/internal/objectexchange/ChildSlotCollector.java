package com.db4o.cs.internal.objectexchange;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.slots.*;

public class ChildSlotCollector {
	
	private ObjectExchangeConfiguration _config;
	private SlotAccessor _slotAccessor;
	private ReferenceCollector _referenceCollector;
	
	public ChildSlotCollector(ObjectExchangeConfiguration config, ReferenceCollector collector, SlotAccessor accessor) {
	    _config = config;
	    _slotAccessor = accessor;
	    _referenceCollector = collector;
    }

	public List<Pair<Integer, Slot>> collect(Iterator4 roots) {
		return childSlotsFor(roots);
    }

	private List<Pair<Integer, Slot>> childSlotsFor(Iterator4 slots) {
		
		final ArrayList<Pair<Integer, Slot>> result = new ArrayList<Pair<Integer, Slot>>();
		if (_config.prefetchDepth < 2) {
			return result;
		}
		
		while (slots.moveNext()) {
			final int id = (Integer)slots.current();
			
			final Iterator4 childIds = collectChildIdsFor(id);
			while (childIds.moveNext()) {
				final Integer childId = (Integer)childIds.current();
				result.add(idSlotPairFor(childId));
			}
        }
		
		return result;
    }

	private Iterator4 collectChildIdsFor(final int id) {
		return _referenceCollector.referencesFrom(id);
    }
	
	private Pair<Integer, Slot> idSlotPairFor(final int id) {
		return Pair.of(id, _slotAccessor.currentSlotOfID(id));
    }
}
