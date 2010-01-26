/* Copyright (C) 2004 - 2006  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.slots;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;


/**
 * @exclude
 */
public class SlotChange extends TreeInt {
	
	private static final boolean NEW_LOGIC_ENABLED = true;
	
	private static class SlotChangeOperation {
		
		private final String _type;
		
		public SlotChangeOperation(String type) {
			_type = type;
		}

		static final SlotChangeOperation create = new SlotChangeOperation("create");
		
		static final SlotChangeOperation update = new SlotChangeOperation("update");
		
		static final SlotChangeOperation delete = new SlotChangeOperation("delete");
		
		@Override
		public String toString() {
			return _type;
		}
		
	}
	
	private final Collection4 _freed = new Collection4();
	
	private SlotChangeOperation _firstOperation;
	
	private SlotChangeOperation _currentOperation;
	
	private int _action;

	private Slot _newSlot;

	private ReferencedSlot _shared;

	private static final int FREE_ON_COMMIT_BIT = 1;

	private static final int FREE_ON_ROLLBACK_BIT = 2;

	private static final int SET_POINTER_BIT = 3;
	
	private static final int FREE_POINTER_ON_COMMIT_BIT = 4;
	
    private static final int FREE_POINTER_ON_ROLLBACK_BIT = 5; 
    
    private static final int FREESPACE_BIT = 6;
    
    public SlotChange _newLogic;

	private int _numberOfOperationCalls;
    
	public SlotChange(int id) {
		super(id);
		createNewLogic(id);
	}
	
	protected SlotChange(int id, boolean isNewLogic){
		super(id);
		_newLogic = null;
	}
	
	protected void createNewLogic(int id){
		_newLogic = new SlotChange(id, true);
	}

	public Object shallowClone() {
		SlotChange sc = new SlotChange(0);
		sc._action = _action;
		sc.newSlot(_newSlot);
		sc._shared = _shared;
		sc._newLogic._key = _key;
		return super.shallowCloneInternal(sc);
	}

	private final void doFreeOnCommit() {
		setBit(FREE_ON_COMMIT_BIT);
	}

	private final void doFreeOnRollback() {
		setBit(FREE_ON_ROLLBACK_BIT);
	}
	
	private final void doFreePointerOnCommit(){
		setBit(FREE_POINTER_ON_COMMIT_BIT);
	}
	
	private final void doFreePointerOnRollback(){
		setBit(FREE_POINTER_ON_ROLLBACK_BIT);
	}

	private final void doSetPointer() {
		setBit(SET_POINTER_BIT);
	}

	public void freeDuringCommit(LocalObjectContainer file, boolean forFreespace) {
		
        if (isFreeOnCommit() && (isForFreeSpace() == forFreespace)) {
        	free(file, _shared.slot());
            file.freeDuringCommit(_shared, _newSlot);
        }
        
        if( ! NEW_LOGIC_ENABLED){
        	return;
        }
        Collection4 freedByOldLogic = new Collection4();
        if(isForFreeSpace() == forFreespace){
        	freedByOldLogic.addAll(_freed);
        }
        Collection4 freedByNewLogic = new Collection4();
        
        if( (_newLogic instanceof FreespaceSlotChange) == forFreespace){
        	if(_newLogic._firstOperation != SlotChangeOperation.create){
        		if(_newLogic._currentOperation == SlotChangeOperation.update || _newLogic._currentOperation == SlotChangeOperation.delete){
        			Slot slot = file.idSystem().getCommittedSlotOfID(_key);
        			
        			// If we don't get a valid slot, the object may have just 
        			// been stored by the SystemTransaction and not committed yet. 
        			if(! (this instanceof SystemSlotChange)){
	        			if(slot == null || slot.isNull()){
	        				slot = file.idSystem().getCurrentSlotOfID((LocalTransaction)file.systemTransaction(), _key);
	        			}
        			}
        			
        			// No old slot at all. This can be the case if the object
        			// has been deleted by another transaction and we add it again.
        			if(slot != null && ! slot.isNull()){
        				_newLogic.free(file, slot);
        			}
        		}
        	}
        	freedByNewLogic.addAll(_newLogic._freed);
        }
        
        assertSameContent(freedByOldLogic, freedByNewLogic);
	}
	
