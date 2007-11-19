package com.db4odoc.nqoptimize;

import java.io.*;
import java.net.*;

import com.db4o.instrumentation.classfilter.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.main.*;
import com.db4o.nativequery.main.*;

/**
 * Starts NQExample with NQ optimization enhancement
 */
public class NQEnhancedStarter {

	public static void main(String[] args) throws Exception {
		// Create class filter to point to the predicates to be optimized
		ClassFilter filter = new ByNameClassFilter("com.db4odoc.nqoptimize.", true);
		// Create NQ optimization class edit
		BloatClassEdit[] edits = { new TranslateNQToSODAEdit()};
		URL[] urls = { new File("e:/db4o/main/reference/bin").toURI().toURL() };
		// launch the application using the class edit and the filter
		Db4oInstrumentationLauncher.launch(edits, urls, NQExample.class.getName(), new String[]{});
	}
	// end main

}
