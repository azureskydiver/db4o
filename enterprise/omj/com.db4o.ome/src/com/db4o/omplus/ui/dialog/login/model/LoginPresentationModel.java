/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.ui.dialog.login.model;

import java.util.*;

import com.db4o.omplus.*;
import com.db4o.omplus.connection.*;

public class LoginPresentationModel {

	private static final int MAX_PORT = 65535;

	private final List<LocalSelectionListener> localListeners = new LinkedList<LocalSelectionListener>();
	private final List<RemoteSelectionListener> remoteListeners = new LinkedList<RemoteSelectionListener>();
	
	private final RecentConnectionList recentConnections;
	private final ErrorMessageSink err;
	private final Connector connector;
	
	public LoginPresentationModel(RecentConnectionList recentConnections, ErrorMessageSink err, Connector connector) {
		this.recentConnections = recentConnections;
		this.err = err;
		this.connector = connector;
	}
	
	public void select(LoginMode mode, int idx) {
		mode.notifySelected(this, idx);
	}

	public String[] recentConnections(LoginMode mode) {
		return recentConnections(mode.connections(recentConnections));
	}

	public boolean connect(String path, boolean readOnly) {
		path = trim(path);
		if(path.length() == 0) {
			err.error("Path is empty.");
			return false;
		}
		return connect(new FileConnectionParams(path, readOnly));
	}

	public boolean connect(String host, String port, String user, String pwd) {
		host = trim(host);
		if(host.length() == 0) {
			err.error("Host is empty.");
			return false;
		}
		port = trim(port);
		if(port.length() == 0) {
			err.error("Port is empty.");
			return false;
		}
		int portNum = -1;
		try {
			portNum = Integer.parseInt(port);
		}
		catch(NumberFormatException exc) {
			err.error("Illegal port: " + exc.getMessage());
			return false;
		}
		if(portNum < 0 || portNum > MAX_PORT) {
			err.error("Port not in range (0-" + MAX_PORT + "): " + portNum);
			return false;
		}
		user = trim(user);
		if(user.length() == 0) {
			err.error("User is empty.");
			return false;
		}
		pwd = trim(pwd);
		if(pwd.length() == 0) {
			err.error("Password is empty.");
			return false;
		}
		return connect(new RemoteConnectionParams(host, portNum, user, pwd));
	}

	public void addListener(LocalSelectionListener listener) {
		localListeners.add(listener);
	}

	public void addListener(RemoteSelectionListener listener) {
		remoteListeners.add(listener);
	}

	private boolean connect(ConnectionParams params) {
		try {
			connector.connect(params);
			recentConnections.addNewConnection(params);
			return true;
		}
		catch(Exception exc) {
			error("Could not connect to " + params.getPath() + ": " + exc.getMessage(), exc);
			return false;
		}
	}

	private String[] recentConnections(List<? extends ConnectionParams> connections) {
		String[] paths = new String[connections.size()];
		for (int idx = 0; idx < paths.length; idx++) {
			paths[idx] = connections.get(idx).getPath();
		}
		return paths;
	}

	private String trim(String str) {
		return str == null ? "" : str.trim();
	}
	
	private void error(String msg, Throwable exc) {
		err.error(msg);
		err.exc(exc);
	}

	public static enum LoginMode {
		LOCAL  {
			@Override
			protected List<? extends ConnectionParams> connections(RecentConnectionList recentConnections) {
				return recentConnections.getRecentFileConnections();
			}

			@Override
			public void notifySelected(LoginPresentationModel model, int idx) {
				FileConnectionParams localParams = (FileConnectionParams) connections(model.recentConnections).get(idx);
				for (LocalSelectionListener listener : model.localListeners) {
					listener.localSelection(localParams.getPath(), localParams.readOnly());
				}
			}
		},
		REMOTE {
			@Override
			protected List<? extends ConnectionParams> connections(RecentConnectionList recentConnections) {
				return recentConnections.getRecentRemoteConnections();
			}

			@Override
			public void notifySelected(LoginPresentationModel model, int idx) {
				RemoteConnectionParams remoteParams = (RemoteConnectionParams) connections(model.recentConnections).get(idx);
				for (RemoteSelectionListener listener : model.remoteListeners) {
					listener.remoteSelection(remoteParams.getHost(), remoteParams.getPort(), remoteParams.getUser(), remoteParams.getPassword());
				}
			}
		};
		
		protected abstract List<? extends ConnectionParams> connections(RecentConnectionList recentConnections);
		protected abstract void notifySelected(LoginPresentationModel model, int idx);
	}

	public static interface LocalSelectionListener {
		void localSelection(String path, boolean readOnly);
	}

	public static interface RemoteSelectionListener {
		void remoteSelection(String host, int port, String user, String pwd);
	}
}
