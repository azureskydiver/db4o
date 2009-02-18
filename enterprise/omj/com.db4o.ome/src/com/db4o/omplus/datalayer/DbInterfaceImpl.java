package com.db4o.omplus.datalayer;

import java.util.TreeSet;

import com.db4o.*;
import com.db4o.ext.StoredClass;
import com.db4o.ext.SystemInfo;
import com.db4o.internal.ObjectContainerBase;
import com.db4o.reflect.Reflector;

/**
 * Singleton class
 */
public class DbInterfaceImpl implements IDbInterface {
	
	private static final String JAVA_LANG = "java.";
	private static final String DB4O_PACK = "com.db4o.";
	private static final String DB4O_NET = "Db4objects.";

	private static ObjectContainer objContainer;
	
	private static String dbPath;
	
	private static DbInterfaceImpl instance;
	
	private DbInterfaceImpl(){
	}

	public static DbInterfaceImpl getInstance(){
		if(instance == null){
			return new DbInterfaceImpl();
		}
		return instance;
	}

	public String getVersion(){
		return Db4o.version();
	}
	
	public Object[] getStoredClasses() {
		if(objContainer == null)
			return null;
		StoredClass []classes = null;
		try{
			classes = objContainer.ext().storedClasses();
		}catch(Exception ex){
			throw new RuntimeException(ex.getClass().getName()+ " occured when getting the"+
					" stored classes from db.");// OME currently doesn't handle translators, if configured.
		}
		TreeSet<String> temp = new TreeSet<String>();
		if(classes != null){
			for(StoredClass claxx: classes){
				String className = claxx.getName();
				if (! excludeClass(className)){
					temp.add(className);
				}
				
			}
		}
		return temp.toArray();
	}

	private boolean excludeClass(String className) {
		try {
			Class clazz = Class.forName(className);
			return Internal4.class.isAssignableFrom(clazz);
		} catch (ClassNotFoundException e) {
			// Can happen if the class definition is not available.
		}
		return false;
	}
	
	public ObjectContainer getDB(){
		return objContainer;
	}
	
	public void setDB(ObjectContainer oc){
		objContainer = oc;
	}
	
	public void setDbPath(String path) {
		if(path != null)
			dbPath = path;
	}
	
	public String getDbPath() {
		return dbPath;
	}

	public Reflector reflector(){
		return objContainer.ext().reflector();
	}
	
	public int getNumOfObjects(String className){
		try {
			if(objContainer != null && className != null){
				StoredClass strClass = objContainer.ext().storedClass(className);
				if(strClass != null)
					return strClass.getIDs().length;
			}
		}catch(Exception ex){
			
		}
		return 0;
	}
	
	public void commit(){
		if(objContainer != null)
			objContainer.commit();
	}
	
	public void close(){
		if(objContainer != null){
			objContainer.close();
			objContainer = null;
			dbPath = null;
		}
	}

	public long getDBSize() {
		if(objContainer != null && !isClient()){
			SystemInfo sysInfo = objContainer.ext().systemInfo();
			if(sysInfo != null)
				return sysInfo.totalSize();
		}			
		return 0;
	}
	
	public long getFreespaceSize() {
		if(objContainer != null && !isClient())
			return objContainer.ext().systemInfo().freespaceSize();
		return 0;
	}
	
	public boolean isClient(){
		boolean isClient = false;
		if(objContainer != null)
			isClient = ((ObjectContainerBase)objContainer).isClient();
		return isClient;
	}

	public Object getObjectById(long objId) {
		return objContainer.ext().getByID(objId);		
	}

	public void activate(Object resObj, int i) {
		if(objContainer != null)
			objContainer.activate(resObj, i);		
	}

	public StoredClass getStoredClass(String name) {
		if(name == null || objContainer == null)
			return null;
		return objContainer.ext().storedClass(name);
		
	}

	public void refreshObj(Object obj) {
		if(obj != null)
			objContainer.ext().refresh(obj, 1);
	}
}