	private void assertSameContent(Collection4 expectedList, Collection4 actualList) {
		Iterator4 expected = expectedList.iterator();
		Iterator4 actual = actualList.iterator();
		final Collection4 allExpected = new Collection4();
		while(expected.moveNext()){
			allExpected.add(expected.current());
		}
		while (actual.moveNext()) {
			final Object current = actual.current();
			final boolean removed = allExpected.remove(current);
			if (! removed) {
				newLogicDiffersFromOld(expectedList, actualList);
			}
		}
		if(! allExpected.isEmpty()){
			newLogicDiffersFromOld(expectedList, actualList);
		}
	}
	
	private void newLogicDiffersFromOld(Collection4 expected, Collection4 actual){
		throw new IllegalStateException();
//		System.err.println("Freed slots differ");
//		System.err.println("Old " + expected);
//		System.err.println("New " + actual);
	}


	public final void freeOnCommit(LocalObjectContainer file, Slot slot) {
		
		if( this instanceof FreespaceSlotChange){
			if ( ! isForFreeSpace()){
				throw new IllegalStateException();
			}
		}

		if (_shared != null) {

			// second call or later.
			// The object has already been rewritten once, so we can free
			// directly

			file.free(slot);
			free(file, slot);
			return;
		}

		doFreeOnCommit();

		ReferencedSlot refSlot = file.produceFreeOnCommitEntry(_key);

		if (refSlot.addReferenceIsFirst()) {
			refSlot.pointTo(slot);
		}

		_shared = refSlot;
	}
	
	public void freeOnRollback(Slot slot) {
		doFreeOnRollback();
		newSlot(slot);
	}

	public void freeOnRollbackSetPointer(Slot slot) {
		doSetPointer();
		freeOnRollback(slot);
	}

	public void freePointerOnCommit() {
		doFreePointerOnCommit();
	}
	
	public void freePointerOnRollback() {
		doFreePointerOnRollback();
	}

	private final boolean isBitSet(int bitPos) {
		return (_action | (1 << bitPos)) == _action;
	}

	public boolean isDeleted() {
		return isSetPointer() && _newSlot.isNull();
	}
	
	public boolean isNew() {
		return isFreePointerOnRollback();
	}
    
    private final boolean isForFreeSpace() {
        return isBitSet(FREESPACE_BIT);
    }
    
	private final boolean isFreeOnCommit() {
		return isBitSet(FREE_ON_COMMIT_BIT);
	}

	private final boolean isFreeOnRollback() {
		boolean isBitSet = isBitSet(FREE_ON_ROLLBACK_BIT);
		boolean newFreeOnRollback = _newLogic._newSlot != null && ! _newLogic._newSlot.isNull();
		if(isBitSet != newFreeOnRollback){
			throw new IllegalStateException();
		}
		return isBitSet;
	}

	public final boolean isSetPointer() {
		boolean isBitSet = isBitSet(SET_POINTER_BIT);
		if( (_newLogic._newSlot == null)  == isBitSet){
			throw new IllegalStateException();
		}
		return isBitSet;
	}
	
	/**
	 * FIXME:	Check where pointers should be freed on commit.
	 * 			This should be triggered in this class.
	 */
//	private final boolean isFreePointerOnCommit() {
//		return isBitSet(FREE_POINTER_ON_COMMIT_BIT);
//	}

	public final boolean isFreePointerOnRollback() {
		boolean isBitSet = isBitSet(FREE_POINTER_ON_ROLLBACK_BIT);
		boolean newFreePointerOnRollback = _newLogic._firstOperation == SlotChangeOperation.create;
		if(isBitSet != newFreePointerOnRollback){
			throw new IllegalStateException();
		}
		return isBitSet;
	}

	public Slot newSlot() {
		return _newSlot;
	}
    
