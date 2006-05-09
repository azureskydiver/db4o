package db4otesteclipse;

import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.launching.*;

public class Db4oRegressionTestLaunchConfiguration extends AbstractJavaLaunchConfigurationDelegate {

	public void handleDebugEvents(DebugEvent[] events) {
		// TODO Auto-generated method stub
		
	}

	public boolean buildForLaunch(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean finalLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean preLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	private IType[] findTestTypes(ILaunchConfiguration configuration, IProgressMonitor pm) throws CoreException {
		IJavaProject javaProject= getJavaProject(configuration);
		if ((javaProject == null) || !javaProject.exists()) {
			abort("No Java project.", null, IJavaLaunchConfigurationConstants.ERR_NOT_A_JAVA_PROJECT); 
		}
		if (!hasTestType(javaProject)) {
			abort("Db4o Test main type not found for project.", null, IJavaLaunchConfigurationConstants.ERR_UNSPECIFIED_MAIN_TYPE);
		}
//		final ITestSearchExtent testTarget= testSearchTarget(configuration, javaProject, pm);
//		TestSearchResult searchResult= TestKindRegistry.getDefault().getTestTypes(configuration, testTarget);
//		if (searchResult.isEmpty()) {
//			abort(JUnitMessages.JUnitBaseLaunchConfiguration_error_notests, null, IJavaLaunchConfigurationConstants.ERR_UNSPECIFIED_MAIN_TYPE); 
//		}
//		return searchResult;
		return null;
	}

	private static boolean hasTestType(IJavaProject javaProject) {
		try {
			return javaProject.findType("com.db4o.test.Test")!=null;
		} catch (JavaModelException e) {
			Activator.log(e.getStatus());
			return false;
		}
	}
}
