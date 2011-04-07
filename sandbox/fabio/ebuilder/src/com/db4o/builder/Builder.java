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
		final IFile mainClasses = output.file("classes");
		final IFile testClasses = output.file("test-classes");

		IFile root = findWorkspace(".");
		Workspace w = new EclipseWorkspace(root);
		
		w.addProjectRoot(root.file("../polepos"));
		w.importUserLibrary(new RealFile("versant.userlibraries"));
		
		List<Project> coreProjects = new ArrayList<Project>();
		
		coreProjects.add(w.project("db4oj"));
		coreProjects.add(w.project("db4o.cs"));
		coreProjects.add(w.project("db4o.cs.optional"));
		coreProjects.add(w.project("db4ounit"));
		coreProjects.add(w.project("db4o.instrumentation"));
		coreProjects.add(w.project("db4onqopt"));
		coreProjects.add(w.project("db4oj.optional"));
		coreProjects.add(w.project("db4otaj"));
		coreProjects.add(w.project("db4otools"));
		

		Set<Project> buildList = buildListFor(coreProjects);
		
		final Set<Project> testsBuildList = new LinkedHashSet<Project>();
		
		for(final Project project : buildList) {
			
			project.accept(new ProjectBuilderVisitor() {
				
				@Override
				protected IFile resolveProjectOutputDir(Project project, IFile dir) {
					return mainClasses.file(project.name());
				}
				
				@Override
				public void visitEnd() {
					System.out.println("building " + project.name());
					super.visitEnd();
				}
				
				@Override
				public void visitSourceFolder(IFile dir) {
					if ("test".equals(dir.name()) || "test".equals(dir.parent().name()) || "tutorial".equals(dir.name()) || "tutorial".equals(dir.parent().name())) {
						testsBuildList.add(project);
						return;
					}
					super.visitSourceFolder(dir);
				}
			});
		}
		
		System.out.println("-----");
		
		for(final Project project : testsBuildList) {
			
			project.accept(new ProjectBuilderVisitor() {
				
				@Override
				protected IFile resolveProjectOutputDir(Project p, IFile dir) {
					return p == project ? testClasses.file(p.name()) : mainClasses.file(p.name());
				}
				
				@Override
				public void visitEnd() {
					System.out.println("building tests for " + project.name());
					super.visitEnd();
				}
				
				@Override
				public void visitSourceFolder(IFile dir) {
					if ("test".equals(dir.name()) || "test".equals(dir.parent().name()) || "tutorial".equals(dir.name()) || "tutorial".equals(dir.parent().name())) {
						super.visitSourceFolder(dir);
					} else {
						addClasspathEntry(mainClasses.file(project.name()));
					}
				}
			});
		}

		System.out.println("-----");
		
		for(Project project : coreProjects) {
			String projectName = project.name();
			System.out.println("creating "+projectName+".jar");
			createJar(mainClasses.file(projectName), output.file(projectName+".jar"));
		}

//		
//		
//		Manifest manifest = new Manifest();
//		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
//
//		final JarOutputStream jout = new JarOutputStream(new FileOutputStream("drs-all.jar"), manifest);
//		
//		testsProject.accept(new ProjectVisitorAdapter (){
//			
//			Set<Project> knownProjects = new HashSet<Project>();
//			
//			@Override
//			public void visitOutputFolder(IFile dir) {
//				dir.accept(new JarFileCollector(jout));
//			}
//			@Override
//			public void visitExternalProject(Project project) {
//				if (knownProjects.add(project)) {
//					project.accept(this);
//				}
//			}
//		});
//		
//		jout.flush();
//		jout.close();
	}

	private static Set<Project> buildListFor(List<Project> coreProjects) {
		Set<Project> buildList = new LinkedHashSet<Project>();
		
		for(Project p : coreProjects) {
			p.accept(new DependencyCollectorVisitor(buildList));
		}
		return buildList;
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
