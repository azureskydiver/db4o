/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;

import com.db4o.ext.*;

abstract class YapFile extends YapStream {
    
    private YapConfig i_configuration;
    PBootRecord i_bootRecord;

    private Collection4 i_dirty;
    private Tree i_freeByAddress;
    private Tree i_freeBySize;

    private boolean i_isServer = false;

    private Tree i_prefetchedIDs;

    private Hashtable4 i_semaphores;

    int i_timerAddress;
    int i_transactionPointerAddress;

    int i_writeAt = LENGTH;
    
    private final TreeIntObject           i_finder              = new TreeIntObject(0);

    YapFile(YapStream a_parent) {
        super(a_parent);
    }
    
    protected int blockSize(){
        return i_configuration.i_blockSize;
    }

    boolean close2() {
        boolean ret = super.close2();
        i_freeBySize = null;
        i_freeByAddress = null;
        i_dirty = null;
        return ret;
    }

    final void commit1() {
        checkClosed();
        try {
            i_entryCounter++;
            write(false);
        } catch (Throwable t) {
            fatalException(t);
        }
        i_entryCounter--;
    }

    void configureNewFile() {
        i_configuration = new YapConfig(this);
        i_configuration.setEncoding(i_config.i_encoding);
        i_configuration.i_blockSize = i_config.i_blockSize;
        i_configuration.write();
        i_configuration.go();
        initNewClassCollection();
        initializeEssentialClasses();
        initBootRecord();
    }
    
    long currentVersion(){
        return i_bootRecord.i_versionGenerator;
    }
    
    void initNewClassCollection(){
        // overridden in YapObjectCarrier to do nothing
        i_classCollection.initTables(1);
    }

    final ClassIndex createClassIndex(YapClass a_yapClass) {
        return new ClassIndex();
    }

    final QResult createQResult(Transaction a_ta) {
        return new QResult(a_ta);
    }

    final boolean delete5(Transaction ta, YapObject yo, int a_cascade) {
        int id = yo.getID();
        YapWriter reader = readWriterByID(ta, id);
        if (reader != null) {
            Object obj = yo.getObject();
            if (obj != null) {
                if ((!showInternalClasses()) && YapConst.CLASS_INTERNAL.isAssignableFrom(obj.getClass())) {
                    return false;
                }
            }
            reader.setCascadeDeletes(a_cascade);
            ta.setPointer(id, 0, 0);
            YapClass yc = yo.getYapClass();
            yc.delete(reader, obj);
            ta.freeOnCommit(id, reader.getAddress(), reader.getLength());

            // The following will not work with this approach.
            // Free blocks are identified in the Transaction by their ID.
            // TODO: Add a second tree specifically to free pointers.

            //			if(SecondClass.class.isAssignableFrom(yc.getJavaClass())){
            //				ta.freePointer(id);
            //			}

            return true;
        }
        return false;
    }

    abstract int fileLength();

    abstract String fileName();

    void addFreeSlotNodes(int a_address, int a_length) {
        FreeSlotNode addressNode = new FreeSlotNode(a_address);
        addressNode.createPeer(a_length);
        i_freeByAddress = Tree.add(i_freeByAddress, addressNode);
        i_freeBySize = Tree.add(i_freeBySize, addressNode.i_peer);
    }
    
