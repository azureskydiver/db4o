package com.db4o.util.file;

import java.io.*;

public interface IFile {

	IFile file(String name);

	XMLParser xml();

	InputStream openInputStream();

	String getAbsolutePath();

	String name();

}
