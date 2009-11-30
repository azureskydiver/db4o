/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

public class IdSystem {
	private final byte[] _pointerBuffer = new byte[Const4.POINTER_LENGTH];
    
    protected final StatefulBuffer i_pointerIo;    
    
    private final LockedTree _slotChanges = new LockedTree();
	
	protected final LocalObjectContainer _file;
	
	private IdSystem _systemIdSystem;
	
	private TransactionLogHandler _transactionLogHandler = new EmbeddedTransactionLogHandler();

	public IdSystem(ObjectContainerBase container) {
		_file = (LocalObjectContainer) container;
        i_pointerIo = new StatefulBuffer(_file.transaction(), Const4.POINTER_LENGTH);        
    	initializeTransactionLogHandler();
	}

	private void initializeTransactionLogHandler() {
    	if(!isSystemIdSystem()){
    		_transactionLogHandler = _systemIdSystem._transactionLogHandler;
    		return;
    	}
		boolean fileBased = config().fileBasedTransactionLog() && file() instanceof IoAdaptedObjectContainer;
		if(! fileBased){
			_transactionLogHandler = new EmbeddedTransactionLogHandler();
			return;
		}
		String fileName = ((IoAdaptedObjectContainer)file()).fileName();
		_transactionLogHandler = new FileBasedTransactionLogHandler(this, fileName); 
	}

	private boolean isSystemIdSystem() {
		return _systemIdSystem == null;
	}

	public Config4Impl config() {
		return file().config();
	}

	public LocalObjectContainer file() {
		return _file;
	}
	
    public void commit() {
        synchronized (file().lock()) {
        	commitImpl();
        	commitClear();
        }
    }	

    private void commitImpl(){
        
        Slot reservedSlot = _transactionLogHandler.allocateSlot(this, false);
        
        freeSlotChanges(false);
                
        freespaceBeginCommit();
        
        commitFreespace();
        
        freeSlotChanges(true);
        
        _transactionLogHandler.applySlotChanges(this, reservedSlot);
        
        freespaceEndCommit();
    }
	
	public final void freeSlotChanges(final boolean forFreespace) {
        Visitor4 visitor = new Visitor4() {
            public void visit(Object obj) {
                ((SlotChange)obj).freeDuringCommit(_file, forFreespace);
            }
        };
        if(isSystemIdSystem()){
            _slotChanges.traverseMutable(visitor);
            return;
        }
        _slotChanges.traverseLocked(visitor);
        if(_systemIdSystem != null){
            _systemIdSystem.freeSlotChanges(forFreespace);
        }
    }
	
	private void commitClear(){
		if(_systemIdSystem  != null){
            _systemIdSystem.commitClear();
        }
        clear();
    }

	
	protected void clear() {
		_slotChanges.clear();
	}	
	
    public void rollback() {
        synchronized (file().lock()) {
            
            rollbackSlotChanges();            
            
            clear();
        }
    }
    
	protected void rollbackSlotChanges() {
		_slotChanges.traverseLocked(new Visitor4() {
            public void visit(Object a_object) {
                ((SlotChange) a_object).rollback(_file);
            }
        });
	}

	public boolean isDeleted(int id) {
    	return slotChangeIsFlaggedDeleted(id);
    }
	
    public void writeZeroPointer(int id){
        writePointer(id, Slot.ZERO);   
    }
    
    public void writePointer(Pointer4 pointer) {
        writePointer(pointer._id, pointer._slot);
    }

	public void writePointer(int id, Slot slot) {
        if(DTrace.enabled){
            DTrace.WRITE_POINTER.log(id);
            DTrace.WRITE_POINTER.logLength(slot);
        }
        i_pointerIo.useSlot(id);
        if (Deploy.debug) {
            i_pointerIo.writeBegin(Const4.YAPPOINTER);
        }
        i_pointerIo.writeInt(slot.address());
    	i_pointerIo.writeInt(slot.length());
        if (Deploy.debug) {
            i_pointerIo.writeEnd();
        }
        if (Debug4.xbytes && Deploy.overwrite) {
            i_pointerIo.setID(Const4.IGNORE_ID);
        }
        i_pointerIo.write();
    }
	
    public boolean writeSlots() {
        final BooleanByRef ret = new BooleanByRef();
        traverseSlotChanges(new Visitor4() {
			public void visit(Object obj) {
				((SlotChange)obj).writePointer(systemTransaction());
				ret.value = true;
			}
		});
        return ret.value;
    }
	
    public void flushFile(){
        if(DTrace.enabled){
            DTrace.TRANS_FLUSH.log();
        }
        _file.syncFiles();
    }
    
    private SlotChange produceSlotChange(int id){
    	if(DTrace.enabled){
    		DTrace.PRODUCE_SLOT_CHANGE.log(id);
    	}
        SlotChange slot = new SlotChange(id);
        _slotChanges.add(slot);
        return (SlotChange)slot.addedOrExisting();
    }    
    
    public final SlotChange findSlotChange(int a_id) {
        return (SlotChange)_slotChanges.find(a_id);
    }    

