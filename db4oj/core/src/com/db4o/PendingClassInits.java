package com.db4o;

import com.db4o.foundation.*;

class PendingClassInits {
	
	private final YapClassCollection _classColl;
	
	private Collection4 _pending = new Collection4();

	private List4 _members;
	private List4 _statics;
    private List4 _writes;
    private List4 _inits;
	
	private boolean _running = false;
	
	PendingClassInits(YapClassCollection classColl){
		_classColl = classColl;
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
		_members = new List4(_members, newYapClass);
		
		if(_running) {
			return;
		}
		
		_running = true;
		
		checkInits();
		
		_pending = new Collection4();
		
		_running = false;
	}

	
	private void checkMembers() {
		while(_members != null) {
			Iterator4 members = new Iterator4Impl(_members);
			_members = null;
			while(members.hasNext()) {
				YapClass yc = (YapClass)members.next();
				yc.addMembers(_classColl.i_stream);
				_statics = new List4(_statics, yc);
			}
		}
	}
	
	private void checkStatics() {
		checkMembers();
		while(_statics != null) {
			Iterator4 statics = new Iterator4Impl(_statics);
			_statics = null;
			while(statics.hasNext()) {
				YapClass yc = (YapClass)statics.next();
				yc.storeStaticFieldValues(_classColl.i_systemTrans, true);
				_writes = new List4(_writes, yc);
				checkMembers();
			}
		}
	}
	
	private void checkWrites() {
		checkStatics();
		while(_writes != null) {
			Iterator4 writes = new Iterator4Impl(_writes);
			_writes = null;
			while(writes.hasNext()) {
				YapClass yc = (YapClass)writes.next();
		        yc.setStateDirty();
		        yc.write(_classColl.i_systemTrans);
                _inits = new List4(_inits, yc);
				checkStatics();
			}
		}
	}
    
    private void checkInits() {
        checkWrites();
        while(_inits != null) {
            Iterator4 inits = new Iterator4Impl(_inits);
            _inits = null;
            while(inits.hasNext()) {
                YapClass yc = (YapClass)inits.next();
                yc.initConfigOnUp(_classColl.i_systemTrans);
                checkWrites();
            }
        }
    }


}
