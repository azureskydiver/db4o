/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */

package com.db4o.omplus.ui.dialog.login.model;

import java.util.*;

import com.db4o.omplus.*;
import com.db4o.omplus.connection.*;

public abstract class ConnectionPresentationModel<P extends ConnectionParams> {
	
	private LoginPresentationModel model;
	private LoginPresentationState state;
	
	private abstract class LoginPresentationState {
		abstract P params() throws DBConnectException;
		abstract void customConfig(String[] jarPaths, String[] customConfigClassNames);
	}

	private class SelectedState extends LoginPresentationState {
		private final P params;
		
		public SelectedState(P params) {
			this.params = params;
		}

		P params() {
			return params;
		}
		
		void customConfig(String[] jarPaths, String[] customConfigClassNames) {
		}
	}

	private class NewState extends LoginPresentationState {
		private String[] jarPaths = {};
		private String[] configNames = {};

		public P params() throws DBConnectException {
			return fromState(jarPaths, configNames);
		}

		public void customConfig(String[] jarPaths, String[] customConfigClassNames) {
			this.jarPaths = jarPaths;
			this.configNames = customConfigClassNames;
		}
	}
	
	public ConnectionPresentationModel(LoginPresentationModel model) {
		this.model = model;
		this.state = new NewState();
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
			model.connect(state.params());
			return true;
		} 
		catch (DBConnectException exc) {
			model.err().error(exc);
			return false;
		}
	}
	
	public void select(int idx) {
		P selected = connections().get(idx);
		state = new SelectedState(selected);
		selected(selected);
	}
	
	public void customConfig(String[] jarFiles, String[] customConfigClassNames) {
		state.customConfig(jarFiles, customConfigClassNames);
	}

	protected List<P> connections() {
		return connections(model.recentConnections());
	}
	
	protected void newState() {
		state = new NewState();
	}
	
	// FIXME revert to protected
	public ErrorMessageHandler err() {
		return model.err();
	}
	
	protected abstract P fromState(String[] jarPaths, String[] configNames) throws DBConnectException;
	protected abstract void selected(P selected);
	protected abstract List<P> connections(RecentConnectionList recentConnections);
	

//	private void notifyListenersCustomState() {
//		String[] jarPaths = new String[jarFiles.length];
//		for (int jarIdx = 0; jarIdx < jarFiles.length; jarIdx++) {
//			jarPaths[jarIdx] = jarFiles[jarIdx].getAbsolutePath();
//		}
//		for (LocalSelectionListener listener : localListeners) {
//			listener.customConfig(jarPaths, configClassNames);
//		}
//	}

}
