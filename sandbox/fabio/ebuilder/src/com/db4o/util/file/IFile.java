package com.db4o.util.file;

import java.io.*;

public interface IFile {

	IFile file(String name);

	XMLParser xml();

	InputStream openInputStream();
	OutputStream openOutputStream(boolean append);

	String getAbsolutePath();

	String name();

	RandomAccessBuffer asBuffer();

}
