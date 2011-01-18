/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */

package com.db4o.omplus.ui.dialog.login.model;

import java.io.*;
import java.util.*;

import com.db4o.omplus.connection.*;

public class LocalPresentationModel extends ConnectionPresentationModel<FileConnectionParams> {

	private String path = "";
	private boolean readOnly = false;
	private List<File> jarFiles = new ArrayList<File>();
	private List<String> configClassNames = new ArrayList<String>();
	private List<Integer> selectedConfigIndices = new ArrayList<Integer>();
	
	public static interface LocalSelectionListener {
		void localSelection(String path, boolean readOnly);
		void customConfig(String[] jarPaths, String[] configClassNames);
	}

	private final List<LocalSelectionListener> localListeners = new LinkedList<LocalSelectionListener>();
	private final ConfiguratorExtractor extractor;

	public LocalPresentationModel(LoginPresentationModel model, ConfiguratorExtractor extractor) {
		super(model);
		this.extractor = extractor;
	}

	public void addListener(LocalSelectionListener listener) {
		localListeners.add(listener);
	}
	
	public void state(String path, boolean readOnly) {
		if(this.path.equals(path) && this.readOnly == readOnly) {
			return;
		}
		unselect();
		this.path = path;
		this.readOnly = readOnly;
		jarFiles = new ArrayList<File>();
		configClassNames = new ArrayList<String>();
		notifyListeners(path, readOnly);
		notifyListenersCustomState();
	}
	
	public void jarPaths(String[] jarPaths) {
		try {
			List<File> jarFiles = new ArrayList<File>();
			for (int jarIdx = 0; jarIdx < jarPaths.length; jarIdx++) {
				File jarFile = new File(jarPaths[jarIdx]);
				if(!extractor.acceptJarFile(jarFile)) {
					err().error("not an existing jar file: " + jarFile.getAbsolutePath());
					return;
				}
				jarFiles.add(jarFile);
			}
			Collections.sort(jarFiles);
			this.configClassNames = extractor.configuratorClassNames(jarFiles);
			this.jarFiles = jarFiles;
		} 
		catch (DBConnectException exc) {
			err().error(exc);
		}
		finally {
			notifyListenersCustomState();
		}
	}

	@Override
	protected FileConnectionParams fromState() throws DBConnectException {
		if(path == null || path.length() == 0) {
			throw new DBConnectException("Path is empty.");
		}
		return new FileConnectionParams(path, readOnly);
	}

	@Override
	protected List<FileConnectionParams> connections(RecentConnectionList recentConnections) {
		return recentConnections.getRecentFileConnections();
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

	private void notifyListenersCustomState() {
		String[] jarPaths = new String[jarFiles.size()];
		for (int jarIdx = 0; jarIdx < jarFiles.size(); jarIdx++) {
			jarPaths[jarIdx] = jarFiles.get(jarIdx).getAbsolutePath();
		}
		for (LocalSelectionListener listener : localListeners) {
			listener.customConfig(jarPaths, configClassNames.toArray(new String[configClassNames.size()]));
		}
	}
}
