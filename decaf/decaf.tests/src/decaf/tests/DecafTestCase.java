package decaf.tests;

import java.io.*;

import javatocsharp.core.*;
import junit.framework.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;

import decaf.builder.*;

public class DecafTestCase extends TestCase {
	
	private JavaProject _project;

	@Override
	protected void setUp() throws Exception {
		_project = new JavaProject();
	}
	
	@Override
	protected void tearDown() throws Exception {
		_project.dispose();
	}
	
	public void testForEachArray() throws Exception {
		runResourceTestCase("ForEachArray");
	}
	
	public void testForEachArrayMethod() throws Exception {
		runResourceTestCase("ForEachArrayMethod");
	}

	private void runResourceTestCase(String resourceName) throws Exception {
		DecafTestResource resource = new DecafTestResource(resourceName);
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

	private ICompilationUnit createCompilationUnit(DecafTestResource resource)
			throws CoreException, IOException {
		return _project.createCompilationUnit(resource.packageName(), resource.javaFileName(), resource.actualStringContents());
	}

}
