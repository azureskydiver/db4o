package com.db4o.cs.server.protocol;

import java.io.*;

/**
 * User: treeder
 * Date: Oct 30, 2006
 * Time: 11:56:27 PM
 */
public interface Protocol {
	
	/**
	 * The main handler method for a protocol.
	 *
	 * @param in
	 * @param out
	 */
	void handle(InputStream in, OutputStream out) throws IOException;
}
