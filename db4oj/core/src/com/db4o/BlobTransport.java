/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o;

import java.io.*;

import com.db4o.internal.*;


/**
 * @exclude
 * 
 * @sharpen.if !SILVERLIGHT
 */
public interface BlobTransport {

	void writeBlobTo(Transaction trans, BlobImpl blob) throws IOException;

	void readBlobFrom(Transaction trans, BlobImpl blob) throws IOException;

	void deleteBlobFile(Transaction trans, BlobImpl blob);
	
}