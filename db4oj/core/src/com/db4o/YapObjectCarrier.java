/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.inside.convert.*;
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
public class YapObjectCarrier extends YapMemoryFile {
	
	YapObjectCarrier (Configuration config,YapStream a_callingStream, MemoryFile memoryFile) {
	    super(config,a_callingStream, memoryFile);
	}
	
	void initialize0b(){
		// do nothing
	}
	
	void initialize1(Configuration config){
	    i_handlers = i_parent.i_handlers;
        _classCollection = i_parent.classCollection();
		i_config = i_parent.configImpl();
		i_references = new YapReferences(this);
		initialize2();
	}
	
	void initialize2NObjectCarrier(){
		// do nothing
	}
	
	void initializeEssentialClasses(){
	    // do nothing
	}
	
	void initialize4NObjectCarrier(){
		// do nothing
	}
	
	void initNewClassCollection(){
	    // do nothing
	}
	
    boolean canUpdate(){
        return false;
    }
    
	void configureNewFile() {
	    // do nothing
	}
    
    public int converterVersion() {
        return Converter.VERSION;
    }
		
    public boolean close() {
        
        // TODO: An object carrier can simply be gc'd.
        // It does not need to be cleaned up.
        
        synchronized (i_lock) {
            boolean ret = close1();
            if (ret) {
				i_config = null;
            }
            return ret;
        }
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
    
	void write(boolean shuttingDown) {
		checkNeededUpdates();
		writeDirty();
		getTransaction().commit();
	}
	
	final void writeHeader(boolean shuttingDown) {
	    // do nothing
	}
    
    protected void writeVariableHeader(){
        
    }


}