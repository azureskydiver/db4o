package com.db4o.util.eclipse.parser;

import com.db4o.util.file.*;

public interface ClasspathVisitor {
	
	void visit(IFile classpathEntry);

}