    void free(int a_address, int a_length) {
        if (a_length > i_config.i_discardFreeSpace) {
            i_finder.i_key = a_address;
            FreeSlotNode sizeNode;
            FreeSlotNode addressnode = (FreeSlotNode)Tree.findSmaller(i_freeByAddress, i_finder);
            if ((addressnode != null)
                && ((addressnode.i_key + addressnode.i_peer.i_key) == a_address)) {
                sizeNode = addressnode.i_peer;
                i_freeBySize = i_freeBySize.removeNode(sizeNode);
                sizeNode.i_key += a_length;
                FreeSlotNode secondAddressNode = 
                    (FreeSlotNode)Tree.findGreaterOrEqual(i_freeByAddress, i_finder);
                if ((secondAddressNode != null)
                    && (a_address + a_length == secondAddressNode.i_key)) {
                    sizeNode.i_key += secondAddressNode.i_peer.i_key;
                    i_freeBySize = i_freeBySize.removeNode(secondAddressNode.i_peer);
                    i_freeByAddress = i_freeByAddress.removeNode(secondAddressNode);
                }
                sizeNode.removeChildren();
                i_freeBySize = Tree.add(i_freeBySize, sizeNode);
            } else {
                addressnode = (FreeSlotNode)Tree.findGreaterOrEqual(i_freeByAddress, i_finder);
                if ((addressnode != null) && (a_address + a_length == addressnode.i_key)) {
                    sizeNode = addressnode.i_peer;
                    i_freeByAddress = i_freeByAddress.removeNode(addressnode);
                    i_freeBySize = i_freeBySize.removeNode(sizeNode);
                    sizeNode.i_key += a_length;
                    addressnode.i_key = a_address;
                    addressnode.removeChildren();
                    sizeNode.removeChildren();
                    i_freeByAddress = Tree.add(i_freeByAddress, addressnode);
                    i_freeBySize = Tree.add(i_freeBySize, sizeNode);
                } else {
                    addFreeSlotNodes(a_address, a_length);
                }
            }
            if (Deploy.debug) {
                writeXBytes(a_address, a_length);
            }
        }
    }

    final void freePrefetchedPointers() {
        if (i_prefetchedIDs != null) {
            i_prefetchedIDs.traverse(new Visitor4() {
                public void visit(Object a_object) {
                    free(((TreeInt)a_object).i_key, YapConst.POINTER_LENGTH);
                }
            });
        }
        i_prefetchedIDs = null;
    }

    void getAll(Transaction ta, final QResult a_res) {

        // duplicates because of inheritance hierarchies
        final Tree[] duplicates = new Tree[1];

        YapClassCollectionIterator i = i_classCollection.iterator();
        while (i.hasNext()) {
            YapClass yapClass = i.nextClass();
            if (yapClass.getName() != null) {
                Class jc = yapClass.getJavaClass();
                if (jc == null || !(YapConst.CLASS_INTERNAL.isAssignableFrom(jc))) {
                    Tree tree = yapClass.getIndex(ta);
                    if (tree != null) {
                        tree.traverse(new Visitor4() {
                            public void visit(Object obj) {
                                int id = ((TreeInt)obj).i_key;
                                TreeInt newNode = new TreeInt(id);
                                duplicates[0] = Tree.add(duplicates[0], newNode);
                                if (newNode.i_size != 0) {
                                    a_res.add(id);
                                }
                            }
                        });
                    }
                }
            }
        }
    }
    
    final int getPointerSlot() {
        int id = getSlot(YapConst.POINTER_LENGTH);
        i_systemTrans.writePointer(id, 0, 0);
        // write a zero pointer first
        // to prevent delete interaction trouble
        return id;
    }

    int getSlot(int a_length) {
        int address;
        i_finder.i_key = a_length;
        i_finder.i_object = null;
        i_freeBySize = FreeSlotNode.removeGreaterOrEqual((FreeSlotNode)i_freeBySize, i_finder);
        if (i_finder.i_object != null) {
            FreeSlotNode node = (FreeSlotNode)i_finder.i_object;
            address = node.i_peer.i_key;
            int length = node.i_key;
            i_freeByAddress = i_freeByAddress.removeNode(node.i_peer);
            if (length > a_length) {
                addFreeSlotNodes(address + a_length, length - a_length);
            }
        } else {
            if (Deploy.debug && Deploy.overwrite) {
                writeXBytes(i_writeAt, a_length);
            }
            address = i_writeAt;
            i_writeAt += a_length;
        }
        return address;
    }
    
    public Db4oDatabase identity() {
        return i_bootRecord.i_db;
    }

    void initialize2() {
        i_dirty = new Collection4();
        super.initialize2();
    }
    
    private void initBootRecord() {
        showInternalClasses(true);
        i_bootRecord = new PBootRecord();
        i_bootRecord.i_stream = this;
        i_bootRecord.init(i_config);
        setInternal(i_systemTrans, i_bootRecord, false);
        i_configuration.i_bootRecordID = getID1(i_systemTrans, i_bootRecord);
        i_configuration.write();
        showInternalClasses(false);
    }

    boolean isServer() {
        return i_isServer;
    }

