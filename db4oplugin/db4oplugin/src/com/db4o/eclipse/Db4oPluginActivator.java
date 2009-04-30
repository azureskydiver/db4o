package com.db4o.eclipse;

import org.eclipse.jface.resource.*;
import org.eclipse.ui.plugin.*;
import org.osgi.framework.*;

public class Db4oPluginActivator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "db4oplugin";

	private static Db4oPluginActivator plugin;

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
		
	public static Db4oPluginActivator getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
}
