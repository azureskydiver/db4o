package decaf.application;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.equinox.app.*;

import sharpen.core.*;
import sharpen.core.framework.*;
import sharpen.core.framework.resources.*;
import decaf.core.*;

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
				.persistentProperty(DecafProjectSettings.TARGET_PLATFORMS, commaSeparatedPlatformIds(commandLine.targetPlatforms))
				.project;
			
			project.buildProject(monitor);
			
			decafProjectFor(commandLine.project).build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
		} 
		catch (CoreException x) {
			IStatus[] children = x.getStatus().getChildren();
			for(IStatus child : children) {
				System.err.println(child);
				Throwable childExc = child.getException();
				if(childExc != null) {
					childExc.printStackTrace();
				}
			}
			x.printStackTrace();
			throw x;
		}
		catch (Exception x) {
			x.printStackTrace();
			throw x;
		}
		return IApplication.EXIT_OK;
	}

	private String commaSeparatedPlatformIds(List<TargetPlatform> targetPlatforms) {
		final StringBuilder value = new StringBuilder();
		for (TargetPlatform targetPlatform : targetPlatforms) {
			if (value.length() > 0) {
				value.append(",");
			}
			value.append(targetPlatform.toString());
		}
		return value.toString();
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
