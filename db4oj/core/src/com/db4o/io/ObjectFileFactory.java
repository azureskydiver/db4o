package com.db4o.io;

import java.io.IOException;

public interface ObjectFileFactory {
	ObjectFile getFile(String filename) throws IOException;
	ObjectFile getFile(String filename,long initlength) throws IOException;
}
