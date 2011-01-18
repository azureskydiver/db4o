/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */

package com.db4o.omplus.ui.dialog.login.model;

import java.util.*;

import com.db4o.omplus.connection.*;

public class RemotePresentationModel extends ConnectionPresentationModel<RemoteConnectionParams> {

	private static final int MAX_PORT = 65535;

	private String host = "";
	private String port = "";
	private String user = "";
	private String pwd = "";
	
	public static interface RemoteSelectionListener {
		void remoteSelection(String host, String port, String user, String pwd);
	}

	private final List<RemoteSelectionListener> remoteListeners = new LinkedList<RemoteSelectionListener>();
	
	public RemotePresentationModel(LoginPresentationModel model, CustomConfigSource configSource) {
		super(model, configSource);
	}

	public void addListener(RemoteSelectionListener listener) {
		remoteListeners.add(listener);
	}

	public void host(String host) {
		if(this.host.equals(host)) {
			return;
		}
		this.host = host;
		newState();
		notifyListeners();
	}

	public void port(String port) {
		if(this.port.equals(port)) {
			return;
		}
		this.port = port;
		newState();
		notifyListeners();
	}

	public void user(String user) {
		if(this.user.equals(user)) {
			return;
		}
		this.user = user;
		newState();
		notifyListeners();
	}

	public void password(String password) {
		if(this.pwd.equals(password)) {
			return;
		}
		this.pwd = password;
		newState();
		notifyListeners();
	}

	@Override
	protected RemoteConnectionParams fromState(String[] jarPaths, String[] configNames) throws DBConnectException {
		host = trim(host);
		if(host.length() == 0) {
			throw new DBConnectException("Host is empty.");
		}
		port = trim(port);
		if(port.length() == 0) {
			throw new DBConnectException("Port is empty.");
		}
		int portNum = -1;
		try {
			portNum = Integer.parseInt(port);
		}
		catch(NumberFormatException exc) {
			throw new DBConnectException("Illegal port: " + port, exc);
		}
		if(portNum < 0 || portNum > MAX_PORT) {
			throw new DBConnectException("Port not in range (0-" + MAX_PORT + "): " + portNum);
		}
		user = trim(user);
		if(user.length() == 0) {
			throw new DBConnectException("User is empty.");
		}
		pwd = trim(pwd);
		if(pwd.length() == 0) {
			throw new DBConnectException("Password is empty.");
		}
		return new RemoteConnectionParams(host, portNum, user, pwd, jarPaths, configNames);
	}

	@Override
	protected List<RemoteConnectionParams> connections(RecentConnectionList recentConnections) {
		return recentConnections.getRecentConnections(RemoteConnectionParams.class);
	}

	@Override
	protected void selected(RemoteConnectionParams remoteParams) {
		host = remoteParams.getHost();
		port = String.valueOf(remoteParams.getPort());
		user = remoteParams.getUser();
		pwd = remoteParams.getPassword();
		notifyListeners();
	}

	private void notifyListeners() {
		for (RemoteSelectionListener listener : remoteListeners) {
			listener.remoteSelection(host, port, user, pwd);
		}
	}
	
	private String trim(String str) {
		return str == null ? "" : str.trim();
	}

}
