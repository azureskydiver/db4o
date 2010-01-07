package com.db4o.enhance.test;

import java.io.*;

import org.apache.tools.ant.*;

import db4ounit.*;

public class Db4oEnhancerAntTaskTestCaseLauncher implements TestCase {

	private static final String BUILD_XML = "testscript/Db4oEnhancerAntTaskBuild.xml";

	public static void main(String[] args) {
		new ConsoleTestRunner(Db4oEnhancerAntTaskTestCaseLauncher.class).run();
	}
	
	public void test() throws Exception {
		File buildFile = new File(BUILD_XML);
		Project p = new Project();

		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_VERBOSE);
		p.addBuildListener(consoleLogger);

		try {
			p.fireBuildStarted();
			p.init();
			p.setUserProperty("ant.file", buildFile.getAbsolutePath());
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			p.addReference("ant.projectHelper", helper);
			helper.parse(p, buildFile);
			p.executeTarget(p.getDefaultTarget());
			p.fireBuildFinished(null);
		}
		catch(Exception exc) {
			p.fireBuildFinished(exc);
			throw exc;
		}
	}
	
}
