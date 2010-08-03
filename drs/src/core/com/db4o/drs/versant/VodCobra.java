/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.util.*;

import com.db4o.internal.*;
import com.versant.odbms.*;
import com.versant.odbms.model.*;
import com.versant.odbms.query.*;

public class VodCobra {
	
	private DatastoreManager _dm;

	public VodCobra(VodDatabase vod) {
		_dm = vod.createDatastoreManager();
		_dm.beginTransaction();
	}
	
	public static long loidAsLong(String loidAsString){
		return DatastoreLoid.asValue(loidAsString);
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

	public long store(Object obj) {
		DatastoreObject datastoreObject = newDatastoreObject(obj.getClass());
		writeFields(obj, datastoreObject);
		write(datastoreObject);
		return datastoreObject.getLOID();
	}

	private void writeFields(Object obj, DatastoreObject datastoreObject) {
		for (CobraField field : fields(classOf(datastoreObject))) {
			field.write(obj, datastoreObject);
		}
	}

	private void write(DatastoreObject datastoreObject) {
		_dm.groupWriteObjects(new DatastoreObject[] { datastoreObject }, Options.NO_OPTIONS);
	}
	
	public void store(long loid, Object obj) {
		DatastoreObject datastoreObject = datastoreObjectForUpdate(loid);
		Class<Object> clazz = classOf(datastoreObject);
		for (CobraField field : fields(clazz)) {
			field.write(obj, datastoreObject);
		}
		write(datastoreObject);
	}
	
	public Collection<Long> loids(Class<?> extent) {
		Object[] loids = datastoreLoids(extent);
		ArrayList<Long> result = new ArrayList<Long>();
		for ( int i = 0; i < loids.length; i++ ){
			result.add(((DatastoreLoid) loids[i]).value());
		}
		return result;
	}
	
	public <T> T objectByLoid(long loid){
		DatastoreObject datastoreObject = existingDatastoreObject(loid);
		Class<T> clazz = classOf(datastoreObject);
		T result;
		try {
			result = clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		CobraField[] fields = fields(clazz);
		for (int j = 0; j < fields.length; j++) {
			fields[j].read(result, datastoreObject);
		}
		return result;
	}

	private <T> Class<T> classOf(DatastoreObject datastoreObject) {
		DatastoreSchemaClass schemaClass = datastoreObject.getSchemaClass();
		Class<T> clazz;
		try {
			clazz = (Class<T>) Class.forName(schemaClass.getName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return clazz;
	}
	
	private DatastoreObject newDatastoreObject(Class clazz) {
		DatastoreObject datastoreObject = new DatastoreObject(_dm.getNewLoid(), datastoreSchemaClass(clazz), _dm.getDefaultDatastore());
		datastoreObject.setTimestamp(1);
		datastoreObject.setIsNew(true);
		datastoreObject.allocate();
		return datastoreObject;
	}

	private DatastoreObject existingDatastoreObject(long loid) {
		DatastoreObject datastoreObject = new DatastoreObject(new DatastoreLoid(loid));
		_dm.readObject(datastoreObject, DataStoreLockMode.NOLOCK, Options.NO_OPTIONS);
		return datastoreObject;
	}
	
	private DatastoreObject datastoreObjectForUpdate(long loid) {
		DatastoreObject oldDatastoreObject = existingDatastoreObject(loid);
		
		DatastoreObject newDatastoreObject = new DatastoreObject(loid, oldDatastoreObject.getSchemaClass(), oldDatastoreObject.getDatastoreInfo());
		newDatastoreObject.setTimestamp(oldDatastoreObject.getTimestamp() + 1);
		newDatastoreObject.setIsNew(false);
		newDatastoreObject.allocate();
		
		return newDatastoreObject;
	}

	public <T> Collection<T> query(Class<T> extent) {
		Object[] loids = datastoreLoids(extent);
		if(loids.length == 0){
			return new ArrayList<T>();
		}
		DatastoreObject[] datastoreObjects = new DatastoreObject[loids.length];
		for ( int i = 0; i < loids.length; i++ ){
			datastoreObjects[i]= new DatastoreObject((DatastoreLoid) loids[i]);
		}
		_dm.groupReadObjects(datastoreObjects, DataStoreLockMode.NOLOCK, Options.NO_OPTIONS);
		CobraField[] fields = fields(extent);
		List<T> result = new ArrayList<T>();
		for ( int i = 0; i < datastoreObjects.length; i++ ) {
			try {
				T obj = extent.newInstance();
				for (int j = 0; j < fields.length; j++) {
					fields[j].read(obj, datastoreObjects[i]);
				}
				result.add(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private Object[] datastoreLoids(Class<?> extent) {
		DatastoreQuery query = new DatastoreQuery(extent.getName());
		Object[] loids = _dm.executeQuery(query, DataStoreLockMode.NOLOCK,
				DataStoreLockMode.NOLOCK, Options.NO_OPTIONS);
		return loids;
	}
	
	private DatastoreSchemaClass datastoreSchemaClass(Class clazz) {
		// Using clazz.getName here, assuming fully qualified name
		return _dm.getSchemaEditor().findClass(clazz.getName(), _dm.getDefaultDatastore());
	}
	
	public void commit(){
		_dm.commitTransaction();
		_dm.beginTransaction();
	}
	
	public void rollback(){
		_dm.rollbackTransaction();
		_dm.beginTransaction();
	}
	
	private CobraField[] fields(Class clazz){
		DatastoreSchemaClass datastoreSchemaClass = datastoreSchemaClass(clazz);
		DatastoreSchemaField[] datastoreSchemaFields = datastoreSchemaClass.getFields();
		CobraField[] cobraFields = new CobraField[datastoreSchemaFields.length];
		for (int i = 0; i < datastoreSchemaFields.length; i++) {
			java.lang.reflect.Field field = Reflection4.getField(clazz, datastoreSchemaFields[i].getName());
			cobraFields[i] = new CobraField(datastoreSchemaFields[i], field);
		}
		return cobraFields;
	}
	
	class CobraField {
		
		private DatastoreSchemaField _datastoreSchemaField;
		
		private java.lang.reflect.Field _field;
		
		public CobraField(DatastoreSchemaField datastoreSchemaField, java.lang.reflect.Field field){
			_datastoreSchemaField = datastoreSchemaField;
			_field = field;
		}

		public void write(Object obj, DatastoreObject datastoreObject) {
			datastoreObject.writeObject(_datastoreSchemaField, Reflection4.getFieldValue(obj, _datastoreSchemaField.getName()));
		}
		
		public void read(Object obj, DatastoreObject datastoreObject) {
			if(_field == null){
				return;
			}
			try {
				_field.set(obj, datastoreObject.readObject(_datastoreSchemaField));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}



	

}
