/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers.detail.generator;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

/**
 * StringInputStream.  A class that converts a java.lang.String into a java.io.InputStream.
 *
 * @author djo
 */
public class StringInputStreamFactory {
	public static InputStream construct(final String rawMaterial) {
		InputStream result;
		final PrintStream output;
		
		// Make a stream for writing output
		PipedOutputStream intermediate;

		intermediate = new PipedOutputStream();
		output = new PrintStream(intermediate);
		try {
			result = new PipedInputStream(intermediate);
		} catch (IOException e) {
			throw new RuntimeException("Unable to open stream", e);
		}
		
		Thread printer = new Thread() {
			public void run() {
				output.print(rawMaterial);
				output.close();
			}
		};
		printer.start();
		
		return result;
	}

}
