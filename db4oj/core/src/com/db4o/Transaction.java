/* Copyright (C) 2004 - 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;
import com.db4o.inside.ix.*;
import com.db4o.inside.marshall.ObjectHeader;
import com.db4o.inside.slots.Slot;
import com.db4o.inside.slots.SlotChange;
import com.db4o.reflect.Reflector;

/**
 * @exclude
 */
public class Transaction {

    private Tree            _slotChanges;

    private int             i_address;                                  // only used to pass address to Thread
    
    private final byte[]          _pointerBuffer = new byte[YapConst.POINTER_LENGTH];

    // contains TreeIntObject nodes
    // if TreeIntObject#i_object is null then this means DONT delete.
    // Otherwise TreeIntObject#i_object contains the YapObject
    public Tree          i_delete;  // public for .NET conversion

    private List4           i_dirtyFieldIndexes;
    
    public final YapFile           i_file;

    final Transaction       i_parentTransaction;

    private final YapWriter i_pointerIo;    

    private final YapStream         i_stream;
    
    private List4           i_transactionListeners;
    
    protected Tree			i_writtenUpdateDeletedMembers;
    
    // TODO: join _dirtyBTree and _enlistedIndices
    private final Collection4 _participants = new Collection4(); 

    Transaction(YapStream a_stream, Transaction a_parent) {
        i_stream = a_stream;
        i_file = (a_stream instanceof YapFile) ? (YapFile) a_stream : null;
        i_parentTransaction = a_parent;
        i_pointerIo = new YapWriter(this, YapConst.POINTER_LENGTH);
    }

    public void addDirtyFieldIndex(IndexTransaction a_xft) {
        i_dirtyFieldIndexes = new List4(i_dirtyFieldIndexes, a_xft);
    }

	public final void checkSynchronization() {
		if(Debug.checkSychronization){
            stream().i_lock.notify();
        }
	}

    public void addTransactionListener(TransactionListener a_listener) {
        i_transactionListeners = new List4(i_transactionListeners, a_listener);
    }
    
    private void appendSlotChanges(final YapWriter writer){
        
        if(i_parentTransaction != null){
            i_parentTransaction.appendSlotChanges(writer);
        }
        
        Tree.traverse(_slotChanges, new Visitor4() {
            public void visit(Object obj) {
                ((TreeInt)obj).write(writer);
            }
        });
        
    }

    void beginEndSet() {
        checkSynchronization();
        if (i_delete != null) {
            final boolean[] foundOne = { false};
            final Transaction finalThis = this;
            do {
                foundOne[0] = false;
                Tree delete = i_delete;
                i_delete = null;
                
                
                delete.traverse(new Visitor4() {
                    public void visit(Object a_object) {
                        DeleteInfo info  = (DeleteInfo)a_object;
                        if(! info._delete){
                            i_delete = Tree.add(i_delete, new DeleteInfo(info._key, null, false, info._cascade)); 
                        }
                    }
                });

                
                delete.traverse(new Visitor4() {
                    public void visit(Object a_object) {
                        DeleteInfo info  = (DeleteInfo)a_object;
                        if(info._delete){
                            foundOne[0] = true;
                            Object obj = null;
                            if(info._reference != null){
                                obj = info._reference.getObject();
                            }
                            if(obj == null){
                                
                                // This means the object was gc'd.
                                
                                // Let's try to read it again, but this may fail in CS mode
                                // if another transaction has deleted it. We are taking care
                                // of possible nulls in #delete4().
                                
                                Object[] arr  = finalThis.stream().getObjectAndYapObjectByID(finalThis, info._key);
                                obj = arr[0];
                                info._reference = (YapObject)arr[1]; 
                            }
                            stream().delete4(finalThis,info._reference ,info._cascade, false);
                        }
                        i_delete = Tree.add(i_delete, new DeleteInfo(info._key, null, false, info._cascade)); 
                    }
                });
            } while (foundOne[0]);
        }
        i_delete = null;
        i_writtenUpdateDeletedMembers = null;
    }
    

