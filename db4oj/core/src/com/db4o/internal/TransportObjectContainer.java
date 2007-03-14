/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.convert.*;
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
public class TransportObjectContainer extends InMemoryObjectContainer {
	
	public TransportObjectContainer (ObjectContainerBase serviceProvider, MemoryFile memoryFile) {
	    super(serviceProvider.config(),serviceProvider, memoryFile);
	    i_showInternalClasses = serviceProvider.i_showInternalClasses;
	}
	
	protected void initialize1(Configuration config){
	    i_handlers = i_parent.i_handlers;
        _classCollection = i_parent.classCollection();
		i_config = i_parent.configImpl();
		i_references = new WeakReferenceCollector(this);
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
    
    public ClassMetadata classMetadataForId(int id) {
    	return i_parent.classMetadataForId(id);
    }
    
	void configureNewFile() {
	    // do nothing
	}
    
    public int converterVersion() {
        return Converter.VERSION;
    }
		
    protected void dropReferences() {
        i_config = null;
    }
    
    protected void handleExceptionOnClose(Exception exc) {
    	// do nothing here
    }

	final public Transaction newTransaction(Transaction parentTransaction) {
		if (null != parentTransaction) {
			return parentTransaction;
		}
		return new TransactionObjectCarrier(this, null);
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
	
	public int getSlot(int length){
        return appendBlocks(length);
	}
	
	public Db4oDatabase identity() {
	    return i_parent.identity();
	}
	
	public boolean maintainsIndices(){
		return false;
	}
	
	void message(String msg){
		// do nothing
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
		getTransaction().commit();
	}
	
	final void writeHeader(boolean startFileLockingThread, boolean shuttingDown) {
	    // do nothing
	}
    
    protected void writeVariableHeader(){
        
    }


}