package com.db4o.util.eclipse.parser.impl;

import com.db4o.util.eclipse.parser.*;
import com.db4o.util.file.*;

final class SourceClasspathEntry extends ClasspathEntryBase {
	public SourceClasspathEntry(ClasspathImpl cp, IFile path) {
		super(cp, path);
	}

	@Override
	public String toString() {
		return "src["+path()+"]";
	}

	public void accept(ClasspathVisitor visitor) {
		visitor.visit(classpath().project().getOutpuDir());
	}
}