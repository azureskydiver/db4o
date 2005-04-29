package com.db4o.browser.model;

import java.io.*;
import java.net.*;
import java.util.*;

import com.db4o.*;
import com.db4o.reflect.*;
import com.db4o.reflect.jdk.*;
import com.swtworkbench.community.xswt.metalogger.*;

public abstract class Db4oConnectionSpec {
	private boolean readOnly;
	private int activationDepth;
	private String[] classpath;
	
	protected Db4oConnectionSpec(boolean readOnly,int depth, String[] classpath) {
		this.readOnly=readOnly;
		activationDepth = depth;
		this.classpath = classpath;
	}
	
	public ObjectContainer connect() {
		Db4o.configure().readOnly(readOnly);
		Db4o.configure().activationDepth(activationDepth);
		Db4o.configure().reflectWith(createReflector());
		return connectInternal();
	}

	private Reflector createReflector() {
		List urllist=new ArrayList(classpath.length);
		for (int idx = 0; idx < classpath.length; idx++) {
			URL cururl=path2URL(classpath[idx]);
			if(cururl!=null) {
				urllist.add(cururl);
			}
		}
		URL[] urls=(URL[])urllist.toArray(new URL[urllist.size()]);
		URLClassLoader classloader=new URLClassLoader(urls);
		return new JdkReflector(classloader);
	}
	
	private URL path2URL(String filePath) {
		File file=new File(filePath);
		if(!file.exists()) {
			Logger.log().message("Could not find classpath entry: "+filePath);
			return null;
		}
		try {
			return file.toURL();
		} catch (MalformedURLException exc) {
			Logger.log().error(exc,"Could not convert classpath entry to URL: "+file.getAbsolutePath());
			return null;
		}
	}
	
	public abstract String path();
	protected abstract ObjectContainer connectInternal();
}
