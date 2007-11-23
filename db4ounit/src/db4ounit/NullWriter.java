/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package db4ounit;

import java.io.*;

/**
 * @exclude
 * @sharpen.ignore
 */
public class NullWriter extends Writer {

	public void close() throws IOException {
	}

	public void flush() throws IOException {
	}

	public void write(char[] cbuf, int off, int len) throws IOException {
	}

}
