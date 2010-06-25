/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.util.*;

import javax.jdo.*;

import com.db4o.*;
import com.db4o.drs.foundation.*;
import com.db4o.drs.inside.*;

public class VodReplicationProvider implements SimpleObjectContainer{
	
	private final VodDatabase _vod;
	
	private final PersistenceManager _pm;
	
	public VodReplicationProvider(VodDatabase vod) {
		_vod = vod;
		_pm = vod.createPersistenceManager();
		_pm.currentTransaction().begin();
	}

	public void commit() {
		_pm.currentTransaction().commit();
		_pm.currentTransaction().begin();
	}

	public void delete(Object obj) {
		_pm.deletePersistent(obj);
	}

	public void deleteAllInstances(Class clazz) {
		_pm.deletePersistentAll((Collection) _pm.newQuery(clazz).execute());
	}

	public ObjectSet getStoredObjects(Class type) {
		Collection collection = (Collection) _pm.newQuery(type).execute();
		return new ObjectSetCollectionFacade(collection);
	}

	public void storeNew(Object obj) {
		_pm.makePersistent(obj);
	}

	public void update(Object obj) {
		// do nothing
		// JDO is transparent persistence
	}

	public void destroy() {
		_pm.currentTransaction().rollback();
		_pm.close();
	}

}
