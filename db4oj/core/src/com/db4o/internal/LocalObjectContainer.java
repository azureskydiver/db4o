/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.convert.*;
import com.db4o.internal.fileheader.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.query.result.*;
import com.db4o.internal.references.*;
import com.db4o.internal.slots.*;
import com.db4o.internal.transactionlog.*;


/**
 * @exclude
 */
public abstract class LocalObjectContainer extends ExternalObjectContainer implements InternalObjectContainer, EmbeddedObjectContainer{
    
    private static final int DEFAULT_FREESPACE_ID = 0;

	protected FileHeader       _fileHeader;
    
    private Collection4         i_dirty;
    
    private FreespaceManager _freespaceManager;
    
    private boolean             i_isServer = false;

    private Lock4 				_semaphoresLock = new Lock4();
    private Hashtable4          _semaphores;

    private int _blockEndAddress;
    
    private SystemData          _systemData;
    
    private final IdSystem _idSystem;
    
	private final byte[] _pointerBuffer = new byte[Const4.POINTER_LENGTH];

	protected final ByteArrayBuffer _pointerIo = new ByteArrayBuffer(Const4.POINTER_LENGTH);    

    LocalObjectContainer(Configuration config) {
        super(config);
        _idSystem = newIdSystem();
    }
    
    public Transaction newTransaction(Transaction parentTransaction, ReferenceSystem referenceSystem, boolean isSystemTransaction) {
		LocalTransaction transaction = new LocalTransaction(this, parentTransaction, referenceSystem);
		if(isSystemTransaction){
			idSystem().systemTransaction(transaction);
		}else {
			idSystem().addTransaction(transaction);
		}
		return transaction;
	}

    protected IdSystem newIdSystem() {
    	return new StandardIdSystem(this);
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
        _blockEndAddress = bytesToBlocks(address);
    }
    
    final protected void close2() {
    	try {
	    	if (!_config.isReadOnly()) {
				commitTransaction();
				shutdown();
			}
    	}
    	finally {
    		shutdownObjectContainer();
    	}
    }

    public void commit1(Transaction trans) {
        trans.commit();
    }

    void configureNewFile() {
        
        newSystemData(configImpl().freespaceSystem());
        systemData().converterVersion(Converter.VERSION);
        createStringIO(_systemData.stringEncoding());
        
        generateNewIdentity();
        
        _freespaceManager = AbstractFreespaceManager.createNew(this);
        
        blockSize(configImpl().blockSize());
        
        _fileHeader = new FileHeader1();
        
        setRegularEndAddress(_fileHeader.length());
        
        initNewClassCollection();
        initializeEssentialClasses();
        
        _fileHeader.initNew(this);
        
        _freespaceManager.start(_systemData.freespaceAddress());
    }
    
    private void newSystemData(byte freespaceSystem){
        _systemData = new SystemData();
        _systemData.stringEncoding(configImpl().encoding());
        _systemData.freespaceSystem(freespaceSystem);
    }
    
    public int converterVersion() {
        return _systemData.converterVersion();
    }
    
    public long currentVersion() {
        return _timeStampIdGenerator.lastTimeStampId();
    }

    void initNewClassCollection() {
        // overridden in YapObjectCarrier to do nothing
        classCollection().initTables(1);
    }
    
    public final BTree createBTreeClassIndex(int id){
        return new BTree(_transaction, id, new IDHandler());
    }
    
    public final AbstractQueryResult newQueryResult(Transaction trans) {
    	return newQueryResult(trans, config().evaluationMode());
    }

    public final AbstractQueryResult newQueryResult(Transaction trans, QueryEvaluationMode mode) {
    	if (trans == null) {
    		throw new ArgumentNullException();
    	}
    	if(mode == QueryEvaluationMode.IMMEDIATE){
        	return new IdListQueryResult(trans);
    	}
    	return new HybridQueryResult(trans, mode);
    }

