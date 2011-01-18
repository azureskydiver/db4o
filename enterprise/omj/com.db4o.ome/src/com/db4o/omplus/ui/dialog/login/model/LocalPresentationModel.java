/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */

package com.db4o.omplus.ui.dialog.login.model;

import java.util.*;

import com.db4o.omplus.connection.*;

public class LocalPresentationModel extends ConnectionPresentationModel<FileConnectionParams> {

	private String path = "";
	private boolean readOnly = false;
	
	public static interface LocalSelectionListener {
		void localSelection(String path, boolean readOnly);
	}

	private final List<LocalSelectionListener> localListeners = new LinkedList<LocalSelectionListener>();

	public LocalPresentationModel(LoginPresentationModel model) {
		super(model);
	}

	public void addListener(LocalSelectionListener listener) {
		localListeners.add(listener);
	}
	
	public void state(String path, boolean readOnly) {
		if(this.path.equals(path) && this.readOnly == readOnly) {
			return;
		}
		this.path = path;
		this.readOnly = readOnly;
		newState();
		notifyListeners(path, readOnly);
	}
	
	@Override
	protected FileConnectionParams fromState(String[] jarPaths, String[] configNames) throws DBConnectException {
		if(path == null || path.length() == 0) {
			throw new DBConnectException("Path is empty.");
		}
		return new FileConnectionParams(path, readOnly, jarPaths, configNames);
	}

	@Override
	protected List<FileConnectionParams> connections(RecentConnectionList recentConnections) {
		return recentConnections.getRecentConnections(FileConnectionParams.class);
	}

	@Override
	protected void selected(FileConnectionParams selected) {
		path = "";
		readOnly = false;
		notifyListeners(selected.getPath(), selected.readOnly());
	}

	private void notifyListeners(String path, boolean readOnly) {
		for (LocalSelectionListener listener : localListeners) {
			listener.localSelection(path, readOnly);
		}
	}
}
