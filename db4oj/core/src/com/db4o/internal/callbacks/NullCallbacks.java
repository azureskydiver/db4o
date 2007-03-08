/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.callbacks;

import com.db4o.ext.*;
import com.db4o.query.Query;

public class NullCallbacks implements Callbacks {

	public void onQueryFinished(Query query) {
	}

	public void onQueryStarted(Query query) {
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

	public void commitOnStarted(ObjectInfoCollection added, ObjectInfoCollection deleted, ObjectInfoCollection updated) {
	}

	public boolean caresAboutCommit() {
		return false;
	}
}
