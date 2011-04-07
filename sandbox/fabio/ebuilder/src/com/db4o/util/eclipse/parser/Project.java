package com.db4o.util.eclipse.parser;

import com.db4o.util.file.*;


public interface Project {

	String name();

	void accept(ProjectVisitor visitor);

	void accept(ProjectVisitor visitor, int visitorOptions);

	String getRelativePathToRoot(IFile file);

}
