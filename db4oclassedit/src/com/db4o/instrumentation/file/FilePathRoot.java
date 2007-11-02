package com.db4o.instrumentation.file;

import java.io.*;
import java.util.*;

/**
 * @exclude
 */
public interface FilePathRoot {
	String[] rootDirs() throws IOException;
	Iterator files();
}
