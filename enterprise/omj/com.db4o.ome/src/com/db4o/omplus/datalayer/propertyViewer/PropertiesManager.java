package com.db4o.omplus.datalayer.propertyViewer;

import java.util.HashMap;

import com.db4o.ObjectContainer;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.ext.ObjectInfo;
import com.db4o.ext.StoredClass;
import com.db4o.ext.StoredField;
import com.db4o.omplus.datalayer.DbInterfaceImpl;
import com.db4o.omplus.datalayer.ReflectHelper;
import com.db4o.omplus.datalayer.propertyViewer.classProperties.ClassProperties;
import com.db4o.omplus.datalayer.propertyViewer.classProperties.FieldProperties;
import com.db4o.omplus.datalayer.propertyViewer.dbProperties.DBProperties;
import com.db4o.omplus.datalayer.propertyViewer.objectProperties.ObjectProperties;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;


public class PropertiesManager {

	private static PropertiesManager propertiesMgr;
	
	private static HashMap<String, ClassProperties> properties;
	
	private ObjectContainer oc;
	
	private PropertiesManager(){
		if(properties == null)
			properties = new HashMap<String, ClassProperties>();
	}
	
	public static PropertiesManager getInstance(){
		if( propertiesMgr == null)
			propertiesMgr = new PropertiesManager();
		return propertiesMgr;
	}
	
	public ObjectProperties getObjectProperties(Object obj){
		ObjectProperties objProp = null;
		if(obj != null){
			ObjectContainer oc = DbInterfaceImpl.getInstance().getDB();
			if(oc != null){
				ExtObjectContainer db = oc.ext();
				objProp = new ObjectProperties();
				objProp.setLocalID(db.getID(obj));
				ObjectInfo objInfo = db.getObjectInfo(obj);
				if(objInfo != null){
					objProp.setUuid(objInfo.getUUID());
					objProp.setVersion(objInfo.getVersion());
				}
			}
	//			TODO: check depth and reference
		}
		return objProp;
	}
	
	public ClassProperties getClassProperties(String className){
		ClassProperties clsProperties = null;
		if((properties != null) && (className != null) && (className.trim().length() > 0)){
			
			if(properties.containsKey(className)){
				clsProperties = properties.get(className);
				clsProperties.setNumberOfObjects(getNumberOfObj(className));
				return clsProperties;
			}
			else{
				oc = DbInterfaceImpl.getInstance().getDB();
				clsProperties = buildClassProperties(className);
				if(clsProperties != null)
					properties.put(className, clsProperties);
			}
		}
		return clsProperties;
	}	
	
	public DBProperties getDBProperties(){
		DbInterfaceImpl dbInterface = DbInterfaceImpl.getInstance();
		DBProperties dbProperties = new DBProperties();
		if(dbInterface.getDB() != null){
			dbProperties.setDbSize(dbInterface.getDBSize());
			dbProperties.setFreeSpace(dbInterface.getFreespaceSize());
			Object[] classes = null;
			try {
				classes = dbInterface.getStoredClasses();
			} catch (Exception e) {
			}
			if(classes != null)
				dbProperties.setNoOfClasses(classes.length);
		}
		return dbProperties;
	}

//	TODO: move them to another Property builder class
	private ClassProperties buildClassProperties(String className) {
		ClassProperties clsProperties = new ClassProperties();
		clsProperties.setClassname(className);
		ReflectClass clazz = ReflectHelper.getReflectClazz(className);
		clsProperties.setNumberOfObjects(getNumberOfObj(className));
		clsProperties.setFields(buildFieldProperties(clazz));
		return clsProperties;
	}

	private FieldProperties[] buildFieldProperties(ReflectClass clazz) {
		
		ReflectField []fields =  ReflectHelper.getDeclaredFieldsInHierarchy(clazz);
		FieldProperties []fp = new FieldProperties[fields.length];
		int count = 0;
		for(ReflectField rfield : fields){
			FieldProperties fieldProp = new FieldProperties();
			fieldProp.setFieldName(rfield.getName());
			if(rfield.isPublic())
			{	
				fieldProp.setAccessModifier("Public");
			}
			else
				fieldProp.setAccessModifier("NP");
			ReflectClass fieldType = rfield.getFieldType();
			fieldProp.setFieldDataType(fieldType.getName());
			fieldProp.setPrimitive(fieldType.isPrimitive());
			// Add further properties required
			fieldProp.setIndexed(isIndexed(clazz.getName(), rfield.getName()));
			fp[count++] = fieldProp;
		}
		return fp;
	}

	private boolean isIndexed(String clsname, String fname) {
		StoredClass storedClass = oc.ext().storedClass(clsname);
		if(storedClass != null){
			StoredField[] fields = ReflectHelper.getDeclaredFieldsInHeirarchy(storedClass);
			if(fields != null){
				for( StoredField storedFld : fields){
					if(storedFld.getName().equals(fname)){
						return storedFld.hasIndex();
					}
				}
			}
		}
		return false;
	}

/*	private String[] getIndexedFieldNames(className, fieldName) {
		//String fieldNames
		return null;
	}*/

	private int getNumberOfObj(String className) {
		DbInterfaceImpl dbInterface = DbInterfaceImpl.getInstance();
		return dbInterface.getNumOfObjects(className);
	}
	
	public int getDepthForObj(Object obj){
//		@blunder has to be recursive
	/*	int objDepth = 0, depth = 0;
		if(obj != null){
			ReflectClass clazz = ReflectHelper.getReflectClazz(obj);
			if(clazz != null){
				objDepth = depth = 1;
				ReflectField []fields = ReflectHelper.getDeclaredFieldsInHierarchy(clazz);
				for(ReflectField rField : fields){
					
				}
			}
		}*/
		return 0;
	}
}
