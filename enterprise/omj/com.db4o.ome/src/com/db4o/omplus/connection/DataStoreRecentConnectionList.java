package com.db4o.omplus.connection;

import java.util.*;

import com.db4o.omplus.datalayer.*;

public class DataStoreRecentConnectionList implements RecentConnectionList {
	
	private final static String LOCAL = "LOCAL_CONN";
	private final static String REMOTE = "REMOTE_CONN";

	private final OMEDataStore dataStore;

	public DataStoreRecentConnectionList(OMEDataStore dataStore) {
		this.dataStore = dataStore;
	}
	
	public List<FileConnectionParams> getRecentFileConnections() {
		List<FileConnectionParams> connections = connections(LOCAL);
		return Collections.unmodifiableList(connections);
	}
	
	public List<RemoteConnectionParams> getRecentRemoteConnections() {
		List<RemoteConnectionParams>  connections = connections(REMOTE);
		return Collections.unmodifiableList(connections);
	}

	public void addNewConnection(ConnectionParams params) {
		if (params == null) {
			return;
		}
		if (params.isRemote()) {
			internalAddParams((RemoteConnectionParams)params, dataStore, connections(REMOTE));
		}
		else {
			internalAddParams((FileConnectionParams)params, dataStore, connections(LOCAL));
		}
	}

	private <T extends ConnectionParams> List<T> connections(String key) {
		List<T> connections = dataStore.getGlobalEntry(key);
		if(connections == null) {
			connections = new LinkedList<T>();
			dataStore.setGlobalEntry(key, connections);
		}
		return connections;
	}
	
	private <T extends ConnectionParams> void connections(String key, List<T> list){
		dataStore.setGlobalEntry(key, list);
	}

	private <T extends ConnectionParams> void internalAddParams(T params, OMEDataStore omeData, List<T> connections) {
		if(connections.contains(params)) {
			if(params.equals(connections.get(0))) {
				return;
			}
			connections.remove(params);
		}
		connections.add(0, params);
		connections((params.isRemote() ? REMOTE : LOCAL), connections);
	}

}
