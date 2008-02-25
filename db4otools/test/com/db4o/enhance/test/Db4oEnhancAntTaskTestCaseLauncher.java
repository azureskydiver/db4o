package com.db4o.enhance.test;

import java.io.*;

import org.apache.tools.ant.*;

import db4ounit.*;

public class Db4oEnhancAntTaskTestCaseLauncher implements TestCase {

	private static final String BUILD_XML = "Db4oEnhancerAntTaskBuild.xml";

	public static void main(String[] args) {
		new ConsoleTestRunner(Db4oEnhancAntTaskTestCaseLauncher.class).run();
	}
	
	public void test() throws Exception {
		File buildFile = new File(BUILD_XML);
		System.out.println(buildFile.getAbsolutePath());
		Project p = new Project();
		p.setUserProperty("ant.file", buildFile.getAbsolutePath());
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildFile);
		p.executeTarget(p.getDefaultTarget());
	}
	
}
