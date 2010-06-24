/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.util.*;

import javax.jdo.*;

import com.db4o.*;
import com.db4o.drs.foundation.*;
import com.db4o.drs.inside.*;

public class VodReplicationProvider implements SimpleObjectContainer{
	
	private final PersistenceManager _pm;
	
	private final VodDatabase _vod;

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
		// TODO Auto-generated method stub
		
	}

	public void deleteAllInstances(Class clazz) {
		// TODO Auto-generated method stub
		
	}

	public ObjectSet getStoredObjects(Class type) {
		Collection collection = (Collection) _pm.newQuery(type).execute();
		return new ObjectSetCollectionFacade(collection);
	}

	public void storeNew(Object o) {
		_pm.makePersistent(o);
	}

	public void update(Object o) {
		// TODO Auto-generated method stub
		
	}

}
