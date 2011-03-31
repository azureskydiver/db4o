package com.db4o.builder;

import com.db4o.util.eclipse.parser.*;
import com.db4o.util.file.*;

public class ProjectVisitorAdapter implements ProjectVisitor {
	@Override
	public void visit(Project project, String name) {
	}

	@Override
	public void visitSourceFolder(IFile dir) {
	}

	@Override
	public void visitArchive(IFile jar) {
	}

	@Override
	public void visitExternalProject(Project project) {
	}

	@Override
	public void visitEnd() {
	}

	@Override
	public void visitUnresolvedDependency(String token) {
	}

	@Override
	public void visitOutputFolder(IFile dir) {
	}
}
