package decaf.application;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.equinox.app.*;

import decaf.builder.*;

import sharpen.core.*;
import sharpen.core.framework.*;
import sharpen.core.resources.WorkspaceUtilities;

public class DecafApplication implements IApplication {

	public Object start(IApplicationContext context) throws Exception {
		try {
			final DecafCommandLine commandLine = DecafCommandLineParser.parse(argv(context));
			
			disableAutoBuilding();
			
			final ConsoleProgressMonitor monitor = new ConsoleProgressMonitor();
			
			final JavaProject project = new JavaProject.Builder(monitor, commandLine.project)
				.sourceFolder("src")
				.nature(DecafNature.NATURE_ID)
				.projectReferences(commandLine.projectReferences)
				.classpath(commandLine.classpath)
				.project;
			project.buildProject(monitor);
			
			decafProjectFor(commandLine.project).build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
		} catch (Exception x) {
			x.printStackTrace();
			throw x;
		}
		return IApplication.EXIT_OK;
	}

	private IProject decafProjectFor(final String projectName) {
		return project(projectName + ".decaf");
	}

	private IProject project(final String name) {
		return WorkspaceUtilities.getProject(name);
	}

	private static void disableAutoBuilding() throws CoreException {
		WorkspaceUtilities.setAutoBuilding(false);
	}

	public void stop() {
		// nothing to do
	}
	
	private String[] argv(IApplicationContext context) {
		return (String[])context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
	}
}
