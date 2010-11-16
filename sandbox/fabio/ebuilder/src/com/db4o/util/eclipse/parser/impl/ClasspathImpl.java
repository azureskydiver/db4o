package com.db4o.util.eclipse.parser.impl;

import java.util.*;

import org.w3c.dom.*;

import com.db4o.util.eclipse.parser.*;
import com.db4o.util.file.*;

final class ClasspathImpl implements Classpath {
	private ArrayList<ClasspathEntry> entries;
	private final ProjectImpl project;

	public ClasspathImpl(ProjectImpl project) {
		this.project = project;
	}

	public ProjectImpl project() {
		return project;
	}

	public Collection<ClasspathEntry> entries() {
		if (entries != null) {
			return entries;
		}

		entries = new ArrayList<ClasspathEntry>();

		IFile cp = project().root().file(".classpath");

		Element root = cp.xml().root();
		
		NodeList list = root.getElementsByTagName("classpathentry");
		for(int i=0;i<list.getLength();i++) {
			Element entry = (Element) list.item(i);
			String kind = entry.getAttribute("kind");
			String name = entry.getAttribute("path");
			IFile path;
			if (name.startsWith("/")) {
				path = project().workspace().root().file(name);
			} else {
				path = project().root().file(name);
			}
			if ("src".equals(kind)) {
				if ("false".equals(entry.getAttribute("combineaccessrules"))) {
					entries.add(new ProjectClasspathEntry(this, path));
				} else {
					entries.add(new SourceClasspathEntry(this, path));
				}
			} else if ("lib".equals(kind)) {
				entries.add(new LibClasspathEntry(this, path));
			} else if ("output".equals(kind)) {
				project().setOutputDir(path);
			}
		}

		return entries;
	}
}