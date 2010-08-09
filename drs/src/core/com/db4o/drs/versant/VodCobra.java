/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.util.*;

import static com.db4o.qlin.QLinSupport.*;

import com.db4o.drs.inside.*;
import com.db4o.drs.versant.cobra.qlin.*;
import com.db4o.drs.versant.metadata.*;
import com.db4o.internal.*;
import com.db4o.qlin.*;
import com.versant.odbms.*;
import com.versant.odbms.model.*;
import com.versant.odbms.query.*;

public class VodCobra implements QLinable{
	
	private DatastoreManager _dm;

	public VodCobra(VodDatabase vod) {
		_dm = vod.createDatastoreManager();
		_dm.beginTransaction();
	}
	
	public static long loidAsLong(String loidAsString){
		return DatastoreLoid.asValue(loidAsString);
	}
	
	public void close() {
		_dm.close();
	}

	public VodId idFor(long loid) {
		
		ObjectLifecycleEvent event = prototype(ObjectLifecycleEvent.class);
		Collection<ObjectLifecycleEvent> events = 
			from(ObjectLifecycleEvent.class)
				.where(event.objectLoid())
				.equal(loid)
				.orderBy(event.timestamp(), descending())
				.limit(1)
				.select();
		if(DrsDebug.verbose){
			for (ObjectLifecycleEvent objectLifecycleEvent : events) {
				System.out.println("#idFor() found: " + objectLifecycleEvent);
			}
		}
		long timestamp = events.isEmpty() ? 0 : events.iterator().next().timestamp();
		DatastoreLoid datastoreLoid = new DatastoreLoid(loid);
		return new VodId(datastoreLoid.getDatabaseId(), datastoreLoid.getObjectId1(), datastoreLoid.getObjectId2(), timestamp);
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
		return readObjects(extent, loids);
	}
	
	public <T> Collection<T> readObjects(Class<T> extent, Object[] loids) {
		return readObjects(extent, loids, -1);
	}

	public <T> Collection<T> readObjects(Class<T> extent, Object[] loids, int limit) {
		int size = limit > 0 ? Math.min(loids.length, limit) : loids.length;
		DatastoreObject[] datastoreObjects = new DatastoreObject[size];
		for ( int i = 0; i < size; i++ ){
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
		return executeQuery(new DatastoreQuery(extent.getName()));
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
			cobraFields[i] = new CobraField(clazz, datastoreSchemaFields[i]);
		}
		return cobraFields;
	}
	
	public <T> Long singleInstanceLoid(Class<T> extent) {
		Collection<Long> loids = loids(extent);
	    
	    switch(loids.size()){
	    	case 0:
		    	return null;
	    	case 1:
	    		return loids.iterator().next();
	    	default:
	    		throw new IllegalStateException("Multiple " + extent.getSimpleName() + " instances in database");
	    }
	}
	
	private class CobraField {
		
		private DatastoreSchemaField _datastoreSchemaField;
		
		private java.lang.reflect.Field _field;
		
		public CobraField(Class clazz, DatastoreSchemaField datastoreSchemaField){
			_datastoreSchemaField = datastoreSchemaField;
			_field = Reflection4.getField(clazz, name());
		}

		public void write(Object obj, DatastoreObject datastoreObject) {
			datastoreObject.writeObject(_datastoreSchemaField, Reflection4.getFieldValue(obj, name()));
		}
		
		private String name(){
			return _datastoreSchemaField.getName();
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

	public <T> QLin<T> from(Class<T> clazz) {
		return new CLinRoot<T>(this, clazz);
	}

	public Object[] executeQuery(DatastoreQuery query) {
		return _dm.executeQuery(query, DataStoreLockMode.NOLOCK,
				DataStoreLockMode.NOLOCK, Options.NO_OPTIONS);
	}

}
