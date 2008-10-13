package decaf.tests.builder;

import org.eclipse.core.resources.*;
import org.junit.*;

import sharpen.core.*;
import sharpen.core.framework.resources.*;
import decaf.core.*;
import decaf.tests.*;

public class DecafProjectBuilderTestCase extends DecafTestCaseBase {
	
	@Test
	public void testDecafProjectReferencesGetMapped() throws Exception {
		
		final JavaProject main = javaProject();
		main.addNature(DecafNature.NATURE_ID);
		final DecafTestResource dependencyResource = testResourceFor("Dependency", TargetPlatform.NONE);
		createCompilationUnitIn(main, dependencyResource);
		main.buildProject(null);
		
		final JavaProject dependent = new JavaProject("Dependent");
		try {
			dependent.addReferencedProject(main.getProject(), null);
			dependent.addNature(DecafNature.NATURE_ID);
			final DecafTestResource dependentResource = testResourceFor("Dependent", TargetPlatform.NONE);
			createCompilationUnitIn(dependent, dependentResource);
			dependent.buildProject(null);
			
			final IProject decafDependent = targetDecafProjectFor(dependent, TargetPlatform.JDK11);
			final IProject decafMain = targetDecafProjectFor(main, TargetPlatform.JDK11);
			Assert.assertArrayEquals(new Object[] { decafMain }, decafDependent.getReferencedProjects());
		} finally {
			dependent.dispose();
		}
	}
	

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
		
		final IProject decaf = targetDecafProjectFor(project, TargetPlatform.JDK11);
		assertTrue(decaf.exists());
		
		final IFolder decafResources = decaf.getFolder("resources");
		assertFalse(decafResources.exists());
		
		testResource1.assertFile(decaf.getFile("src/decaf/builder/Foo.java"));
		testResource2.assertFile(decaf.getFile("src2/decaf/builder/FooImpl.java"));
	}


	private IProject targetDecafProjectFor(final JavaProject project, final TargetPlatform targetPlatform) {
	    return WorkspaceUtilities.getProject(targetPlatform.appendPlatformId(project.getName() + ".decaf", "."));
    }
	
	@Override
	protected String packagePath() {
		return "builder";
	}

}
