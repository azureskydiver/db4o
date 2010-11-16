package com.db4o.util.eclipse.parser;


public interface Project {

	Classpath classpath();

	void accept(ClasspathVisitor visitor);

}
