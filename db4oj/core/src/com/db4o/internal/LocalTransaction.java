/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.ext.ObjectInfoCollection;
import com.db4o.foundation.*;
import com.db4o.internal.callbacks.Callbacks;
import com.db4o.internal.marshall.ObjectHeader;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class LocalTransaction extends Transaction {

    private final byte[] _pointerBuffer = new byte[Const4.POINTER_LENGTH];
    
    private int i_address;	// only used to pass address to Thread
	
	private Tree _slotChanges;
	
    private Tree _writtenUpdateDeletedMembers;

	public LocalTransaction(ObjectContainerBase a_stream, Transaction a_parent) {
		super(a_stream, a_parent);
	}
	
    public void commit() {
        synchronized (stream().i_lock) {
        	triggerCommitOnStarted();
            i_file.freeSpaceBeginCommit();
            commitExceptForFreespace();
            i_file.freeSpaceEndCommit();
        }
    }
    
	private void commitExceptForFreespace(){
        
        if(DTrace.enabled){
            boolean systemTrans = (i_parentTransaction == null);
            DTrace.TRANS_COMMIT.logInfo( "server == " + stream().isServer() + ", systemtrans == " +  systemTrans);
        }
        
        commit2Listeners();
        
        commit3Stream();
        
        commit4FieldIndexes();
        
        commitParticipants();
        
        stream().writeDirty();
        
        commit6WriteChanges();
        
        freeOnCommit();
        
        commit7ClearAll();
    }
	
	private void commit2Listeners(){
        commitParentListeners(); 
        commitTransactionListeners();
    }

	private void commitParentListeners() {
		if (i_parentTransaction != null) {
            parentLocalTransaction().commit2Listeners();
        }
	}
    
    private void commit3Stream(){
        stream().checkNeededUpdates();
        stream().writeDirty();
        stream().classCollection().write(stream().getSystemTransaction());
    }
    
	private LocalTransaction parentLocalTransaction() {
		return (LocalTransaction) i_parentTransaction;
	}
    
	private void commit7ClearAll(){
        commit7ParentClearAll();
        clearAll();
    }

	private void commit7ParentClearAll() {
		if(i_parentTransaction != null){
            parentLocalTransaction().commit7ClearAll();
        }
	}

	
	protected void clearAll() {
		_slotChanges = null;
		super.clearAll();
	}
	
	protected void rollbackSlotChanges() {
		if(_slotChanges != null) {
			_slotChanges.traverse(new Visitor4() {
				public void visit(Object a_object) {
					((SlotChange)a_object).rollback(i_file);
				}
			});
		}
	}

	public boolean isDeleted(int id) {
    	return slotChangeIsFlaggedDeleted(id);
    }
	
	protected void commit6WriteChanges() {
        checkSynchronization();
            
        final int slotSetPointerCount = countSlotChanges();
        
        if (slotSetPointerCount > 0) {
            int length = (((slotSetPointerCount * 3) + 2) * Const4.INT_LENGTH);
            int address = i_file.getSlot(length);
            final StatefulBuffer bytes = new StatefulBuffer(this, address, length);
            bytes.writeInt(length);
            bytes.writeInt(slotSetPointerCount);
            
            appendSlotChanges(bytes);
            
            bytes.write();
            flushFile();
            
            stream().writeTransactionPointer(address);
            flushFile();
            
            if(writeSlots()){
                flushFile();
            }
            
            stream().writeTransactionPointer(0);
            flushFile();
            
            i_file.free(address, length);
        }
    }
	
    private boolean writeSlots() {
        
        checkSynchronization();
        
        boolean ret = false;
        
        if(i_parentTransaction != null){
            if(parentLocalTransaction().writeSlots()){
                ret = true;
            }
        }
        
        if(_slotChanges != null){
            _slotChanges.traverse(new Visitor4() {
                public void visit(Object a_object) {
                    ((SlotChange)a_object).writePointer(LocalTransaction.this);
                }
            });
            ret = true;
        }
        
        return ret;
    }
	
    protected void flushFile(){
        if(DTrace.enabled){
            DTrace.TRANS_FLUSH.log();
        }
        if(i_file.configImpl().flushFileBuffers()){
            i_file.syncFiles();
        }
    }
    
    private SlotChange produceSlotChange(int id){
    	if(DTrace.enabled){
    		DTrace.PRODUCE_SLOT_CHANGE.log(id);
    	}
        SlotChange slot = new SlotChange(id);
        _slotChanges = Tree.add(_slotChanges, slot);
        return (SlotChange)slot.addedOrExisting();
    }
    
    
    private final SlotChange findSlotChange(int a_id) {
        checkSynchronization();
        return (SlotChange)TreeInt.find(_slotChanges, a_id);
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
        
        if (i_parentTransaction != null) {
            Slot parentSlot = parentLocalTransaction().getCurrentSlotOfID(id); 
            if (parentSlot != null) {
                return parentSlot;
            }
        }
        return readCommittedSlotOfID(id);
    }
    
    public Slot getCommittedSlotOfID(int id){
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
        
        if (i_parentTransaction != null) {
            Slot parentSlot = parentLocalTransaction().getCommittedSlotOfID(id); 
            if (parentSlot != null) {
                return parentSlot;
            }
        }
        return readCommittedSlotOfID(id);
    }

    private Slot readCommittedSlotOfID(int id) {
        if (Deploy.debug) {
            i_pointerIo.useSlot(id);
            i_pointerIo.read();
            i_pointerIo.readBegin(Const4.YAPPOINTER);
            int debugAddress = i_pointerIo.readInt();
            int debugLength = i_pointerIo.readInt();
            i_pointerIo.readEnd();
            return new Slot(debugAddress, debugLength);
        }
        // FIXME: This shouldn't be silently swallowed. Currently this situation can occur for
        // a class collection in a new yap file that already has its ID assigned but hasn't been
        // written yet. Should be fixed in the YapClassCollection logic.
        try {
        	i_file.readBytes(_pointerBuffer, id, Const4.POINTER_LENGTH);
        }
        catch(RuntimeException exc) {
        	return null;
        }
        int address = (_pointerBuffer[3] & 255)
            | (_pointerBuffer[2] & 255) << 8 | (_pointerBuffer[1] & 255) << 16
            | _pointerBuffer[0] << 24;
        int length = (_pointerBuffer[7] & 255)
            | (_pointerBuffer[6] & 255) << 8 | (_pointerBuffer[5] & 255) << 16
            | _pointerBuffer[4] << 24;
        return new Slot(address, length);
    }
    
    public void setPointer(int a_id, int a_address, int a_length) {
        if(DTrace.enabled){
            DTrace.SLOT_SET_POINTER.log(a_id);
            DTrace.SLOT_SET_POINTER.logLength(a_address, a_length);
        }
        checkSynchronization();
        produceSlotChange(a_id).setPointer(a_address, a_length);
    }
    
    private boolean slotChangeIsFlaggedDeleted(int id){
        SlotChange slot = findSlotChange(id);
        if (slot != null) {
            return slot.isDeleted();
        }
        if (i_parentTransaction != null) {
            return parentLocalTransaction().slotChangeIsFlaggedDeleted(id);
        }
        return false;
    }

	
	 private int countSlotChanges(){
	        
	        int count = 0;
	        
	        if(i_parentTransaction != null){
	            count += parentLocalTransaction().countSlotChanges();
	        }
	        
	        final int slotSetPointerCount[]  = {count};
	        
	        if(_slotChanges != null){
	            _slotChanges.traverse(new Visitor4() {
	                public void visit(Object obj) {
	                    SlotChange slot = (SlotChange)obj;
	                    if(slot.isSetPointer()){
	                        slotSetPointerCount[0] ++;
	                    }
	                }
	            });
	        }
	        
	        return slotSetPointerCount[0];
	    }
	
	void writeOld() {
        synchronized (stream().i_lock) {
            i_pointerIo.useSlot(i_address);
            i_pointerIo.read();
            int length = i_pointerIo.readInt();
            if (length > 0) {
                StatefulBuffer bytes = new StatefulBuffer(this, i_address, length);
                bytes.read();
                bytes.incrementOffset(Const4.INT_LENGTH);
                _slotChanges = new TreeReader(bytes, new SlotChange(0)).read();
                if(writeSlots()){
                    flushFile();
                }
                stream().writeTransactionPointer(0);
                flushFile();
                freeOnCommit();
            } else {
                stream().writeTransactionPointer(0);
                flushFile();
            }
        }
    }
	
	protected final void freeOnCommit() {
        checkSynchronization();
        if(i_parentTransaction != null){
        	parentLocalTransaction().freeOnCommit();
        }
        if(_slotChanges != null){
            _slotChanges.traverse(new Visitor4() {
                public void visit(Object obj) {
                    ((SlotChange)obj).freeDuringCommit(i_file);
                }
            });
        }
    }
	
	private void appendSlotChanges(final Buffer writer){
        
        if(i_parentTransaction != null){
        	parentLocalTransaction().appendSlotChanges(writer);
        }
        
        Tree.traverse(_slotChanges, new Visitor4() {
            public void visit(Object obj) {
                ((TreeInt)obj).write(writer);
            }
        });
        
    }
	
	public void slotDelete(int a_id, int a_address, int a_length) {
        checkSynchronization();
        if(DTrace.enabled){
            DTrace.SLOT_DELETE.log(a_id);
            DTrace.SLOT_DELETE.logLength(a_address, a_length);
        }
        if (a_id == 0) {
            return;
        }
        SlotChange slot = produceSlotChange(a_id);
        slot.freeOnCommit(i_file, new Slot(a_address, a_length));
        slot.setPointer(0, 0);
    }
	
    private void slotFreeOnCommit(int id, Slot slot){
    	if(slot == null){
    		return;
    	}
    	slotFreeOnCommit(id, slot.getAddress(), slot.getLength());    	
    }

    public void slotFreeOnCommit(int a_id, int a_address, int a_length) {
        checkSynchronization();
        if(DTrace.enabled){
            DTrace.SLOT_FREE_ON_COMMIT.log(a_id);
            DTrace.SLOT_FREE_ON_COMMIT.logLength(a_address, a_length);
        }
        if (a_id == 0) {
            return;
        }
        produceSlotChange(a_id).freeOnCommit(i_file, new Slot(a_address, a_length));
    }

    public void slotFreeOnRollback(int a_id, int a_address, int a_length) {
        checkSynchronization();
        if(DTrace.enabled){
            DTrace.SLOT_FREE_ON_ROLLBACK_ID.log(a_id);
            DTrace.SLOT_FREE_ON_ROLLBACK_ADDRESS.logLength(a_address, a_length);
        }
        produceSlotChange(a_id).freeOnRollback(a_address, a_length);
    }

    void slotFreeOnRollbackCommitSetPointer(int a_id, int newAddress, int newLength) {
        
        Slot slot = getCurrentSlotOfID(a_id);
        if(slot==null) {
        	return;
        }
        
        checkSynchronization();
        
        if(DTrace.enabled){
            DTrace.FREE_ON_ROLLBACK.log(a_id);
            DTrace.FREE_ON_ROLLBACK.logLength(newAddress, newLength);
            DTrace.FREE_ON_COMMIT.log(a_id);
            DTrace.FREE_ON_COMMIT.logLength(slot._address, slot._length);
        }
        
        SlotChange change = produceSlotChange(a_id);
        change.freeOnRollbackSetPointer(newAddress, newLength);
        change.freeOnCommit(i_file, slot);
    }

    void producteUpdateSlotChange(int a_id, int a_address, int a_length) {
        checkSynchronization();
        if(DTrace.enabled){
            DTrace.FREE_ON_ROLLBACK.log(a_id);
            DTrace.FREE_ON_ROLLBACK.logLength(a_address, a_length);
        }
        
        final SlotChange slotChange = produceSlotChange(a_id);
        slotChange.freeOnRollbackSetPointer(a_address, a_length);
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
        
        slotFreeOnCommit(a_id, slot._address, slot._length);
    }
    
    void slotFreePointerOnCommit(int a_id, int a_address, int a_length) {
        checkSynchronization();
        slotFreeOnCommit(a_address, a_address, a_length);
        
        // FIXME: This does not look nice
        slotFreeOnCommit(a_id, a_id, Const4.POINTER_LENGTH);
        
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
					Object obj = null;
					if (info._reference != null) {
						obj = info._reference.getObject();
					}
					if (obj == null) {

						// This means the object was gc'd.

						// Let's try to read it again, but this may fail in
						// CS mode
						// if another transaction has deleted it. We are
						// taking care
						// of possible nulls in #delete4().

						HardObjectReference hardRef = stream().getHardObjectReferenceById(
								LocalTransaction.this, info._key);
						info._reference = hardRef._reference;
						info._reference
								.flagForDelete(stream().topLevelCallId());
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
    
	private void triggerCommitOnStarted() {
		final Callbacks callbacks = stream().callbacks();
		if (!callbacks.caresAboutCommit()) {
			return;
		}
		ObjectInfoCollection[] collections = partitionSlotChangesInAddedDeletedUpdated();
		callbacks.commitOnStarted(collections[0], collections[1], collections[2]);
	}

	private static final class ObjectInfoCollectionImpl implements ObjectInfoCollection {
		
		public static final ObjectInfoCollection EMPTY = new ObjectInfoCollectionImpl(Iterators.EMPTY_ITERABLE);
		
		private final Iterable4 _collection;

		public ObjectInfoCollectionImpl(Iterable4 collection) {
			_collection = collection;
		}

		public Iterator4 iterator() {
			return _collection.iterator();
		}
	}

	private ObjectInfoCollection[] partitionSlotChangesInAddedDeletedUpdated() {
		if (null == _slotChanges) {
			return new ObjectInfoCollection[] { 
				ObjectInfoCollectionImpl.EMPTY,
				ObjectInfoCollectionImpl.EMPTY,
				ObjectInfoCollectionImpl.EMPTY
			};
		}
		final Collection4 added = new Collection4();
		final Collection4 deleted = new Collection4();
		final Collection4 updated = new Collection4();
		_slotChanges.traverse(new Visitor4() {
			public void visit(Object obj) {
				final SlotChange slotChange = ((SlotChange)obj);
				final ObjectReference reference = stream().referenceForId(slotChange._key);
				if (slotChange.isDeleted()) {					
					deleted.add(reference);
				} else if (slotChange.isNew()) {
					added.add(reference);
				} else {
					updated.add(reference);
				}
			}
		});
		
		return new ObjectInfoCollection[] {
			new ObjectInfoCollectionImpl(added),
			new ObjectInfoCollectionImpl(deleted),
			new ObjectInfoCollectionImpl(updated),
		};
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


}
