/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.callbacks;

import com.db4o.ext.ObjectInfoCollection;
import com.db4o.query.Query;

public interface Callbacks {

	boolean objectCanNew(Object obj);
	boolean objectCanActivate(Object obj);
	boolean objectCanUpdate(Object obj);
	boolean objectCanDelete(Object obj);
	boolean objectCanDeactivate(Object obj);

	void objectOnActivate(Object obj);
	void objectOnNew(Object obj);
	void objectOnUpdate(Object obj);
	void objectOnDelete(Object obj);
	void objectOnDeactivate(Object obj);
	
	void onQueryStarted(Query query);
	void onQueryFinished(Query query);
	
	void commitOnStarted(ObjectInfoCollection added, ObjectInfoCollection deleted, ObjectInfoCollection updated);
	boolean caresAboutCommit();
}
