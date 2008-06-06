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

	protected void runResourceTestCase(String resourceName) throws Exception {
		DecafTestResource resource = new DecafTestResource("decaf/" + resourceName);
		ICompilationUnit cu = createCompilationUnit(resource);
	
		IFile decafFile = decafFileFor(cu.getResource());
	
		FileRewriter.rewriteFile(DecafRewriter.rewrite(cu, null), decafFile.getFullPath());
	
		resource.assertFile(decafFile);
	}

	private IFile decafFileFor(IResource originalFile) throws CoreException {
		IFolder targetFolder = _project.createFolder("decaf");
		IFile actualFile = targetFolder.getFile("decaf.txt");
		originalFile.copy(actualFile.getFullPath(), true, null);
		return actualFile;
	}

	private ICompilationUnit createCompilationUnit(DecafTestResource resource) throws CoreException,
			IOException {
				return _project.createCompilationUnit(resource.packageName(), resource.javaFileName(), resource.actualStringContents());
			}

}