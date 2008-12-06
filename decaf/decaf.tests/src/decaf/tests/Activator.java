package decaf.tests;

import java.io.*;
import java.net.*;

import org.eclipse.core.runtime.*;
import org.osgi.framework.*;

public class Activator extends Plugin {
	
	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public static String getResource(String path) throws IOException {
		URL url = FileLocator.find(getDefault().getBundle(), new Path(path), null);
		return new File(FileLocator.toFileURL(url).getFile()).getCanonicalPath();
	}
}
