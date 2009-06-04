package com.db4o.omplus.connection;

import com.db4o.omplus.*;
import com.db4o.omplus.datalayer.DbInterfaceImpl;

public class ConnectionStatus {
	
	public boolean isConnected() {
		if(Activator.getDefault().getDatabaseInterface().getDB() != null){
			return true;
		}
		return false;
	}
	
	public String getVersion(){
		return Activator.getDefault().getDatabaseInterface().getVersion();
	}
	
	public String getCurrentDB(){
		return Activator.getDefault().getDatabaseInterface().getDbPath();
	}
	
	public void closeExistingDB(){
		Activator.getDefault().getDatabaseInterface().close();
	}

}