    public Slot getCurrentSlotOfID(int id) {
        if (id == 0) {
            return null;
        }
        SlotChange change = findSlotChange(id);
        if (change != null) {
            if(change.isSetPointer()){
                return change.newSlot();
            }
        }
        
        if (_systemIdSystem != null) {
            Slot parentSlot = _systemIdSystem.getCurrentSlotOfID(id); 
            if (parentSlot != null) {
                return parentSlot;
            }
        }
        return readPointer(id)._slot;
    }
    
    public Slot getCommittedSlotOfID(int id) {
        if (id == 0) {
            return null;
        }
        SlotChange change = findSlotChange(id);
        if (change != null) {
            Slot slot = change.oldSlot();
            if(slot != null){
                return slot;
            }
        }
        
        if (_systemIdSystem != null) {
            Slot parentSlot = _systemIdSystem.getCommittedSlotOfID(id); 
            if (parentSlot != null) {
                return parentSlot;
            }
        }
		return readPointer(id)._slot;
    }

    public Pointer4 readPointer(int id) {
        if (Deploy.debug) {
            return debugReadPointer(id);
        }
        if(!isValidId(id)){
        	throw new InvalidIDException(id);
        }
        
       	_file.readBytes(_pointerBuffer, id, Const4.POINTER_LENGTH);
        int address = (_pointerBuffer[3] & 255)
            | (_pointerBuffer[2] & 255) << 8 | (_pointerBuffer[1] & 255) << 16
            | _pointerBuffer[0] << 24;
        int length = (_pointerBuffer[7] & 255)
            | (_pointerBuffer[6] & 255) << 8 | (_pointerBuffer[5] & 255) << 16
            | _pointerBuffer[4] << 24;
        
        if(!isValidSlot(address, length)){
        	throw new InvalidSlotException(address, length, id);
        }
        
        return new Pointer4(id, new Slot(address, length));
    }

	private boolean isValidId(int id) {
		return _file.fileLength() >= id;
	}

	private boolean isValidSlot(int address, int length) {
		// just in case overflow 
		long fileLength = _file.fileLength();
		
		boolean validAddress = fileLength >= address;
        boolean validLength = fileLength >= length ;
        boolean validSlot = fileLength >= (address+length);
        
        return validAddress && validLength && validSlot;
	}

	private Pointer4 debugReadPointer(int id) {
        if (Deploy.debug) {
    		i_pointerIo.useSlot(id);
    		i_pointerIo.read();
    		i_pointerIo.readBegin(Const4.YAPPOINTER);
    		int debugAddress = i_pointerIo.readInt();
    		int debugLength = i_pointerIo.readInt();
    		i_pointerIo.readEnd();
    		return new Pointer4(id, new Slot(debugAddress, debugLength));
        }
        return null;
	}
    
    public void setPointer(int a_id, Slot slot) {
        if(DTrace.enabled){
            DTrace.SLOT_SET_POINTER.log(a_id);
            DTrace.SLOT_SET_POINTER.logLength(slot);
        }
        produceSlotChange(a_id).setPointer(slot);
    }
    
    private boolean slotChangeIsFlaggedDeleted(int id){
        SlotChange slot = findSlotChange(id);
        if (slot != null) {
            return slot.isDeleted();
        }
        if (_systemIdSystem != null) {
            return _systemIdSystem.slotChangeIsFlaggedDeleted(id);
        }
        return false;
    }
	
	final void completeInterruptedTransaction() {
        synchronized (file().lock()) {
        	_transactionLogHandler.completeInterruptedTransaction(this);
        }
    }
	
	public void traverseSlotChanges(Visitor4 visitor){
        if(_systemIdSystem != null){
        	_systemIdSystem.traverseSlotChanges(visitor);
        }
        _slotChanges.traverseLocked(visitor);
	}
	
	public void slotDelete(int id, Slot slot) {
        if(DTrace.enabled){
            DTrace.SLOT_DELETE.log(id);
            DTrace.SLOT_DELETE.logLength(slot);
        }
        if (id == 0) {
            return;
        }
        SlotChange slotChange = produceSlotChange(id);
        slotChange.freeOnCommit(_file, slot);
        slotChange.setPointer(Slot.ZERO);
    }
	
    public void slotFreeOnCommit(int id, Slot slot) {
        if(DTrace.enabled){
            DTrace.SLOT_FREE_ON_COMMIT.log(id);
            DTrace.SLOT_FREE_ON_COMMIT.logLength(slot);
        }
        if (id == 0) {
            return;
        }
        produceSlotChange(id).freeOnCommit(_file, slot);
    }

    public void slotFreeOnRollback(int id, Slot slot) {
        if(DTrace.enabled){
            DTrace.SLOT_FREE_ON_ROLLBACK_ID.log(id);
            DTrace.SLOT_FREE_ON_ROLLBACK_ADDRESS.logLength(slot);
        }
        produceSlotChange(id).freeOnRollback(slot);
    }

