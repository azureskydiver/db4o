package com.db4o.util.eclipse.parser.impl;

import com.db4o.util.eclipse.parser.*;
import com.db4o.util.file.*;

final class LibClasspathEntry extends ClasspathEntryBase {
	public LibClasspathEntry(ClasspathImpl cp, IFile path) {
		super(cp, path);
	}
	@Override
	public String toString() {
		return "lib["+path()+"]";
	}
	public void accept(ClasspathVisitor visitor) {
		visitor.visit(path());
	}

}