/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;


/** 
 * marker interface for special db4o datatypes
 * 
 * @exclude
 */
public interface Db4oTypeImpl extends TransactionAware {
	
    boolean canBind();
	
	Object createDefault(Transaction trans);
	
	boolean hasClassIndex();
	
	void setObjectReference(ObjectReference ref);
	
	/**
	 * @deprecated should no longer be called
	 */
	Object storedTo(Transaction trans);
	
	/**
	 * @deprecated should no longer be called
	 */
	void preDeactivate();
	
}
