/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o;

import java.io.*;

import com.db4o.inside.*;


/**
 * @exclude
 */
public interface BlobTransport {

	public void writeBlobTo(Transaction trans, BlobImpl blob, File file) throws IOException;

	public void readBlobFrom(Transaction trans, BlobImpl blob, File file) throws IOException;

}