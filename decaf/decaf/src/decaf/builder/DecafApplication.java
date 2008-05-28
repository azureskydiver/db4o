package decaf.builder;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.equinox.app.*;

import sharpen.core.*;
import sharpen.core.framework.*;

public class DecafApplication implements IApplication {

	public Object start(IApplicationContext context) throws Exception {
		final String[] args = argv(context);
		final String projectName = args[0];
		try {
			disableAutoBuilding();
			
			final ConsoleProgressMonitor monitor = new ConsoleProgressMonitor();
			
			final JavaProject project = new JavaProject(projectName);
			project.addSourceFolder("src");
			project.addNature(DecafNature.NATURE_ID);
			project.buildProject(monitor);
			
			decafProjectFor(projectName).build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
		} catch (CoreException x) {
			x.printStackTrace();
		}
		return IApplication.EXIT_OK;
	}

	private IProject decafProjectFor(final String projectName) {
		return WorkspaceUtilities.getProject(projectName + ".decaf");
	}

	private void disableAutoBuilding() throws CoreException {
		IWorkspace workspace = WorkspaceUtilities.getWorkspaceRoot().getWorkspace();
		IWorkspaceDescription workspaceDescription = workspace.getDescription();
		workspaceDescription.setAutoBuilding(false);
		workspace.setDescription(workspaceDescription);
	}

	public void stop() {
		// nothing to do
	}
	
	private String[] argv(IApplicationContext context) {
		return (String[])context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
	}
}