    final YapWriter newObject(Transaction a_trans, YapMeta a_object) {
        int length = a_object.ownLength();
        int[] slot = newSlot(a_trans, length);
        a_object.setID(this, slot[0]);
        YapWriter writer = new YapWriter(a_trans, length);
        writer.useSlot(slot[0], slot[1], length);
        if (Deploy.debug) {
            writer.writeBegin(a_object.getIdentifier(), length);
        }
        return writer;
    }

    final int[] newSlot(Transaction a_trans, int a_length) {
        int id = getPointerSlot();
        int address = getSlot(a_length);
        a_trans.setPointer(id, address, a_length);
        return new int[] { id, address };
    }

    final int newUserObject() {
        return getPointerSlot();
    }

    void prefetchedIDConsumed(int a_id) {
        i_finder.i_key = a_id;
        i_prefetchedIDs = i_prefetchedIDs.removeLike(i_finder);
    }

    int prefetchID() {
        int id = getPointerSlot();
        i_prefetchedIDs = Tree.add(i_prefetchedIDs, new TreeInt(id));
        return id;
    }
    
    void raiseVersion(long a_minimumVersion){
        if(i_bootRecord.i_versionGenerator < a_minimumVersion){
            i_bootRecord.i_versionGenerator = a_minimumVersion;
            i_bootRecord.setDirty();
            i_bootRecord.store(1);
        }
    }

    YapWriter readWriterByID(Transaction a_ta, int a_id) {
        // TODO:
        // load from cache here
        if (a_id == 0) {
            return null;
        }
        int[] addressLength = new int[2];
        try {
            a_ta.getSlotInformation(a_id, addressLength);
        } catch (Exception e) {
            if (Debug.atHome) {
                System.out.println("YapFile.WriterByID failed for ID: " + a_id);
                e.printStackTrace();
            }
            return null;
        }
        if (addressLength[0] == 0) {
            return null;
        }
        YapWriter reader = getWriter(a_ta, addressLength[0], addressLength[1]);
        reader.setID(a_id);

        reader.readEncrypt(this, addressLength[0]);
        return reader;
    }

    YapReader readReaderByID(Transaction a_ta, int a_id) {
        // TODO:
        // load from cache here
        if (a_id == 0) {
            return null;
        }
        int[] addressLength = new int[2];
        try {
            a_ta.getSlotInformation(a_id, addressLength);
        } catch (Exception e) {
            if (Debug.atHome) {
                System.out.println("YapFile.readReaderByID failed for ID: " + a_id);
                e.printStackTrace();
            }
            return null;
        }
        if (addressLength[0] == 0) {
            return null;
        }
        YapReader reader = new YapReader(addressLength[1]);
        reader.readEncrypt(this, addressLength[0]);
        return reader;
    }

    void readThis() {
        YapWriter myreader = getWriter(i_systemTrans, 0, LENGTH);
        myreader.read();
        if (myreader.readByte() == YapConst.YAPBEGIN) {
            if (myreader.readByte() == YapConst.YAPFILE) {
                i_writeAt = fileLength();

                i_configuration = new YapConfig(this);
                i_configuration.read(myreader);

                // configuration lock time skipped
                myreader.incrementOffset(YapConst.YAPID_LENGTH);

                i_classCollection.setID(this, myreader.readInt());
                i_classCollection.read(i_systemTrans);

                int freeID = myreader.getAddress() + myreader.i_offset;
                int freeSlotsID = myreader.readInt();

                i_freeBySize = null;
                i_freeByAddress = null;

                if (freeSlotsID > 0 && (i_config.i_discardFreeSpace != Integer.MAX_VALUE)) {
                    YapWriter reader = readWriterByID(i_systemTrans, freeSlotsID);
                    if (reader != null) {

                        FreeSlotNode.sizeLimit = i_config.i_discardFreeSpace;

                        i_freeBySize = new TreeReader(reader, new FreeSlotNode(0), true).read();

                        final Tree[] addressTree = new Tree[1];
                        if (i_freeBySize != null) {
                            i_freeBySize.traverse(new Visitor4() {
                                public void visit(Object a_object) {
                                    FreeSlotNode node = ((FreeSlotNode)a_object).i_peer;
                                    addressTree[0] = Tree.add(addressTree[0], node);
                                }
                            });
                        }
                        i_freeByAddress = addressTree[0];

                        free(freeSlotsID, YapConst.POINTER_LENGTH);
                        free(reader.getAddress(), reader.getLength());
                    }
                }
                showInternalClasses(true);
                Object bootRecord = null;
                if(i_configuration.i_bootRecordID > 0) {
                    bootRecord = getByID1(i_systemTrans, i_configuration.i_bootRecordID);
                }
                if(bootRecord instanceof PBootRecord) {
                     i_bootRecord = (PBootRecord)bootRecord;
                     i_bootRecord.checkActive();
                     i_bootRecord.i_stream = this;
                     if(i_bootRecord.initConfig(i_config)) {
                         i_classCollection.reReadYapClass(getYapClass(YapConst.CLASS_PBOOTRECORD, false));
                         setInternal(i_systemTrans, i_bootRecord, false);
                     }
                }else {
                    initBootRecord();
                }
                showInternalClasses(false);
                writeHeader(false);
                Transaction trans = i_configuration.getTransactionToCommit();
                if (trans != null) {
                    if(! i_config.i_disableCommitRecovery) {
                        trans.writeOld();
                    }
                }
                return;
            }
        }
        Db4o.throwRuntimeException(17);
    }