    public final boolean delete4(Transaction transaction, ObjectReference ref, Object obj, int cascade, boolean userCall) {
        int id = ref.getID();
        StatefulBuffer reader = readWriterByID(transaction, id);
        if (reader != null) {
            if (obj != null) {
                if ((!showInternalClasses())
                    && Const4.CLASS_INTERNAL.isAssignableFrom(obj.getClass())) {
                    return false;
                }
            }
            reader.setCascadeDeletes(cascade);
            idSystem().notifySlotDeleted(transaction, id, SlotChangeFactory.USER_OBJECTS);
            ClassMetadata classMetadata = ref.classMetadata();
            classMetadata.delete(reader, obj);

            return true;
        }
        return false;
    }

    public abstract long fileLength();

    public abstract String fileName();
    
    public void free(Slot slot) {
        if(slot.isNull()){
        	return;
        	
        	// TODO: This should really be an IllegalArgumentException but old database files 
        	//       with index-based FreespaceManagers appear to deliver zeroed slots.
            // throw new IllegalArgumentException();
        }
        if(_freespaceManager == null){
            // Can happen on early free before freespacemanager
            // is up, during conversion.
           return;
        }
        Slot blockedSlot = toBlockedLength(slot);
        
        if(DTrace.enabled){
            DTrace.FILE_FREE.logLength(blockedSlot.address(), blockedSlot.length());
        }
        
        _freespaceManager.free(blockedSlot);

    }
    
    public Slot toBlockedLength(Slot slot){
    	return new Slot(slot.address(), bytesToBlocks(slot.length()));
    }
    
    public Slot toNonBlockedLength(Slot slot){
    	return new Slot(slot.address(), blocksToBytes(slot.length()));
    }

    public void free(int address, int a_length) {
        free(new Slot(address, a_length));
    }
    
    public void generateNewIdentity(){
    	synchronized(_lock){
    		setIdentity(Db4oDatabase.generate());
    	}
    }

    public AbstractQueryResult queryAllObjects(Transaction trans) {
        return getAll(trans, config().evaluationMode());
    }
    
    public AbstractQueryResult getAll(Transaction trans, QueryEvaluationMode mode) {
    	final AbstractQueryResult queryResult = newQueryResult(trans, mode);
    	queryResult.loadFromClassIndexes(classCollection().iterator());
        return queryResult;
    }

    public int allocatePointerSlot() {
    	
        int id = allocateSlot(Const4.POINTER_LENGTH).address();
        if(!isValidPointer(id)){
        	return allocatePointerSlot();
        }
        
        // write a zero pointer first
        // to prevent delete interaction trouble
        writePointer(id, Slot.ZERO);
        
        if(DTrace.enabled){
            DTrace.GET_POINTER_SLOT.log(id);
        }
            
        return id;
    }

	protected boolean isValidPointer(int id) {
		// We have to make sure that object IDs do not collide
        // with built-in type IDs.
		return ! _handlers.isSystemHandler(id);
	}

	public Slot allocateSlot(int length){
    	int blocks = bytesToBlocks(length);
    	Slot slot = allocateBlockedSlot(blocks);
        if(DTrace.enabled){
            DTrace.GET_SLOT.logLength(slot.address(), slot.length());
        }
        return toNonBlockedLength(slot);
    }

    private final Slot allocateBlockedSlot(int blocks) {
        if(blocks <= 0){
        	throw new IllegalArgumentException();
        }
        if(_freespaceManager != null){
        	Slot slot = _freespaceManager.allocateSlot(blocks);
            if(slot != null){
                return slot;
            }
            while(growDatabaseByConfiguredSize()){
            	slot = _freespaceManager.allocateSlot(blocks);
                if(slot != null){
                    return slot;
                }
            }
        }
		return appendBlocks(blocks);
    }

	private boolean growDatabaseByConfiguredSize() {
		int reservedStorageSpace = configImpl().databaseGrowthSize();
		if(reservedStorageSpace <= 0){
			return false;
		}
		int reservedBlocks = bytesToBlocks(reservedStorageSpace);
		int reservedBytes = blocksToBytes(reservedBlocks);
		Slot slot = new Slot(_blockEndAddress, reservedBlocks);
        if (Debug4.xbytes && Deploy.overwrite) {
            overwriteDeletedBlockedSlot(slot);
        }else{
			writeBytes(new ByteArrayBuffer(reservedBytes), _blockEndAddress, 0);
        }
		_freespaceManager.free(slot);
		_blockEndAddress += reservedBlocks;
		return true;
	}
    
