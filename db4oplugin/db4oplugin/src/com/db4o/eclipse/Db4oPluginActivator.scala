package com.db4o.eclipse;

import org.eclipse.jface.resource._;
import org.eclipse.ui.plugin._;
import org.osgi.framework._;

object Db4oPluginActivator {
	val PLUGIN_ID = "db4oplugin"

 	var plugin: Db4oPluginActivator = null

	def getDefault = plugin

	def getImageDescriptor(path: String) = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path)
}

class Db4oPluginActivator extends AbstractUIPlugin {

	override def start(context: BundleContext) {
		super.start(context);
		Db4oPluginActivator.plugin = this;
	}

	override def stop(context: BundleContext) {
		Db4oPluginActivator.plugin = null;
		super.stop(context);
	}
}