    public void releaseSemaphore(String name) {
        releaseSemaphore(checkTransaction(null), name);
    }

    void releaseSemaphore(Transaction ta, String name) {
        if (i_semaphores != null) {
            synchronized (i_semaphores) {
                if (i_semaphores != null && ta == i_semaphores.get(name)) {
                    i_semaphores.remove(name);
                    i_semaphores.notifyAll();
                }
            }
        }
    }

    void releaseSemaphores(Transaction ta) {
        if (i_semaphores != null) {
            synchronized (i_semaphores) {
                i_semaphores.forEachKey(new Visitor4() {
                    public void visit(Object a_object) {
                        i_semaphores.remove(a_object);
                    }
                });
                i_semaphores.notifyAll();
            }
        }
    }

    final void rollback1() {
        checkClosed();
        i_entryCounter++;
        getTransaction().rollback();
        i_entryCounter--;
    }

    final void setDirty(UseSystemTransaction a_object) {
        ((YapMeta)a_object).setStateDirty();
        ((YapMeta)a_object).cacheDirty(i_dirty);
    }

    public boolean setSemaphore(String name, int timeout) {
        return setSemaphore(checkTransaction(null), name, timeout);
    }

    boolean setSemaphore(Transaction ta, String name, int timeout) {
        if (name == null) {
            throw new NullPointerException();
        }
        if (i_semaphores == null) {
            synchronized (i_lock) {
                if (i_semaphores == null) {
                    i_semaphores = new Hashtable4(10);
                }
            }
        }
        synchronized (i_semaphores) {
            Object obj = i_semaphores.get(name);
            if (obj == null) {
                i_semaphores.put(name, ta);
                return true;
            }
            if (ta == obj) {
                return true;
            }
            long endtime = System.currentTimeMillis() + timeout;
            long waitTime = timeout;
            while (waitTime > 0) {
                try {
                    i_semaphores.wait(waitTime);
                } catch (Exception e) {
                    if (Debug.atHome) {
                        e.printStackTrace();
                    }
                }
                if (i_classCollection == null) {
                    return false;
                }

                obj = i_semaphores.get(name);

                if (obj == null) {
                    i_semaphores.put(name, ta);
                    return true;
                }

                waitTime = endtime - System.currentTimeMillis();
            }
            return false;
        }
    }

    void setServer(boolean flag) {
        i_isServer = flag;
    }

    void setTimerAddress(int a_address) {
        i_timerAddress = a_address;
    }

    void setTransactionPointerAddress(int a_address) {
        i_transactionPointerAddress = a_address;
    }
    
    abstract void copy(int a_oldAddress, int a_newAddress, int a_length);

    abstract void syncFiles();

    public String toString() {
        if(Debug.toStrings){
            return super.toString();
        }
        return fileName();
    }

    final YapWriter updateObject(Transaction a_trans, YapMeta a_object) {
        int length = a_object.ownLength();
        int id = a_object.getID();
        int address = getSlot(length);

        int[] oldAddressLength = new int[2];
        a_trans.getSlotInformation(id, oldAddressLength);

        a_trans.freeOnCommit(id, oldAddressLength[0], oldAddressLength[1]);
        a_trans.freeOnRollback(id, address, length);
        a_trans.setPointer(id, address, length);

        YapWriter writer = a_trans.i_stream.getWriter(a_trans, length);
        writer.useSlot(id, address, length);

        if (Deploy.debug) {
            writer.writeBegin(a_object.getIdentifier(), length);
        }
        return writer;
    }

