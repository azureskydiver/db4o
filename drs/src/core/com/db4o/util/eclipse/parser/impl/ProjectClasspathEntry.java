package com.db4o.util.eclipse.parser.impl;

import com.db4o.util.eclipse.parser.*;
import com.db4o.util.file.*;

final class ProjectClasspathEntry extends ClasspathEntryBase {
	private ProjectImpl project;

	public ProjectClasspathEntry(ClasspathImpl cp, IFile path) {
		super(cp, path);
	}

	@Override
	public String toString() {
		return "project["+path()+"]";
	}

	public void accept(ClasspathVisitor visitor) {
		project().accept(visitor);
	}

	private ProjectImpl project() {
		if (project == null) {
			project = classpath().project().workspace().project(path());
		}
		return project;
	}

}