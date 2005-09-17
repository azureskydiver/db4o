/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.inside.ix.*;
import com.db4o.inside.slots.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public class Transaction {

    public final YapStream         i_stream;

    public final YapFile           i_file;

    final Transaction       i_parentTransaction;

    private final YapWriter i_pointerIo;

    private Tree            i_slots;

    private Tree            i_addToClassIndex;

    private Tree            i_removeFromClassIndex;

    private List4           i_freeOnCommit;

    private Tree            i_freeOnRollback;

    private List4           i_freeOnBoth;

    private List4           i_dirtyFieldIndexes;

    private List4           i_transactionListeners;

    private int             i_address;                                  // only used to pass address to Thread

    private byte[]          i_bytes = new byte[YapConst.POINTER_LENGTH];

    // contains TreeIntObject nodes
    // if TreeIntObject#i_object is null then this means DONT delete.
    // Otherwise TreeIntObject#i_object contains the YapObject
    public Tree          i_delete;  // public for .NET conversion
    
    protected Tree			i_writtenUpdateDeletedMembers;

    Transaction(YapStream a_stream, Transaction a_parent) {
        i_stream = a_stream;
        i_file = (a_stream instanceof YapFile) ? (YapFile) a_stream : null;
        i_parentTransaction = a_parent;
        i_pointerIo = new YapWriter(this, YapConst.POINTER_LENGTH);
    }

    public void addTransactionListener(TransactionListener a_listener) {
        i_transactionListeners = new List4(i_transactionListeners, a_listener);
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
        return ((2 // Transaction slot length
            + (Tree.size(i_slots) * 3)) * YapConst.YAPINT_LENGTH)
            + Tree.byteCount(i_addToClassIndex)
            + Tree.byteCount(i_removeFromClassIndex);
    }

    private final void clearAll() {
        i_slots = null;
        i_addToClassIndex = null;
        i_removeFromClassIndex = null;
        i_freeOnCommit = null;
        i_freeOnRollback = null;
        i_dirtyFieldIndexes = null;
        i_transactionListeners = null;
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

    void commitTransactionListeners() {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        if (i_transactionListeners != null) {
            Iterator4 i = new Iterator4(i_transactionListeners);
            while (i.hasNext()) {
                ((TransactionListener) i.next()).preCommit();
            }
            i_transactionListeners = null;
        }
    }

    void commit() {
        synchronized (i_stream.i_lock) {
            
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
            i_stream.i_classCollection.write(i_stream, i_stream
                .getSystemTransaction());

            if (i_dirtyFieldIndexes != null) {
                Iterator4 i = new Iterator4(i_dirtyFieldIndexes);
                while (i.hasNext()) {
                    ((IndexTransaction) i.next()).commit();
                }
            }
            if (i_parentTransaction != null) {
                i_parentTransaction.commit();
            } else {
                i_stream.writeDirty();
            }
            write();
            freeOnCommit();
            clearAll();
        }
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
    
    
    // This method should really by in Db4oDatabase but we don't want to
    // expose the package methods used 
    public int ensureDb4oDatabase(Db4oDatabase a_db){
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        Db4oDatabase stored = (Db4oDatabase)i_stream.db4oTypeStored(this,a_db);
        if (stored == null) {
            i_stream.showInternalClasses(true);
            i_stream.set3(this,a_db, 2, false);
            int newID = i_stream.getID1(this, a_db);
            i_stream.showInternalClasses(false);
            return newID;
        }
        if(stored == a_db){
            return i_stream.getID1(this, a_db);
        }
        i_stream.showInternalClasses(true);
        int id = i_stream.getID1(this, stored);
        i_stream.bind(a_db, id);
        i_stream.showInternalClasses(false);
        return id;
    }

    boolean isDeleted(int a_id) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        ReferencedSlot slot = findSlotInHierarchy(a_id);
        if (slot != null) {
            return slot._address == 0;
        }
        return false;
    }

    private final ReferencedSlot findSlot(int a_id) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        Tree tree = TreeInt.find(i_slots, a_id);
        if (tree != null) {
            return (ReferencedSlot) ((TreeIntObject) tree).i_object;
        }
        return null;
    }

    private final ReferencedSlot findSlotInHierarchy(int a_id) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        ReferencedSlot slot = findSlot(a_id);
        if (slot != null) {
            return slot;
        }
        if (i_parentTransaction != null) {
            return i_parentTransaction.findSlotInHierarchy(a_id);
        }
        return null;
    }

    private final void freeOnBoth() {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        Iterator4 i = new Iterator4(i_freeOnBoth);
        while (i.hasNext()) {
            ReferencedSlot slot = (ReferencedSlot) i.next();
            i_file.free(slot._address, slot._length);
        }
        i_freeOnBoth = null;
    }

    private final void freeOnCommit() {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        freeOnBoth();
        if (i_freeOnCommit != null) {
            Iterator4 i = new Iterator4(i_freeOnCommit);
            while (i.hasNext()) {
                int id = ((Integer) i.next()).intValue();
                Tree node = TreeInt.find(i_stream.i_freeOnCommit, id);
                if (node != null) {
                    ReferencedSlot slot = (ReferencedSlot) ((TreeIntObject) node).i_object;
                    i_file.free(slot._address, slot._length);
                    
                    //TODO: Instead of working with a reference counting
                    //      system it could work better to push the 
                    //      information from the system transaction to
                    //      individual transactions on commit. Consider.
                    
                    slot._references--;
                    
                    boolean removeNode = true;
                    if (slot._references > 0) {
                        Tree tio = TreeInt.find(i_freeOnRollback, id);
                        if (tio != null) {
                            ReferencedSlot newSlot = (ReferencedSlot) ((TreeIntObject) tio).i_object;
                            if (slot._address != newSlot._address) {
                                slot._address = newSlot._address;
                                slot._length = newSlot._length;
                                removeNode = false;
                            }
                        }
                    }
                    if (removeNode) {
                        i_stream.i_freeOnCommit = i_stream.i_freeOnCommit
                            .removeNode(node);
                    }
                }
            }
        }
        i_freeOnCommit = null;
    }

    void freeOnCommit(int a_id, int a_address, int a_length) {
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
        Tree isSecondWrite = TreeInt.find(i_freeOnRollback, a_id);
        if (isSecondWrite != null) {
            i_freeOnBoth = new List4(i_freeOnBoth,
                ((TreeIntObject) isSecondWrite).i_object);
            i_freeOnRollback = i_freeOnRollback.removeNode(isSecondWrite);
        } else {
            Tree node = TreeInt.find(i_stream.i_freeOnCommit, a_id);
            if (node != null) {
                ReferencedSlot slot = (ReferencedSlot) ((TreeIntObject) node).i_object;
                slot._references++;
                if (Debug.atHome) {
                    if (slot._address != a_address
                        || slot._length != a_length) {
                        System.out
                            .println("Unexpected condition in Transaction::freeOnCommit: Differing addresses.");
                    }
                }
            } else {
                ReferencedSlot slot = new ReferencedSlot(a_address, a_length);
                slot._references = 1;
                i_stream.i_freeOnCommit = Tree.add(i_stream.i_freeOnCommit,
                    new TreeIntObject(a_id, slot));
            }
            i_freeOnCommit = new List4(i_freeOnCommit, new Integer(a_id));
        }
    }

    void freeOnRollback(int a_id, int a_address, int a_length) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        if(DTrace.enabled){
            DTrace.FREE_ON_ROLLBACK.log(a_id);
            DTrace.FREE_ON_ROLLBACK.logLength(a_address, a_length);
        }

        if (Deploy.debug) {
            if (a_id == 0) {
                throw new RuntimeException();
            }
        }
        i_freeOnRollback = Tree.add(i_freeOnRollback, new TreeIntObject(a_id,
            new ReferencedSlot(a_address, a_length)));
    }

    void freePointer(int a_id) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        freeOnCommit(a_id, a_id, YapConst.POINTER_LENGTH);
    }

    void getSlotInformation(int a_id, int[] a_addressLength) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        if (a_id != 0) {
            ReferencedSlot slot = findSlot(a_id);
            if (slot != null) {
                a_addressLength[0] = slot._address;
                a_addressLength[1] = slot._length;
            } else {
                if (i_parentTransaction != null) {
                    i_parentTransaction.getSlotInformation(a_id,
                        a_addressLength);
                    if (a_addressLength[0] != 0) {
                        return;
                    }
                }
                if (Deploy.debug) {
                    i_pointerIo.useSlot(a_id);
                    i_pointerIo.read();
                    i_pointerIo.readBegin(YapConst.YAPPOINTER);
                    a_addressLength[0] = i_pointerIo.readInt();
                    a_addressLength[1] = i_pointerIo.readInt();
                    i_pointerIo.readEnd();
                } else {
                    i_file.readBytes(i_bytes, a_id, YapConst.POINTER_LENGTH);
                    a_addressLength[0] = (i_bytes[3] & 255)
                        | (i_bytes[2] & 255) << 8 | (i_bytes[1] & 255) << 16
                        | i_bytes[0] << 24;
                    a_addressLength[1] = (i_bytes[7] & 255)
                        | (i_bytes[6] & 255) << 8 | (i_bytes[5] & 255) << 16
                        | i_bytes[4] << 24;
                }

            }
        }
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
                Iterator4 i = new Iterator4(i_dirtyFieldIndexes);
                while (i.hasNext()) {
                    ((IndexTransaction) i.next()).rollback();
                }
            }
            if (i_freeOnCommit != null) {
                Iterator4 i = new Iterator4(i_freeOnCommit);
                while (i.hasNext()) {
                    Tree node = TreeInt.find(i_stream.i_freeOnCommit,
                        ((Integer) i.next()).intValue());
                    if (node != null) {
                        ReferencedSlot slot = (ReferencedSlot) ((TreeIntObject) node).i_object;
                        slot._references--;
                        if (slot._references < 1) {
                            i_stream.i_freeOnCommit = i_stream.i_freeOnCommit
                                .removeNode(node);
                        }
                    }
                }
            }
            if (i_freeOnRollback != null) {
                i_freeOnRollback.traverse(new Visitor4() {

                    public void visit(Object obj) {
                        TreeIntObject node = (TreeIntObject) obj;
                        ReferencedSlot slot = (ReferencedSlot) node.i_object;
                        ((YapFile) i_stream)
                            .free(slot._address, slot._length);
                    }
                });
            }
            freeOnBoth();
            rollBackTransactionListeners();
            clearAll();
        }
    }

    void rollBackTransactionListeners() {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        if (i_transactionListeners != null) {
            Iterator4 i = new Iterator4(i_transactionListeners);
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
        ReferencedSlot slot = findSlot(a_id);
        if (slot != null) {
            slot._address = a_address;
            slot._length = a_length;
        } else {
            i_slots = Tree.add(i_slots, new TreeIntObject(a_id, new ReferencedSlot(
                a_address, a_length)));
        }
    }

    void traverseAddedClassIDs(int a_yapClassID, Visitor4 visitor) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        traverseDeep(i_addToClassIndex, a_yapClassID, visitor);
    }

    void traverseRemovedClassIDs(int a_yapClassID, Visitor4 visitor) {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        traverseDeep(i_removeFromClassIndex, a_yapClassID, visitor);
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

    private void write() {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        if (!(i_slots == null && i_addToClassIndex == null && i_removeFromClassIndex == null)) {
            int length = calculateLength();
            int address = ((YapFile) i_stream).getSlot(length);
            freeOnCommit(address, address, length);
            final YapWriter bytes = new YapWriter(this, address, length);
            bytes.writeInt(length);
            Tree.write(bytes, i_slots);
            Tree.write(bytes, i_addToClassIndex);
            Tree.write(bytes, i_removeFromClassIndex);
            bytes.write();
            i_stream.writeTransactionPointer(address);
            writeSlots();
            i_stream.writeTransactionPointer(0);
        }
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
                    YapClass yapClass = i_stream.getYapClass(node.i_key);
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

    private void writeSlots() {
        if(Debug.checkSychronization){
            i_stream.i_lock.notify();
        }
        final Collection4 indicesToBeWritten = new Collection4();
        traverseYapClassEntries(i_addToClassIndex, true, indicesToBeWritten);
        traverseYapClassEntries(i_removeFromClassIndex, false,
            indicesToBeWritten);
        Iterator4 i = indicesToBeWritten.iterator();
        while (i.hasNext()) {
            ClassIndex classIndex = (ClassIndex) i.next();
            classIndex.setDirty(i_stream);
            classIndex.write(i_stream, this);
        }
        if (i_slots != null) {
            i_slots.traverse(new Visitor4() {

                public void visit(Object obj) {
                    TreeIntObject node = (TreeIntObject) obj;
                    ReferencedSlot slot = (ReferencedSlot) node.i_object;
                    writePointer(node.i_key, slot._address, slot._length);
                }
            });
        }

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
                i_slots = new TreeReader(bytes, new TreeIntObject(0, new ReferencedSlot(
                    0, 0))).read();
                i_addToClassIndex = new TreeReader(bytes, new TreeIntObject(0,
                    new TreeInt(0))).read();
                i_removeFromClassIndex = new TreeReader(bytes,
                    new TreeIntObject(0, new TreeInt(0))).read();
                writeSlots();
                i_stream.writeTransactionPointer(0);
                freeOnCommit();
            } else {
                i_stream.writeTransactionPointer(0);
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
        if (Deploy.debug && Deploy.overwrite) {
            i_pointerIo.setID(YapConst.IGNORE_ID);
        }
        i_pointerIo.write();
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
        freeOnCommit(a_id, objectBytes.getAddress(), objectBytes.getLength());
    }

    public String toString() {
        return i_stream.toString();
    }
}