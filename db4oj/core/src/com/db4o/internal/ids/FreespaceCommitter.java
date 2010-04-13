/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.ids;

import java.util.*;

import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class FreespaceCommitter {
	
	public static final FreespaceCommitter DO_NOTHING = new NullFreespaceCommitter();
	
	private final List<TransactionalIdSystem> _idSystems = new ArrayList<TransactionalIdSystem>();
	
	private final List<Slot> _toFree = new ArrayList<Slot>();
	
	private final FreespaceManager _freespaceManager;
	
	public FreespaceCommitter(FreespaceManager freespaceManager) {
		_freespaceManager = freespaceManager == null ? NullFreespaceManager.INSTANCE : freespaceManager;
	}
	
	public void commit() {
		apply();
		_freespaceManager.beginCommit();
		_freespaceManager.commit();
		accumulate(true);
		apply();
		_freespaceManager.endCommit();
	}

	private void apply() {
		for(Slot slot : _toFree){
			_freespaceManager.free(slot);
		}
		_toFree.clear();
	}

	private void accumulate(boolean forFreespace) {
		for (TransactionalIdSystem idSystem : _idSystems) {
			idSystem.accumulateFreeSlots(this, forFreespace);
		}
	}

	public void register(TransactionalIdSystem transactionalIdSystem) {
		_idSystems.add(transactionalIdSystem);
	}
	
	private static class NullFreespaceCommitter extends FreespaceCommitter {

		public NullFreespaceCommitter() {
			super(NullFreespaceManager.INSTANCE);
		}
		
		@Override
		public void register(TransactionalIdSystem transactionalIdSystem) {
			// do nothing
		}
		
		@Override
		public void commit() {
			// do nothing
		}
		
	}

	public void delayedFree(Slot slot) {
		_toFree.add(slot);
	}

}
