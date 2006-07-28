/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.main;

import java.lang.reflect.*;

public class Db4oRunner {
	public static void main(String[] args) throws Throwable {
		ClassLoader loader=new Db4oEnhancingClassloader(Thread.currentThread().getContextClassLoader());
		Thread.currentThread().setContextClassLoader(loader);
		Class mainClass=loader.loadClass(args[0]);
		Method mainMethod=mainClass.getMethod("main",new Class[]{String[].class});
		String[] delegateArgs=new String[args.length-1];
		System.arraycopy(args,1,delegateArgs,0,delegateArgs.length);
		mainMethod.invoke(null,new Object[]{delegateArgs});
	}
}
