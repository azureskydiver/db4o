package decaf.tests.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.junit.Test;

import sharpen.core.JavaProject;
import sharpen.core.framework.resources.WorkspaceUtilities;
import decaf.core.DecafNature;
import decaf.core.TargetPlatform;
import decaf.tests.DecafTestCaseBase;
import decaf.tests.DecafTestResource;

public class DecafProjectBuilderTestCase extends DecafTestCaseBase {
	

	@Test
	public void testDecafOnlySourceFolders() throws Exception {
		final JavaProject project = javaProject();
		
		final IFolder resourcesFolder = project.createFolder("resources");
		final IFile ignoredFile = resourcesFolder.getFile("Resource.java");
		WorkspaceUtilities.writeText(ignoredFile, testResourceFor("Resource", TargetPlatform.NONE).actualStringContents());
		
		final DecafTestResource testResource1 = testResourceFor("Foo", TargetPlatform.NONE);
		createCompilationUnit(testResource1);
		
		final DecafTestResource testResource2 = testResourceFor("FooImpl", TargetPlatform.NONE);
		createCompilationUnit(project.addSourceFolder("src2"), testResource2);
		
		project.addNature(DecafNature.NATURE_ID);
		project.buildProject(null);
		
		final IProject decaf = WorkspaceUtilities.getProject(TargetPlatform.JDK11.appendPlatformId(project.getName() + ".decaf", "."));
		assertTrue(decaf.exists());
		
		final IFolder decafResources = decaf.getFolder("resources");
		assertFalse(decafResources.exists());
		
		testResource1.assertFile(decaf.getFile("src/decaf/builder/Foo.java"));
		testResource2.assertFile(decaf.getFile("src2/decaf/builder/FooImpl.java"));
	}
	
	@Override
	protected String packagePath() {
		return "builder";
	}

}
