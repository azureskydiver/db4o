/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public abstract class YapFile extends YapStream {

    YapConfigBlock    			i_configBlock;
    PBootRecord                 i_bootRecord;

    private Collection4         i_dirty;
    private Tree                i_freeByAddress;
    private Tree                i_freeBySize;

    private boolean             i_isServer = false;

    private Tree                i_prefetchedIDs;

    private Hashtable4          i_semaphores;

    int                         i_writeAt;

    private final TreeIntObject i_finder   = new TreeIntObject(0);

    YapFile(YapStream a_parent) {
        super(a_parent);
    }
    
    byte blockSize(){
        return 1;
    }

    
    void blockSize(int blockSize){
        // do nothing, overwridden in YapRandomAccessFile 
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
        i_entryCounter++;
        try {
            write(false);
        } catch (Throwable t) {
            fatalException(t);
        }
        i_entryCounter--;
    }

    void configureNewFile() {
        blockSize(i_config.i_blockSize);
        i_writeAt = blocksFor(HEADER_LENGTH);
        i_configBlock = new YapConfigBlock(this, i_config.i_encoding);
        i_configBlock.write();
        i_configBlock.go();
        initNewClassCollection();
        initializeEssentialClasses();
        initBootRecord();
    }

    long currentVersion() {
        return i_bootRecord.i_versionGenerator;
    }

    void initNewClassCollection() {
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
                if ((!showInternalClasses())
                    && YapConst.CLASS_INTERNAL.isAssignableFrom(obj.getClass())) {
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

    abstract long fileLength();

    abstract String fileName();

    void addFreeSlotNodes(int a_address, int a_length) {
        FreeSlotNode addressNode = new FreeSlotNode(a_address);
        addressNode.createPeer(a_length);
        i_freeByAddress = Tree.add(i_freeByAddress, addressNode);
        i_freeBySize = Tree.add(i_freeBySize, addressNode.i_peer);
    }

    void free(int a_address, int a_length) {
        if(DTrace.enabled){
            DTrace.FREE.logLength(a_address, a_length);
        }
        if (a_length > i_config.i_discardFreeSpace) {
            a_length = blocksFor(a_length);
            i_finder.i_key = a_address;
            FreeSlotNode sizeNode;
            FreeSlotNode addressnode = (FreeSlotNode) Tree.findSmaller(
                i_freeByAddress, i_finder);
            if ((addressnode != null)
                && ((addressnode.i_key + addressnode.i_peer.i_key) == a_address)) {
                sizeNode = addressnode.i_peer;
                i_freeBySize = i_freeBySize.removeNode(sizeNode);
                sizeNode.i_key += a_length;
                FreeSlotNode secondAddressNode = (FreeSlotNode) Tree
                    .findGreaterOrEqual(i_freeByAddress, i_finder);
                if ((secondAddressNode != null)
                    && (a_address + a_length == secondAddressNode.i_key)) {
                    sizeNode.i_key += secondAddressNode.i_peer.i_key;
                    i_freeBySize = i_freeBySize
                        .removeNode(secondAddressNode.i_peer);
                    i_freeByAddress = i_freeByAddress
                        .removeNode(secondAddressNode);
                }
                sizeNode.removeChildren();
                i_freeBySize = Tree.add(i_freeBySize, sizeNode);
            } else {
                addressnode = (FreeSlotNode) Tree.findGreaterOrEqual(
                    i_freeByAddress, i_finder);
                if ((addressnode != null)
                    && (a_address + a_length == addressnode.i_key)) {
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
                writeXBytes(a_address, a_length * blockSize());
            }
        }
    }

    final void freePrefetchedPointers() {
        if (i_prefetchedIDs != null) {
            i_prefetchedIDs.traverse(new Visitor4() {

                public void visit(Object a_object) {
                    free(((TreeInt) a_object).i_key, YapConst.POINTER_LENGTH);
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
                ReflectClass claxx = yapClass.classReflector();
                if (claxx == null
                    || !( i_handlers.ICLASS_INTERNAL.isAssignableFrom(claxx))) {
                    Tree tree = yapClass.getIndex(ta);
                    if (tree != null) {
                        tree.traverse(new Visitor4() {

                            public void visit(Object obj) {
                                int id = ((TreeInt) obj).i_key;
                                TreeInt newNode = new TreeInt(id);
                                duplicates[0] = Tree
                                    .add(duplicates[0], newNode);
                                if (newNode.size() != 0) {
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

        // write a zero pointer first
        // to prevent delete interaction trouble
        i_systemTrans.writePointer(id, 0, 0);
        
        
        // We have to make sure that object IDs do not collide
        // with built-in type IDs.
        if(id <= i_handlers.maxTypeID()){
            return getPointerSlot();
        }
            
        return id;
    }
    
    private int blocksFor(long bytes){
        int blockLen = blockSize();
        int result = (int)(bytes / blockLen);
        if (bytes % blockLen != 0) result++;
        return result;
    }
    
    int getSlot(int a_length){
        int address = getSlot1(a_length);
        if(DTrace.enabled){
            DTrace.GET_SLOT.logLength(address, a_length);
        }
        return address;
    }

    private final int getSlot1(int bytes) {
        int blocksNeeded = blocksFor(bytes);
        i_finder.i_key = blocksNeeded;
        i_finder.i_object = null;
        i_freeBySize = FreeSlotNode.removeGreaterOrEqual(
            (FreeSlotNode) i_freeBySize, i_finder);

        if (i_finder.i_object == null) {
            if (Deploy.debug && Deploy.overwrite) {
                writeXBytes(i_writeAt, blocksNeeded * blockSize());
            }
            int slotAddress = i_writeAt;
            i_writeAt += blocksNeeded;
            return slotAddress;
        }
            
        FreeSlotNode node = (FreeSlotNode) i_finder.i_object;
        int blocksFound = node.i_key;
        int address = node.i_peer.i_key;
        i_freeByAddress = i_freeByAddress.removeNode(node.i_peer);
        if (blocksFound > blocksNeeded) {
            addFreeSlotNodes(address + blocksNeeded, blocksFound - blocksNeeded);
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
        i_configBlock._bootRecordID = getID1(i_systemTrans, i_bootRecord);
        i_configBlock.write();
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
        return new int[] { id, address};
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

    void raiseVersion(long a_minimumVersion) {
        if (i_bootRecord.i_versionGenerator < a_minimumVersion) {
            i_bootRecord.i_versionGenerator = a_minimumVersion;
            i_bootRecord.setDirty();
            i_bootRecord.store(1);
        }
    }

    public YapWriter readWriterByID(Transaction a_ta, int a_id) {
        return (YapWriter)readReaderOrWriterByID(a_ta, a_id, false);    
    }

    YapReader readReaderByID(Transaction a_ta, int a_id) {
        return readReaderOrWriterByID(a_ta, a_id, true);
    }
    
    private final YapReader readReaderOrWriterByID(Transaction a_ta, int a_id, boolean useReader) {
        if (a_id == 0) {
            return null;
        }
        
        if(DTrace.enabled){
            DTrace.READ_ID.log(a_id);
        }
        
        int[] addressLength = new int[2];
        
        try {
            a_ta.getSlotInformation(a_id, addressLength);
            if (addressLength[0] == 0) {
                return null;
            }
            
            if(DTrace.enabled){
                DTrace.READ_SLOT.logLength(addressLength[0], addressLength[1]);
            }
            
            YapReader reader = null;
            if(useReader){
                reader = new YapReader(addressLength[1]);
            }else{
                reader = getWriter(a_ta, addressLength[0], addressLength[1]);
                ((YapWriter)reader).setID(a_id);
            }

            reader.readEncrypt(this, addressLength[0]);
            return reader;
            
        } catch (Exception e) {
            
            // This is a tough catch-all block, but it does make sense:
            // A call for getById() could accidentally find something
            // that looks like a slot and try to use it.
            
            // TODO: For debug purposes analyse the caller stack and
            // differentiate here in debug mode.

            if (Debug.atHome) {
                System.out.println("YapFile.WriterByID failed for ID: " + a_id);
                e.printStackTrace();
            }
        }
        return null;        
        
    }

    void readThis() {
        YapWriter myreader = getWriter(i_systemTrans, 0, HEADER_LENGTH);
        myreader.read();

        byte firstFileByte = myreader.readByte();
        byte blockLen = 1;

        if (firstFileByte != YapConst.YAPBEGIN) {
            
            if(firstFileByte != YapConst.YAPFILEVERSION){
                Db4o.throwRuntimeException(17);
            }
            
            blockLen = myreader.readByte();
            
        }else{
	        if (myreader.readByte() != YapConst.YAPFILE) {
	            Db4o.throwRuntimeException(17);
	        }
        }
        
        blockSize(blockLen);
        
// Test code to force a big database file        
        
//        long len = fileLength();
//        long min = Integer.MAX_VALUE;
//        min *= (long)2;
//        if(len < min){
//            len = min;
//        }
//        i_writeAt = blocksFor(len);
        
        i_writeAt = blocksFor(fileLength());

        i_configBlock = new YapConfigBlock(this, blockLen);
        i_configBlock.read(myreader);

        // configuration lock time skipped
        myreader.incrementOffset(YapConst.YAPID_LENGTH);

        i_classCollection.setID(this, myreader.readInt());
        i_classCollection.read(i_systemTrans);

        int freeID = myreader.getAddress() + myreader._offset;
        int freeSlotsID = myreader.readInt();

        i_freeBySize = null;
        i_freeByAddress = null;

        if (freeSlotsID > 0
            && (i_config.i_discardFreeSpace != Integer.MAX_VALUE)) {
            YapWriter reader = readWriterByID(i_systemTrans, freeSlotsID);
            if (reader != null) {

                FreeSlotNode.sizeLimit = i_config.i_discardFreeSpace;

                i_freeBySize = new TreeReader(reader, new FreeSlotNode(0), true)
                    .read();

                final Tree[] addressTree = new Tree[1];
                if (i_freeBySize != null) {
                    i_freeBySize.traverse(new Visitor4() {

                        public void visit(Object a_object) {
                            FreeSlotNode node = ((FreeSlotNode) a_object).i_peer;
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
        if (i_configBlock._bootRecordID > 0) {
            bootRecord = getByID1(i_systemTrans, i_configBlock._bootRecordID);
        }
        if (bootRecord instanceof PBootRecord) {
            i_bootRecord = (PBootRecord) bootRecord;
            i_bootRecord.checkActive();
            i_bootRecord.i_stream = this;
            if (i_bootRecord.initConfig(i_config)) {
                i_classCollection.reReadYapClass(getYapClass(
                    i_handlers.ICLASS_PBOOTRECORD, false));
                setInternal(i_systemTrans, i_bootRecord, false);
            }
        } else {
            initBootRecord();
        }
        showInternalClasses(false);
        writeHeader(false);
        Transaction trans = i_configBlock.getTransactionToCommit();
        if (trans != null) {
            if (!i_config.i_disableCommitRecovery) {
                trans.writeOld();
            }
        }
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
        ((YapMeta) a_object).setStateDirty();
        ((YapMeta) a_object).cacheDirty(i_dirty);
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

    abstract void copy(int oldAddress, int oldAddressOffset, int newAddress, int newAddressOffset, int length);

    abstract void syncFiles();

    public String toString() {
        if (Debug.toStrings) {
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

        if (shuttingDown) {
            writeHeader(shuttingDown);
        }
    }

    abstract boolean writeAccessTime() throws IOException;

    abstract void writeBytes(YapWriter a_Bytes);

    final void writeDirty() {
        YapMeta dirty;
        Iterator4 i = i_dirty.iterator();
        while (i.hasNext()) {
            dirty = (YapMeta) i.next();
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
        a_child.address(address);
        a_child.writeEncrypt();
        int offsetBackup = a_parent._offset;
        a_parent._offset = a_child.getID();
        a_parent.writeInt(address);
        a_parent._offset = offsetBackup;
    }

    void writeHeader(boolean shuttingDown) {
        int freeBySizeID = 0;
        if (shuttingDown) {
            int length = Tree.byteCount(i_freeBySize);
            int[] slot = newSlot(i_systemTrans, length);
            freeBySizeID = slot[0];
            YapWriter sdwriter = new YapWriter(i_systemTrans, length);
            sdwriter.useSlot(freeBySizeID, slot[1], length);
            Tree.write(sdwriter, i_freeBySize);
            sdwriter.writeEncrypt();
            i_systemTrans.writePointer(slot[0], slot[1], length);
        }
        YapWriter writer = getWriter(i_systemTrans, 0, HEADER_LENGTH);
        writer.append(YapConst.YAPFILEVERSION);
        writer.append(blockSize());
        writer.writeInt(i_configBlock._address);
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
            a_yapClass.addToIndex(this, aWriter.getTransaction(), aWriter
                .getID());
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
        YapWriter bytes = new YapWriter(i_systemTrans,
            i_configBlock._address, YapConst.YAPINT_LENGTH * 2);
        bytes.moveForward(YapConfigBlock.TRANSACTION_OFFSET);
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
        a_bytes.address(address);
        trans.setPointer(id, address, length);
        trans.freeOnRollback(id, address, length);
        i_handlers.encrypt(a_bytes);
        a_bytes.write();
    }
}