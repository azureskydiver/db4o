package com.db4o.omplus.datalayer;

import com.db4o.ext.DatabaseClosedException;
import com.db4o.ext.Db4oIOException;
import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.defragment.Defragment;
import com.db4o.defragment.DefragmentConfig;
import com.db4o.foundation.NotSupportedException;

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
		setDB(oc);
		setDbPath(path);
	}
		
	private void setDbPath(String path) {
		DbInterfaceImpl.getInstance().setDbPath(path);
	}

	private void closeDB() {
		DbInterfaceImpl.getInstance().close();
	}
	
	private void setDB(ObjectContainer oc) {
		DbInterfaceImpl.getInstance().setDB(oc);
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
		return DbInterfaceImpl.getInstance().isClient();
	}

	private ObjectContainer getObjectContainer(){
		DbInterfaceImpl dbinterface = DbInterfaceImpl.getInstance();
		oc = dbinterface.getDB();
		return oc;
	}
		
}
