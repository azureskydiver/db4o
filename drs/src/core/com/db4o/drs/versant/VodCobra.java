/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.versant.odbms.*;
import com.versant.odbms.model.*;

public class VodCobra {
	
	private final VodDatabase _vod;
	
	private DatastoreManager _dm;


	public VodCobra(VodDatabase vod) {
		_vod = vod;
		_dm = _vod.createDatastoreManager();
	}
	
	public static long loidAsLong(String loidAsString){
		return DatastoreLoid.asValue(loidAsString);
	}

	public Object fieldValue(String loidAsString, String fieldName) {
		return fieldValue(DatastoreLoid.asValue(loidAsString), fieldName);
	}
	
	public Object fieldValue(final long loid, final String fieldName) {
		return transactional(new Closure4<Object>() {
			public Object run() {
				DatastoreObject datastoreObject = new DatastoreObject(loid);
				_dm.readObject(datastoreObject, DataStoreLockMode.NOLOCK, Options.NO_OPTIONS);
				DatastoreSchemaField[] fields = datastoreObject.getSchemaClass().getFields();
				for (DatastoreSchemaField field : fields) {
					if(fieldName.equals(field.getName())){
						return datastoreObject.readObject(field);
					}
				}
				throw new IllegalArgumentException("Field " + fieldName + " not found.");
			}
		});
	}

	
	private Object transactional(Closure4<Object> closure) {
		boolean transactionActive = _dm.isTransactionActive();
		if(! transactionActive){
			beginTransaction();
		}
		try{
			return closure.run();
		}finally{
			if(! transactionActive){
				rollbackTranssaction();
			}
		}
	}
	
	private DatastoreObject datastoreObject(long loid) {
		DatastoreLoid datastoreLoid = new DatastoreLoid(loid);
		DatastoreObject[] loidsAsDSO = _dm.getLoidsAsDSO(new Object[] { datastoreLoid });
		_dm.groupReadObjects(loidsAsDSO, DataStoreLockMode.NOLOCK, Options.NO_OPTIONS);
		return loidsAsDSO[0];
	}

	public void close() {
		_dm.close();
	}

	public VodId idFor(long loid) {
		DatastoreLoid datastoreLoid = new DatastoreLoid(loid);
		
		DatastoreObject datastoreObject = datastoreObject(loid);
		// TODO: Create correct timestamp here
		
		return new VodId(datastoreLoid.getDatabaseId(), datastoreLoid.getObjectId1(), datastoreLoid.getObjectId2(), datastoreObject.getTimestamp());
	}

	// Expecting only objects where classes are stored with fully qualified name:
	// Relying on: obj.getClass().getName()
	public long store(Object obj) {
		DatastoreInfo info = _dm.getDefaultDatastore();
		SchemaEditor schemaEditor = _dm.getSchemaEditor();
		
		DatastoreSchemaClass datastoreSchemaClass = schemaEditor.findClass(obj.getClass().getName(), info);
		
		
		long loid = _dm.getNewLoid();
		
		DatastoreObject datastoreObject = new DatastoreObject(loid, datastoreSchemaClass, info);
		datastoreObject.setTimestamp(1);
		datastoreObject.allocate();
		datastoreObject.setIsNew(true);
		
		for (DatastoreSchemaField field : datastoreSchemaClass.getFields()) {
			datastoreObject.writeObject(field, Reflection4.getFieldValue(obj, field.getName()));
		}
		
		_dm.groupWriteObjects(new DatastoreObject[] { datastoreObject },Options.NO_OPTIONS);
		return loid;
	}

	public void beginTransaction() {
		_dm.beginTransaction();
	}
	
	public void commitTransaction(){
		_dm.commitTransaction();
	}
	
	public void rollbackTranssaction(){
		_dm.rollbackTransaction();
	}
	

}
