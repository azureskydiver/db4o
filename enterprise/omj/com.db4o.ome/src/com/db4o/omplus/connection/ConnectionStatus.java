package com.db4o.omplus.connection;

import com.db4o.omplus.datalayer.DbInterfaceImpl;

public class ConnectionStatus {
	
	public boolean isConnected() {
		if(DbInterfaceImpl.getInstance().getDB() != null){
			return true;
		}
		return false;
	}
	
	public String getVersion(){
		return DbInterfaceImpl.getInstance().getVersion();
	}
	
	public String getCurrentDB(){
		return DbInterfaceImpl.getInstance().getDbPath();
	}
	
	public void closeExistingDB(){
		DbInterfaceImpl.getInstance().close();
	}

}
