package com.db4o.util.eclipse.parser;


public interface Project {

	String name();

	void accept(ProjectVisitor visitor);

	void accept(ProjectVisitor visitor, int visitorOptions);

}
