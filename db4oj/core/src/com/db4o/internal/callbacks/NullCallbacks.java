/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.callbacks;

import com.db4o.internal.CallbackObjectInfoCollections;
import com.db4o.query.Query;

public class NullCallbacks implements Callbacks {

	public void queryOnFinished(Query query) {
	}

	public void queryOnStarted(Query query) {
	}

	public boolean objectCanNew(Object obj) {
		return true;
	}

	public boolean objectCanActivate(Object obj) {
		return true;
	}
	
	public boolean objectCanUpdate(Object obj) {
		return true;
	}
	
	public boolean objectCanDelete(Object obj) {
		return true;
	}
	
	public boolean objectCanDeactivate(Object obj) {
		return true;
	}
	
	public void objectOnNew(Object obj) {
	}
	
	public void objectOnActivate(Object obj) {
	}

	public void objectOnUpdate(Object obj) {
	}

	public void objectOnDelete(Object obj) {
	}

	public void objectOnDeactivate(Object obj) {	
	}

	public void objectOnInstantiate(Object obj) {
	}

	public void commitOnStarted(Object transaction, CallbackObjectInfoCollections objectInfoCollections) {
	}
	
	public void commitOnCompleted(Object transaction, CallbackObjectInfoCollections objectInfoCollections) {
	}

	public boolean caresAboutCommitting() {
		return false;
	}

	public boolean caresAboutCommitted() {
		return false;
	}

}
