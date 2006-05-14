package db4otesteclipse;

import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.*;
import org.eclipse.debug.ui.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.launching.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.ui.*;

public abstract class RunTestAction implements IObjectActionDelegate {
	private static final String TESTCLASS_NAME = "com.db4o.test.AllTests";
	private ISelection selection;
	private String mode;
	
	protected RunTestAction(String mode) {
		this.mode = mode;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
		try {
			List testTypes=collectTestTypes();
			if(testTypes.isEmpty()) {
				return;
			}
			IJavaProject container=containerForTypes(testTypes);
			if(container==null) {
				return;
			}
			ILaunchConfiguration config = getLaunchConfig(container,testTypes);
			DebugUITools.launch(config, ILaunchManager.RUN_MODE);
		} catch (CoreException exc) {
			Activator.log(new Status(Status.ERROR,Activator.PLUGIN_ID,0,"Could not run db4o regression test",exc));
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection=selection;
	}

	private String parameterString(List testTypes) {
		StringBuffer buf=new StringBuffer();
		boolean firstRun=true;
		for (Iterator testTypeIter = testTypes.iterator(); testTypeIter.hasNext();firstRun=false) {
			IType type = (IType) testTypeIter.next();
			if(!firstRun) {
				buf.append(' ');
			}
			buf.append(type.getFullyQualifiedName());
		}
		return buf.toString();
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

	private ILaunchConfiguration getLaunchConfig(IJavaProject javaProject,List testTypes) throws CoreException {
		ILaunchManager launchMgr=DebugPlugin.getDefault().getLaunchManager();
		//ILaunchConfigurationType launchType=launchMgr.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
		ILaunchConfigurationType launchType=launchMgr.getLaunchConfigurationType(Db4oRegressionTestLaunchConfiguration.LAUNCHCONFIG_ID);
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
			List curTestTypes=launchConfigs[launchConfigIdx].getAttribute(Db4oRegressionTestLaunchConfiguration.TESTTYPES_KEY, (List)null);
			if(launchConfigs[launchConfigIdx].getName().equals(name)) {
				if(namesForTestTypes(testTypes).equals(curTestTypes)) {
					return launchConfigs[launchConfigIdx];
				}
			}
		}
		return null;
	}
	
	private ILaunchConfigurationWorkingCopy createLaunchConfig(IJavaProject javaProject,ILaunchConfigurationType launchType,String name) throws CoreException {
		ILaunchConfigurationWorkingCopy workingCopy = launchType.newInstance(javaProject.getProject(),name);
		return workingCopy;
	}

	private void configureLaunchConfig(IJavaProject javaProject, List testTypes, ILaunchConfigurationWorkingCopy workingCopy) throws CoreException {
		String args = parameterString(testTypes);
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
				TESTCLASS_NAME);
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, 
				"-"+mode+" "+args);
		IRuntimeClasspathEntry projectClasspath=JavaRuntime.newDefaultProjectClasspathEntry(javaProject);
		List classPath=new ArrayList();
		classPath.add(projectClasspath.getMemento());
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classPath);
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
		workingCopy.setAttribute(Db4oRegressionTestLaunchConfiguration.TESTTYPES_KEY, namesForTestTypes(testTypes));
	}
	
	private List collectTestTypes() throws CoreException {
		Set testTypes=new HashSet();
		if(selection instanceof IStructuredSelection) {
			IStructuredSelection structured=(IStructuredSelection)selection;
			for(Iterator iter=structured.iterator();iter.hasNext();) {
				collectTestTypes(iter.next(),testTypes);
			}
		}
		List sorted=new ArrayList(testTypes);
		Collections.sort(sorted,new Comparator() {
			public int compare(Object a, Object b) {
				return ((IType)a).getFullyQualifiedName().compareTo(((IType)b).getFullyQualifiedName());
			}
		});
		return sorted;
	}
	
	private void collectTestTypes(Object selected,Set testTypes) throws CoreException {
		if(selected instanceof IType) {
			addTestType(testTypes,(IType)selected);
			return;
		}
		if(selected instanceof ICompilationUnit) {
			addTestTypes(testTypes, (ICompilationUnit)selected);
			return;
		}
		if(selected instanceof IPackageFragment) {
			addTestTypes(testTypes, (IPackageFragment)selected);
			return;
		}
	}

	private void addTestTypes(Set testTypes, IPackageFragment packageFrag) throws JavaModelException, CoreException {
		ICompilationUnit[] cus=packageFrag.getCompilationUnits();
		for (int cuIdx = 0; cuIdx < cus.length; cuIdx++) {
			addTestTypes(testTypes, cus[cuIdx]);
		}
	}

	private void addTestTypes(Set testTypes, ICompilationUnit cu) throws JavaModelException, CoreException {
		IType[] types=cu.getTypes();
		for (int typeIdx = 0; typeIdx < types.length; typeIdx++) {
			addTestType(testTypes, types[typeIdx]);
		}
	}

	private void addTestType(Set testTypes, IType type) throws CoreException {
		if(isTestType(type)) {
			testTypes.add(type);
		}
	}

	private boolean isTestType(IType type) throws CoreException {
		if(!type.exists()||!Flags.isPublic(type.getFlags())||Flags.isAbstract(type.getFlags())) {
			log("Not public or abstract: "+type.getFullyQualifiedName());
			return false;
		}
		IMethod[] methods=type.getMethods();
		boolean declaresDefaultConstructor=false;
		boolean declaresTestMethod=false;
		boolean declaresConstructor=false;
		for (int methodIdx = 0; methodIdx < methods.length; methodIdx++) {
			IMethod method = methods[methodIdx];
			if(method.getElementName().startsWith("test")&&Flags.isPublic(method.getFlags())) {
				declaresTestMethod=true;
			}
			if(method.getElementName().equals(type.getElementName())) {
				declaresConstructor=true;
				if(method.getParameterNames().length==0&&Flags.isPublic(method.getFlags())) {
					declaresDefaultConstructor=true;
				}
			}
		}
		if(!declaresTestMethod) {
			log("No testMethod: "+type.getFullyQualifiedName());
			return false;
		}
		if(declaresConstructor&&!declaresDefaultConstructor) {
			log("No default constructor: "+type.getFullyQualifiedName());
			return false;
		}
		return true;
	}
	
	private String nameForTestTypes(List testTypes) {
		String name=((IType)testTypes.iterator().next()).getFullyQualifiedName();
		if(testTypes.size()>1) {
			name+=",...["+testTypes.size()+"]";
		}
		name+=" "+mode.toUpperCase();
		return name;
	}

	private List namesForTestTypes(List testTypes) {
		List names=new ArrayList(testTypes.size());
		for (Iterator iter = testTypes.iterator(); iter.hasNext();) {
			names.add(((IType)iter.next()).getFullyQualifiedName());
		}
		return names;
	}
	
	private void log(String msg) {
		Activator.log(new Status(Status.INFO,Activator.PLUGIN_ID,0,msg,null));
	}
}