    private final void clearAll() {
        _slotChanges = null;
        i_dirtyFieldIndexes = null;
        i_transactionListeners = null;
        disposeParticipants();
        _participants.clear();
    }

	private void disposeParticipants() {
		Iterator4 iterator = _participants.iterator();
        while (iterator.moveNext()) {
        	((TransactionParticipant)iterator.current()).dispose(this);
        }
	}

    void close(boolean a_rollbackOnClose) {
        try {
            if (stream() != null) {
                checkSynchronization();
                stream().releaseSemaphores(this);
            }
        } catch (Exception e) {
            if (Debug.atHome) {
                e.printStackTrace();
            }
        }
        if (a_rollbackOnClose) {
            try {
                rollback();
            } catch (Exception e) {
                if (Debug.atHome) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void commit() {
        synchronized (stream().i_lock) {
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
        
        commit1BeginEndSet();
        
        commit2Listeners();
        
        commit3Stream();
        
        commit4FieldIndexes();
        
        commit5Participants();
        
        stream().writeDirty();
        
        commit6WriteChanges();
        
        freeOnCommit();
        
        commit7ClearAll();
    }
    
    private void commit1BeginEndSet(){
        if (i_parentTransaction != null) {
            i_parentTransaction.commit1BeginEndSet();
        } 
        beginEndSet();
    }
    
    private void commit2Listeners(){
        if (i_parentTransaction != null) {
            i_parentTransaction.commit2Listeners();
        } 
        commitTransactionListeners();
    }
    
    
    private void commit3Stream(){
        stream().checkNeededUpdates();
        stream().writeDirty();
        stream().classCollection().write(stream().getSystemTransaction());
    }
    
    
    private void commit4FieldIndexes(){
        if(i_parentTransaction != null){
            i_parentTransaction.commit4FieldIndexes();
        }
        if (i_dirtyFieldIndexes != null) {
            Iterator4 i = new Iterator4Impl(i_dirtyFieldIndexes);
            while (i.moveNext()) {
                ((IndexTransaction) i.current()).commit();
            }
        }
    }
    
    private void commit5Participants() {
        if (i_parentTransaction != null) {
            i_parentTransaction.commit5Participants();
        }
        
        Iterator4 iterator = _participants.iterator();
		while (iterator.moveNext()) {
			((TransactionParticipant)iterator.current()).commit(this);
		}
    }

	private void commit6WriteChanges() {
        checkSynchronization();
            
        final int slotSetPointerCount = countSlotChanges();
        
        if (slotSetPointerCount > 0) {
            int length = (((slotSetPointerCount * 3) + 2) * YapConst.INT_LENGTH);
            int address = i_file.getSlot(length);
            final YapWriter bytes = new YapWriter(this, address, length);
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
    
    private void commit7ClearAll(){
        if(i_parentTransaction != null){
            i_parentTransaction.commit7ClearAll();
        }
        clearAll();
    }
    
    void commitTransactionListeners() {
        checkSynchronization();
        if (i_transactionListeners != null) {
            Iterator4 i = new Iterator4Impl(i_transactionListeners);
            while (i.moveNext()) {
                ((TransactionListener) i.current()).preCommit();
            }
            i_transactionListeners = null;
        }
    }
    
    private int countSlotChanges(){
        
        int count = 0;
        
        if(i_parentTransaction != null){
            count += i_parentTransaction.countSlotChanges();
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

    void delete(YapObject a_yo, int a_cascade) {
        checkSynchronization();
        int id = a_yo.getID();
        if(DTrace.enabled){
            DTrace.TRANS_DELETE.log(id);
        }
        
        DeleteInfo info = (DeleteInfo) TreeInt.find(i_delete, id);
        if(info == null){
            info = new DeleteInfo(id, a_yo, true, a_cascade);
            i_delete = Tree.add(i_delete, info);
            return;
        }
        info._reference = a_yo;
        if(a_cascade > info._cascade){
            info._cascade = a_cascade;
        }
    }
    
    void dontDelete(int classID, int a_id) {
        checkSynchronization();
        if(DTrace.enabled){
            DTrace.TRANS_DONT_DELETE.log(a_id);
        }
        DeleteInfo info = (DeleteInfo) TreeInt.find(i_delete, a_id);
        if(info == null){
            i_delete = Tree.add(i_delete, new DeleteInfo(a_id, null, false, 0));
        }else{
            info._delete = false;
        }
        YapClass yc = stream().getYapClass(classID);
        dontDeleteAllAncestors(yc, a_id);
    }
    
    void dontDeleteAllAncestors(YapClass yapClass, int objectID){
        if(yapClass == null){
            return;
        }
        yapClass.index().dontDelete(this, objectID);
        dontDeleteAllAncestors(yapClass.i_ancestor, objectID);
    }
    
    void dontRemoveFromClassIndex(int a_yapClassID, int a_id) {
        // If objects are deleted and rewritten during a cascade
        // on delete, we dont want them to be gone.        
        checkSynchronization();
        YapClass yapClass = stream().getYapClass(a_yapClassID);
        yapClass.index().add(this, a_id);
    }
    
    private final SlotChange findSlotChange(int a_id) {
        checkSynchronization();
        return (SlotChange)TreeInt.find(_slotChanges, a_id);
    }

    private void flushFile(){
        if(DTrace.enabled){
            DTrace.TRANS_FLUSH.log();
        }
        if(i_file.configImpl().flushFileBuffers()){
            i_file.syncFiles();
        }
    }

    private final void freeOnCommit() {
        checkSynchronization();
        if(i_parentTransaction != null){
            i_parentTransaction.freeOnCommit();
        }
        if(_slotChanges != null){
            _slotChanges.traverse(new Visitor4() {
                public void visit(Object obj) {
                    ((SlotChange)obj).freeDuringCommit(i_file);
                }
            });
        }
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
            Slot parentSlot = i_parentTransaction.getCurrentSlotOfID(id); 
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
            Slot parentSlot = i_parentTransaction.getCommittedSlotOfID(id); 
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
            i_pointerIo.readBegin(YapConst.YAPPOINTER);
            int debugAddress = i_pointerIo.readInt();
            int debugLength = i_pointerIo.readInt();
            i_pointerIo.readEnd();
            return new Slot(debugAddress, debugLength);
        }
        i_file.readBytes(_pointerBuffer, id, YapConst.POINTER_LENGTH);
        int address = (_pointerBuffer[3] & 255)
            | (_pointerBuffer[2] & 255) << 8 | (_pointerBuffer[1] & 255) << 16
            | _pointerBuffer[0] << 24;
        int length = (_pointerBuffer[7] & 255)
            | (_pointerBuffer[6] & 255) << 8 | (_pointerBuffer[5] & 255) << 16
            | _pointerBuffer[4] << 24;
        return new Slot(address, length);
    }

    boolean isDeleted(int a_id) {
        checkSynchronization();
        SlotChange slot = findSlotChange(a_id);
        if (slot != null) {
            return slot.isDeleted();
        }
        if (i_parentTransaction != null) {
            return i_parentTransaction.isDeleted(a_id);
        }
        return false;
    }
    
    Object[] objectAndYapObjectBySignature(final long a_uuid, final byte[] a_signature) {
        checkSynchronization();  
        return stream().getFieldUUID().objectAndYapObjectBySignature(this, a_uuid, a_signature);
    }
    
    private SlotChange produceSlotChange(int id){
        SlotChange slot = new SlotChange(id);
        _slotChanges = Tree.add(_slotChanges, slot);
        return (SlotChange)slot.duplicateOrThis();
    }
    
    Reflector reflector(){
    	return stream().reflector();
    }

    public void rollback() {
        synchronized (stream().i_lock) {
            
            beginEndSet();
            
            rollbackParticipants();
            
            rollbackFieldIndexes();
            
            rollbackSlotChanges();
            
            rollBackTransactionListeners();
            
            clearAll();
        }
    }

	private void rollbackSlotChanges() {
		if(_slotChanges != null){
		    _slotChanges.traverse(new Visitor4() {
		        public void visit(Object a_object) {
		            ((SlotChange)a_object).rollback(i_file);
		        }
		    });
		}
	}

	private void rollbackFieldIndexes() {
		if (i_dirtyFieldIndexes != null) {
		    Iterator4 i = new Iterator4Impl(i_dirtyFieldIndexes);
		    while (i.moveNext()) {
		        ((IndexTransaction) i.current()).rollback();
		    }
		}
	}
    
    private void rollbackParticipants() {
    	Iterator4 iterator = _participants.iterator();
		while (iterator.moveNext()) {
			((TransactionParticipant)iterator.current()).rollback(this);
		}
	}

	void rollBackTransactionListeners() {
        checkSynchronization();
        if (i_transactionListeners != null) {
            Iterator4 i = new Iterator4Impl(i_transactionListeners);
            while (i.moveNext()) {
                ((TransactionListener) i.current()).postRollback();
            }
            i_transactionListeners = null;
        }
    }

    void setAddress(int a_address) {
        i_address = a_address;
    }

    public void setPointer(int a_id, int a_address, int a_length) {
        checkSynchronization();
        produceSlotChange(a_id).setPointer(a_address, a_length);
    }

    void slotDelete(int a_id, int a_address, int a_length) {
        checkSynchronization();
        if(DTrace.enabled){
            DTrace.FREE_ON_COMMIT.log(a_id);
            DTrace.FREE_ON_COMMIT.logLength(a_address, a_length);
        }
        if (a_id == 0) {
            return;
        }
        SlotChange slot = produceSlotChange(a_id);
        slot.freeOnCommit(i_file, new Slot(a_address, a_length));
        slot.setPointer(0, 0);
    }

    public void slotFreeOnCommit(int a_id, int a_address, int a_length) {
        checkSynchronization();
        if(DTrace.enabled){
            DTrace.FREE_ON_COMMIT.log(a_id);
            DTrace.FREE_ON_COMMIT.logLength(a_address, a_length);
        }
        if (a_id == 0) {
            return;
        }
        produceSlotChange(a_id).freeOnCommit(i_file, new Slot(a_address, a_length));
    }

    void slotFreeOnRollback(int a_id, int a_address, int a_length) {
        checkSynchronization();
        if(DTrace.enabled){
            DTrace.FREE_ON_ROLLBACK.log(a_id);
            DTrace.FREE_ON_ROLLBACK.logLength(a_address, a_length);
        }
        produceSlotChange(a_id).freeOnRollback(a_address, a_length);
    }

    void slotFreeOnRollbackCommitSetPointer(int a_id, int newAddress, int newLength) {
        
        Slot slot = getCurrentSlotOfID(a_id);
        
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

    void slotFreeOnRollbackSetPointer(int a_id, int a_address, int a_length) {
        checkSynchronization();
        if(DTrace.enabled){
            DTrace.FREE_ON_ROLLBACK.log(a_id);
            DTrace.FREE_ON_ROLLBACK.logLength(a_address, a_length);
        }
        produceSlotChange(a_id).freeOnRollbackSetPointer(a_address, a_length);
    }
    
    public void slotFreePointerOnCommit(int a_id) {
        checkSynchronization();
        Slot slot = getCurrentSlotOfID(a_id);
        if(slot == null){
            return;
        }
        
        // FIXME: From looking at this it should call slotFreePointerOnCommit
        //        Write a test case and check.
        
        slotFreeOnCommit(a_id, slot._address, slot._length);
    }
    
    void slotFreePointerOnCommit(int a_id, int a_address, int a_length) {
        checkSynchronization();
        slotFreeOnCommit(a_address, a_address, a_length);
        slotFreeOnCommit(a_id, a_id, YapConst.POINTER_LENGTH);
    }

    boolean supportsVirtualFields(){
        return true;
    }
    
    public Transaction systemTransaction(){
        if(i_parentTransaction != null){
            return i_parentTransaction;
        }
        return this;
    }

    public String toString() {
        return stream().toString();
    }    

    void writeOld() {
        synchronized (stream().i_lock) {
            i_pointerIo.useSlot(i_address);
            i_pointerIo.read();
            int length = i_pointerIo.readInt();
            if (length > 0) {
                YapWriter bytes = new YapWriter(this, i_address, length);
                bytes.read();
                bytes.incrementOffset(YapConst.INT_LENGTH);
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

    public void writePointer(int a_id, int a_address, int a_length) {
        if(DTrace.enabled){
            DTrace.WRITE_POINTER.log(a_id);
            DTrace.WRITE_POINTER.logLength(a_address, a_length);
        }
        checkSynchronization();
        i_pointerIo.useSlot(a_id);
        if (Deploy.debug) {
            i_pointerIo.writeBegin(YapConst.YAPPOINTER);
        }
        i_pointerIo.writeInt(a_address);
        i_pointerIo.writeInt(a_length);
        if (Deploy.debug) {
            i_pointerIo.writeEnd();
        }
        if (Debug.xbytes && Deploy.overwrite) {
            i_pointerIo.setID(YapConst.IGNORE_ID);
        }
        i_pointerIo.write();
    }
    
    
    private boolean writeSlots() {
        
        checkSynchronization();
        
        boolean ret = false;
        
        if(i_parentTransaction != null){
            if(i_parentTransaction.writeSlots()){
                ret = true;
            }
        }
        
        if(_slotChanges != null){
            _slotChanges.traverse(new Visitor4() {
                public void visit(Object a_object) {
                    ((SlotChange)a_object).writePointer(Transaction.this);
                }
            });
            ret = true;
        }
        
        return ret;
    }
    
    void writeUpdateDeleteMembers(int a_id, YapClass a_yc, int a_type, int a_cascade) {
        checkSynchronization();
        if(Tree.find(i_writtenUpdateDeletedMembers, new TreeInt(a_id)) != null){
            return;
        }
        if(DTrace.enabled){
            DTrace.WRITE_UPDATE_DELETE_MEMBERS.log(a_id);
        }
        i_writtenUpdateDeletedMembers = Tree.add(i_writtenUpdateDeletedMembers, new TreeInt(a_id));
        YapWriter objectBytes = stream().readWriterByID(this, a_id);
        if(objectBytes == null){
            if (a_yc.hasIndex()) {
                dontRemoveFromClassIndex(a_yc.getID(), a_id);
            }
            return;
        }
        
        ObjectHeader oh = new ObjectHeader(stream(), a_yc, objectBytes);
        
        DeleteInfo info = (DeleteInfo)TreeInt.find(i_delete, a_id);
        if(info != null){
            if(info._cascade > a_cascade){
                a_cascade = info._cascade;
            }
        }
        
        objectBytes.setCascadeDeletes(a_cascade);
        a_yc.deleteMembers(oh._marshallerFamily, oh._headerAttributes, objectBytes, a_type, true);
        slotFreeOnCommit(a_id, objectBytes.getAddress(), objectBytes.getLength());
    }

    public YapStream stream() {
        return i_stream;
    }

	public void enlist(TransactionParticipant participant) {
		if (null == participant) {
			throw new ArgumentNullException("participant");
		}
		checkSynchronization();	
		if (!_participants.containsByIdentity(participant)) {
			_participants.add(participant);
		}
	}
}