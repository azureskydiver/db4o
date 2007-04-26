/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import java.io.IOException;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.btree.BTree;
import com.db4o.internal.convert.*;
import com.db4o.internal.fileheader.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.query.processor.QQuery;
import com.db4o.internal.query.result.*;
import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public abstract class LocalObjectContainer extends ObjectContainerBase {
    
    private static final int DEFAULT_FREESPACE_ID = 0;

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
        
    LocalObjectContainer(Configuration config,ObjectContainerBase a_parent) {
        super(config,a_parent);
    }
    
    public Transaction newTransaction(Transaction parentTransaction) {
		return new LocalTransaction(this, parentTransaction);
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
    
    final protected void close2() {
    	freeInternalResources();
    	commitTransaction();
		shutdown();
        shutdownObjectContainer();
    }

    protected abstract void freeInternalResources();
    
    public void commit1() {
        commitTransaction();
    }

    void configureNewFile() throws IOException{
        
        newSystemData(configImpl().freespaceSystem());
        systemData().converterVersion(Converter.VERSION);
        createStringIO(_systemData.stringEncoding());
        
        generateNewIdentity();
        
        _freespaceManager = AbstractFreespaceManager.createNew(this);
        
        if(Debug.freespaceChecker){
            _fmChecker = new FreespaceManagerRam(this);
        }        
        
        blockSize(configImpl().blockSize());
        
        _fileHeader = new FileHeader1();
        
        setRegularEndAddress(_fileHeader.length());
        
        initNewClassCollection();
        initializeEssentialClasses();
        
        _fileHeader.initNew(this);
        
        _freespaceManager.onNew(this);
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
        return new BTree(i_trans, id, new IDHandler(this));
    }
    
    public final AbstractQueryResult newQueryResult(Transaction trans) {
    	return newQueryResult(trans, config().queryEvaluationMode());
    }

    public final AbstractQueryResult newQueryResult(Transaction trans, QueryEvaluationMode mode) {
    	if(mode == QueryEvaluationMode.IMMEDIATE){
        	return new IdListQueryResult(trans);
    	}
    	return new HybridQueryResult(trans, mode);
    }

    public final boolean delete4(Transaction ta, ObjectReference yo, int a_cascade, boolean userCall) {
        int id = yo.getID();
        StatefulBuffer reader = readWriterByID(ta, id);
        if (reader != null) {
            Object obj = yo.getObject();
            if (obj != null) {
                if ((!showInternalClasses())
                    && Const4.CLASS_INTERNAL.isAssignableFrom(obj.getClass())) {
                    return false;
                }
            }
            reader.setCascadeDeletes(a_cascade);
            reader.slotDelete();
            ClassMetadata yc = yo.getYapClass();
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

    public abstract String fileName();
    
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
        _freespaceManager.free(new Slot(a_address, a_length));
        if(Debug.freespace && Debug.freespaceChecker){
            _fmChecker.free(new Slot(a_address, a_length));
        }
    }

    final void freePrefetchedPointers() {
        if (i_prefetchedIDs != null) {
            i_prefetchedIDs.traverse(new Visitor4() {

                public void visit(Object a_object) {
                    free(((TreeInt) a_object)._key, Const4.POINTER_LENGTH);
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
    	synchronized(i_lock){
    		setIdentity(Db4oDatabase.generate());
    	}
    }

    public AbstractQueryResult getAll(Transaction trans) {
        return getAll(trans, config().queryEvaluationMode());
    }
    
    public AbstractQueryResult getAll(Transaction trans, QueryEvaluationMode mode) {
    	final AbstractQueryResult queryResult = newQueryResult(trans, mode);
    	queryResult.loadFromClassIndexes(classCollection().iterator());
        return queryResult;
    }

    final int getPointerSlot() {
        int id = getSlot(Const4.POINTER_LENGTH);

        // write a zero pointer first
        // to prevent delete interaction trouble
        ((LocalTransaction)systemTransaction()).writePointer(id, 0, 0);
        
        
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
        
        if(bytes <= 0){
        	throw new IllegalArgumentException();
        }
        
        if(_freespaceManager != null){
            
        	Slot slot = _freespaceManager.getSlot(bytes);
            
            if(Debug.freespace && Debug.freespaceChecker){
                if(slot != null){
                	int freeAddress = slot._address;
                    Collection4 wrongOnes = new Collection4();
                    Slot freeCheck = _fmChecker.getSlot(bytes);
                    
                    while(freeCheck != null && freeCheck._address != freeAddress ){
                        // System.out.println("Freecheck alternative found: "  + freeCheck);
                        wrongOnes.add(new int[]{freeCheck._address, bytes});
                        freeCheck = _fmChecker.getSlot(bytes);
                    }
                    Iterator4 i = wrongOnes.iterator();
                    while(i.moveNext()){
                        int[] adrLength = (int[])i.current();
                        _fmChecker.free(new Slot(adrLength[0], adrLength[1]) );
                    }
                    if(freeCheck == null){
                    	System.out.println(_freespaceManager);
                    	System.out.println(_fmChecker);
                    }
                }
            }
            
            if(slot != null){
                return slot._address;
            }
        }
        
        int blocksNeeded = blocksFor(bytes);
        if (Debug.xbytes && Deploy.overwrite) {
            overwriteDeletedBytes(_blockEndAddress, blocksNeeded * blockSize());
        }
        return appendBlocks(blocksNeeded);
    }
    
    protected int appendBlocks(int blockCount){
    	int blockedStartAddress = _blockEndAddress;
        int blockedEndAddress = _blockEndAddress + blockCount;
        checkBlockedAddress(blockedEndAddress);
        _blockEndAddress = blockedEndAddress;
        return blockedStartAddress;
    }
    
    private void checkBlockedAddress(int blockedAddress) {
    	if(blockedAddress < 0) {
    		rollback1();
    		switchToReadOnlyMode();
    		Exceptions4.throwRuntimeException(69);
    	}
    }

	private void switchToReadOnlyMode() {
		i_config.readOnly(true);
	}
    
    void ensureLastSlotWritten(){
        if (!Debug.xbytes){
            if(Deploy.overwrite){
                if(_blockEndAddress > blocksFor(fileLength())){
                    StatefulBuffer writer = getWriter(systemTransaction(), _blockEndAddress - 1, blockSize());
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

    public StatefulBuffer readWriterByID(Transaction a_ta, int a_id) {
        return (StatefulBuffer)readReaderOrWriterByID(a_ta, a_id, false);    
    }
    
    public StatefulBuffer[] readWritersByIDs(Transaction a_ta, int ids[]) {
		StatefulBuffer[] yapWriters = new StatefulBuffer[ids.length];
		for (int i = 0; i < ids.length; ++i) {
			if (ids[i] == 0) {
				yapWriters[i] = null;
			} else {
				yapWriters[i] = (StatefulBuffer) readReaderOrWriterByID(a_ta,
						ids[i], false);
			}
		}
		return yapWriters;
	}

    public Buffer readReaderByID(Transaction a_ta, int a_id) {
        return readReaderOrWriterByID(a_ta, a_id, true);
    }
    
    private final Buffer readReaderOrWriterByID(Transaction a_ta, int a_id, boolean useReader) {
        if (a_id <= 0) {
            throw new IllegalArgumentException("id must be greater than 0");
        }
        
        if(DTrace.enabled){
            DTrace.READ_ID.log(a_id);
        }
        
        try {
            Slot slot = ((LocalTransaction)a_ta).getCurrentSlotOfID(a_id);
            if (slot == null) {
                return null;
            }
            
            if (slot._address == 0) {
                return null;
            }
            
            if(DTrace.enabled){
                DTrace.READ_SLOT.logLength(slot._address, slot._length);
            }
            
            Buffer reader = null;
            if(useReader){
                reader = new Buffer(slot._length);
            }else{
                reader = getWriter(a_ta, slot._address, slot._length);
                ((StatefulBuffer)reader).setID(a_id);
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
    
    protected boolean doFinalize() {
    	return _fileHeader != null;
    }

    void readThis() throws IOException {
        
        newSystemData(AbstractFreespaceManager.FM_LEGACY_RAM);
        blockSizeReadFromFile(1);
        
        _fileHeader = FileHeader.readFixedPart(this);
        
        createStringIO(_systemData.stringEncoding());
        
        classCollection().setID(_systemData.classCollectionID());
        classCollection().read(systemTransaction());
        
        Converter.convert(new ConversionStage.ClassCollectionAvailableStage(this));
        
        readHeaderVariablePart();
        
        _freespaceManager = AbstractFreespaceManager.createNew(this, _systemData.freespaceSystem());
        _freespaceManager.read(_systemData.freespaceID());
       
        if(Debug.freespace){
            _fmChecker = new FreespaceManagerRam(this);
            _fmChecker.read(_systemData.freespaceID());
        }
        
        _freespaceManager.start(_systemData.freespaceAddress());
        
        if(Debug.freespace){
            _fmChecker.start(0);
        }
        
        if(needFreespaceMigration()){
        	migrateFreespace();
        }
        
        writeHeader(true, false);
        
        LocalTransaction trans = (LocalTransaction) _fileHeader.interruptedTransaction();
        
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
    
    private boolean needFreespaceMigration() {
		byte readSystem = _systemData.freespaceSystem();
		byte configuredSystem = configImpl().freespaceSystem();
		return (configuredSystem != 0 || readSystem == AbstractFreespaceManager.FM_LEGACY_RAM)
			&& (_freespaceManager.systemType() != configuredSystem);
	}

	private void migrateFreespace() throws IOException {
		FreespaceManager oldFreespaceManager = _freespaceManager;
		_freespaceManager = AbstractFreespaceManager.createNew(this, _systemData.freespaceSystem());
		_freespaceManager.start(createFreespaceSlot(_systemData.freespaceSystem()));
		AbstractFreespaceManager.migrate(oldFreespaceManager, _freespaceManager);
		_fileHeader.writeVariablePart(this, 1);
	}

	private void readHeaderVariablePart() {
		_fileHeader.readVariablePart(this);
        setNextTimeStampId(systemData().lastTimeStampID());
	}
    
    public final int createFreespaceSlot(byte freespaceSystem){
        _systemData.freespaceAddress(AbstractFreespaceManager.initSlot(this));
        _systemData.freespaceSystem(freespaceSystem);
        return _systemData.freespaceAddress();
    }
    
    public int ensureFreespaceSlot(){
        int address = systemData().freespaceAddress();
		if(address == 0){
            return createFreespaceSlot(systemData().freespaceSystem());
        }
        return address;
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
        getTransaction().rollback();
    }

    public final void setDirtyInSystemTransaction(PersistentBase a_object) {
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
        synchronized (i_lock) {
        	if (i_semaphores == null) {
            	i_semaphores = new Hashtable4(10);
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

    public void shutdown() {
    	if(i_config.isReadOnly()) {
    		// TODO: throw exception instead of returning silently
    		return;
    	}
        writeHeader(false, true);
    }
    
    public void commitTransaction() {
    	if(i_config.isReadOnly()) {
    		// TODO: throw exception instead of returning silently
    		return;
    	}
        // This will also commit the System Transaction,
        // since it is the parent or the same object.
        i_trans.commit();
    }

    public abstract void writeBytes(Buffer a_Bytes, int address, int addressOffset);

    public final void writeDirty() {        
        writeCachedDirty();
        writeVariableHeader();
    }

	private void writeCachedDirty() {
		Iterator4 i = i_dirty.iterator();
        while (i.moveNext()) {
        	PersistentBase dirty = (PersistentBase) i.current();
            dirty.write(systemTransaction());
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
    
    public final void writeEmbedded(StatefulBuffer a_parent, StatefulBuffer a_child) {
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

    void writeHeader(boolean startFileLockingThread, boolean shuttingDown) {
        
        int freespaceID=DEFAULT_FREESPACE_ID;
        if(shuttingDown){
            freespaceID = _freespaceManager.write();
            _freespaceManager = null;
        }
        
        if(Debug.freespace && Debug.freespaceChecker){
            freespaceID = _fmChecker.write();
        }
        
        // FIXME: blocksize should be already valid in FileHeader
        StatefulBuffer writer = getWriter(systemTransaction(), 0, _fileHeader.length());
        
        _fileHeader.writeFixedPart(this, startFileLockingThread, shuttingDown, writer, blockSize(), freespaceID);
        
        if(shuttingDown){
            ensureLastSlotWritten();
        }
        syncFiles();
    }

    public final void writeNew(ClassMetadata a_yapClass, StatefulBuffer aWriter) {
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

    public abstract void overwriteDeletedBytes(int a_address, int a_length);

    public final void writeTransactionPointer(int address) {
        _fileHeader.writeTransactionPointer(systemTransaction(), address);
    }
    
    public final void getSlotForUpdate(StatefulBuffer forWriter){
        Transaction trans = forWriter.getTransaction();
        int id = forWriter.getID();
        int length = forWriter.getLength();
        int address = getSlot(length);
        forWriter.address(address);
        trans.produceUpdateSlotChange(id, address, length);
    }

    public final void writeUpdate(ClassMetadata a_yapClass, StatefulBuffer a_bytes) {
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

    public void installDebugFreespaceManager(AbstractFreespaceManager manager) {
        _freespaceManager = manager;
    }

    public SystemData systemData() {
        return _systemData;
    }
    
    public long[] getIDsForClass(Transaction trans, ClassMetadata clazz){
		final IntArrayList ids = new IntArrayList();
        clazz.index().traverseAll(trans, new Visitor4() {
        	public void visit(Object obj) {
        		ids.add(((Integer)obj).intValue());
        	}
        });        
        return ids.asLong();
    }
    
    public QueryResult classOnlyQuery(Transaction trans, ClassMetadata clazz){
        if (!clazz.hasIndex()) {
        	
        	// TODO: If the class does not have an index, we won't be
        	//       able to get objects for it, so why not return an
        	//       empty QueryResult here, to signal that no further
        	//       processing needs to take place?
			return null;
		}
		
		final AbstractQueryResult queryResult = newQueryResult(trans);
		queryResult.loadFromClassIndex(clazz);
		return queryResult;
    }
    
    public QueryResult executeQuery(QQuery query){
    	AbstractQueryResult queryResult = newQueryResult(query.getTransaction());
    	queryResult.loadFromQuery(query);
    	return queryResult;
    }

	public LocalTransaction getLocalSystemTransaction() {
		return (LocalTransaction)systemTransaction();
	}
	
	public void onCommittedListener() {
		
	}
}