package com.db4o.omplus.datalayer.propertyViewer;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.omplus.*;
import com.db4o.omplus.datalayer.*;
import com.db4o.omplus.datalayer.propertyViewer.classProperties.*;
import com.db4o.reflect.*;

public class ConfigureIndex {
	
	IDbInterface db;
	
	public ConfigureIndex() {
		db = Activator.getDefault().getDatabaseInterface();
	}
	
	public static boolean isLocal(){
		return (!Activator.getDefault().getDatabaseInterface().isClient());
	}
	
	private void reconnect(){
		String path = Activator.getDefault().getDatabaseInterface().getDbPath();
		db.close();
		ObjectContainer oc = Db4o.openFile(path);
		db.setDB(oc, path);
	}
	
	private boolean isIndexable(StoredField storedField, ObjectContainer db) {
		
		ReflectClass storedType = null;
		try {
			storedType = storedField.getStoredType();
		}catch(Exception e){
			
		}
		if (storedType != null) { // primitive arrays return null
			if (storedType.isPrimitive() ) {
				return true;
			}
		}
		return false;
	}
	
	public void createIndex(ClassProperties clsProperties){
		boolean reconnect = false;
		ReflectClass clazz = ReflectHelper.getReflectClazz(clsProperties.getClassname());
		StoredClass storedClz = db.getStoredClass(clazz.getName());
		if(storedClz != null) {
			for(FieldProperties field : clsProperties.getFields()) {
				StoredField sField = storedClz.storedField(field.getFieldName(), storedClz);
				if(isIndexable(sField, db.getDB())) {
					index(clazz, field.getFieldName(), field.isIndexed());
					if(!reconnect)
						reconnect = true;
				}
			}
			if(reconnect)
				reconnect();
		}
	}
	
	@SuppressWarnings("deprecation")
	private void index(ReflectClass clazz, String fieldName, boolean isIndexed){
		Db4o.configure().objectClass(clazz).objectField(fieldName).indexed(isIndexed);
	}

}
