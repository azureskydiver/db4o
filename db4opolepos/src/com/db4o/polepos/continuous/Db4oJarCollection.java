/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */

package com.db4o.polepos.continuous;

import java.io.*;
import java.util.*;

public class Db4oJarCollection {
	private final File _currentJar;
	private final Set<File> _otherJars;
	
	public Db4oJarCollection(File[] jars) {
		_currentJar = jars[0];
		_otherJars = new HashSet<File>();
		for (int jarIdx = 1; jarIdx < jars.length; jarIdx++) {
			_otherJars.add(jars[jarIdx]);
		}
	}
	
	public Set<File> otherJars() {
		return Collections.unmodifiableSet(_otherJars);
	}
	
	public File currentJar() {
		return _currentJar;
	}
}
