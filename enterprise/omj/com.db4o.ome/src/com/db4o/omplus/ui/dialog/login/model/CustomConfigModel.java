/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */

package com.db4o.omplus.ui.dialog.login.model;

import java.io.*;
import java.util.*;

import com.db4o.omplus.*;
import com.db4o.omplus.connection.*;

public class CustomConfigModel {

	public static interface CustomConfigListener {
		void customConfig(String[] jarPaths, String[] configClassNames);
	}

	private final CustomConfigSink sink;
	private final ConfiguratorExtractor extractor;
	private final ErrorMessageHandler err;

	private Set<CustomConfigListener> listeners = new HashSet<CustomConfigListener>();

	private List<File> jarFiles = new ArrayList<File>();
	private List<String> configClassNames = new ArrayList<String>();

	public CustomConfigModel(CustomConfigSink sink, ConfiguratorExtractor extractor, ErrorMessageHandler err) {
		this.sink = sink;
		this.extractor = extractor;
		this.err = err;
	}

	public void addListener(CustomConfigListener listener) {
		listeners.add(listener);
	}

	public void addJarPaths(String... jarPaths) {
		try {
			Set<File> jarFileSet = new HashSet<File>(jarFiles);
			for (int jarIdx = 0; jarIdx < jarPaths.length; jarIdx++) {
				try {
					File jarFile = new File(jarPaths[jarIdx]).getCanonicalFile();
					if(!extractor.acceptJarFile(jarFile)) {
						err.error("not an existing jar file: " + jarFile.getAbsolutePath());
						return;
					}
					jarFileSet.add(jarFile);
				} 
				catch (IOException exc) {
					err.error("could not interpret path: " + jarPaths[jarIdx], exc);
					return;
				}
			}
			List<File> jarFileList = new ArrayList<File>(jarFileSet);
			Collections.sort(jarFileList);
			this.configClassNames = extractor.configuratorClassNames(jarFileList);
			this.jarFiles = jarFileList;
		} 
		catch (DBConnectException exc) {
			err.error(exc);
		} 
		finally {
			notifyListeners();
		}
	}

	public void removeJarPaths(String... jarPaths) {
		try {
			List<File> jarFiles = new ArrayList<File>(this.jarFiles);
			for (String jarPath : jarPaths) {
				try {
					jarFiles.remove(new File(jarPath).getCanonicalFile());
				} 
				catch (IOException exc) {
					err.error("could not interpret path: " + jarPath, exc);
				}
			}
			try {
				configClassNames = extractor.configuratorClassNames(jarFiles);
			}
			catch(DBConnectException exc) {
				err.error("inconsistent state, purging configurator list - could not extract configurators from: " + Arrays.toString(jarPaths), exc);
				configClassNames = new ArrayList<String>();
			}
			this.jarFiles = jarFiles;
		} 
		finally {
			notifyListeners();
		}
	}

	public void commit() {
		sink.customConfig(jarFiles.toArray(new File[jarFiles.size()]), configClassNames.toArray(new String[configClassNames.size()]));
	}
	
	private void notifyListeners() {
		String[] jarPaths = new String[jarFiles.size()];
		for (int jarIdx = 0; jarIdx < jarFiles.size(); jarIdx++) {
			jarPaths[jarIdx] = jarFiles.get(jarIdx).getAbsolutePath();
		}
		for (CustomConfigListener listener : listeners) {
			listener.customConfig(jarPaths, configClassNames.toArray(new String[configClassNames.size()]));
		}
	}

}
