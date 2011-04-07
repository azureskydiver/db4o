package com.db4o.util.eclipse.parser.impl;

import java.io.*;
import java.util.*;

import com.db4o.builder.*;
import com.db4o.builder.Compiler;
import com.db4o.util.eclipse.parser.*;
import com.db4o.util.file.*;

public class ProjectBuilderVisitor extends ProjectVisitorAdapter {
	private Set<IFile> knownSources = new LinkedHashSet<IFile>();
	private Set<Pair<IFile,String>> knownResources = new LinkedHashSet<Pair<IFile,String>>();
	private Set<IFile> classpath = new LinkedHashSet<IFile>();
	private Compiler ecj = new EclipseCompiler();
	private IFile target;
	private Project project;

	public ProjectBuilderVisitor() {
		ecj.sourceVersion(Compiler.Version.Java16);
		ecj.targetVersion(Compiler.Version.Java16);
		ecj.debugEnabled();
		ecj.outputWriter(new PrintWriter(System.out));
		ecj.errorWriter(new PrintWriter(System.err));
	}
	
	@Override
	public void visit(Project project, String name) {
		this.project = project;
	}

	@Override
	public void visitSourceFolder(final IFile dir) {
		dir.accept(new FileVisitor() {
			
			@Override
			public void visit(IFile child) {
				if (child.isFile()) {
					if (child.name().toLowerCase().endsWith(".java")) {
						if (knownSources.add(child)) {
							ecj.addSourceFile(child);
						}
					} else {
						knownResources.add(Pair.of(child, child.getRelativePathTo(dir)));
					}
				} else if (child.isDirectory()) {
					child.accept(this);
				}
			}
		});
	}

	@Override
	public void visitArchive(IFile jar) {
		addClasspathEntry(jar);
	}

	@Override
	public void visitExternalProject(final Project project) {
		project.accept(new ProjectVisitorAdapter() {
			@Override
			public void visitArchive(IFile jar) {
				addClasspathEntry(jar);
			}
			
			@Override
			public void visitOutputFolder(IFile dir) {
				dir = resolveProjectOutputDir(project, dir);
				addClasspathEntry(dir);
			}
			
			@Override
			public void visitExternalProject(Project project) {
				ProjectBuilderVisitor.this.visitExternalProject(project);
//				project.accept(this);
			}
		});
	}

	@Override
	public void visitEnd() {
		for(Pair<IFile, String> r : knownResources) {
			try {
				OutputStream out = target.file(r.second).openOutputStream(false);
				r.first.copyTo(out);
				out.flush();
				out.close();
			} catch (IOException e) {
				throw new java.lang.RuntimeException(e);
			}
		}
		if (!ecj.compile()) {
			throw new IllegalStateException("Compilation error");
		}
	}

	@Override
	public void visitOutputFolder(IFile dir) {
		dir = resolveProjectOutputDir(project, dir);
		target = dir;
		dir.mkdir();
		ecj.targetFolder(dir.getAbsolutePath());
	}

	protected IFile resolveProjectOutputDir(Project project, IFile dir) {
		return dir;
	}

	public void addClasspathEntry(IFile jar) {
		if (classpath.add(jar)) {
			ecj.addClasspathEntry(jar);
		}
	}
}