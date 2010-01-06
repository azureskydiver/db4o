/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.connection;

import java.util.*;

public interface RecentConnectionList {

	List<FileConnectionParams> getRecentFileConnections();

	List<RemoteConnectionParams> getRecentRemoteConnections();

	void addNewConnection(ConnectionParams params);

}