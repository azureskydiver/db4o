/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.taexamples;

import com.db4o.types.Blob;

public class Image {
	Blob _blob;

	private String _fileName = null;

	public Image(String fileName) {
		_fileName = fileName;
	}

	// Image recording and reading functionality to be implemented ...
}
