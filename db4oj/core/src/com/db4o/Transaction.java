/* Copyright (C) 2004 - 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;
import com.db4o.inside.ix.*;
import com.db4o.inside.slots.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public class Transaction {

    private Tree            _slotChanges;

    private int             i_address;                                  // only used to pass address to Thread

    private Tree            i_addToClassIndex;

    private byte[]          i_bytes = new byte[YapConst.POINTER_LENGTH];

    // contains TreeIntObject nodes
    // if TreeIntObject#i_object is null then this means DONT delete.
    // Otherwise TreeIntObject#i_object contains the YapObject
    public Tree          i_delete;  // public for .NET conversion

    private List4           i_dirtyFieldIndexes;

    public final YapFile           i_file;

    final Transaction       i_parentTransaction;

    private final YapWriter i_pointerIo;

    private Tree            i_removeFromClassIndex;

    public final YapStream         i_stream;
    
    private List4           i_transactionListeners;
    
    protected Tree			i_writtenUpdateDeletedMembers;

    Transaction(YapStream a_stream, Transaction a_parent) {
        i_stream = a_stream;
        i_file = (a_stream instanceof YapFile) ? (YapFile) a_stream : null;
        i_parentTransaction = a_parent;
        i_pointerIo = new YapWriter(this, YapConst.POINTER_LENGTH);
    }

    public void addDirtyFieldIndex(IndexTransaction a_xft) {
        i_dirtyFieldIndexes = new List4(i_dirtyFieldIndexes, a_xft);
    }

    void addToClassIndex(int a_yapClassID, int a_id) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        if(DTrace.enabled){
            DTrace.ADD_TO_CLASS_INDEX.log(a_id);
        }
        if (Deploy.debug) {
            if (a_id == 0) {
                throw new RuntimeException();
            }
        }
        removeFromClassIndexTree(i_removeFromClassIndex, a_yapClassID, a_id);
        i_addToClassIndex = addToClassIndexTree(i_addToClassIndex,
            a_yapClassID, a_id);
    }

    private final Tree addToClassIndexTree(Tree a_tree, int a_yapClassID,
        int a_id) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        TreeIntObject[] node = new TreeIntObject[] { new TreeIntObject(
            a_yapClassID)};
        a_tree = createClassIndexNode(a_tree, node);
        node[0].i_object = Tree.add((Tree) node[0].i_object, new TreeInt(a_id));
        return a_tree;
    }

    public void addTransactionListener(TransactionListener a_listener) {
        i_transactionListeners = new List4(i_transactionListeners, a_listener);
    }

    void beginEndSet() {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
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
                                
                                Object[] arr  = finalThis.i_stream.getObjectAndYapObjectByID(finalThis, info.i_key);
                                obj = arr[0];
                                info._reference = (YapObject)arr[1]; 
                            }
                            i_stream.delete4(finalThis,info._reference , obj, info._cascade, false);
                        }
                        i_delete = Tree.add(i_delete, new DeleteInfo(info.i_key, null, false, info._cascade)); 
                    }
                });
            } while (foundOne[0]);
        }
        i_delete = null;
        i_writtenUpdateDeletedMembers = null;
    }
    
    private final int calculateLength() {
        
        final int[] count = {0};
        
        if(_slotChanges != null){
            _slotChanges.traverse(new Visitor4() {
                public void visit(Object obj) {
                    SlotChange slot = (SlotChange)obj;
                    if(slot.isSetPointer()){
                        count[0] ++;
                    }
                }
            });
        }
        
        return ((2 // Transaction slot length
            + (count[0] * 3)) * YapConst.YAPINT_LENGTH)
            + Tree.byteCount(i_addToClassIndex)
            + Tree.byteCount(i_removeFromClassIndex);
    }

    private final void clearAll() {
        _slotChanges = null;
        i_addToClassIndex = null;
        i_removeFromClassIndex = null;
        i_dirtyFieldIndexes = null;
        i_transactionListeners = null;
    }

    void close(boolean a_rollbackOnClose) {
        try {
            if (i_stream != null) {
                if(Debug.checkSychronization){
                    i_stream.i_lock.notify();
                }
                i_stream.releaseSemaphores(this);
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

    void commit() {
        synchronized (i_stream.i_lock) {
            i_file.freeSpaceBeginCommit();
            commitExceptForFreespace();
            i_file.freeSpaceEndCommit();
        }
    }

    private void commitExceptForFreespace(){
        
        if(DTrace.enabled){
            boolean systemTrans = (i_parentTransaction == null);
            DTrace.TRANS_COMMIT.logInfo( "server == " + i_stream.isServer() + ", systemtrans == " +  systemTrans);
        }
        
        // Just to make sure that no pending deletes 
        // get carried into the next transaction.
        beginEndSet();
        
        commitTransactionListeners();

        i_stream.checkNeededUpdates();
        
        i_stream.writeDirty();
        
        i_stream.i_classCollection.write(i_stream.getSystemTransaction());

        if (i_dirtyFieldIndexes != null) {
            Iterator4 i = new Iterator4Impl(i_dirtyFieldIndexes);
            while (i.hasNext()) {
                ((IndexTransaction) i.next()).commit();
            }
        }
        
        if (i_parentTransaction != null) {
            i_parentTransaction.commitExceptForFreespace();
        } else {
            i_stream.writeDirty();
        }
        
        write();
        
        freeOnCommit();
        
        clearAll();
    }
    
    void commitTransactionListeners() {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        if (i_transactionListeners != null) {
            Iterator4 i = new Iterator4Impl(i_transactionListeners);
            while (i.hasNext()) {
                ((TransactionListener) i.next()).preCommit();
            }
            i_transactionListeners = null;
        }
    }
    

    private final Tree createClassIndexNode(Tree a_tree, Tree[] a_node) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        if (a_tree != null) {
            Tree existing = a_tree.find(a_node[0]);
            if (existing != null) {
                a_node[0] = existing;
            } else {
                a_tree = a_tree.add(a_node[0]);
            }
        } else {
            a_tree = a_node[0];
        }
        return a_tree;
    }

    void delete(YapObject a_yo, int a_cascade) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
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
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        if(DTrace.enabled){
            DTrace.TRANS_DONT_DELETE.log(a_id);
        }
        DeleteInfo info = (DeleteInfo) TreeInt.find(i_delete, a_id);
        if(info == null){
            i_delete = Tree.add(i_delete, new DeleteInfo(a_id, null, false, 0));
        }else{
            info._delete = false;
        }
        YapClass yc = i_stream.getYapClass(classID);
        dontDeleteAllAncestors(yc, a_id);
    }
    
    void dontDeleteAllAncestors(YapClass yapClass, int objectID){
        if(yapClass == null){
            return;
        }
        removeFromClassIndexTree(i_removeFromClassIndex, yapClass.getID(), objectID);
        dontDeleteAllAncestors(yapClass.i_ancestor, objectID);
    }
    
    void dontRemoveFromClassIndex(int a_yapClassID, int a_id) {
        // If objects are deleted and rewritten during a cascade
        // on delete, we dont want them to be gone.
        
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }

        if (Deploy.debug) {
            if (a_id == 0) {
                throw new RuntimeException();
            }
        }
        removeFromClassIndexTree(i_removeFromClassIndex, a_yapClassID, a_id);

        YapClass yapClass = i_stream.getYapClass(a_yapClassID);
        if (TreeInt.find(yapClass.getIndexRoot(), a_id) == null) {
            addToClassIndex(a_yapClassID, a_id);
        }
    }
    
    private final SlotChange findSlotChange(int a_id) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        return (SlotChange)TreeInt.find(_slotChanges, a_id);
    }

    private void flushFile(){
        if(i_file.i_config._flushFileBuffers){
            i_file.syncFiles();
        }
    }

    private final void freeOnCommit() {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        if(_slotChanges != null){
            _slotChanges.traverse(new Visitor4() {
                public void visit(Object obj) {
                    ((SlotChange)obj).freeDuringCommit(i_file);
                }
            });
        }
    }

    Slot getSlotInformation(int a_id) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        if (a_id == 0) {
            return null;
        }
        SlotChange change = findSlotChange(a_id);
        if (change != null) {
            
            
            // FIXME: Do we have to check that?
            
            if(change.isSetPointer()){
                return change.newSlot();
            }
        }
        
        if (i_parentTransaction != null) {
            Slot parentSlot = i_parentTransaction.getSlotInformation(a_id); 
            if (parentSlot != null) {
                return parentSlot;
            }
        }
        if (Deploy.debug) {
            i_pointerIo.useSlot(a_id);
            i_pointerIo.read();
            i_pointerIo.readBegin(YapConst.YAPPOINTER);
            int debugAddress = i_pointerIo.readInt();
            int debugLength = i_pointerIo.readInt();
            i_pointerIo.readEnd();
            return new Slot(debugAddress, debugLength);
        }
        i_file.readBytes(i_bytes, a_id, YapConst.POINTER_LENGTH);
        int address = (i_bytes[3] & 255)
            | (i_bytes[2] & 255) << 8 | (i_bytes[1] & 255) << 16
            | i_bytes[0] << 24;
        int length = (i_bytes[7] & 255)
            | (i_bytes[6] & 255) << 8 | (i_bytes[5] & 255) << 16
            | i_bytes[4] << 24;
        return new Slot(address, length);
    }

    boolean isDeleted(int a_id) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
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
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        final Object[] ret = new Object[2];
        IxTree ixTree = (IxTree) i_stream.i_handlers.i_indexes.i_fieldUUID.getIndexRoot(this);
        IxTraverser ixTraverser = new IxTraverser();
        int count = ixTraverser.findBoundsExactMatch(new Long(a_uuid), ixTree);
        if (count > 0) {
            final Transaction finalThis = this;
            ixTraverser.visitAll(new Visitor4() {
                public void visit(Object a_object) {
                    Object[] arr = finalThis.i_stream.getObjectAndYapObjectByID(
                        finalThis, ((Integer)a_object).intValue());
                    if (arr[1] != null) {
                        YapObject yod = (YapObject) arr[1];
                        VirtualAttributes vad = yod.virtualAttributes(finalThis);
                        byte[] cmp = vad.i_database.i_signature;
                        boolean same = true;
                        if (a_signature.length == cmp.length) {
                            for (int i = 0; i < a_signature.length; i++) {
                                if (a_signature[i] != cmp[i]) {
                                    same = false;
                                    break;
                                }
                            }
                        } else {
                            same = false;
                        }
                        if (same) {
                            ret[0] = arr[0];
                            ret[1] = arr[1];
                        }
                    }
                }
            });
            
        }
        return ret;
    }
    
    private SlotChange produceSlotChange(int id){
        SlotChange slot = (SlotChange)TreeInt.find(_slotChanges, id);
        if(slot == null){
            slot = new SlotChange(id);
            _slotChanges = Tree.add(_slotChanges, slot);
        }
        return slot;
    }
    
    Reflector reflector(){
    	return i_stream.reflector();
    }

    void removeFromClassIndex(int a_yapClassID, int a_id) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        if (Deploy.debug) {
            if (a_id == 0) {
                throw new RuntimeException();
            }
        }
        if(DTrace.enabled){
            DTrace.REMOVE_FROM_CLASS_INDEX.log(a_id);
        }
        removeFromClassIndexTree(i_addToClassIndex, a_yapClassID, a_id);
        i_removeFromClassIndex = addToClassIndexTree(i_removeFromClassIndex,
            a_yapClassID, a_id);
    }
    
    private final void removeFromClassIndexTree(Tree a_tree, int a_yapClassID,
        int a_id) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        if (a_tree != null) {
            TreeIntObject node = (TreeIntObject) ((TreeInt) a_tree)
                .find(a_yapClassID);
            if (node != null) {
                node.i_object = Tree.removeLike((Tree) node.i_object,
                    new TreeInt(a_id));
            }
        }
    }
    
    public void rollback() {
        synchronized (i_stream.i_lock) {
            
            beginEndSet();
            
            if (i_dirtyFieldIndexes != null) {
                Iterator4 i = new Iterator4Impl(i_dirtyFieldIndexes);
                while (i.hasNext()) {
                    ((IndexTransaction) i.next()).rollback();
                }
            }
            if(_slotChanges != null){
                _slotChanges.traverse(new Visitor4() {
                    public void visit(Object a_object) {
                        ((SlotChange)a_object).rollback(i_file);
                    }
                });
            }
            rollBackTransactionListeners();
            clearAll();
        }
    }
    
    void rollBackTransactionListeners() {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        if (i_transactionListeners != null) {
            Iterator4 i = new Iterator4Impl(i_transactionListeners);
            while (i.hasNext()) {
                ((TransactionListener) i.next()).postRollback();
            }
            i_transactionListeners = null;
        }
    }

    void setAddress(int a_address) {
        i_address = a_address;
    }

    void setPointer(int a_id, int a_address, int a_length) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        produceSlotChange(a_id).setPointer(a_address, a_length);
    }

    void slotDelete(int a_id, int a_address, int a_length) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
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

    void slotFreeOnCommit(int a_id, int a_address, int a_length) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
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
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        if(DTrace.enabled){
            DTrace.FREE_ON_ROLLBACK.log(a_id);
            DTrace.FREE_ON_ROLLBACK.logLength(a_address, a_length);
        }
        produceSlotChange(a_id).freeOnRollback(a_address, a_length);
    }

    void slotFreeOnRollbackCommitSetPointer(int a_id, int newAddress, int newLength) {
        
        Slot slot = getSlotInformation(a_id);
        
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        
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
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        if(DTrace.enabled){
            DTrace.FREE_ON_ROLLBACK.log(a_id);
            DTrace.FREE_ON_ROLLBACK.logLength(a_address, a_length);
        }
        produceSlotChange(a_id).freeOnRollbackSetPointer(a_address, a_length);
    }
    
    void slotFreePointerOnCommit(int a_id, int a_address, int a_length) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        slotFreeOnCommit(a_address, a_address, a_length);
        slotFreeOnCommit(a_id, a_id, YapConst.POINTER_LENGTH);
    }

    boolean supportsVirtualFields(){
        return true;
    }

    public String toString() {
        return i_stream.toString();
    }
    
    void traverseAddedClassIDs(int a_yapClassID, Visitor4 visitor) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        traverseDeep(i_addToClassIndex, a_yapClassID, visitor);
    }

    void traverseDeep(Tree a_tree, int a_yapClassID, Visitor4 visitor) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        if (a_tree != null) {
            TreeIntObject node = (TreeIntObject) ((TreeInt) a_tree)
                .find(a_yapClassID);
            if (node != null && node.i_object != null) {
                ((Tree) node.i_object).traverse(visitor);
            }
        }
    }

    void traverseRemovedClassIDs(int a_yapClassID, Visitor4 visitor) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        traverseDeep(i_removeFromClassIndex, a_yapClassID, visitor);
    }

    private void traverseYapClassEntries(final Tree a_tree,
        final boolean a_add, final Collection4 a_indices) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        if (a_tree != null) {
            a_tree.traverse(new Visitor4() {

                public void visit(Object obj) {
                    TreeIntObject node = (TreeIntObject) obj;
                    YapClass yapClass = i_stream.i_classCollection.getYapClass(node.i_key);
                    final ClassIndex classIndex = yapClass.getIndex();
                    if (node.i_object != null) {
                        Visitor4 visitor = null;
                        if (a_add) {
                            visitor = new Visitor4() {
                                public void visit(Object a_object) {
                                    classIndex.add(((TreeInt) a_object).i_key);
                                }
                            };
                        } else {
                            visitor = new Visitor4() {

                                public void visit(Object a_object) {
                                    int id = ((TreeInt) a_object).i_key;
                                    YapObject yo = i_stream.getYapObject(id);
                                    if (yo != null) {
                                        i_stream.yapObjectGCd(yo);
                                    }
                                    classIndex.remove(id);
                                }

                            };
                        }
                        ((Tree) node.i_object).traverse(visitor);
                        if (!a_indices.containsByIdentity(classIndex)) {
                            a_indices.add(classIndex);
                        }

                    }
                }
            });
        }
    }

    private void write() {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        if (i_addToClassIndex == null && i_removeFromClassIndex == null && _slotChanges == null) {
            return;
        }
        
        int length = calculateLength();
        int address = i_file.getSlot(length);
        final YapWriter bytes = new YapWriter(this, address, length);
        bytes.writeInt(length);
        Tree.write(bytes, _slotChanges);
        Tree.write(bytes, i_addToClassIndex);
        Tree.write(bytes, i_removeFromClassIndex);
        bytes.write();
        flushFile();
        i_stream.writeTransactionPointer(address);
        flushFile();
        writeSlots();
        flushFile();
        i_stream.writeTransactionPointer(0);
        flushFile();
        i_file.free(address, length);
        
    }

    void writeOld() {
        synchronized (i_stream.i_lock) {
            i_pointerIo.useSlot(i_address);
            i_pointerIo.read();
            int length = i_pointerIo.readInt();
            if (length > 0) {
                YapWriter bytes = new YapWriter(this, i_address, length);
                bytes.read();
                bytes.incrementOffset(YapConst.YAPINT_LENGTH);
                _slotChanges = new TreeReader(bytes, new SlotChange(0)).read();
                i_addToClassIndex = new TreeReader(bytes, new TreeIntObject(0,
                    new TreeInt(0))).read();
                i_removeFromClassIndex = new TreeReader(bytes,
                    new TreeIntObject(0, new TreeInt(0))).read();
                writeSlots();
                flushFile();
                i_stream.writeTransactionPointer(0);
                flushFile();
                freeOnCommit();
            } else {
                i_stream.writeTransactionPointer(0);
                flushFile();
            }
        }
    }

    public void writePointer(int a_id, int a_address, int a_length) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
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

    private void writeSlots() {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        final Collection4 indicesToBeWritten = new Collection4();
        traverseYapClassEntries(i_addToClassIndex, true, indicesToBeWritten);
        traverseYapClassEntries(i_removeFromClassIndex, false,
            indicesToBeWritten);
        if(indicesToBeWritten.size() >= 0){
            Iterator4 i = indicesToBeWritten.iterator();
            while (i.hasNext()) {
                ClassIndex classIndex = (ClassIndex) i.next();
                classIndex.setDirty(i_stream);
                classIndex.write(this);
            }
            flushFile();
        }
        if(_slotChanges != null){
            _slotChanges.traverse(new Visitor4() {
                public void visit(Object a_object) {
                    ((SlotChange)a_object).writePointer(Transaction.this);
                }
            });
        }
    }
    
    void writeUpdateDeleteMembers(int a_id, YapClass a_yc, int a_type, int a_cascade) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        if(Tree.find(i_writtenUpdateDeletedMembers, new TreeInt(a_id)) != null){
            return;
        }
        if(DTrace.enabled){
            DTrace.WRITE_UPDATE_DELETE_MEMBERS.log(a_id);
        }
        i_writtenUpdateDeletedMembers = Tree.add(i_writtenUpdateDeletedMembers, new TreeInt(a_id));
        YapWriter objectBytes = i_stream.readWriterByID(this, a_id);
        if(objectBytes == null){
            if (a_yc.hasIndex()) {
                dontRemoveFromClassIndex(a_yc.getID(), a_id);
            }
            return;
        }
        a_yc.readObjectHeader(objectBytes, a_id);
        
        DeleteInfo info = (DeleteInfo)TreeInt.find(i_delete, a_id);
        if(info != null){
            if(info._cascade > a_cascade){
                a_cascade = info._cascade;
            }
        }
        
        objectBytes.setCascadeDeletes(a_cascade);
        a_yc.deleteMembers(objectBytes, a_type);
        slotFreeOnCommit(a_id, objectBytes.getAddress(), objectBytes.getLength());
    }
}