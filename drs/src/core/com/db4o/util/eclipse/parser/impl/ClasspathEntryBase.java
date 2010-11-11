package com.db4o.util.eclipse.parser.impl;

import com.db4o.util.eclipse.parser.*;
import com.db4o.util.file.*;

public abstract class ClasspathEntryBase implements ClasspathEntry {

	private final ClasspathImpl cp;
	private final IFile path;

	public ClasspathEntryBase(ClasspathImpl cp, IFile path) {
		this.cp = cp;
		this.path = path;
	}
	
	public IFile path() {
		return path;
	}
	
	public ClasspathImpl classpath() {
		return cp;
	}
}