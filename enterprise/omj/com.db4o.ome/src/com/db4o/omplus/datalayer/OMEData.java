package com.db4o.omplus.datalayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;

@SuppressWarnings("unchecked")
public class OMEData {
	
	private final static String USR_HOME_DIR = "user.home";
	private final static String OME_DATA_DB = "OMEDATA.yap";
	private final static String SEPERATOR ="/";
	
	 private static transient final String settingsFile = new File(new File(System
				.getProperty(USR_HOME_DIR)), OME_DATA_DB)
				.getAbsolutePath();
	
	private static transient ObjectContainer db;

	private  static OMEData omeData;
	@SuppressWarnings("unchecked")
	private HashMap<String, ArrayList> data;
	private boolean isLastConnRemote;
	
	private OMEData(){
		data = new HashMap<String, ArrayList>();
		isLastConnRemote = true;
	}
	
	public synchronized static OMEData getInstance(){
		if( omeData == null){
			OMEData temp = null;
			db = getObjectContainer();
			ObjectSet set = getObjectSet(db);
			if(set.hasNext()){
				temp = (OMEData)set.next();
				omeData = temp;
//				omeData.setData(temp.getData());
//				omeData.setIsLastConnRemote(temp.getIsLastConnRemote());
			}else {
				omeData = new OMEData();
				db.store(omeData);
			}
			close(db);
		}
		return omeData;
	}
	
	private static ObjectContainer getObjectContainer() {
		return Db4o.openFile(configure(), settingsFile);
	}

	private static ObjectSet<OMEData> getObjectSet(ObjectContainer db){
		return db.query(OMEData.class);
	}
	
	@SuppressWarnings("deprecation")
	private static Configuration configure() {
		Configuration config = Db4o.configure();
		config.objectClass(OMEData.class).minimumActivationDepth(Integer.MAX_VALUE);
		config.objectClass(OMEData.class).updateDepth(Integer.MAX_VALUE);
		config.allowVersionUpdates(true);
		return config;
	}
	
	public static void close(ObjectContainer db){
		db.commit();
		db.close();
	}

	public HashMap<String, ArrayList> getData() {
		return data;
	}

	public void setData(HashMap<String, ArrayList> data) {
		this.data = data;
	}
	
	public ArrayList getConnections(String key){
//		OMEData temp = readData();
//		data = temp.getData();
		return data.get(key);
	}
	
	public void setConnections(String key, ArrayList list){
		System.out.println("In Set Connections start");
		if(data != null && key != null && list != null){
			data.put(key, list);
			writeData(omeData);
			System.out.println("In Set Connections end");
		}
	}
	
	private synchronized void writeData(OMEData omData) {
		OMEData temp = null;
		db = getObjectContainer();
		ObjectSet result = getObjectSet(db);
		if (result.hasNext()){
			temp = (OMEData) result.next();
			temp.setData(omData.getData());
			temp.setIsLastConnRemote(omData.getIsLastConnRemote());
		}
		else {
			temp = omeData;
		}
		db.store(temp);
		close(db);
	}

	/*	public ArrayList getRecentRemoteConnections(){
		return data.get(REMOTE);
	}
	
	public void setRecentRemoteConnections(ArrayList list){
		if(data != null && list != null){
			data.put(REMOTE, list);
			commit();
		}
	}*/
	
	public ArrayList getDataValue(String key){
//		if( omeData == null)
//			initialize();
//		OMEData temp = readData();
//		data = temp.getData();
		if(key != null){
			String dKey = getKey(key);
			return data.get(dKey);
		}
		return null;
	}
	
	public void setDataValue(String key, ArrayList list){
		System.out.println("In Set data Value start "+key);
		if(data != null && key != null && list != null){
			String dKey = getKey(key);
			data.put(dKey, list);
			writeData(omeData);
			System.out.println("In Set data Value end"+key);
		}
	}

	private String getKey(String key) {
		String path = getDbPath();
		StringBuilder sb = new StringBuilder();
		if(path!= null)
			sb.append(path);
		sb.append(SEPERATOR);
		sb.append(key);
		return sb.toString();
	}


	
	public String getDbPath() {
		return DbInterfaceImpl.getInstance().getDbPath();
	}

	public boolean getIsLastConnRemote() {
//		OMEData temp = readData();
//		isLastConnRemote = temp.getIsLastConnRemote();
		return isLastConnRemote;
	}

	public void setIsLastConnRemote(boolean isLastConnRemote) {
		this.isLastConnRemote = isLastConnRemote;
		System.out.println("In Set IsLastConnRemote start");
//		writeData(omeData);
		System.out.println("In Set IsLastConnRemote end");
	}

}
