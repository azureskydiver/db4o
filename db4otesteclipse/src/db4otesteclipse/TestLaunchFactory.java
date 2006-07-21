package db4otesteclipse;

import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.launching.*;

public class TestLaunchFactory {
	
	private final static String LAUNCHCONFIG_ID="db4otesteclipse.launchconfig.type";
	private final static String TESTTYPES_KEY="db4otesteclipse.testtypes";

	private TestTypeSpec spec;
	
	public TestLaunchFactory(TestTypeSpec spec) {
		this.spec = spec;
	}

	public ILaunchConfiguration getLaunchConfig(List testTypes) throws CoreException {
		IJavaProject javaProject=containerForTypes(testTypes);
		ILaunchManager launchMgr=DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType launchType=launchMgr.getLaunchConfigurationType(LAUNCHCONFIG_ID);
		String name=nameForTestTypes(testTypes);
		ILaunchConfiguration config=findExistingLaunchConfig(launchMgr,launchType,testTypes,name);
		if(config!=null) {
			return config;
		}
		ILaunchConfigurationWorkingCopy workingCopy = createLaunchConfig(javaProject,launchType,name);
		configureLaunchConfig(javaProject, testTypes, workingCopy);
		try {
			return workingCopy.doSave();
		}
		catch(RuntimeException exc) {
			exc.printStackTrace();
			throw exc;
		}
	}

	private ILaunchConfiguration findExistingLaunchConfig(ILaunchManager launchMgr,ILaunchConfigurationType launchType,List testTypes,String name) throws CoreException {
		ILaunchConfiguration[] launchConfigs=launchMgr.getLaunchConfigurations(launchType);
		for (int launchConfigIdx = 0; launchConfigIdx < launchConfigs.length; launchConfigIdx++) {
			List curTestTypes=launchConfigs[launchConfigIdx].getAttribute(TESTTYPES_KEY, (List)null);
			if(launchConfigs[launchConfigIdx].getName().equals(name)) {
				if(namesForTestTypes(testTypes).equals(curTestTypes)) {
					return launchConfigs[launchConfigIdx];
				}
			}
		}
		return null;
	}

	private List namesForTestTypes(List testTypes) {
		List names=new ArrayList(testTypes.size());
		for (Iterator iter = testTypes.iterator(); iter.hasNext();) {
			names.add(((IType)iter.next()).getFullyQualifiedName());
		}
		return names;
	}

	private ILaunchConfigurationWorkingCopy createLaunchConfig(IJavaProject javaProject,ILaunchConfigurationType launchType,String name) throws CoreException {
		ILaunchConfigurationWorkingCopy workingCopy = launchType.newInstance(javaProject.getProject(),name);
		return workingCopy;
	}

	private void configureLaunchConfig(IJavaProject javaProject, List testTypes, ILaunchConfigurationWorkingCopy workingCopy) throws CoreException {
		spec.configureSpecific(parameterString(testTypes), workingCopy);
		IRuntimeClasspathEntry projectClasspath=JavaRuntime.newDefaultProjectClasspathEntry(javaProject);
		List classPath=new ArrayList();
		classPath.add(projectClasspath.getMemento());
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classPath);
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
		workingCopy.setAttribute(TESTTYPES_KEY, namesForTestTypes(testTypes));
	}
	
	private IJavaProject containerForTypes(List testTypes) {
		IJavaProject container=null;
		for (Iterator testTypeIter = testTypes.iterator(); testTypeIter.hasNext();) {
			IType type = (IType) testTypeIter.next();
			IJavaProject curContainer = type.getJavaProject();
			if(container!=null) {
				if(!container.equals(curContainer)) {
					return null;
				}
			}
			else {
				container=curContainer;
			}
		}
		return container;
	}
	
	private String parameterString(List testTypes) {
		StringBuffer buf = new StringBuffer();
		boolean firstRun = true;
		for (Iterator testTypeIter = testTypes.iterator(); testTypeIter
				.hasNext(); firstRun = false) {
			IType type = (IType) testTypeIter.next();
			if (!firstRun) {
				buf.append(' ');
			}
			buf.append(type.getFullyQualifiedName());
		}
		return buf.toString();
	}
	

	private String nameForTestTypes(List testTypes) {
		String name=((IType)testTypes.iterator().next()).getFullyQualifiedName();
		if(testTypes.size()>1) {
			name+=",...["+testTypes.size()+"]";
		}
		return name;
	}
}
