package com.db4o.omplus.connection;

import java.util.*;

import com.db4o.omplus.*;
import com.db4o.omplus.datalayer.*;

@SuppressWarnings("unchecked")
public class RecentConnectionList {
	
	private final String LOCAL = "LOCAL_CONN";
	private final String REMOTE = "REMOTE_CONN";
	
	public ArrayList<ConnectionParams> getRecentFileConnections(){
		OMEDataStore omeData = Activator.getDefault().getOMEDataStore();
		ArrayList<ConnectionParams> connections = omeData.getGlobalEntry(LOCAL);
		if(connections == null)
			connections = new ArrayList<ConnectionParams>();
		return connections;
	}
	
	private void setRecentFileConnections(ArrayList list){
		OMEDataStore omeData = Activator.getDefault().getOMEDataStore();
		if(omeData != null && list != null){
			omeData.setGlobalEntry(LOCAL, list);
		}
	}
	
	public ArrayList<ConnectionParams>  getRecentRemoteConnections(){
		OMEDataStore omeData = Activator.getDefault().getOMEDataStore();
		ArrayList<ConnectionParams>  connections = omeData.getGlobalEntry(REMOTE);
		if(connections == null)
			connections = new ArrayList<ConnectionParams> ();
		return connections;
	}
	
	private void setRecentRemoteConnections(ArrayList list){
		OMEDataStore omeData = Activator.getDefault().getOMEDataStore();
		if(omeData != null && list != null){
			omeData.setGlobalEntry(REMOTE, list);
		}
	}
	
	public boolean showRemoteConn(){
		OMEDataStore omeData = Activator.getDefault().getOMEDataStore();
		return omeData.getIsLastConnRemote();
	}
	
	 public void addNewConnection(ConnectionParams params)
	 { // make sure it's not already here
		 OMEDataStore omeData = Activator.getDefault().getOMEDataStore();
		 if(params != null)
		 {
			ArrayList<ConnectionParams> connections = null;
			if(params.isRemote())
				connections = getRecentRemoteConnections();
			else
				connections = getRecentFileConnections();
			int size = connections.size();
			boolean modify = true;
			if(size  > 0)
			{
				for (int i = size-1; i > -1 ; i--)
				{
					ConnectionParams rcConn = (ConnectionParams) connections.get(i);
					if (rcConn.getPath().equals(params.getPath())) 
					{
						if( i == size - 1)
						{
							modify = false;
							omeData.setIsLastConnRemote(params.isRemote());
							break;
						}
						else
							connections.remove(rcConn);
					}
			    }
			}
			if(modify)
			{
				connections.add(params);
				if(size > 10)
					connections.remove(0);
				boolean isRemote = params.isRemote();
				omeData.setIsLastConnRemote(isRemote);
				if(isRemote)
					setRecentRemoteConnections(connections);
				else
					setRecentFileConnections(connections);
			}
		 }
	}

}