    void write(boolean shuttingDown) {
        
        // This will also commit the System Transaction,
        // since it is the parent or the same object.
        i_trans.commit();
        
        if(shuttingDown){
            writeHeader(shuttingDown);
        }
    }

    abstract boolean writeAccessTime() throws IOException;

    abstract void writeBytes(YapWriter a_Bytes);

    final void writeDirty() {
        YapMeta dirty;
        Iterator4 i = i_dirty.iterator();
        while (i.hasNext()) {
            dirty = (YapMeta)i.next();
            dirty.write(this, i_systemTrans);
            dirty.notCachedDirty();
        }
        i_dirty.clear();
        writeBootRecord();
    }

    final void writeEmbedded(YapWriter a_parent, YapWriter a_child) {
        int length = a_child.getLength();
        int address = getSlot(length);
        a_child.getTransaction().freeOnRollback(address, address, length);
        a_child.setAddress(address);
        a_child.writeEncrypt();
        int offsetBackup = a_parent.i_offset;
        a_parent.i_offset = a_child.getID();
        a_parent.writeInt(address);
        a_parent.i_offset = offsetBackup;
    }

    void writeHeader(boolean shuttingDown) {
        int freeBySizeID = 0;
        if (shuttingDown) {
            int length = Tree.byteCount(i_freeBySize);
            int[] slot = newSlot(i_systemTrans, length);
            freeBySizeID = slot[0];
            YapWriter writer = new YapWriter(i_systemTrans, length);
            writer.useSlot(freeBySizeID, slot[1], length);
            Tree.write(writer, i_freeBySize);
            writer.writeEncrypt();
            i_systemTrans.writePointer(slot[0], slot[1], length);
        }
        YapWriter writer = getWriter(i_systemTrans, 0, LENGTH);
        writer.append(YapConst.YAPBEGIN);
        writer.append(YapConst.YAPFILE);
        writer.writeInt(i_configuration.getAddress());
        writer.writeInt(0);
        writer.writeInt(i_classCollection.getID());
        if (shuttingDown) {
            writer.writeInt(freeBySizeID);
        } else {
            writer.writeInt(0);
        }
        if (Deploy.debug && Deploy.overwrite) {
            writer.setID(YapConst.IGNORE_ID);
        }
        writer.write();
    }

    final void writeNew(YapClass a_yapClass, YapWriter aWriter) {
        writeObject(null, aWriter);
        if (maintainsIndices()) {
            a_yapClass.addToIndex(this, aWriter.getTransaction(), aWriter.getID());
        }
    }

    final void writeObject(YapMeta a_object, YapWriter a_writer) {
        i_handlers.encrypt(a_writer);
        writeBytes(a_writer);
    }
    
    void writeBootRecord() {
        i_bootRecord.store(1);
    }
    
    // This is a reroute of writeBytes to write the free blocks
    // unchecked.

    abstract void writeXBytes(int a_address, int a_length);

    YapWriter xBytes(int a_address, int a_length) {
        if (Deploy.debug) {
            YapWriter bytes = getWriter(i_systemTrans, a_address, a_length);
            for (int i = 0; i < a_length; i++) {
                bytes.append(YapConst.XBYTE);
            }
            return bytes;
        } else {
            throw YapConst.virtualException();
        }
    }

    final void writeTransactionPointer(int a_address) {
        YapWriter bytes =
            new YapWriter(i_systemTrans, i_transactionPointerAddress, YapConst.YAPINT_LENGTH * 2);
        bytes.writeInt(a_address);
        bytes.writeInt(a_address);
        if (Deploy.debug && Deploy.overwrite) {
            bytes.setID(YapConst.IGNORE_ID);
        }
        bytes.write();
    }

    final void writeUpdate(YapClass a_yapClass, YapWriter a_bytes) {
        Transaction trans = a_bytes.getTransaction();
        int id = a_bytes.getID();
        int length = a_bytes.getLength();
        int address = getSlot(length);
        a_bytes.setAddress(address);
        trans.setPointer(id, address, length);
        trans.freeOnRollback(id, address, length);
        i_handlers.encrypt(a_bytes);
        a_bytes.write();
    }
}