    void slotFreeOnRollbackCommitSetPointer(int id, Slot newSlot, boolean forFreespace) {
        
        Slot oldSlot = getCurrentSlotOfID(id);
        if(oldSlot==null) {
        	return;
        }
        
        if(DTrace.enabled){
            DTrace.FREE_ON_ROLLBACK.log(id);
            DTrace.FREE_ON_ROLLBACK.logLength(newSlot);
            DTrace.FREE_ON_COMMIT.log(id);
            DTrace.FREE_ON_COMMIT.logLength(oldSlot);
        }
        
        SlotChange change = produceSlotChange(id);
        change.freeOnRollbackSetPointer(newSlot);
        change.freeOnCommit(_file, oldSlot);
        change.forFreespace(forFreespace);
    }

    void produceUpdateSlotChange(int id, Slot slot) {
        if(DTrace.enabled){
            DTrace.FREE_ON_ROLLBACK.log(id);
            DTrace.FREE_ON_ROLLBACK.logLength(slot);
        }
        
        final SlotChange slotChange = produceSlotChange(id);
        slotChange.freeOnRollbackSetPointer(slot);
    }
    
    public void slotFreePointerOnCommit(int a_id) {
        Slot slot = getCurrentSlotOfID(a_id);
        if(slot == null){
            return;
        }
        
        // FIXME: From looking at this it should call slotFreePointerOnCommit
        //        Write a test case and check.
        
        //        Looking at references, this method is only called from freed
        //        BTree nodes. Indeed it should be checked what happens here.
        
        slotFreeOnCommit(a_id, slot);
    }
    
    void slotFreePointerOnCommit(int a_id, Slot slot) {
        slotFreeOnCommit(slot.address(), slot);
        
        // FIXME: This does not look nice
        slotFreeOnCommit(a_id, slot);
        
        // FIXME: It should rather work like this:
        // produceSlotChange(a_id).freePointerOnCommit();
    }
    
    public void slotFreePointerOnRollback(int id) {
    	produceSlotChange(id).freePointerOnRollback();
    }

	public CallbackObjectInfoCollections collectCommittingCallbackInfo(final LocalTransaction trans) {
		if (null == _slotChanges) {
			return CallbackObjectInfoCollections.EMTPY;
		}
		
		final Collection4 added = new Collection4();
		final Collection4 deleted = new Collection4();
		final Collection4 updated = new Collection4();		
		collectSlotChanges(new SlotChangeCollector() {
			public void added(int id) {
				added.add(trans.lazyReferenceFor(id));
			}

			public void updated(int id) {
				updated.add(trans.lazyReferenceFor(id));
			}
			
			public void deleted(int id){
				ObjectInfo ref = trans.frozenReferenceFor(id);
				if(ref != null){
					deleted.add(ref);
				}
			}
		});
		return newCallbackObjectInfoCollections(added, updated, deleted);
	}

	private CallbackObjectInfoCollections newCallbackObjectInfoCollections(
			final Collection4 added,
			final Collection4 updated,
			final Collection4 deleted) {
		return new CallbackObjectInfoCollections(
				new ObjectInfoCollectionImpl(added),
				new ObjectInfoCollectionImpl(updated),
				new ObjectInfoCollectionImpl(deleted));
	}

	private void collectSlotChanges(final SlotChangeCollector collector) {
		if (null == _slotChanges) {
			return;
		}
		_slotChanges.traverseLocked(new Visitor4() {
			public void visit(Object obj) {
				final SlotChange slotChange = ((SlotChange)obj);
				final int id = slotChange._key;
				if (slotChange.isDeleted()) {
					if(! slotChange.isNew()){
						collector.deleted(id);
					}
				} else if (slotChange.isNew()) {
					collector.added(id);
				} else {
					collector.updated(id);
				}
			}
		});
	}
	
	public static Transaction readInterruptedTransaction(LocalObjectContainer file, ByteArrayBuffer reader) {
		LocalTransaction transaction = (LocalTransaction) file.newTransaction(null, null);
		if(transaction.wasInterrupted(reader)){
			return transaction;
		}
	    return null;
	}
	
	public boolean wasInterrupted(ByteArrayBuffer reader){
		return _transactionLogHandler.checkForInterruptedTransaction(this, reader);
	}
	
	public FreespaceManager freespaceManager(){
		return _file.freespaceManager();
	}
	
    private void freespaceBeginCommit(){
        if(freespaceManager() == null){
            return;
        }
        freespaceManager().beginCommit();
    }
    
    private void freespaceEndCommit(){
        if(freespaceManager() == null){
            return;
        }
        freespaceManager().endCommit();
    }
    
    private void commitFreespace(){
        if(freespaceManager() == null){
            return;
        }
        freespaceManager().commit();
    }

	public void readSlotChanges(ByteArrayBuffer buffer) {
		_slotChanges.read(buffer, new SlotChange(0));
	}

	public void close() {
		_transactionLogHandler.close();
	}

	public LocalTransaction systemTransaction() {
		return (LocalTransaction) file().systemTransaction();
	}
}