    protected final Slot appendBlocks(int blockCount){
    	int blockedStartAddress = _blockEndAddress;
        int blockedEndAddress = _blockEndAddress + blockCount;
        checkBlockedAddress(blockedEndAddress);
        _blockEndAddress = blockedEndAddress;
        Slot slot = new Slot(blockedStartAddress, blockCount);
        if (Debug4.xbytes && Deploy.overwrite) {
            overwriteDeletedBlockedSlot(slot);
        }
        return slot; 
    }
    
    public final Slot appendBytes(long bytes){
    	Slot slot = appendBlocks(bytesToBlocks(bytes));
    	return toNonBlockedLength(slot);
    }
    
    private void checkBlockedAddress(int blockedAddress) {
    	if(blockedAddress < 0) {
    		switchToReadOnlyMode();
    		throw new DatabaseMaximumSizeReachedException();
    	}
    }

	private void switchToReadOnlyMode() {
		_config.readOnly(true);
	}
    
	// When a file gets opened, it uses the file size to determine where 
	// new slots can be appended. If this method would not be called, the
	// freespace system could already contain a slot that points beyond
	// the end of the file and this space could be allocated and used twice,
	// for instance if a slot was allocated and freed without ever being
	// written to file.
    void ensureLastSlotWritten(){
        if (!Debug4.xbytes){
            if(Deploy.overwrite){
                if(_blockEndAddress > bytesToBlocks(fileLength())){
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

    public final Pointer4 newSlot(int length) {
        return new Pointer4(allocatePointerSlot(), allocateSlot(length));
    }

    public final int idForNewUserObject(Transaction trans) {
    	return idSystem().newId(trans, SlotChangeFactory.USER_OBJECTS);
    }

    public void raiseVersion(long a_minimumVersion) {
        synchronized (lock()) {
            _timeStampIdGenerator.setMinimumNext(a_minimumVersion);
        }
    }

    public StatefulBuffer readWriterByID(Transaction transaction, int id, boolean lastCommitted) {
        return (StatefulBuffer)readReaderOrWriterByID((LocalTransaction)transaction, id, false, lastCommitted);    
    }
    
    public StatefulBuffer readWriterByID(Transaction a_ta, int a_id) {
        return readWriterByID(a_ta, a_id, false);
    }
    
    @Override
    public ByteArrayBuffer[] readSlotBuffers(Transaction transaction, int ids[]) {
    	ByteArrayBuffer[] buffers = new ByteArrayBuffer[ids.length];
		for (int i = 0; i < ids.length; ++i) {
			if (ids[i] == 0) {
				buffers[i] = null;
			} else {
				buffers[i] = readReaderOrWriterByID((LocalTransaction)transaction, ids[i], true);
			}
		}
		return buffers;
	}
    
    public ByteArrayBuffer readReaderByID(Transaction transaction, int id, boolean lastCommitted) {
        return readReaderOrWriterByID((LocalTransaction)transaction, id, true, lastCommitted);
    }

    public ByteArrayBuffer readReaderByID(Transaction trans, int id) {
        return readReaderByID(trans, id, false);
    }
    
    private final ByteArrayBuffer readReaderOrWriterByID(LocalTransaction transaction, int id,
			boolean useReader) {
    	return readReaderOrWriterByID(transaction, id, useReader, false);
    }
    
    private final ByteArrayBuffer readReaderOrWriterByID(LocalTransaction trans, int id,
			boolean useReader, boolean lastCommitted) {
		if (id <= 0) {
			throw new IllegalArgumentException();
		}

		if (DTrace.enabled) {
			DTrace.READ_ID.log(id);
		}

		Slot slot = lastCommitted ? idSystem().getCommittedSlotOfID(id) :  
			idSystem().getCurrentSlotOfID(trans, id);
		
		return readReaderOrWriterBySlot(trans, id, useReader, slot);
	}
    
    public ByteArrayBuffer readSlotBuffer(Slot slot) {
    	ByteArrayBuffer reader = new ByteArrayBuffer(slot.length());
		reader.readEncrypt(this, slot.address());
		reader.skip(0);
		return reader;
    }

	ByteArrayBuffer readReaderOrWriterBySlot(Transaction a_ta, int a_id, boolean useReader, Slot slot) {
		if (slot == null) {
			return null;
		}

		if (slot.isNull()) {
			return null;
		}

		if (DTrace.enabled) {
			DTrace.READ_SLOT.logLength(slot.address(), slot.length());
		}

		ByteArrayBuffer reader = null;
		if (useReader) {
			reader = new ByteArrayBuffer(slot.length());
		} else {
			reader = getWriter(a_ta, slot.address(), slot.length());
			((StatefulBuffer) reader).setID(a_id);
		}

		reader.readEncrypt(this, slot.address());
		return reader;
	}
    
    protected boolean doFinalize() {
    	return _fileHeader != null;
    }

    void readThis() throws OldFormatException {
        
        newSystemData(AbstractFreespaceManager.FM_LEGACY_RAM);
        blockSizeReadFromFile(1);
        
        _fileHeader = FileHeader.readFixedPart(this);
        
        createStringIO(_systemData.stringEncoding());
        
        classCollection().setID(_systemData.classCollectionID());
        classCollection().read(systemTransaction());
        
        Converter.convert(new ConversionStage.ClassCollectionAvailableStage(this));
        
        readHeaderVariablePart();
        
        if(_config.isReadOnly()) {
        	return;
        }
        
		_freespaceManager = AbstractFreespaceManager.createNew(this,
				_systemData.freespaceSystem());
		_freespaceManager.read(_systemData.freespaceID());
		_freespaceManager.start(_systemData.freespaceAddress());
        
        if(freespaceMigrationRequired()){
        	migrateFreespace();
        }
        
        writeHeader(true, false);
        
        InterruptedTransactionHandler interruptedTransactionHandler =  _fileHeader.interruptedTransactionHandler();
        
        if (interruptedTransactionHandler != null) {
            if (!configImpl().commitRecoveryDisabled()) {
            	interruptedTransactionHandler.completeInterruptedTransaction();
            }
        }

        if(Converter.convert(new ConversionStage.SystemUpStage(this))){
            _systemData.converterVersion(Converter.VERSION);
            _fileHeader.writeVariablePart(this, 1);
            transaction().commit();
        }
        
    }
    
    private boolean freespaceMigrationRequired() {
		if(_freespaceManager == null){
			return false;
		}
		byte readSystem = _systemData.freespaceSystem();
		byte configuredSystem = configImpl().freespaceSystem();
		if(_freespaceManager.systemType() == configuredSystem){
			return false;
		}
		if (configuredSystem != 0){
			return true;
		}
		return AbstractFreespaceManager.migrationRequired(readSystem);
	}

	private void migrateFreespace() {
		FreespaceManager oldFreespaceManager = _freespaceManager;
		
		FreespaceManager newFreespaceManager = AbstractFreespaceManager.createNew(this, configImpl().freespaceSystem());
		newFreespaceManager.start(0);
		
		systemData().freespaceSystem(configImpl().freespaceSystem());
        
        _freespaceManager = newFreespaceManager;
		
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

    public final void releaseSemaphore(String name) {
        releaseSemaphore(null, name);
    }

    public final void releaseSemaphore(final Transaction trans, final String name) {
        synchronized(_lock){
            if (_semaphores == null) {
                return;
            }
        }
        _semaphoresLock.run(new Closure4() { public Object run() {
            Transaction transaction = checkTransaction(trans);
            if (_semaphores != null && transaction == _semaphores.get(name)) {
                _semaphores.remove(name);
            }
            _semaphoresLock.awake();
            
            return null;
        }});
    }

    public void releaseSemaphores(final Transaction trans) {
        if (_semaphores != null) {
            final Hashtable4 semaphores = _semaphores;
            _semaphoresLock.run(new Closure4() { public Object run() {
                semaphores.forEachKeyForIdentity(new Visitor4() {
                    public void visit(Object a_object) {
                        semaphores.remove(a_object);
                    }
                }, trans);
                
                _semaphoresLock.awake();
                return null;
             }});            
        }
    }

    public final void rollback1(Transaction trans) {
        trans.rollback();
    }

    public final void setDirtyInSystemTransaction(PersistentBase a_object) {
        a_object.setStateDirty();
        a_object.cacheDirty(i_dirty);
    }

    public final boolean setSemaphore(String name, int timeout) {
        return setSemaphore(null, name, timeout);
    }

    public final boolean setSemaphore(final Transaction trans, final String name, final int timeout) {
        if (name == null) {
            throw new NullPointerException();
        }
        synchronized (_lock) {
        	if (_semaphores == null) {
            	_semaphores = new Hashtable4(10);
            }
        }
        
        final BooleanByRef acquired = new BooleanByRef();
        _semaphoresLock.run(new Closure4() { public Object run() {
        	try{
	            Transaction transaction = checkTransaction(trans);
	            Object candidateTransaction = _semaphores.get(name);
	            if (trans == candidateTransaction) {
	            	acquired.value = true;
	                return null;
	            }
	            
	            if (candidateTransaction == null) {
	                _semaphores.put(name, transaction);
	                acquired.value = true;
	                return null;
	            }
	            
	            long endtime = System.currentTimeMillis() + timeout;
	            long waitTime = timeout;
	            while (waitTime > 0) {
	                _semaphoresLock.awake();
					_semaphoresLock.snooze(waitTime);
					
	                if (classCollection() == null) {
	                    acquired.value = false;
	                	return null;
	                }
	
	                candidateTransaction = _semaphores.get(name);	
	                if (candidateTransaction == null) {
	                    _semaphores.put(name, transaction);
	                    acquired.value = true;
	                    return null;
	                }
	
	                waitTime = endtime - System.currentTimeMillis();
	            }
	            
	            acquired.value = false;
	            return null;
        	} finally{
        		_semaphoresLock.awake();
        	}        	
        }});
        
        return acquired.value;
    }

    public void setServer(boolean flag) {
        i_isServer = flag;
    }

    public abstract void syncFiles();

    protected String defaultToString() {
        return fileName();
    }

    public void shutdown() {
        writeHeader(false, true);
    }
    
    public final void commitTransaction() {
        _transaction.commit();
    }

    public abstract void writeBytes(ByteArrayBuffer buffer, int blockedAddress, int addressOffset);

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
	
    public final void writeEncrypt(ByteArrayBuffer buffer, int address, int addressOffset) {
        _handlers.encrypt(buffer);
        writeBytes(buffer, address, addressOffset);
        _handlers.decrypt(buffer);
    }
    
    protected void writeVariableHeader(){
        if(! _timeStampIdGenerator.isDirty()){
        	return;
        }
        _systemData.lastTimeStampID(_timeStampIdGenerator.lastTimeStampId());
        _fileHeader.writeVariablePart(this, 2);
        _timeStampIdGenerator.setClean();
    }
    
    void writeHeader(boolean startFileLockingThread, boolean shuttingDown) {
        
        int freespaceID=DEFAULT_FREESPACE_ID;
        if(shuttingDown){
            freespaceID = _freespaceManager.write();
            _freespaceManager = null;
        }
        
        StatefulBuffer writer = getWriter(systemTransaction(), 0, _fileHeader.length());
        
        _fileHeader.writeFixedPart(this, startFileLockingThread, shuttingDown, writer, blockSize(), freespaceID);
        
        if(shuttingDown){
            ensureLastSlotWritten();
        }
        syncFiles();
    }

    public final void writeNew(Transaction trans, Pointer4 pointer, ClassMetadata classMetadata, ByteArrayBuffer buffer) {
        writeEncrypt(buffer, pointer.address(), 0);
        if(classMetadata == null){
            return;
        }
        classMetadata.addToIndex(trans, pointer.id());
    }

    // This is a reroute of writeBytes to write the free blocks
    // unchecked.

    public abstract void overwriteDeletedBytes(int address, int length);
    
    public void overwriteDeletedBlockedSlot(Slot slot) {
    	overwriteDeletedBytes(slot.address(), blocksToBytes(slot.length()));	
    }

    public final void writeTransactionPointer(int address) {
        _fileHeader.writeTransactionPointer(systemTransaction(), address);
    }
    
    public final Slot allocateSlotForUserObjectUpdate(Transaction trans, int id, int length){
        Slot slot = allocateSlot(length);
        idSystem().notifySlotChanged(trans, id, slot, SlotChangeFactory.USER_OBJECTS);
        return slot;
    }
    
    public final Slot allocateSlotForNewUserObject(Transaction trans, int id, int length){
        Slot slot = allocateSlot(length);
        idSystem().notifySlotCreated(trans, id, slot, SlotChangeFactory.USER_OBJECTS);
        return slot;
    }

    public final void writeUpdate(Transaction trans, Pointer4 pointer, ClassMetadata classMetadata, ArrayType arrayType, ByteArrayBuffer buffer) {
        int address = pointer.address();
        if(address == 0){
            address = allocateSlotForUserObjectUpdate(trans, pointer.id(), pointer.length()).address();
        }
        writeEncrypt(buffer, address, 0);
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
    
    public QueryResult classOnlyQuery(QQueryBase query, ClassMetadata clazz){
        if (!clazz.hasClassIndex()) {
        	return new IdListQueryResult(query.transaction());
		}
		
		final AbstractQueryResult queryResult = newQueryResult(query.transaction());
		queryResult.loadFromClassIndex(clazz);
		return queryResult;
    }
    
    public QueryResult executeQuery(QQuery query){
    	AbstractQueryResult queryResult = newQueryResult(query.transaction());
    	queryResult.loadFromQuery(query);
    	return queryResult;
    }

	public LocalTransaction getLocalSystemTransaction() {
		return (LocalTransaction)systemTransaction();
	}
	
    public void onCommittedListener() {
        // do nothing
    }

	public int instanceCount(ClassMetadata clazz, Transaction trans) {
		synchronized(lock()) {
			return clazz.indexEntryCount(trans);
		}
	}
	
	public ObjectContainer openSession(){
		synchronized(lock()) {
			return new ObjectContainerSession(this);
		}
	}
	
	public IdSystem idSystem(){
		return _idSystem;
	}
	
	@Override
	public boolean isDeleted(Transaction trans, int id){
		return idSystem().isDeleted(trans, id);
	}
	
	public void writePointer(int id, Slot slot) {
        if(DTrace.enabled){
            DTrace.WRITE_POINTER.log(id);
            DTrace.WRITE_POINTER.logLength(slot);
        }
        _pointerIo.seek(0);
        if (Deploy.debug) {
            _pointerIo.writeBegin(Const4.YAPPOINTER);
        }
        _pointerIo.writeInt(slot.address());
    	_pointerIo.writeInt(slot.length());
        if (Deploy.debug) {
            _pointerIo.writeEnd();
        }
        writeBytes(_pointerIo, id, 0);
    }
	
	public Pointer4 debugReadPointer(int id) {
        if (Deploy.debug) {
    		readBytes(_pointerIo._buffer, id, Const4.POINTER_LENGTH);
    		_pointerIo.seek(0);
    		_pointerIo.readBegin(Const4.YAPPOINTER);
    		int debugAddress = _pointerIo.readInt();
    		int debugLength = _pointerIo.readInt();
    		_pointerIo.readEnd();
    		return new Pointer4(id, new Slot(debugAddress, debugLength));
        }
        return null;
	}
    
    public Pointer4 readPointer(int id) {
        if (Deploy.debug) {
            return debugReadPointer(id);
        }
        if(!isValidId(id)){
        	throw new InvalidIDException(id);
        }
        
       	readBytes(_pointerBuffer, id, Const4.POINTER_LENGTH);
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
		return fileLength() >= id;
	}
	
	private boolean isValidSlot(int address, int length) {
		// just in case overflow 
		long fileLength = fileLength();
		
		boolean validAddress = fileLength >= address;
        boolean validLength = fileLength >= length ;
        boolean validSlot = fileLength >= (address+length);
        
        return validAddress && validLength && validSlot;
	}


	
}