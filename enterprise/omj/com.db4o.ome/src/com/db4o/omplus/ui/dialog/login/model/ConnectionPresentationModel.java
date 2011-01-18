/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */

package com.db4o.omplus.ui.dialog.login.model;

import java.util.*;

import com.db4o.omplus.*;
import com.db4o.omplus.connection.*;

public abstract class ConnectionPresentationModel<P extends ConnectionParams> implements CustomConfigSink {
	
	private final LoginPresentationModel model;
	private final CustomConfigSource configSource;
	
	private LoginPresentationState state;
	private final Set<ConnectionPresentationListener> listeners = new HashSet<ConnectionPresentationListener>();

	public ConnectionPresentationModel(LoginPresentationModel model, CustomConfigSource configSource) {
		this.model = model;
		this.configSource = configSource;
		this.state = new NewState();
	}

	public void addCustomConfigListener(ConnectionPresentationListener listener) {
		listeners.add(listener);
		listener.editingEnabled(state.editEnabled());
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
		state(new SelectedState(selected));
		selected(selected);
		notifyCustomConfigEditEnabled(false);
	}
	
	public void requestCustomConfig() {
		state.requestCustomConfig();
	}
	
	public void customConfig(String[] jarFiles, String[] customConfigClassNames) {
		state.customConfig(jarFiles, customConfigClassNames);
	}

	protected List<P> connections() {
		return connections(model.recentConnections());
	}
	
	protected void newState() {
		state(new NewState());
	}
	
	protected boolean editEnabled() {
		return state.editEnabled();
	}
	
	// FIXME revert to protected
	public ErrorMessageHandler err() {
		return model.err();
	}
	
	protected abstract P fromState(String[] jarPaths, String[] configNames) throws DBConnectException;
	protected abstract void selected(P selected);
	protected abstract List<P> connections(RecentConnectionList recentConnections);
	
	private void notifyCustomConfigEditEnabled(boolean enabled) {
		for (ConnectionPresentationListener listener : listeners) {
			listener.editingEnabled(enabled);
		}
	}
	
	private void state(LoginPresentationState state) {
		boolean editEnabledPrev = this.state.editEnabled();
		this.state = state;
		boolean editEnabledNew = state.editEnabled();
		if(editEnabledPrev != editEnabledNew) {
			notifyCustomConfigEditEnabled(editEnabledNew);
		}
	}
	
	private abstract class LoginPresentationState {
		abstract P params() throws DBConnectException;
		abstract void requestCustomConfig();
		abstract boolean editEnabled();
		abstract void customConfig(String[] jarPaths, String[] customConfigClassNames);
	}

	private class SelectedState extends LoginPresentationState {
		private final P params;
		
		public SelectedState(P params) {
			this.params = params;
		}

		@Override
		P params() {
			return params;
		}
		
		@Override
		void customConfig(String[] jarPaths, String[] customConfigClassNames) {
			err().error("custom config editing disabled for history items");
		}

		@Override
		boolean editEnabled() {
			return false;
		}

		@Override
		void requestCustomConfig() {
			err().error("custom config editing disabled for history items");
		}
	}

	private class NewState extends LoginPresentationState {
		private String[] jarPaths = {};
		private String[] configNames = {};

		@Override
		public P params() throws DBConnectException {
			return fromState(jarPaths, configNames);
		}

		@Override
		public void customConfig(String[] jarPaths, String[] customConfigClassNames) {
			this.jarPaths = jarPaths;
			this.configNames = customConfigClassNames;
		}

		@Override
		boolean editEnabled() {
			return true;
		}

		@Override
		void requestCustomConfig() {
			configSource.requestCustomConfig(ConnectionPresentationModel.this, jarPaths, configNames);
		}
	}
	
	public static interface ConnectionPresentationListener {
		void editingEnabled(boolean enabled);
		void customConfigStateChanged(int jarPathCount, int configuratorCount);
	}


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
