/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.util.*;

import javax.jdo.*;

import com.versant.core.jdo.*;
import com.versant.core.metadata.*;
import com.versant.core.storagemanager.*;
import com.versant.core.vds.*;
import com.versant.odbms.model.*;
import com.versant.odbms.model.UserSchemaClass;

public class VodJdo {
	
	private final VodDatabase _vod;
	
	private final PersistenceManager _pm;

	public VodJdo(VodDatabase vod) {
		_vod = vod;
		_pm = _vod.createPersistenceManager();
		_pm.currentTransaction().begin();
	}
	
	public PersistenceManager persistenceManager(){
		return _pm;
	}

	public long loid(Object obj) {
		return VdsUtils.getLOID(obj, _pm);
	}

	public Object objectByLoid(long loid) {
		return VdsUtils.getObjectByLOID(loid, true, _pm);
	}
	
	private ModelMetaData modelMetadata() {
		VersantPMFInternal internalPersistenceManagerFactory = (VersantPMFInternal) _vod.persistenceManagerFactory();
		StorageManagerFactory storageManagerFactory = internalPersistenceManagerFactory.getStorageManagerFactory();
		return storageManagerFactory.getModelMetaData();
	}
	
	public String schemaName(Class clazz) {
		ModelMetaData modelMetadata = modelMetadata();
		UserSchemaModel userModel = (UserSchemaModel)modelMetadata.vdsModel;
		ClassMetaData classMetaData = modelMetadata.getClassMetaData(clazz);
		UserSchemaClass userSchemaClass = userModel.getAssociatedSchemaClass(classMetaData);
		return userSchemaClass.getName();
	}
	
	public void close() {
		_pm.currentTransaction().rollback();
		_pm.close();
	}
	
	public void commit(){
		_pm.currentTransaction().commit();
		_pm.currentTransaction().begin();
	}
	
	public void rollback(){
		_pm.currentTransaction().rollback();
		_pm.currentTransaction().begin();
	}

	public <T> Collection<T> query(Class<T> extent) {
		return (Collection<T>) _pm.newQuery(extent).execute();
	}

	public void store(Object obj) {
		_pm.makePersistent(obj);
	}
	

}


