/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.callbacks;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.query.*;

public interface Callbacks {

	boolean objectCanNew(Transaction transaction, Object obj);
	boolean objectCanActivate(Transaction transaction, Object obj);
	boolean objectCanUpdate(Transaction transaction, Object obj);
	boolean objectCanDelete(Transaction transaction, Object obj);
	boolean objectCanDeactivate(Transaction transaction, Object obj);

	void objectOnActivate(Transaction transaction, Object obj);
	void objectOnNew(Transaction transaction, Object obj);
	void objectOnUpdate(Transaction transaction, Object obj);
	void objectOnDelete(Transaction transaction, Object obj);
	void objectOnDeactivate(Transaction transaction, Object obj);
	void objectOnInstantiate(Transaction transaction, Object obj);

	void queryOnStarted(Transaction transaction, Query query);
	void queryOnFinished(Transaction transaction, Query query);
	
	boolean caresAboutCommitting();
	boolean caresAboutCommitted();
	
	void classOnRegistered(ClassMetadata clazz);
	
	void commitOnStarted(Transaction transaction, CallbackObjectInfoCollections objectInfoCollections);
	void commitOnCompleted(Transaction transaction, CallbackObjectInfoCollections objectInfoCollections);

    boolean caresAboutDeleting();
    boolean caresAboutDeleted();
    
    void closeOnStarted(ObjectContainer container);
}
