/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.callbacks;

import com.db4o.internal.*;
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
	void objectOnInstantiate(Object obj);

	void queryOnStarted(Query query);
	void queryOnFinished(Query query);
	
	boolean caresAboutCommitting();
	boolean caresAboutCommitted();
	
	void classOnRegistered(ClassMetadata clazz);
	
	void commitOnStarted(Object transaction, CallbackObjectInfoCollections objectInfoCollections);
	void commitOnCompleted(Object transaction, CallbackObjectInfoCollections objectInfoCollections);

    boolean caresAboutDeleting();
    boolean caresAboutDeleted();
}
