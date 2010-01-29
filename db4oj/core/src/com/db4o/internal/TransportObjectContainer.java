/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.convert.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.references.*;
import com.db4o.internal.slots.*;
import com.db4o.internal.weakref.*;
import com.db4o.io.*;
import com.db4o.reflect.*;
import com.db4o.types.*;


/**
 * no reading
 * no writing
 * no updates
 * no weak references
 * navigation by ID only both sides need synchronised ClassCollections and
 * MetaInformationCaches
 * 
 * @exclude
 */
public class TransportObjectContainer extends LocalObjectContainer {

	private final ObjectContainerBase _parent;
	private final MemoryBin _memoryBin;
	
	public TransportObjectContainer(ObjectContainerBase parent, MemoryBin memoryFile) {
	    super(parent.config());
	    _memoryBin = memoryFile;
	    _parent = parent;
	    _lock = parent.lock();
	    _showInternalClasses = parent._showInternalClasses;
	    open();
	} 
	
	protected void initialize1(Configuration config){
	    _handlers = _parent._handlers;
        _classCollection = _parent.classCollection();
		_config = _parent.configImpl();
		_references = WeakReferenceSupportFactory.disabledWeakReferenceSupport();
		initialize2();
	}
	
	void initialize2NObjectCarrier(){
		// do nothing
	}
	
	void initializeEssentialClasses(){
	    // do nothing
	}
	
	protected void initializePostOpenExcludingTransportObjectContainer(){
		// do nothing
	}
	
	void initNewClassCollection(){
	    // do nothing
	}
	
    boolean canUpdate(){
        return false;
    }
    
    public ClassMetadata classMetadataForID(int id) {
    	return _parent.classMetadataForID(id);
    }
    
	void configureNewFile() {
	    // do nothing
	}
    
    public int converterVersion() {
        return Converter.VERSION;
    }
		
    protected void dropReferences() {
        _config = null;
    }
    
    protected void handleExceptionOnClose(Exception exc) {
    	// do nothing here
    }

	public final Transaction newTransaction(Transaction parentTransaction, ReferenceSystem referenceSystem, boolean isSystemTransaction) {
		if (null != parentTransaction) {
			return parentTransaction;
		}
		return new TransactionObjectCarrier(this, null, referenceSystem);
	}
	
	public long currentVersion(){
	    return 0;
	}
    
    public Db4oType db4oTypeStored(Transaction a_trans, Object a_object) {
        return null;
    }
	
    public boolean dispatchsEvents() {
        return false;
    }
	
    protected void finalize() {
        // do nothing
    }
	
	
	public final void free(int a_address, int a_length){
		// do nothing
	}
	
	public final void free(Slot slot){
		// do nothing
	}
	
	public Slot allocateSlot(int length){
        return appendBlocks(length);
	}
	
	@Override
	protected boolean isValidPointer(int id) {
		return id != 0 && super.isValidPointer(id);
	}
	
	public Db4oDatabase identity() {
	    return ((ExternalObjectContainer) _parent).identity();
	}
	
	public boolean maintainsIndices(){
		return false;
	}
	
	@Override
	public long generateTimeStampId() {
		return _parent.generateTimeStampId();
	}
	
	void message(String msg){
		// do nothing
	}
	
	public ClassMetadata produceClassMetadata(ReflectClass claxx) {
		return _parent.produceClassMetadata(claxx);
	}
	
	public void raiseVersion(long a_minimumVersion){
	    // do nothing
	}
	
	void readThis(){
		// do nothing
	}
	
	boolean stateMessages(){
		return false; // overridden to do nothing in YapObjectCarrier
	}
    
	public void shutdown() {
		processPendingClassUpdates();
		writeDirty();
		transaction().commit();
	}
	
	final void writeHeader(boolean startFileLockingThread, boolean shuttingDown) {
	    // do nothing
	}
    
    protected void writeVariableHeader(){
        
    }
    
    public static class KnownObjectIdentity {
    	public int _id;
    	public KnownObjectIdentity(int id) {
			_id = id;
		}
    }
    
    public int storeInternal(Transaction trans, Object obj, int depth,
			boolean checkJustSet)
    		throws DatabaseClosedException, DatabaseReadOnlyException {
    	int id = _parent.getID(null, obj);
    	if(id > 0){
    		return super.storeInternal(trans, new KnownObjectIdentity(id), depth, checkJustSet);
    	}
    	return super.storeInternal(trans, obj, depth, checkJustSet);
    }
    
    public Object getByID2(Transaction ta, int id) {
    	Object obj = super.getByID2(ta, id);
    	if(obj instanceof KnownObjectIdentity){
    		KnownObjectIdentity oi = (KnownObjectIdentity)obj;
    		activate(oi);
    		obj = _parent.getByID(null, oi._id);
    	}
    	
    	return obj;
    }

	public void deferredOpen() {
		open();
	}

	protected final void openImpl() throws OldFormatException {
		if (_memoryBin.length() == 0) {
			configureNewFile();
			commitTransaction();
			writeHeader(false, false);
		} else {
			readThis();
		}
	}

	public void backup(Storage targetStorage, String path)
			throws NotSupportedException {
			    throw new NotSupportedException();
			}

	public void blockSize(int size) {
	    // do nothing, blocksize is always 1
	}
	
    @Override
    public void closeTransaction(Transaction transaction, boolean isSystemTransaction, boolean rollbackOnClose) {
    	// do nothing	
    }

	protected void shutdownDataStorage() {
		dropReferences();
	}

	public long fileLength() {
	    return _memoryBin.length();
	}

	public String fileName() {
	    return "Memory File";
	}

	protected boolean hasShutDownHook() {
	    return false;
	}

	public final boolean needsLockFileThread() {
	    return false;
	}

	public void readBytes(byte[] bytes, int address, int length) {
		try {
			_memoryBin.read(address, bytes, length);
		} catch (Exception e) {
			Exceptions4.throwRuntimeException(13, e);
		}
	}

	public void readBytes(byte[] bytes, int address, int addressOffset, int length) {
		readBytes(bytes, address + addressOffset, length);
	}

	public void syncFiles() {
	}

	public void writeBytes(ByteArrayBuffer buffer, int address, int addressOffset) {
		_memoryBin.write(address + addressOffset, buffer._buffer, buffer.length());
	}

	public void overwriteDeletedBytes(int a_address, int a_length) {
	}

	public void reserve(int byteCount) {
		throw new NotSupportedException();
	}

	public byte blockSize() {
		return 1;
	}

	@Override
	protected void fatalStorageShutdown() {
		shutdownDataStorage();
	}
	
	@Override
	public ReferenceSystem createReferenceSystem() {
		return new HashcodeReferenceSystem();
	}
	
    protected IdSystem newIdSystem() {
    	return new TransportStandardIdSystem(this);
    }


}