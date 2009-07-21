/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.polepos.continuous;

import java.io.*;

public interface JarFileSelectionStrategy {

	Db4oJarCollection select(File[] files);
	
}
