/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside;

import com.db4o.foundation.*;


class PendingClassInits {
	
    private final Transaction _systemTransaction;
	
	private Collection4 _pending = new Collection4();

	private Queue4 _members = new Queue4();
	private Queue4 _statics = new Queue4();
    private Queue4 _writes = new Queue4();
    private Queue4 _inits = new Queue4();
	
	private boolean _running = false;
	
	PendingClassInits(Transaction systemTransaction){
        _systemTransaction = systemTransaction;
	}
	
	void process(YapClass newYapClass) {
		
		if(_pending.contains(newYapClass)) {
			return;
		}
		
        YapClass ancestor = newYapClass.getAncestor();
        if (ancestor != null) {
            process(ancestor);
        }
		
		_pending.add(newYapClass);
        
        _members.add(newYapClass);
        
		
		if(_running) {
			return;
		}
		
		_running = true;
		
		checkInits();
		
		_pending = new Collection4();
		
		_running = false;
	}

	
	private void checkMembers() {
		while(_members.hasNext()) {
			YapClass yc = (YapClass)_members.next();
			yc.addMembers(stream());
            _statics.add(yc);
		}
	}

    private ObjectContainerBase stream() {
        return _systemTransaction.stream();
    }
	
	private void checkStatics() {
		checkMembers();
		while(_statics.hasNext()) {
			YapClass yc = (YapClass)_statics.next();
			yc.storeStaticFieldValues(_systemTransaction, true);
			_writes.add(yc);
			checkMembers();
		}
	}
	
	private void checkWrites() {
		checkStatics();
		while(_writes.hasNext()) {
			YapClass yc = (YapClass)_writes.next();
	        yc.setStateDirty();
	        yc.write(_systemTransaction);
            _inits.add(yc);
			checkStatics();
		}
	}
    
    private void checkInits() {
        checkWrites();
        while(_inits.hasNext()) {
            YapClass yc = (YapClass)_inits.next();
            yc.initConfigOnUp(_systemTransaction);
            checkWrites();
        }
    }


}
