package com.db4o.io.jdkdefault;

import java.io.IOException;

import com.db4o.io.ObjectFile;
import com.db4o.io.ObjectFileFactory;

public class RandomAccessObjectFileFactory implements ObjectFileFactory {
	public ObjectFile getFile(String filename) throws IOException {
		return new RandomAccessObjectFile(filename);
	}

	public ObjectFile getFile(String filename,long initlength) throws IOException {
		return new RandomAccessObjectFile(filename,initlength);
	}
}
