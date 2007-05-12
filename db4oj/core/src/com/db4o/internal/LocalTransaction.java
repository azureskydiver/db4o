/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.internal;

import java.io.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.callbacks.*;
import com.db4o.internal.cs.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class LocalTransaction extends Transaction {

    private final byte[] _pointerBuffer = new byte[Const4.POINTER_LENGTH];
    
    protected final StatefulBuffer i_pointerIo;    
    
    private int i_address;	// only used to pass address to Thread
	
    private final Collection4 _participants = new Collection4(); 

    private final LockedTree _slotChanges = new LockedTree();
	
    private Tree _writtenUpdateDeletedMembers;
    
	private final LocalObjectContainer _file;

	public LocalTransaction(ObjectContainerBase container, Transaction parent) {
		super(container, parent);
		_file = (LocalObjectContainer) container;
        i_pointerIo = new StatefulBuffer(this, Const4.POINTER_LENGTH);
	}
	
	public LocalObjectContainer file() {
		return _file;
	}
	
    public void commit() {
    	commit(null);
    }
    
    public void commit(ServerMessageDispatcher dispatcher) {
        synchronized (stream().i_lock) {
        	if(doCommittingCallbacks()){
        		callbacks().commitOnStarted(this, collectCallbackObjectInfos(dispatcher));
        	}
            freespaceBeginCommit();
            commitImpl();
            CallbackObjectInfoCollections committedInfo = null;
        	if(doCommittedCallbacks()){
        		committedInfo = collectCallbackObjectInfos(dispatcher);
        	} 
            commitClearAll();
            freespaceEndCommit();
            if(doCommittedCallbacks()){
    	        if(dispatcher == null){
    	        	callbacks().commitOnCompleted(this, committedInfo);
    	        } else {
    	        	dispatcher.committedInfo(committedInfo);
    	        }
            } 
        }
    }

    private boolean doCommittedCallbacks() {
		return ! isSystemTransaction(); 
		// TODO: #COR-433 adjust
		// return ! isSystemTransaction() && callbacks().caresAboutCommitted();
	}

	private boolean doCommittingCallbacks() {
		return ! isSystemTransaction() && callbacks().caresAboutCommitting();
	}
    
	public void enlist(TransactionParticipant participant) {
		if (null == participant) {
			throw new ArgumentNullException();
		}
		checkSynchronization();	
		if (!_participants.containsByIdentity(participant)) {
			_participants.add(participant);
		}
	}

	private void commitImpl(){
        
        if(DTrace.enabled){
            DTrace.TRANS_COMMIT.logInfo( "server == " + stream().isServer() + ", systemtrans == " +  isSystemTransaction());
        }
        
        commit2Listeners();
        
        commit3Stream();
        
        commit4FieldIndexes();
        
        commitParticipants();
        
        stream().writeDirty();
        
        Slot reservedSlot = allocateTransactionLogSlot(false);
        
        freeSlotChanges();
        
        commitFreespace();
        
        if(_systemTransaction != null){
        	((LocalTransaction)_systemTransaction).freeSlotChanges();
        }
        
        commit6WriteChanges(reservedSlot);
    }
	
	private final void freeSlotChanges() {
        _slotChanges.traverse(new Visitor4() {
			public void visit(Object obj) {
				((SlotChange)obj).freeDuringCommit(_file);
			}
		});
    }
	
	private void commit2Listeners(){
        commitParentListeners(); 
        commitTransactionListeners();
    }

	private void commitParentListeners() {
		if (_systemTransaction != null) {
            parentLocalTransaction().commit2Listeners();
        }
	}
	
    private void commitParticipants() {
        if (parentLocalTransaction() != null) {
        	parentLocalTransaction().commitParticipants();
        }
        
        Iterator4 iterator = _participants.iterator();
		while (iterator.moveNext()) {
			((TransactionParticipant)iterator.current()).commit(this);
		}
    }
    
    private void commit3Stream(){
        stream().processPendingClassUpdates();
        stream().writeDirty();
        stream().classCollection().write(stream().systemTransaction());
    }
    
	private LocalTransaction parentLocalTransaction() {
		return (LocalTransaction) _systemTransaction;
	}
    
	private void commitClearAll(){
		if(_systemTransaction != null){
            parentLocalTransaction().commitClearAll();
        }
        clearAll();
    }

	
	protected void clear() {
		_slotChanges.clear();
		disposeParticipants();
        _participants.clear();
	}
	
	private void disposeParticipants() {
		Iterator4 iterator = _participants.iterator();
        while (iterator.moveNext()) {
        	((TransactionParticipant)iterator.current()).dispose(this);
        }
	}
	
    public void rollback() {
        synchronized (stream().i_lock) {
            
            rollbackParticipants();
            
            rollbackFieldIndexes();
            
            rollbackSlotChanges();
            
            rollBackTransactionListeners();
            
            clearAll();
        }
    }
    
    private void rollbackParticipants() {
    	Iterator4 iterator = _participants.iterator();
		while (iterator.moveNext()) {
			((TransactionParticipant)iterator.current()).rollback(this);
		}
	}
	
	protected void rollbackSlotChanges() {
		_slotChanges.traverse(new Visitor4() {
            public void visit(Object a_object) {
                ((SlotChange) a_object).rollback(_file);
            }
        });
	}

	public boolean isDeleted(int id) {
    	return slotChangeIsFlaggedDeleted(id);
    }
	
    private Slot allocateTransactionLogSlot(boolean appendToFile){
    	if(freespaceManager() != null){
    		int nonBlockedLength = transactionLogSlotLength();
    		int blockedLength = _file.bytesToBlocks(nonBlockedLength);
    		Slot slot = freespaceManager().allocateTransactionLogSlot(blockedLength);
    		if(slot != null){
    			return _file.toNonBlockedLength(slot);
    		}
    	}
    	if(! appendToFile){
    		return null;
    	}
    	return _file.appendSlot(transactionLogSlotLength());
    }
    
    private int transactionLogSlotLength(){
    	// slotchanges * 3 for ID, address, length
    	// 2 ints for slotlength and count
    	return ((countSlotChanges() * 3) + 2) * Const4.INT_LENGTH;
    }
    
    private boolean slotLongEnoughForLog(Slot slot){
    	return slot != null  &&  slot.length() >= transactionLogSlotLength();
    }
    

	protected void commit6WriteChanges(Slot reservedSlot) {
        checkSynchronization();
            
        int slotChangeCount = countSlotChanges();
        
        if (slotChangeCount > 0) {

			Slot transactionLogSlot = slotLongEnoughForLog(reservedSlot) ? reservedSlot
				: allocateTransactionLogSlot(true);

			final StatefulBuffer buffer = new StatefulBuffer(this, transactionLogSlot);
			buffer.writeInt(transactionLogSlot.length());
			buffer.writeInt(slotChangeCount);

			appendSlotChanges(buffer);

			buffer.write();
			flushFile();

			stream().writeTransactionPointer(transactionLogSlot.address());
			flushFile();

			if (writeSlots()) {
				flushFile();
			}

			stream().writeTransactionPointer(0);
			flushFile();
			
			if (transactionLogSlot != reservedSlot) {
				freeTransactionLogSlot(transactionLogSlot);
			}
		}
        freeTransactionLogSlot(reservedSlot);
    }
	
    private void freeTransactionLogSlot(Slot slot) {
    	if(slot == null){
    		return;
    	}
    	freespaceManager().freeTransactionLogSlot(_file.toNonBlockedLength(slot));
	}

	public void writePointer(int id, Slot slot) {
        if(DTrace.enabled){
            DTrace.WRITE_POINTER.log(id);
            DTrace.WRITE_POINTER.logLength(slot);
        }
        checkSynchronization();
        i_pointerIo.useSlot(id);
        if (Deploy.debug) {
            i_pointerIo.writeBegin(Const4.YAPPOINTER);
        }
        i_pointerIo.writeSlot(slot);
        if (Deploy.debug) {
            i_pointerIo.writeEnd();
        }
        if (Debug.xbytes && Deploy.overwrite) {
            i_pointerIo.setID(Const4.IGNORE_ID);
        }
        i_pointerIo.write();
    }
    

	
    private boolean writeSlots() {
        final MutableBoolean ret = new MutableBoolean();
        traverseSlotChanges(new Visitor4() {
			public void visit(Object obj) {
				((SlotChange)obj).writePointer(LocalTransaction.this);
				ret.set(true);
			}
		});
        return ret.value();
    }
	
    public void flushFile(){
        if(DTrace.enabled){
            DTrace.TRANS_FLUSH.log();
        }
        if(_file.configImpl().flushFileBuffers()){
            _file.syncFiles();
        }
    }
    
    private SlotChange produceSlotChange(int id){
    	if(DTrace.enabled){
    		DTrace.PRODUCE_SLOT_CHANGE.log(id);
    	}
        SlotChange slot = new SlotChange(id);
        _slotChanges.add(slot);
        return (SlotChange)slot.addedOrExisting();
    }
    
    
    private final SlotChange findSlotChange(int a_id) {
        checkSynchronization();
        return (SlotChange)_slotChanges.find(a_id);
    }    

    public Slot getCurrentSlotOfID(int id) {
        checkSynchronization();
        if (id == 0) {
            return null;
        }
        SlotChange change = findSlotChange(id);
        if (change != null) {
            if(change.isSetPointer()){
                return change.newSlot();
            }
        }
        
        if (_systemTransaction != null) {
            Slot parentSlot = parentLocalTransaction().getCurrentSlotOfID(id); 
            if (parentSlot != null) {
                return parentSlot;
            }
        }
        return readCommittedSlotOfID(id);
		
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
        
        if (_systemTransaction != null) {
            Slot parentSlot = parentLocalTransaction().getCommittedSlotOfID(id); 
            if (parentSlot != null) {
                return parentSlot;
            }
        }
		return readCommittedSlotOfID(id);
    }

    private Slot readCommittedSlotOfID(int id) {
        if (Deploy.debug) {
            return debugReadCommittedSlotOfID(id);
        }
       	_file.readBytes(_pointerBuffer, id, Const4.POINTER_LENGTH);

        int address = (_pointerBuffer[3] & 255)
            | (_pointerBuffer[2] & 255) << 8 | (_pointerBuffer[1] & 255) << 16
            | _pointerBuffer[0] << 24;
        int length = (_pointerBuffer[7] & 255)
            | (_pointerBuffer[6] & 255) << 8 | (_pointerBuffer[5] & 255) << 16
            | _pointerBuffer[4] << 24;
        return new Slot(address, length);
    }

	private Slot debugReadCommittedSlotOfID(int id) {
		i_pointerIo.useSlot(id);
		i_pointerIo.read();
		i_pointerIo.readBegin(Const4.YAPPOINTER);
		int debugAddress = i_pointerIo.readInt();
		int debugLength = i_pointerIo.readInt();
		i_pointerIo.readEnd();
		return new Slot(debugAddress, debugLength);
	}
    
    public void setPointer(int a_id, Slot slot) {
        if(DTrace.enabled){
            DTrace.SLOT_SET_POINTER.log(a_id);
            DTrace.SLOT_SET_POINTER.logLength(slot);
        }
        checkSynchronization();
        produceSlotChange(a_id).setPointer(slot);
    }
    
    private boolean slotChangeIsFlaggedDeleted(int id){
        SlotChange slot = findSlotChange(id);
        if (slot != null) {
            return slot.isDeleted();
        }
        if (_systemTransaction != null) {
            return parentLocalTransaction().slotChangeIsFlaggedDeleted(id);
        }
        return false;
    }
	
	private int countSlotChanges(){
        final MutableInt count = new MutableInt();
        traverseSlotChanges(new Visitor4() {
			public void visit(Object obj) {
                SlotChange slot = (SlotChange)obj;
                if(slot.isSetPointer()){
                    count.increment();
                }
			}
		});
        return count.value();
	}
	
	final void writeOld() {
        synchronized (stream().i_lock) {
            i_pointerIo.useSlot(i_address);
            i_pointerIo.read();
            int length = i_pointerIo.readInt();
            if (length > 0) {
                StatefulBuffer bytes = new StatefulBuffer(this, i_address, length);
                bytes.read();
                bytes.incrementOffset(Const4.INT_LENGTH);
                _slotChanges.read(bytes, new SlotChange(0));
                if(writeSlots()){
                    flushFile();
                }
                stream().writeTransactionPointer(0);
                flushFile();
                freeSlotChanges();
            } else {
                stream().writeTransactionPointer(0);
                flushFile();
            }
        }
    }
	
	private void appendSlotChanges(final Buffer writer){
		traverseSlotChanges(new Visitor4() {
			public void visit(Object obj) {
				((SlotChange)obj).write(writer);
			}
		});
    }
	
	private void traverseSlotChanges(Visitor4 visitor){
        if(_systemTransaction != null){
        	parentLocalTransaction().traverseSlotChanges(visitor);
        }
        _slotChanges.traverse(visitor);
	}
	
	public void slotDelete(int id, Slot slot) {
        checkSynchronization();
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
        checkSynchronization();
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
        checkSynchronization();
        if(DTrace.enabled){
            DTrace.SLOT_FREE_ON_ROLLBACK_ID.log(id);
            DTrace.SLOT_FREE_ON_ROLLBACK_ADDRESS.logLength(slot);
        }
        produceSlotChange(id).freeOnRollback(slot);
    }

    void slotFreeOnRollbackCommitSetPointer(int id, Slot newSlot) {
        
        Slot oldSlot = getCurrentSlotOfID(id);
        if(oldSlot==null) {
        	return;
        }
        
        checkSynchronization();
        
        if(DTrace.enabled){
            DTrace.FREE_ON_ROLLBACK.log(id);
            DTrace.FREE_ON_ROLLBACK.logLength(newSlot);
            DTrace.FREE_ON_COMMIT.log(id);
            DTrace.FREE_ON_COMMIT.logLength(oldSlot);
        }
        
        SlotChange change = produceSlotChange(id);
        change.freeOnRollbackSetPointer(newSlot);
        change.freeOnCommit(_file, oldSlot);
    }

    void produceUpdateSlotChange(int id, Slot slot) {
        checkSynchronization();
        if(DTrace.enabled){
            DTrace.FREE_ON_ROLLBACK.log(id);
            DTrace.FREE_ON_ROLLBACK.logLength(slot);
        }
        
        final SlotChange slotChange = produceSlotChange(id);
        slotChange.freeOnRollbackSetPointer(slot);
    }
    
    public void slotFreePointerOnCommit(int a_id) {
        checkSynchronization();
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
        checkSynchronization();
        slotFreeOnCommit(slot.address(), slot);
        
        // FIXME: This does not look nice
        slotFreeOnCommit(a_id, slot);
        
        // FIXME: It should rather work like this:
        // produceSlotChange(a_id).freePointerOnCommit();
    }
    
    public void slotFreePointerOnRollback(int id) {
    	produceSlotChange(id).freePointerOnRollback();
    }
	
	public void processDeletes() {
		if (i_delete == null) {
			_writtenUpdateDeletedMembers = null;
			return;
		}

		while (i_delete != null) {

			Tree delete = i_delete;
			i_delete = null;

			delete.traverse(new Visitor4() {
				public void visit(Object a_object) {
					DeleteInfo info = (DeleteInfo) a_object;
					// if the object has been deleted
					if (isDeleted(info._key)) {
						return;
					}
					
					// We need to hold a hard reference here, otherwise we can get 
					// intermediate garbage collection kicking in.
					Object obj = null;  
					
					if (info._reference != null) {
						obj = info._reference.getObject();
					}
					if (obj == null || info._reference.getID() < 0) {

						// This means the object was gc'd.

						// Let's try to read it again, but this may fail in
						// CS mode if another transaction has deleted it. 

						HardObjectReference hardRef = stream().getHardObjectReferenceById(
							LocalTransaction.this, info._key);
						if(hardRef == HardObjectReference.INVALID){
							return;
						}
						info._reference = hardRef._reference;
						info._reference.flagForDelete(stream().topLevelCallId());
						obj = info._reference.getObject();
					}
					stream().delete3(LocalTransaction.this, info._reference,
							info._cascade, false);
				}
			});
		}
		_writtenUpdateDeletedMembers = null;
	}

    public void writeUpdateDeleteMembers(int id, ClassMetadata clazz, int typeInfo, int cascade) {

    	checkSynchronization();
    	
        if(DTrace.enabled){
            DTrace.WRITE_UPDATE_DELETE_MEMBERS.log(id);
        }
        
        TreeInt newNode = new TreeInt(id);
        _writtenUpdateDeletedMembers = Tree.add(_writtenUpdateDeletedMembers, newNode);
        if(! newNode.wasAddedToTree()){
        	return;
        }
        
        if(clazz.canUpdateFast()){
        	slotFreeOnCommit(id, getCurrentSlotOfID(id));
        	return;
        }
        
        StatefulBuffer objectBytes = stream().readWriterByID(this, id);
        if(objectBytes == null){
            if (clazz.hasIndex()) {
                 dontRemoveFromClassIndex(clazz.getID(), id);
            }
            return;
        }
        
        ObjectHeader oh = new ObjectHeader(stream(), clazz, objectBytes);
        
        DeleteInfo info = (DeleteInfo)TreeInt.find(i_delete, id);
        if(info != null){
            if(info._cascade > cascade){
                cascade = info._cascade;
            }
        }
        
        objectBytes.setCascadeDeletes(cascade);
        clazz.deleteMembers(oh._marshallerFamily, oh._headerAttributes, objectBytes, typeInfo, true);
        slotFreeOnCommit(id, new Slot(objectBytes.getAddress(), objectBytes.getLength()));
    }
    
	private Callbacks callbacks(){
		return stream().callbacks();
	}

	private CallbackObjectInfoCollections collectCallbackObjectInfos(ServerMessageDispatcher serverMessageDispatcher) {
		if (null == _slotChanges) {
			return CallbackObjectInfoCollections.EMTPY;
		}
		final Collection4 added = new Collection4();
		final Collection4 deleted = new Collection4();
		final Collection4 updated = new Collection4();
		_slotChanges.traverse(new Visitor4() {
			public void visit(Object obj) {
				SlotChange slotChange = ((SlotChange)obj);
				LazyObjectReference lazyRef = new LazyObjectReference(LocalTransaction.this, slotChange._key);
				if (slotChange.isDeleted()) {					
					deleted.add(lazyRef);
				} else if (slotChange.isNew()) {
					added.add(lazyRef);
				} else {
					updated.add(lazyRef);
				}
			}
		});
		return new CallbackObjectInfoCollections (serverMessageDispatcher, new ObjectInfoCollectionImpl(added), new ObjectInfoCollectionImpl(updated), new ObjectInfoCollectionImpl(deleted));
	}
	
    private void setAddress(int a_address) {
        i_address = a_address;
    }

	public static Transaction readInterruptedTransaction(LocalObjectContainer file, Buffer reader) {
	    int transactionID1 = reader.readInt();
	    int transactionID2 = reader.readInt();
	    if( (transactionID1 > 0)  &&  (transactionID1 == transactionID2)){
	        LocalTransaction transaction = (LocalTransaction) file.newTransaction(null);
	        transaction.setAddress(transactionID1);
	        return transaction;
	    }
	    return null;
	}
	
	private FreespaceManager freespaceManager(){
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
    
}
