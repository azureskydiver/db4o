/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */

package com.db4o.omplus.ui.dialog.login.model;

import java.io.*;
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
	
	public RemotePresentationModel(LoginPresentationModel model) {
		super(model);
	}

	public void addListener(RemoteSelectionListener listener) {
		remoteListeners.add(listener);
	}

	public void state(String host, String port, String user, String pwd) {
		if(this.host.equals(host) && this.port.equals(port) && this.user.equals(user) && this.pwd.equals(pwd)) {
			return;
		}
		this.host = host;
		this.port = port;
		this.user = user;
		this.pwd = pwd;
		newState();
		notifyListeners(host, port, user, pwd);
	}
	
	@Override
	protected RemoteConnectionParams fromState(File[] jarFiles, String[] configNames) throws DBConnectException {
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
		return new RemoteConnectionParams(host, portNum, user, pwd);
	}

	@Override
	protected List<RemoteConnectionParams> connections(RecentConnectionList recentConnections) {
		return recentConnections.getRecentConnections(RemoteConnectionParams.class);
	}

	@Override
	protected void selected(RemoteConnectionParams remoteParams) {
		host = "";
		port = "";
		user = "";
		pwd = "";
		notifyListeners(remoteParams.getHost(), String.valueOf(remoteParams.getPort()), remoteParams.getUser(), remoteParams.getPassword());
	}

	private void notifyListeners(String host, String port, String user, String pwd) {
		for (RemoteSelectionListener listener : remoteListeners) {
			listener.remoteSelection(host, port, user, pwd);
		}
	}
	
	private String trim(String str) {
		return str == null ? "" : str.trim();
	}

}
