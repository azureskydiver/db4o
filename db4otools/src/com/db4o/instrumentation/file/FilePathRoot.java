package com.db4o.instrumentation.file;

import java.util.*;

/**
 * @exclude
 */
public interface FilePathRoot {
	String[] rootDirs();
	Iterator files();
}
