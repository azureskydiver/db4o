package db4otesteclipse.unit;

import org.eclipse.debug.core.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.launching.*;

import db4otesteclipse.*;

public class UnitTestTypeSpec implements TestTypeSpec {
	private static final String TESTLAUNCHER_NAME = "db4ounit.UnitTestMain";
	private static final String TESTINTERFACE_NAME = "TestCase";
	
	public boolean acceptTestType(IType type) throws JavaModelException {
		if (!type.exists() || Flags.isAbstract(type.getFlags())) {
			Activator.log("Not existent or abstract: "
					+ type.getFullyQualifiedName());
			return false;
		}
		// TODO/FIXME: Must be searched recursively? Needs to check fully qualified name.
		String[] interfaceNames=type.getSuperInterfaceNames();
		for (int idx = 0; idx < interfaceNames.length; idx++) {
			if(interfaceNames[idx].endsWith(TESTINTERFACE_NAME)) {
				return true;
			}
		}
		return false;
	}

	public void configureSpecific(String typeList,
			ILaunchConfigurationWorkingCopy workingCopy) {
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
				TESTLAUNCHER_NAME);
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, typeList);
	}
}
