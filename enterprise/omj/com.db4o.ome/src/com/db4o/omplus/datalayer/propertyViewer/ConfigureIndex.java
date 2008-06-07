package com.db4o.omplus.datalayer.propertyViewer;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ext.StoredClass;
import com.db4o.ext.StoredField;
import com.db4o.omplus.datalayer.DbInterfaceImpl;
import com.db4o.omplus.datalayer.ReflectHelper;
import com.db4o.omplus.datalayer.propertyViewer.classProperties.ClassProperties;
import com.db4o.omplus.datalayer.propertyViewer.classProperties.FieldProperties;
import com.db4o.reflect.ReflectClass;

public class ConfigureIndex {
	
	DbInterfaceImpl db;
	
	public ConfigureIndex() {
		db = DbInterfaceImpl.getInstance();
	}
	
	public static boolean isLocal(){
		return (!DbInterfaceImpl.getInstance().isClient());
	}
	
	private void reconnect(){
		String path = DbInterfaceImpl.getInstance().getDbPath();
		db.close();
		ObjectContainer oc = Db4o.openFile(path);
		db.setDB(oc);// any error for path call DbInterface.setDbPath
	}
	
	private boolean isIndexable(StoredField storedField) {
		
		ReflectClass storedType = null;
		try {
			storedType = storedField.getStoredType();
		}catch(Exception e){
			
		}
		if (storedType != null) { // primitive arrays return null
			if (storedType.isPrimitive() || storedType.isSecondClass()) {
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
				if(isIndexable(sField)) {
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
