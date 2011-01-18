/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */

package com.db4o.omplus.ui.dialog.login.model.test;

import java.io.*;
import java.util.*;

import org.junit.*;

import com.db4o.omplus.ui.dialog.login.model.LocalPresentationModel.LocalSelectionListener;
import com.db4o.omplus.ui.dialog.login.test.*;

public class LocalPresentationModelCustomConfigTestCase {
	
	private LoginPresentationModelFixture fixture;

	@Before
	public void setUp() {
		fixture = new LoginPresentationModelFixture();
	}
	
	@Test
	public void testAddJars() {
		CapturingCustomListener listener = new CapturingCustomListener();
		fixture.localModel().addListener(listener);
		fixture.localModel().addJarPaths("foo.jar", "bar.jar");
		Assert.assertEquals(2, listener.jarPathsReceived.length);
		Assert.assertEquals("bar.jar", new File(listener.jarPathsReceived[0]).getName());
		Assert.assertEquals("foo.jar", new File(listener.jarPathsReceived[1]).getName());
		Assert.assertEquals(0, listener.configClassNamesReceived.length);
		fixture.assertNoError();
	}

	@Test
	public void testMultipleAddJars() {
		CapturingCustomListener listener = new CapturingCustomListener();
		fixture.localModel().addListener(listener);
		fixture.localModel().addJarPaths("foo.jar");
		Assert.assertEquals(1, listener.jarPathsReceived.length);
		fixture.localModel().addJarPaths("bar.jar");
		Assert.assertEquals(2, listener.jarPathsReceived.length);
		Assert.assertEquals("bar.jar", new File(listener.jarPathsReceived[0]).getName());
		Assert.assertEquals("foo.jar", new File(listener.jarPathsReceived[1]).getName());
		Assert.assertEquals(0, listener.configClassNamesReceived.length);
		fixture.assertNoError();
	}

	@Test
	public void testAddJarsFailure() {
		CapturingCustomListener listener = new CapturingCustomListener();
		fixture.configNames(null);
		fixture.localModel().addListener(listener);
		fixture.localModel().addJarPaths("foo.jar", "bar.jar");
		Assert.assertEquals(0, listener.jarPathsReceived.length);
		Assert.assertEquals(0, listener.configClassNamesReceived.length);
		fixture.assertExceptionReceived();
	}

	@Test
	public void testRemoveJars() {
		CapturingCustomListener listener = new CapturingCustomListener();
		fixture.localModel().addListener(listener);
		fixture.localModel().addJarPaths("foo.jar", "bar.jar");
		fixture.localModel().removeJarPaths("bar.jar");
		Assert.assertEquals(1, listener.jarPathsReceived.length);
		Assert.assertEquals("foo.jar", new File(listener.jarPathsReceived[0]).getName());
		Assert.assertEquals(0, listener.configClassNamesReceived.length);
		fixture.assertNoError();
	}

	@Test
	public void testMultipleRemoveJars() {
		CapturingCustomListener listener = new CapturingCustomListener();
		fixture.localModel().addListener(listener);
		fixture.localModel().addJarPaths("foo.jar", "bar.jar");
		fixture.localModel().removeJarPaths("bar.jar");
		Assert.assertEquals(1, listener.jarPathsReceived.length);
		fixture.localModel().removeJarPaths("foo.jar");
		Assert.assertEquals(0, listener.jarPathsReceived.length);
		Assert.assertEquals(0, listener.configClassNamesReceived.length);
		fixture.assertNoError();
	}

	@Test
	public void testRemoveJarsFailure() {
		CapturingCustomListener listener = new CapturingCustomListener();
		fixture.localModel().addListener(listener);
		fixture.localModel().addJarPaths("foo.jar", "bar.jar");
		fixture.configNames(null);
		fixture.localModel().removeJarPaths("bar.jar");
		Assert.assertEquals(2, listener.jarPathsReceived.length);
		Assert.assertEquals(0, listener.configClassNamesReceived.length);
		fixture.assertExceptionReceived();
	}

	@Test
	public void testConfigClassNamePropagation() {
		CapturingCustomListener listener = new CapturingCustomListener();
		fixture.configNames(Arrays.asList("configurator"));
		fixture.localModel().addListener(listener);
		fixture.localModel().addJarPaths("foo.jar");
		Assert.assertEquals(1, listener.configClassNamesReceived.length);
		fixture.assertNoError();
	}

	@Test
	public void testConfigIsResetAfterStateChange() {
		CapturingCustomListener listener = new CapturingCustomListener();
		fixture.configNames(Arrays.asList("configurator"));
		fixture.localModel().addListener(listener);
		fixture.localModel().addJarPaths("foo.jar");
		fixture.localModel().state("bar.db4o", false);
		Assert.assertEquals(0, listener.jarPathsReceived.length);
		Assert.assertEquals(0, listener.configClassNamesReceived.length);
		fixture.assertNoError();
	}	
	
	private static class CapturingCustomListener implements LocalSelectionListener {
		String[] jarPathsReceived;
		String[] configClassNamesReceived;

		@Override
		public void localSelection(String path, boolean readOnly) {
		}
		
		@Override
		public void customConfig(String[] jarPaths, String[] configClassNames) {
			jarPathsReceived = jarPaths;
			configClassNamesReceived = configClassNames;
		}
	}
}
