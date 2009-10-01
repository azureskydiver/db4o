/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */

package com.db4o.polepos.continuous;

import java.io.*;
import java.util.*;

public class Db4oJarCollection {
	private final File _currentJar;
	private final Set<File> _otherJars;
	
	public Db4oJarCollection(List<File> jars) {
		_currentJar = jars.get(0);
		_otherJars = new HashSet<File>(jars.subList(1, jars.size()));
	}
	
	public Set<File> otherJars() {
		return Collections.unmodifiableSet(_otherJars);
	}
	
	public File currentJar() {
		return _currentJar;
	}
}
