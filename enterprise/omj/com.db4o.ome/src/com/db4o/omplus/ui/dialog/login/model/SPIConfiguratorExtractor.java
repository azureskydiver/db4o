/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */

package com.db4o.omplus.ui.dialog.login.model;

import java.io.*;
import java.net.*;
import java.util.*;

import com.db4o.config.*;
import com.db4o.omplus.*;
import com.db4o.omplus.connection.*;

public class SPIConfiguratorExtractor implements ConfiguratorExtractor {

	public List<String> configuratorClassNames(List<File> jarFiles) throws DBConnectException {
		URLClassLoader cl = new URLClassLoader(urls(jarFiles), Activator.class.getClassLoader());
		Iterator<EmbeddedConfigurationItem> ps = sun.misc.Service.providers(EmbeddedConfigurationItem.class, cl);
		Set<String> classNames = new HashSet<String>();
		while(ps.hasNext()) {
			EmbeddedConfigurationItem configurator = ps.next();
			classNames.add(configurator.getClass().getName());
		}
		List<String> configClassNames = new ArrayList<String>(classNames);
		Collections.sort(configClassNames);
		return configClassNames;
	}

	private URL[] urls(List<File> jarFiles) throws DBConnectException { // FIXME better exception
		URL[] urls = new URL[jarFiles.size()];
		for (int jarIdx = 0; jarIdx < jarFiles.size(); jarIdx++) {
			File jarFile = jarFiles.get(jarIdx);
			try {
				urls[jarIdx] = jarFile.toURI().toURL();
			} 
			catch (MalformedURLException exc) {
				throw new DBConnectException("invalid jar reference: " + jarFile, exc);
			}
		}
		return urls;
	}

	public boolean acceptJarFile(File file) {
		return file.isFile() && file.getName().endsWith(".jar");
	}
}
