package com.db4o.browser.model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.browser.prefs.activation.ActivationPreferences;
import com.db4o.browser.prefs.classpath.ClasspathPreferences;
import com.db4o.reflect.Reflector;
import com.db4o.reflect.jdk.JdkReflector;
import com.swtworkbench.community.xswt.metalogger.Logger;

public abstract class Db4oConnectionSpec {
    
    
    // Global temporary placeholder for read only setting.
    // TODO: Move to preferences when starting to work on editing.
    public static boolean PREFERENCE_IS_READ_ONLY = true;
    
	private boolean readOnly;
	
	protected Db4oConnectionSpec(boolean readOnly) {
		this.readOnly=readOnly;
	}
	
	public ObjectContainer connect() {
        int activationDepth = ActivationPreferences.getDefault().getInitialActivationDepth();
        String[] classpath = ClasspathPreferences.getDefault().classPath();
		Db4o.configure().readOnly(readOnly);
		Db4o.configure().activationDepth(activationDepth);
		Db4o.configure().reflectWith(createReflector(classpath));
		return connectInternal();
	}

	private Reflector createReflector(String[] classpath) {
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
