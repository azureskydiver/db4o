/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.util.*;

import javax.jdo.*;
import javax.jdo.listener.*;

import com.db4o.drs.versant.metadata.*;
import com.db4o.internal.*;
import com.versant.core.jdo.*;
import com.versant.core.storagemanager.logging.*;
import com.versant.core.vds.*;
import com.versant.odbms.*;


public class VodJdo implements VodJdoFacade {
	
	public interface PreStoreListener {
		void preStore(Object object);
	}

	private final VodDatabase _vod;
	
	private final PersistenceManager _pm;

	public static VodJdoFacade createInstance(VodDatabase vod) {
		return new VodJdo(vod);
		// return ProxyUtil.throwOnConcurrentAccess(new VodJdo(vod));
	}
	
	private VodJdo(VodDatabase vod) {
		_vod = vod;
		_pm = _vod.persistenceManagerFactory().getPersistenceManager();
		_pm.currentTransaction().begin();
	}
	
	public PersistenceManager persistenceManager(){
		return _pm;
	}

	public long loid(Object obj) {
		if (obj instanceof VodLoidAwareObject) {
			VodLoidAwareObject co = (VodLoidAwareObject) obj;
			if (co.loid() != 0) {
				return co.loid();
			}
		}
		return VdsUtils.getLOID(obj, _pm);
	}

	public <T> T objectByLoid(long loid) {
		return (T) VdsUtils.getObjectByLOID(loid, true, _pm);
	}
	
	public void close() {
		_pm.currentTransaction().rollback();
		_pm.close();
	}
	
	public void commit(){
		javax.jdo.Transaction trans = _pm.currentTransaction();
		trans.commit();
		trans.begin();
	}

	
	public void rollback(){
		javax.jdo.Transaction trans = _pm.currentTransaction();
		trans.rollback();
		trans.begin();
	}

	public <T> Collection<T> query(Class<T> extent) {
		return (Collection<T>) _pm.newQuery(extent).execute();
	}

	public void store(Object obj) {
		_pm.makePersistent(obj);
		if (obj instanceof VodLoidAwareObject) {
			VodLoidAwareObject co = (VodLoidAwareObject) obj;
			if (co.loid() == 0) {
				co.loid(loid(co));
			}
		}
	}

	public <T> T peek (T obj) {
		long loid = loid(obj);
		_pm.evict(obj);
		return this.<T>objectByLoid(loid);
	}

	public int deleteAll(Class clazz) {
		Collection q = (Collection) _pm.newQuery(clazz).execute();
		_pm.deletePersistentAll(q);
		return q.size();
	}

	public void delete(Object obj) {
		_pm.deletePersistent(obj);
	}

	public <T> Collection<T> query(Class<T> clazz, String filter) {
		Query query = _pm.newQuery(clazz, filter);
		return (Collection<T>) query.execute();
	}
	
	public <T> T queryOneOrNone(Class<T> clazz, String filter) {
		Collection<T> collection = query(clazz, filter);
		if(collection.isEmpty()){
			return null;
		}
		if(collection.size() > 1){
			throw new IllegalStateException("Expecting one. Found: " +  collection.size() + " [" + clazz + "] " + filter);
		}
		return collection.iterator().next();
	}
	
	public <T> T queryOne(Class<T> clazz, String filter) {
		Collection<T> collection = query(clazz, filter);
		if(collection.size() != 1){
			throw new IllegalStateException("Expecting exactly one object. Found:" +  collection.size() + " [" + clazz + "] " + filter);
		}
		return collection.iterator().next();
	}

	public void refresh(Object obj) {
		_pm.refresh(obj);
	}
	
	private static class PreStoreListenerWrapper implements StoreLifecycleListener, CreateLifecycleListener {

		private PreStoreListener preStoreListener;

		public PreStoreListenerWrapper(PreStoreListener preStoreListener) {
			super();
			this.preStoreListener = preStoreListener;
		}

		public void postCreate(InstanceLifecycleEvent arg0) {
		}

		public void postStore(InstanceLifecycleEvent arg0) {
		}

		public void preStore(InstanceLifecycleEvent arg0) {
			preStoreListener.preStore(arg0.getSource());
		}
		
	}

	public void addPreStoreListener(final PreStoreListener preStoreListener) {
		_pm.addInstanceLifecycleListener(new PreStoreListenerWrapper(preStoreListener), null);
	}

}


