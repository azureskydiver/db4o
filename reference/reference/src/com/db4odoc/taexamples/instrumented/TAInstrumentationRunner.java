/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4odoc.taexamples.instrumented;

import java.io.File;
import java.net.URL;

import com.db4o.instrumentation.classfilter.ByNameClassFilter;
import com.db4o.instrumentation.core.BloatClassEdit;
import com.db4o.instrumentation.core.ClassFilter;
import com.db4o.instrumentation.main.Db4oInstrumentationLauncher;
import com.db4o.ta.instrumentation.InjectTransparentActivationEdit;


public class TAInstrumentationRunner {

	public static void main(String[] args) throws Exception {
		// list the classes that need to be instrumented
		ClassFilter filter = new ByNameClassFilter(new String[] { SensorPanel.class.getName() });
		// inject TA awareness
		BloatClassEdit edits[] = new BloatClassEdit[]{new InjectTransparentActivationEdit(filter)};
		// get URL for the classloader
		URL[] urls = { new File("e:\\sb4o\\trunk\\reference\\bin").toURI().toURL() };
		Db4oInstrumentationLauncher.launch(edits, urls, TAInstrumentationExample.class.getName(), new String[]{});

	}
	// end main
	
}
