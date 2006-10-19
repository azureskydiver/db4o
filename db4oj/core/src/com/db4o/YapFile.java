/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.header.*;
import com.db4o.inside.*;
import com.db4o.inside.btree.*;
import com.db4o.inside.classindex.*;
import com.db4o.inside.convert.*;
import com.db4o.inside.freespace.*;
import com.db4o.inside.query.*;
import com.db4o.inside.slots.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public abstract class YapFile extends YapStream {
    
    protected FileHeader       _fileHeader;
    
    private Collection4         i_dirty;
    
    private FreespaceManager _freespaceManager;
    
    // can be used to check freespace system
    private FreespaceManager _fmChecker;

    private boolean             i_isServer = false;

    private Tree                i_prefetchedIDs;

    private Hashtable4          i_semaphores;

    private int _blockEndAddress;
    
    private Tree                _freeOnCommit;
    
    private SystemData          _systemData;
        
    YapFile(Configuration config,YapStream a_parent) {
        super(config,a_parent);
    }

    public FreespaceManager freespaceManager() {
		return _freespaceManager;
	}
    
    public abstract void blockSize(int size);
    
    public void blockSizeReadFromFile(int size){
        blockSize(size);
        setRegularEndAddress(fileLength());
    }
    
    public void setRegularEndAddress(long address){
        _blockEndAddress = blocksFor(address);
    }
    
    protected boolean close2() {
        boolean ret = super.close2();
        i_dirty = null;
        return ret;
    }

    public void commit1() {
        checkClosed();
        i_entryCounter++;
        try {
            write(false);
        } catch (Throwable t) {
            fatalException(t);
        }
        i_entryCounter--;
    }

    void configureNewFile() throws IOException{
        
        newSystemData(configImpl().freespaceSystem());
        systemData().converterVersion(Converter.VERSION);
        createStringIO(_systemData.stringEncoding());
        
        generateNewIdentity();
        
        _freespaceManager = FreespaceManager.createNew(this);
        
        if(Debug.freespaceChecker){
            _fmChecker = new FreespaceManagerRam(this);
        }        
        
        blockSize(configImpl().blockSize());
        
        _fileHeader = new FileHeader1();
        
        setRegularEndAddress(_fileHeader.length());
        
        initNewClassCollection();
        initializeEssentialClasses();
        
        _fileHeader.initNew(this);
        
        _freespaceManager.start(_systemData.freespaceAddress());
        
        if(Debug.freespace  && Debug.freespaceChecker){
            _fmChecker.start(0);
        }
    }
    
    private void newSystemData(byte freespaceSystem){
        _systemData = new SystemData();
        _systemData.stringEncoding(configImpl().encoding());
        _systemData.freespaceSystem(freespaceSystem);
    }
    
    public int converterVersion() {
        return _systemData.converterVersion();
    }
    
    public abstract void copy(int oldAddress, int oldAddressOffset, int newAddress, int newAddressOffset, int length);
    
    public long currentVersion() {
        return _timeStampIdGenerator.lastTimeStampId();
    }

    void initNewClassCollection() {
        // overridden in YapObjectCarrier to do nothing
        classCollection().initTables(1);
    }
    
    public final BTree createBTreeClassIndex(int id){
        return new BTree(i_trans, id, new YInt(this));
    }

    public final QueryResultImpl createQResult(Transaction a_ta) {
        return new QueryResultImpl(a_ta);
    }

    public final boolean delete5(Transaction ta, YapObject yo, int a_cascade, boolean userCall) {
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
            reader.slotDelete();
            YapClass yc = yo.getYapClass();
            yc.delete(reader, obj);

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

    public abstract long fileLength();

    abstract String fileName();
    
    public void free(Slot slot) {
        if(slot == null){
            return;
        }
        if(slot._address == 0){
            return;
        }
        free(slot._address, slot._length);
    }

    public void free(int a_address, int a_length) {
        if(DTrace.enabled){
            DTrace.FILE_FREE.logLength(a_address, a_length);
        }
        if(_freespaceManager == null){
            // Can happen on early free before freespacemanager
            // is up, during conversion.
           return;
        }
        _freespaceManager.free(a_address, a_length);
        if(Debug.freespace && Debug.freespaceChecker){
            _fmChecker.free(a_address, a_length);
        }
    }

    final void freePrefetchedPointers() {
        if (i_prefetchedIDs != null) {
            i_prefetchedIDs.traverse(new Visitor4() {

                public void visit(Object a_object) {
                    free(((TreeInt) a_object)._key, YapConst.POINTER_LENGTH);
                }
            });
        }
        i_prefetchedIDs = null;
    }
    
    final void freeSpaceBeginCommit(){
        if(_freespaceManager == null){
            return;
        }
        _freespaceManager.beginCommit();
    }
    
    final void freeSpaceEndCommit(){
        if(_freespaceManager == null){
            return;
        }
        _freespaceManager.endCommit();
    }
    
    public void generateNewIdentity(){
        setIdentity(Db4oDatabase.generate());
        
    }

    public QueryResult getAll(Transaction ta) {
    	
    	final QueryResultImpl queryResult = new QueryResultImpl(ta);

        // duplicates because of inheritance hierarchies
        final Tree[] duplicates = new Tree[1];

        YapClassCollectionIterator i = classCollection().iterator();
        while (i.moveNext()) {
			final YapClass yapClass = i.currentClass();
			if (yapClass.getName() != null) {
				ReflectClass claxx = yapClass.classReflector();
				if (claxx == null
						|| !(i_handlers.ICLASS_INTERNAL.isAssignableFrom(claxx))) {
					final ClassIndexStrategy index = yapClass.index();
					index.traverseAll(ta, new Visitor4() {
						public void visit(Object obj) {
							int id = ((Integer)obj).intValue();
							TreeInt newNode = new TreeInt(id);
							duplicates[0] = Tree.add(duplicates[0], newNode);
							if (newNode.size() != 0) {
								queryResult.add(id);
							}
						}
					});
				}
			}
		}
//        a_res.reset();
        
        return queryResult;
    }

    final int getPointerSlot() {
        int id = getSlot(YapConst.POINTER_LENGTH);

        // write a zero pointer first
        // to prevent delete interaction trouble
        i_systemTrans.writePointer(id, 0, 0);
        
        
        // We have to make sure that object IDs do not collide
        // with built-in type IDs.
        if(i_handlers.isSystemHandler(id)){
            return getPointerSlot();
        }
            
        return id;
    }
    
    public int getSlot(int a_length){
        
        if(! DTrace.enabled){
            return getSlot1(a_length);
        }
        
        int address = getSlot1(a_length);
        DTrace.GET_SLOT.logLength(address, a_length);
        return address;
    }

    private final int getSlot1(int bytes) {
        
        if(Deploy.debug){
            if(bytes <= 0){
                throw new RuntimeException("Who wants invalid zero or smaller slots ?");
            }
        }
        
        if(_freespaceManager != null){
            
            int freeAddress = _freespaceManager.getSlot(bytes);
            
            if(Debug.freespace && Debug.freespaceChecker){
                if(freeAddress > 0){
                    Collection4 wrongOnes = new Collection4();
                    int freeCheck = _fmChecker.getSlot(bytes);
                    
                    while(freeCheck != freeAddress  && freeCheck > 0){
                        // System.out.println("Freecheck alternative found: "  + freeCheck);
                        wrongOnes.add(new int[]{freeCheck,bytes});
                        freeCheck = _fmChecker.getSlot(bytes);
                    }
                    Iterator4 i = wrongOnes.iterator();
                    while(i.moveNext()){
                        int[] adrLength = (int[])i.current();
                        _fmChecker.free(adrLength[0], adrLength[1]);
                    }
                    if(freeCheck == 0){
                        _freespaceManager.debug();
                        _fmChecker.debug();
                    }
                }
            }
            
            if(freeAddress > 0){
                return freeAddress;
            }
        }
        
        int blocksNeeded = blocksFor(bytes);
        if (Debug.xbytes && Deploy.overwrite) {
            debugWriteXBytes(_blockEndAddress, blocksNeeded * blockSize());
        }
        return appendBlocks(blocksNeeded);
    }
    
    protected int appendBlocks(int blockCount){
        int blockedAddress = _blockEndAddress;
        _blockEndAddress += blockCount;
        return blockedAddress;
    }
    
    void ensureLastSlotWritten(){
        if (!Debug.xbytes){
            if(Deploy.overwrite){
                if(_blockEndAddress > blocksFor(fileLength())){
                    YapWriter writer = getWriter(i_systemTrans, _blockEndAddress - 1, blockSize());
                    writer.write();
                }
            }
        }
    }

    public Db4oDatabase identity() {
        return _systemData.identity();
    }
    
    public void setIdentity(Db4oDatabase identity){
        _systemData.identity(identity);
        
        // The dirty TimeStampIdGenerator triggers writing of
        // the variable part of the systemdata. We need to
        // make it dirty here, so the new identity is persisted:
        _timeStampIdGenerator.next();
    }

    void initialize2() {
        i_dirty = new Collection4();
        super.initialize2();
    }
    
    boolean isServer() {
        return i_isServer;
    }

    public final Pointer4 newSlot(Transaction a_trans, int a_length) {
        int id = getPointerSlot();
        int address = getSlot(a_length);
        a_trans.setPointer(id, address, a_length);
        return new Pointer4(id, address);
    }

    public final int newUserObject() {
        return getPointerSlot();
    }

    public void prefetchedIDConsumed(int a_id) {
        i_prefetchedIDs = i_prefetchedIDs.removeLike(new TreeIntObject(a_id));
    }

    public int prefetchID() {
        int id = getPointerSlot();
        i_prefetchedIDs = Tree.add(i_prefetchedIDs, new TreeInt(id));
        return id;
    }
    
    public ReferencedSlot produceFreeOnCommitEntry(int id){
        Tree node = TreeInt.find(_freeOnCommit, id);
        if (node != null) {
            return (ReferencedSlot) node;
        }
        ReferencedSlot slot = new ReferencedSlot(id);
        _freeOnCommit = Tree.add(_freeOnCommit, slot);
        return slot;
    }
    
    public void reduceFreeOnCommitReferences(ReferencedSlot slot){
        if(slot.removeReferenceIsLast()){
            _freeOnCommit = _freeOnCommit.removeNode(slot);
        }
    }
    
    public void freeDuringCommit(ReferencedSlot referencedSlot, Slot slot){
        _freeOnCommit = referencedSlot.free(this, _freeOnCommit, slot);
    }

    public void raiseVersion(long a_minimumVersion) {
        synchronized (lock()) {
            _timeStampIdGenerator.setMinimumNext(a_minimumVersion);
        }
    }

    public YapWriter readWriterByID(Transaction a_ta, int a_id) {
        return (YapWriter)readReaderOrWriterByID(a_ta, a_id, false);    
    }

    public YapReader readReaderByID(Transaction a_ta, int a_id) {
        return readReaderOrWriterByID(a_ta, a_id, true);
    }
    
    private final YapReader readReaderOrWriterByID(Transaction a_ta, int a_id, boolean useReader) {
        if (a_id == 0) {
            return null;
        }
        
        if(DTrace.enabled){
            DTrace.READ_ID.log(a_id);
        }
        
        try {
            Slot slot = a_ta.getCurrentSlotOfID(a_id);
            if (slot == null) {
                return null;
            }
            
            if (slot._address == 0) {
                return null;
            }
            
            if(DTrace.enabled){
                DTrace.READ_SLOT.logLength(slot._address, slot._length);
            }
            
            YapReader reader = null;
            if(useReader){
                reader = new YapReader(slot._length);
            }else{
                reader = getWriter(a_ta, slot._address, slot._length);
                ((YapWriter)reader).setID(a_id);
            }

            reader.readEncrypt(this, slot._address);
            return reader;
            
        } catch (Exception e) {
            
            // This is a tough catch-all block, but it does make sense:
            // A call for getById() could accidentally find something
            // that looks like a slot and try to use it.
            
            // TODO: For debug purposes analyse the caller stack and
            // differentiate here in debug mode.

            if (Debug.atHome) {
                System.out.println("YapFile.readReaderOrWriterByID failed for ID: " + a_id);
                e.printStackTrace();
            }
        }
        return null;        
        
    }
    
    

    void readThis() throws IOException {
        
        newSystemData(FreespaceManager.FM_LEGACY_RAM);
        blockSizeReadFromFile(1);
        
        _fileHeader = FileHeader.readFixedPart(this);
        
        createStringIO(_systemData.stringEncoding());
        
        classCollection().setID(_systemData.classCollectionID());
        classCollection().read(i_systemTrans);
        
        Converter.convert(new ConversionStage.ClassCollectionAvailableStage(this));
        
        _freespaceManager = FreespaceManager.createNew(this, _systemData.freespaceSystem());
        _freespaceManager.read(_systemData.freespaceID());
       
        if(Debug.freespace){
            _fmChecker = new FreespaceManagerRam(this);
            _fmChecker.read(_systemData.freespaceID());
        }
        
        _freespaceManager.start(_systemData.freespaceAddress());
        
        if(Debug.freespace){
            _fmChecker.start(0);
        }
        
        readHeaderVariablePart();
        
        if(_freespaceManager.requiresMigration(configImpl().freespaceSystem(), _systemData.freespaceSystem())){
            _freespaceManager = _freespaceManager.migrate(this, configImpl().freespaceSystem());
            _fileHeader.writeVariablePart(this, 1);
        }
        
        writeHeader(false);
        
        Transaction trans = _fileHeader.interruptedTransaction();
        
        if (trans != null) {
            if (!configImpl().commitRecoveryDisabled()) {
                trans.writeOld();
            }
        }

        if(Converter.convert(new ConversionStage.SystemUpStage(this))){
            _systemData.converterVersion(Converter.VERSION);
            _fileHeader.writeVariablePart(this, 1);
            getTransaction().commit();
        }
        
    }

	private void readHeaderVariablePart() {
		_fileHeader.readVariablePart(this);
        setNextTimeStampId(systemData().lastTimeStampID());
	}
    
    public int newFreespaceSlot(byte freespaceSystem){
        _systemData.freespaceAddress(FreespaceManager.initSlot(this));
        _systemData.freespaceSystem(freespaceSystem);
        return _systemData.freespaceAddress();
    }
    
    public void ensureFreespaceSlot(){
        if(systemData().freespaceAddress() == 0){
            newFreespaceSlot(systemData().freespaceSystem());
        }
    }

    public void releaseSemaphore(String name) {
        releaseSemaphore(checkTransaction(null), name);
    }

    public void releaseSemaphore(Transaction ta, String name) {
        if (i_semaphores != null) {
            synchronized (i_semaphores) {
                if (i_semaphores != null && ta == i_semaphores.get(name)) {
                    i_semaphores.remove(name);
                    i_semaphores.notifyAll();
                }
            }
        }
    }

    public void releaseSemaphores(Transaction ta) {
        if (i_semaphores != null) {
            final Hashtable4 semaphores = i_semaphores;
            synchronized (semaphores) {
                semaphores.forEachKeyForIdentity(new Visitor4() {
                    public void visit(Object a_object) {
                        semaphores.remove(a_object);
                    }
                }, ta);
                semaphores.notifyAll();
            }
        }
    }

    public final void rollback1() {
        checkClosed();
        i_entryCounter++;
        getTransaction().rollback();
        i_entryCounter--;
    }

    public final void setDirtyInSystemTransaction(YapMeta a_object) {
        a_object.setStateDirty();
        a_object.cacheDirty(i_dirty);
    }

    public boolean setSemaphore(String name, int timeout) {
        return setSemaphore(checkTransaction(null), name, timeout);
    }

    public boolean setSemaphore(Transaction ta, String name, int timeout) {
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
                if (classCollection() == null) {
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

    public void setServer(boolean flag) {
        i_isServer = flag;
    }

    public abstract void syncFiles();

    public String toString() {
        if (Debug4.prettyToStrings) {
            return super.toString();
        }
        return fileName();
    }

    public void write(boolean shuttingDown) {

        // This will also commit the System Transaction,
        // since it is the parent or the same object.
        i_trans.commit();

        if (shuttingDown) {
            writeHeader(shuttingDown);
        }
    }

    public abstract boolean writeAccessTime(int address, int offset, long time) throws IOException;

    public abstract void writeBytes(YapReader a_Bytes, int address, int addressOffset);

    public final void writeDirty() {        
        writeCachedDirty();
        writeVariableHeader();
    }

	private void writeCachedDirty() {
		Iterator4 i = i_dirty.iterator();
        while (i.moveNext()) {
        	YapMeta dirty = (YapMeta) i.current();
            dirty.write(i_systemTrans);
            dirty.notCachedDirty();
        }
        i_dirty.clear();
	}
    
    protected void writeVariableHeader(){
        if(! _timeStampIdGenerator.isDirty()){
        	return;
        }
        _systemData.lastTimeStampID(_timeStampIdGenerator.lastTimeStampId());
        _fileHeader.writeVariablePart(this, 2);
        _timeStampIdGenerator.setClean();
    }
    
    public final void writeEmbedded(YapWriter a_parent, YapWriter a_child) {
        int length = a_child.getLength();
        int address = getSlot(length);
        a_child.getTransaction().slotFreeOnRollback(address, address, length);
        a_child.address(address);
        a_child.writeEncrypt();
        int offsetBackup = a_parent._offset;
        a_parent._offset = a_child.getID();
        a_parent.writeInt(address);
        a_parent._offset = offsetBackup;
    }

    void writeHeader(boolean shuttingDown) {
        
        int freespaceID = _freespaceManager.write(shuttingDown);
        
        if(shuttingDown){
            _freespaceManager = null;
        }
        
        if(Debug.freespace && Debug.freespaceChecker){
            freespaceID = _fmChecker.write(shuttingDown);
        }
        
        // FIXME: blocksize should be already valid in FileHeader
        YapWriter writer = getWriter(i_systemTrans, 0, _fileHeader.length());
        
        _fileHeader.writeFixedPart(this, shuttingDown, writer, blockSize(), freespaceID);
        
        if(shuttingDown){
            ensureLastSlotWritten();
        }
        syncFiles();
    }

    public final void writeNew(YapClass a_yapClass, YapWriter aWriter) {
        aWriter.writeEncrypt(this, aWriter.getAddress(), 0);
        if(a_yapClass == null){
            return;
        }
        if (maintainsIndices()) {
            a_yapClass.addToIndex(this, aWriter.getTransaction(), aWriter
                .getID());
        }
    }

    // This is a reroute of writeBytes to write the free blocks
    // unchecked.

    public abstract void debugWriteXBytes(int a_address, int a_length);

    YapReader xBytes(int a_address, int a_length) {
        YapReader bytes = getWriter(i_systemTrans, a_address, a_length);
        for (int i = 0; i < a_length; i++) {
            bytes.append(YapConst.XBYTE);
        }
        return bytes;
    }

    public final void writeTransactionPointer(int address) {
        _fileHeader.writeTransactionPointer(getSystemTransaction(), address);
    }
    
    public final void getSlotForUpdate(YapWriter forWriter){
        Transaction trans = forWriter.getTransaction();
        int id = forWriter.getID();
        int length = forWriter.getLength();
        int address = getSlot(length);
        forWriter.address(address);
        trans.slotFreeOnRollbackSetPointer(id, address, length);
    }

    public final void writeUpdate(YapClass a_yapClass, YapWriter a_bytes) {
        if(a_bytes.getAddress() == 0){
            getSlotForUpdate(a_bytes);
        }
        a_bytes.writeEncrypt();
    }

    public void setNextTimeStampId(long val) {
        _timeStampIdGenerator.setMinimumNext(val);
        _timeStampIdGenerator.setClean();
    }
    
    public SystemInfo systemInfo() {
        return new SystemInfoFileImpl(this);
    }

	public FileHeader getFileHeader() {
		return _fileHeader;
	}

    public void installDebugFreespaceManager(FreespaceManager manager) {
        _freespaceManager = manager;
    }

    public SystemData systemData() {
        return _systemData;
    }
    
    public long[] getIDsForClass(Transaction trans, YapClass clazz){
		final IntArrayList ids = new IntArrayList();
        clazz.index().traverseAll(trans, new Visitor4() {
        	public void visit(Object obj) {
        		ids.add(((Integer)obj).intValue());
        	}
        });        
        return ids.asLong();
    }
    
    public QueryResult classOnlyQuery(YapClass clazz){
        if (!clazz.hasIndex()) {
        	
        	// TODO: If the class does not have an index, we won't be
        	//       able to get objects for it, so why not return an
        	//       empty QueryResult here, to signal that no further
        	//       processing needs to take place?
			return null;
		}
		
		final QueryResultImpl resLocal = new QueryResultImpl(i_trans);
		final ClassIndexStrategy index = clazz.index();
		index.traverseAll(i_trans, new Visitor4() {
			public void visit(Object a_object) {
				resLocal.add(((Integer)a_object).intValue());
			}
		});
		return resLocal;
    }
    
    public QueryResult executeQuery(QQuery query){
    	QueryResultImpl queryResult = createQResult(query.getTransaction());
    	query.executeLocal(queryResult);
    	return queryResult;
    }

}