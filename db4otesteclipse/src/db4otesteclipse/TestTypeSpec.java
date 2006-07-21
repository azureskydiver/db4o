package db4otesteclipse;

import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.*;
import org.eclipse.jdt.core.*;

public interface TestTypeSpec {
	void configureSpecific(String typeList, ILaunchConfigurationWorkingCopy workingCopy);
	boolean acceptTestType(IType type) throws CoreException;
}
