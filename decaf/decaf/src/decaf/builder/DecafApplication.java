package decaf.builder;

import org.eclipse.core.runtime.*;
import org.eclipse.equinox.app.*;

import sharpen.core.*;

public class DecafApplication implements IApplication {

	public Object start(IApplicationContext context) throws Exception {
		String[] args = argv(context);
		String projectName = args[0];
		try {
			JavaProject project = new JavaProject(projectName);
//			project.addNature(DecafNature.NATURE_ID);
			project.addSourceFolder("src");
			project.joinBuild();
		} catch (CoreException x) {
			x.printStackTrace();
		}
		return IApplication.EXIT_OK;
	}

	public void stop() {
		// TODO Auto-generated method stub

	}
	
	private String[] argv(IApplicationContext context) {
		return (String[])context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
	}

}