	public Object read(ByteArrayBuffer reader) {
		SlotChange change = new SlotChange(reader.readInt());
		Slot newSlot = new Slot(reader.readInt(), reader.readInt());
		change.newSlot(newSlot);
		change._newLogic._newSlot = newSlot;
		change.doSetPointer();
		return change;
	}

	public void rollback(LocalObjectContainer container) {
		if (_shared != null) {
			container.reduceFreeOnCommitReferences(_shared);
		}
		if (isFreeOnRollback()) {
			container.free(_newSlot);
		}
		if(isFreePointerOnRollback()){
		    if(DTrace.enabled){
		        DTrace.FREE_POINTER_ON_ROLLBACK.logLength(_key, Const4.POINTER_LENGTH);
		    }
			container.free(_key, Const4.POINTER_LENGTH);
		}
	}

	private final void setBit(int bitPos) {
		_action |= (1 << bitPos);
	}

	public void setPointer(Slot slot) {
		doSetPointer();
		newSlot(slot);
	}

	public void write(ByteArrayBuffer writer) {
		compareOldLogicWithNewLogic();
		if (isSetPointer()) {
			writer.writeInt(_key);
			writer.writeInt(_newSlot.address());
			writer.writeInt(_newSlot.length());
		} 
	}

	public final void writePointer(LocalObjectContainer container) {
		compareOldLogicWithNewLogic();
		if (isSetPointer()) {
			container.writePointer(_key, _newSlot);
		}
	}

	private void compareOldLogicWithNewLogic() {
		if(NEW_LOGIC_ENABLED){
			if(_newLogic._newSlot == null){
				if(isSetPointer()){
					throw new IllegalStateException();
				}
			} else {
				if(! _newLogic._newSlot.equals(_newSlot)){
					throw new IllegalStateException();
				}
			}
		}
	}

    public void forFreespace(boolean flag) {
        if(flag){
            setBit(FREESPACE_BIT);
        }
    }
    
    private void newSlot(Slot slot){
    	_newSlot = slot;
    }

	public void notifySlotChanged(LocalObjectContainer file, Slot slot) {
		if(! NEW_LOGIC_ENABLED){
			return;
		}
		if(DTrace.enabled){
			DTrace.NOTIFY_SLOT_CHANGED.log(_key);
			DTrace.NOTIFY_SLOT_CHANGED.logLength(slot);
		}
		freePreviouslyModifiedSlot(file);
		_newLogic._newSlot = slot;
		operation(SlotChangeOperation.update);
	}

	protected void freePreviouslyModifiedSlot(LocalObjectContainer file) {
		if(_newLogic._newSlot == null ){
			return;
		}
		if(_newLogic._newSlot.isNull()){
			return;
		}
		_newLogic.free(file, _newLogic._newSlot);
		_newLogic._newSlot = null;
		
	}

	private void free(LocalObjectContainer file, Slot slot) {
		if(slot.isNull()){
			return;
		}
		_freed.add(slot);
		// file.free(slot);
	}

	private void operation(SlotChangeOperation operation) {
		_numberOfOperationCalls++;
		_newLogic._numberOfOperationCalls++;
		if(_newLogic._firstOperation == null){
			_newLogic._firstOperation = operation;
		}
		_newLogic._currentOperation = operation;
	}

	public void notifySlotCreated(Slot slot) {
		if(! NEW_LOGIC_ENABLED){
			return;
		}
		if(DTrace.enabled){
			DTrace.NOTIFY_SLOT_CREATED.log(_key);
			DTrace.NOTIFY_SLOT_CREATED.logLength(slot);
		}
		operation(SlotChangeOperation.create);
		_newLogic._newSlot = slot;
	}

	public void notifyDeleted(LocalObjectContainer file) {
		if(! NEW_LOGIC_ENABLED){
			return;
		}
		if(DTrace.enabled){
			DTrace.NOTIFY_SLOT_DELETED.log(_key);
		}
		operation(SlotChangeOperation.delete);
		freePreviouslyModifiedSlot(file);
		_newLogic._newSlot = Slot.ZERO;
	}
    
}
