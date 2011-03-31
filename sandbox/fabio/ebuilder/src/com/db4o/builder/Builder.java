package com.db4o.builder;

import java.io.*;
import java.util.*;
import java.util.jar.*;

import com.db4o.util.eclipse.parser.*;
import com.db4o.util.eclipse.parser.impl.*;
import com.db4o.util.file.*;

public class Builder {
	
	public static void main(String[] args) throws IOException {

		final IFile output = new RealFile("output");

		IFile root = findWorkspace(".");
		Workspace w = new EclipseWorkspace(root);
		
		w.addProjectRoot(root.file("../polepos"));
		w.importUserLibrary(new RealFile("versant.userlibraries"));
		
		Project testsProject = w.project("drs");

		Set<Project> buildList = new LinkedHashSet<Project>();
		
		testsProject.accept(new DependencyCollectorVisitor(buildList));
		
		for(final Project project : buildList) {
			
			project.accept(new ProjectBuilderVisitor() {
				
				@Override
				protected IFile resolveProjectOutputDir(Project project, IFile dir) {
					return output.file(project.name());
				}
				
				@Override
				public void visitEnd() {
					String projectName = project.name();
					System.out.println("building " + projectName);
					super.visitEnd();
					System.out.println("   creating jar");
					createJar(output.file(projectName), output.file(projectName+".jar"));
				}
			});
		}
		
	}

	private static void createJar(IFile src, IFile dest) {
		try {
			createJar(src, dest.openOutputStream(false));
		} catch (IOException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	private static void createJar(IFile dir, OutputStream out) throws IOException {
		Manifest manifest = new Manifest();
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

		JarOutputStream jout = new JarOutputStream(out, manifest);
		
		dir.accept(new JarFileCollector(jout));
		
		jout.flush();
		jout.close();
	}
	
	private static IFile findWorkspace(String fileName) {
		IFile file = new RealFile(fileName);
		while (file != null && !file.file(".metadata").exists()) {
			file = file.parent();
		}
		return file;
	}

//	public static void main2(String[] args) {
//
//		IFile root = searchWorkspace(new RealFile("."));
//
//		Workspace w = new EclipseWorkspace(root);
//
//		// w.addVariable("M2_REPO", new
//		// RealFile("/Users/raphaelferraz/devel/hoplon/repository"));
//		// w.addProjectRoot(root.file("../polepos"));
//		// w.importUserLibrary(new RealFile("versant.userlibraries"));
//
//		Compiler ecj = new EclipseCompiler();
//
//		ProjectVisitor compilerFeeder = new CompilerFeeder(ecj);
//
//		w.project("db4oj").accept(compilerFeeder);
//		w.project("db4oj.optional").accept(compilerFeeder);
//		w.project("db4o.cs").accept(compilerFeeder);
//		w.project("db4o.cs.optional").accept(compilerFeeder);
//		w.project("db4onqopt").accept(compilerFeeder);
//
//		ecj.sourceVersion(Compiler.Version.Java16);
//		ecj.targetVersion(Compiler.Version.Java16);
//		ecj.debugEnabled();
//		ecj.targetFolder("bla");
//		ecj.outputWriter(new PrintWriter(System.out));
//		ecj.errorWriter(new PrintWriter(System.err));
//
//		ecj.compile();
//	}

}
