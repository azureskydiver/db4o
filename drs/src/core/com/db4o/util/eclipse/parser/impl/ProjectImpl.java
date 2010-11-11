package com.db4o.util.eclipse.parser.impl;

import com.db4o.util.eclipse.parser.*;
import com.db4o.util.file.*;

final class ProjectImpl implements Project {
	private final WorkspaceImp workspace;
	private final IFile root;
	private IFile outputDir;
	private ClasspathImpl classpath;

	public ProjectImpl(WorkspaceImp workspace, IFile root) {
		this.workspace = workspace;
		this.root = root;
	}

	public WorkspaceImp workspace() {
		return workspace;
	}

	public IFile getOutpuDir() {
		classpath().entries();
		return outputDir;
	}

	public void setOutputDir(IFile outputDir) {
		this.outputDir = outputDir;
	}

	public IFile root() {
		return root;
	}

	public Classpath classpath() {
		if (classpath == null) {
			classpath = new ClasspathImpl(this);
		}
		return classpath;
	}

	public void accept(ClasspathVisitor visitor) {
		for (ClasspathEntry entry : classpath().entries()) {
			entry.accept(visitor);
		}
	}
}