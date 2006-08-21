package com.db4o.objectmanager.model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.objectmanager.api.prefs.ActivationPreferences;
import com.db4o.objectmanager.api.prefs.ClasspathPreferences;
import com.db4o.objectmanager.api.prefs.ConstructorPreferences;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.Reflector;
import com.db4o.reflect.jdk.JdkReflector;

public abstract class Db4oConnectionSpec {

    private static Logger logger = Logger.getLogger(Db4oConnectionSpec.class.getName());
    
    // Global temporary placeholder for read only setting.
    // TODO: Move to preferences when starting to work on editing.
    private static boolean PREFERENCE_IS_READ_ONLY = false;
    
    public static boolean preferenceIsReadOnly() {
    	return PREFERENCE_IS_READ_ONLY;
    }
    
	private boolean readOnly;
	
	protected Db4oConnectionSpec(boolean readOnly) {
		this.readOnly=readOnly;
	}
	
	public ObjectContainer connect() {
        int activationDepth = ActivationPreferences.getDefault().getInitialActivationDepth();
        String[] classpath = ClasspathPreferences.getDefault().classPath();
		Db4o.configure().readOnly(readOnly);
		Db4o.configure().activationDepth(activationDepth);
		Reflector reflector = createReflector(classpath);
		Db4o.configure().reflectWith(reflector);
		
		String[] callConstructorClasses = ConstructorPreferences.getDefault().classes();
		for (int i = 0; i < callConstructorClasses.length; i++) {
			try {
				Class clazz = Class.forName(callConstructorClasses[i]);
				Db4o.configure().objectClass(clazz).callConstructor(true);
			} catch (ClassNotFoundException e) {
				ReflectClass clazz = reflector.forName(callConstructorClasses[i]);
				if (clazz == null) {
					logger.warning(
						"Warning: could not resolve class "
								+ callConstructorClasses[i]
								+ " when specifying classes whose constructors should be called");
					continue;
				}
				Db4o.configure().objectClass(clazz).callConstructor(true);
			}
		}

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
			logger.warning("Could not find classpath entry: "+filePath);
			return null;
		}
		try {
			return file.toURL();
		} catch (MalformedURLException exc) {
			logger.log(Level.SEVERE, "Could not convert classpath entry to URL: "+file.getAbsolutePath(), exc);
			return null;
		}
	}
	
	public abstract String shortPath();
	public abstract String getPath();
	protected abstract ObjectContainer connectInternal();
}
