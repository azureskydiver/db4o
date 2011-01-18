/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */

package com.db4o.omplus.ui.dialog.login.model;

import java.io.*;
import java.util.*;

import com.db4o.omplus.connection.*;

public interface ConfiguratorExtractor {
	List<String> configuratorClassNames(List<File> jarFiles) throws DBConnectException;
	boolean acceptJarFile(File file);
}
