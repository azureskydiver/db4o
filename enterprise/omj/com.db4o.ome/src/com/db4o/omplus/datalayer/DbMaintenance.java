package com.db4o.omplus.datalayer;

import com.db4o.*;
import com.db4o.defragment.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.omplus.*;

public class DbMaintenance {
	
	private int DEFAULT_OBJECT_COMMIT_FREQUENCY = 500000;
	
	private ObjectContainer oc;
	
	public boolean isDBOpened(){
		if(getObjectContainer() == null)
			return false;
		return true;
	}
	
	public void defrag(String path) throws Exception {
				
		closeDB();
			
		DefragmentConfig defragConfig = new DefragmentConfig(path);
		defragConfig.db4oConfig();
		defragConfig.forceBackupDelete(true);
		defragConfig.objectCommitFrequency(DEFAULT_OBJECT_COMMIT_FREQUENCY);
		Defragment.defrag(defragConfig);
		
		ObjectContainer oc = Db4o.openFile(path);
		Activator.getDefault().getDatabaseInterface().setDB(oc, path);
	}
		
	private void closeDB() {
		Activator.getDefault().getDatabaseInterface().close();
	}
	
	public void backup(String path) throws Exception {
		oc = getObjectContainer();
		if(oc != null ){
			try{
				oc.ext().backup(path);
			}
			catch( Db4oIOException ex){
				throw new Db4oIOException(" Operation Failed as IO Exception occurred");
			}
			catch( DatabaseClosedException ex){
				throw new RuntimeException(" Operation Failed as database is closed");
			}
			catch( NotSupportedException ex){
				throw new NotSupportedException(" Operation Failed as backup" +
						" is not supported");
			}
		}
	}

	public boolean isClient() {
		return Activator.getDefault().getDatabaseInterface().isClient();
	}

	private ObjectContainer getObjectContainer(){
		IDbInterface dbinterface = Activator.getDefault().getDatabaseInterface();
		oc = dbinterface.getDB();
		return oc;
	}
		
}
