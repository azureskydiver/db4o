/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;


class PendingClassInits {
	
    private final Transaction _systemTransaction;
	
	private Collection4 _pending = new Collection4();

	private Queue4 _members = new NonblockingQueue();
	private Queue4 _statics = new NonblockingQueue();
    private Queue4 _writes = new NonblockingQueue();
    private Queue4 _inits = new NonblockingQueue();
	
	private boolean _running = false;
	
	PendingClassInits(Transaction systemTransaction){
        _systemTransaction = systemTransaction;
	}
	
	void process(ClassMetadata newClassMetadata) {
		
		if(_pending.contains(newClassMetadata)) {
			return;
		}
		
        final ClassMetadata ancestor = newClassMetadata.getAncestor();
        if (ancestor != null) {
            process(ancestor);
        }
		
		_pending.add(newClassMetadata);
        _members.add(newClassMetadata);
		
		if(_running) {
			return;
		}
		
		_running = true;
		try {
			checkInits();
			_pending = new Collection4();
		} finally {
			_running = false;
		}
	}

	
	private void checkMembers() {
		while(_members.hasNext()) {
			ClassMetadata classMetadata = (ClassMetadata)_members.next();
			classMetadata.addMembers(stream());
            _statics.add(classMetadata);
		}
	}

    private ObjectContainerBase stream() {
        return _systemTransaction.container();
    }
	
	private void checkStatics() {
		checkMembers();
		while(_statics.hasNext()) {
			ClassMetadata yc = (ClassMetadata)_statics.next();
			yc.storeStaticFieldValues(_systemTransaction, true);
			_writes.add(yc);
			checkMembers();
		}
	}
	
	private void checkWrites() {
		checkStatics();
		while(_writes.hasNext()) {
			ClassMetadata yc = (ClassMetadata)_writes.next();
	        yc.setStateDirty();
	        yc.write(_systemTransaction);
            _inits.add(yc);
			checkStatics();
		}
	}
    
    private void checkInits() {
        checkWrites();
        while(_inits.hasNext()) {
            ClassMetadata yc = (ClassMetadata)_inits.next();
            yc.initConfigOnUp(_systemTransaction);
            checkWrites();
        }
    }


}
