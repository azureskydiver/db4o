package decaf.tests;

import java.io.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;

import sharpen.core.*;
import decaf.builder.*;
import junit.framework.*;

public abstract class DecafTestCaseBase extends TestCase {

	private JavaProject _project;

	public DecafTestCaseBase() {
		super();
	}

	public DecafTestCaseBase(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		_project = new JavaProject();
	}

	@Override
	protected void tearDown() throws Exception {
		_project.dispose();
	}

	protected void runPlatformTestCase(String resourceName) throws Exception {
		for (TargetPlatform targetPlatform : TargetPlatform.values()) {
			if(targetPlatform == TargetPlatform.NONE) {
				continue;
			}
			runResourceTestCase(resourceName, targetPlatform);
		}
	}

	protected void runResourceTestCase(String resourceName) throws Exception {
		runResourceTestCase(resourceName, TargetPlatform.NONE);
	}
	
	protected void runResourceTestCase(String resourceName, TargetPlatform targetPlatform) throws Exception {
		DecafTestResource resource = new DecafTestResource(resourcePath(resourceName), targetPlatform);
		ICompilationUnit cu = createCompilationUnit(resource);
	
		IFile decafFile = decafFileFor(cu.getResource(), targetPlatform.fileIDPart());
	
		FileRewriter.rewriteFile(DecafRewriter.rewrite(cu, null, targetPlatform.config()), decafFile.getFullPath());
	
		resource.assertFile(decafFile);
	}

	private String resourcePath(String resourceName) {
		StringBuilder path = new StringBuilder("decaf/");
		if(packagePath() != null) {
			path.append(packagePath()).append('/');
		}
		path.append(resourceName);
		return path.toString();
	}

	protected String packagePath() {
		return null;
	}
	
	private IFile decafFileFor(IResource originalFile, String fileIDPart) throws CoreException {
		String decafFolder = "decaf";
		IFolder targetFolder = _project.getProject().getFolder(decafFolder);
		if(!targetFolder.exists()) {
			targetFolder = _project.createFolder(decafFolder);
		}
		if(fileIDPart != null) {
			targetFolder = _project.createFolder(decafFolder + "/" + fileIDPart);
		}
		IFile actualFile = targetFolder.getFile("decaf.txt");
		originalFile.copy(actualFile.getFullPath(), true, null);
		return actualFile;
	}

	private ICompilationUnit createCompilationUnit(DecafTestResource resource) throws CoreException, IOException {
				return _project.createCompilationUnit(resource.packageName(), resource.javaFileName(), resource.actualStringContents());
			}

}