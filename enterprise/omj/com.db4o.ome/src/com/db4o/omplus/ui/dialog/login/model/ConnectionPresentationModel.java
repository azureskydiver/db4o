/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */

package com.db4o.omplus.ui.dialog.login.model;

import java.util.*;

import com.db4o.omplus.connection.*;

public abstract class ConnectionPresentationModel<P extends ConnectionParams> {
	private LoginPresentationModel model;
	private P selected;
	
	public ConnectionPresentationModel(LoginPresentationModel model) {
		this.model = model;
	}
	
	protected List<P> connections() {
		return connections(model.recentConnections());
	}
	
	public String[] recentConnections() {
		List<P> connections = connections();
		String[] paths = new String[connections.size()];
		for (int idx = 0; idx < paths.length; idx++) {
			paths[idx] = connections.get(idx).getPath();
		}
		return paths;
	}

	public boolean connect() {
		try {
			model.connect(selected != null ? selected : fromState());
			return true;
		} 
		catch (DBConnectException exc) {
			model.err().error(exc);
			return false;
		}
	}
	
	public void select(int idx) {
		selected = connections().get(idx);
		selected(selected);
	}
	
	protected void unselect() {
		selected = null;
	}
	
	protected abstract P fromState() throws DBConnectException;
	protected abstract void selected(P selected);
	protected abstract List<P> connections(RecentConnectionList recentConnections);
}
