/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.callbacks;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.query.*;

public class NullCallbacks implements Callbacks {

	public void queryOnFinished(Transaction transaction, Query query) {
	}

	public void queryOnStarted(Transaction transaction, Query query) {
	}

	public boolean objectCanNew(Transaction transaction, Object obj) {
		return true;
	}

	public boolean objectCanActivate(Transaction transaction, Object obj) {
		return true;
	}
	
	public boolean objectCanUpdate(Transaction transaction, Object obj) {
		return true;
	}
	
	public boolean objectCanDelete(Transaction transaction, Object obj) {
		return true;
	}
	
	public boolean objectCanDeactivate(Transaction transaction, Object obj) {
		return true;
	}
	
	public void objectOnNew(Transaction transaction, Object obj) {
	}
	
	public void objectOnActivate(Transaction transaction, Object obj) {
	}

	public void objectOnUpdate(Transaction transaction, Object obj) {
	}

	public void objectOnDelete(Transaction transaction, Object obj) {
	}

	public void objectOnDeactivate(Transaction transaction, Object obj) {	
	}

	public void objectOnInstantiate(Transaction transaction, Object obj) {
	}

	public void commitOnStarted(Transaction transaction, CallbackObjectInfoCollections objectInfoCollections) {
	}
	
	public void commitOnCompleted(Transaction transaction, CallbackObjectInfoCollections objectInfoCollections) {
	}

	public boolean caresAboutCommitting() {
		return false;
	}

	public boolean caresAboutCommitted() {
		return false;
	}

	public void classOnRegistered(ClassMetadata clazz) {
	}

    public boolean caresAboutDeleting() {
        return false;
    }

    public boolean caresAboutDeleted() {
        return false;
    }

	public void closeOnStarted(ObjectContainer container) {
	}
}
