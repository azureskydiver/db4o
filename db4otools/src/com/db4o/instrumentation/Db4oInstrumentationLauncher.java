/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.instrumentation;

import java.lang.reflect.*;
import java.net.*;

public class Db4oInstrumentationLauncher {

	public static void launch(BloatClassEdit[] edits, URL[] classPath, String mainClazzName, String[] args) throws Exception {
		ClassLoader parentLoader = Thread.currentThread().getContextClassLoader();
		BloatClassEdit compositeEdit = new CompositeBloatClassEdit(edits);
		ClassLoader loader=new BloatInstrumentingClassLoader(classPath, parentLoader, compositeEdit);
		Thread.currentThread().setContextClassLoader(loader);
		Class mainClass=loader.loadClass(mainClazzName);
		Method mainMethod=mainClass.getMethod("main",new Class[]{String[].class});
		mainMethod.invoke(null,new Object[]{args});
	}

}
