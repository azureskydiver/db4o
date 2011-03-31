package com.db4o.builder;

import java.io.*;
import java.util.jar.*;

import com.db4o.util.file.*;

public final class JarFileCollector implements FileVisitor {
	private final JarOutputStream jout;
	
	private String currentBase = "";

	public JarFileCollector(JarOutputStream jout) {
		this.jout = jout;
	}

	@Override
	public void visit(IFile child) {
		
		
		String relativePath = currentBase += "/" + child.name();
		
		if (child.isDirectory()) {
			if (!relativePath.isEmpty()) {
				if (!relativePath.endsWith("/")) {
					relativePath += "/";
				}
				JarEntry entry = new JarEntry(relativePath);
				entry.setTime(child.lastModified());
				try {
					jout.putNextEntry(entry);
					jout.closeEntry();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			String base = currentBase;
			currentBase += "/" + child.name();
			child.accept(this);
			currentBase = base;
			return;
		}


		try {
			JarEntry entry = new JarEntry(relativePath);
			entry.setTime(child.lastModified());
			jout.putNextEntry(entry);
			child.copyTo(jout);
			jout.closeEntry();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}