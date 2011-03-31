package com.db4o.util.eclipse.parser;

import com.db4o.util.file.*;

public interface ProjectVisitor {
	
	public static final int PROJECT = 1 << 0;
	public static final int CLASSPATH = 1 << 1;
	
	void visit(Project project, String name);
	
	void visitEnd();

	void visitArchive(IFile jar);

	void visitSourceFolder(IFile dir);

	void visitOutputFolder(IFile dir);

	void visitExternalProject(Project project);
	
	void visitUnresolvedDependency(String dependency);

}
