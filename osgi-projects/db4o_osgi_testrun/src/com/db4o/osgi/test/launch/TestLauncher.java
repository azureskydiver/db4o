package com.db4o.osgi.test.launch;

import java.io.*;
import java.lang.reflect.*;

import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.osgi.framework.*;

public class TestLauncher {

	private static final String RUNMETHODNAME = "runTests";
	private static final String SERVICENAME = "com.db4o.osgi.test.Db4oTestService";
	private static final String FILENAME = "osgi_test.db4o";

	public static void main(String[] args) throws Exception {
		String[] installBundleList = {
				"db4o_osgi_1.0.0.jar",
				"db4o_osgi_test_1.0.0.jar",
		};
		BundleContext context = setUp(installBundleList, true);
		try {
			int numFailures = runTests(context);
			System.out.println("FAILURES: " + numFailures);
			System.exit(numFailures);
		}
		finally {
			tearDown();
		}
	}

	public static BundleContext setUp(String[] installBundlePathList, boolean debug) throws Exception {

		System.setProperty("osgi.clean", "true");
		System.setProperty("osgi.parentClassloader", "app");

		EclipseStarter.debug = debug;
		if(EclipseStarter.isRunning()) {
			throw new IllegalStateException("Application already running.");
		}

		BundleContext context = EclipseStarter.startup(new String[] {}, null);
		if(!EclipseStarter.isRunning()) {
			throw new IllegalStateException("Could not start application.");
		}

		System.out.println("STARTING");
		for (int bundlePathIdx = 0; bundlePathIdx < installBundlePathList.length; bundlePathIdx++) {
			String bundlePath = "plugins/" + installBundlePathList[bundlePathIdx];
			String bundleURL = new File(bundlePath).toURI().toURL().toString();
			Bundle bundle = context.installBundle(bundleURL);
			bundle.start();
		}
		return context;
	}

	private static int runTests(BundleContext context)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		ServiceReference sRef = context.getServiceReference(SERVICENAME);
		System.out.println("REFERENCE is " + sRef);
	    Object dbs = context.getService(sRef);
		System.out.println("SERVICE is " + dbs);
		Method runMethod = dbs.getClass().getMethod(RUNMETHODNAME, new Class[]{String.class});
		runMethod.setAccessible(true);
		Integer numFailures = (Integer) runMethod.invoke(dbs, new Object[]{FILENAME});
		return numFailures.intValue();
	}

	public static void tearDown() throws Exception {
		// stop eclipse
		EclipseStarter.shutdown();
		if(EclipseStarter.isRunning()) {
			throw new IllegalStateException("Could not stop application.");
		}
	}

}
