package com.db4o.instrumentation.file;

import java.util.*;

public interface FilePathRoot {
	String[] rootDirs();
	Iterator files();
}
