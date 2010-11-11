package com.db4o.util.eclipse.parser.impl;

import java.util.*;

import com.db4o.util.eclipse.parser.*;
import com.db4o.util.file.*;

public final class WorkspaceImp implements Workspace {

	private final IFile root;
	private Map<String, ProjectImpl> projects = new HashMap<String, ProjectImpl>();

	public WorkspaceImp(IFile root) {
		this.root = root;
	}

	public IFile root() {
		return root;
	}

	public ProjectImpl project(IFile path) {
		String name = stripSlashes(path.name());
		ProjectImpl p = projects.get(name);
		if (p == null) {
			p = new ProjectImpl(this, path);
			projects.put(name, p);
		}
		return p;
	}

	public ProjectImpl project(String name) {
		return project(root.file(name));
	}
	
	private String stripSlashes(String path) {
		return path.replaceAll("/", "");
	}
}