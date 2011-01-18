/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */

package com.db4o.omplus.ui.dialog.login.model.test;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.junit.*;

import com.db4o.omplus.*;
import com.db4o.omplus.connection.*;
import com.db4o.omplus.ui.dialog.login.model.*;
import com.db4o.omplus.ui.dialog.login.model.CustomConfigModel.CustomConfigListener;

public class CustomConfigModelTestCase {

	private CustomConfigModel model;

	CapturingCustomListener listener;

	private List<String> presetConfigNames = new ArrayList<String>();
	private boolean failExtraction = false;
	private boolean acceptJarFiles = true;
	
	private File[] jarFilesCommitted;
	private String[] configClassNamesCommitted;
	private Throwable exc;
	private String errMsg;
	
	
	@Before
	public void setUp() {
		CustomConfigSink sink = new CustomConfigSink() {
			@Override
			public void customConfig(File[] jarFiles, String[] configClassNames) {
				CustomConfigModelTestCase.this.jarFilesCommitted = jarFiles;
				CustomConfigModelTestCase.this.configClassNamesCommitted = configClassNames;
			}
		};
		ConfiguratorExtractor extractor = new ConfiguratorExtractor() {
			@Override
			public List<String> configuratorClassNames(List<File> jarFiles) throws DBConnectException {
				if(failExtraction) {
					throw new DBConnectException("");
				}
				return presetConfigNames;
			}
			
			@Override
			public boolean acceptJarFile(File file) {
				return acceptJarFiles;
			}
		};
		ErrorMessageSink errSink = new ErrorMessageSink() {
			public void showError(String msg) {
				CustomConfigModelTestCase.this.errMsg = msg;
			}
			
			public void logExc(Throwable exc) {
				CustomConfigModelTestCase.this.exc = exc;
			}
		};
		model = new CustomConfigModel(sink , extractor , new ErrorMessageHandler(errSink));
		
		listener = new CapturingCustomListener();
		model.addListener(listener);
	}
	
	@Test
	public void testAddJars() {
		String[] jarPaths = { "foo.jar", "bar.jar" };
		model.addJarPaths(jarPaths);
		assertJarFiles(jarPaths, listener.jarPathsReceived);
		assertNull(jarFilesCommitted);
		model.commit();
		assertJarFiles(jarPaths, jarFilesCommitted);
		assertNoError();
	}

	@Test
	public void testMultipleAddJars() {
		model.addJarPaths("foo.jar");
		Assert.assertEquals(1, listener.jarPathsReceived.length);
		model.addJarPaths("bar.jar");
		String[] jarPaths = { "foo.jar", "bar.jar" };
		assertJarFiles(jarPaths, listener.jarPathsReceived);
		model.commit();
		assertJarFiles(jarPaths, jarFilesCommitted);
		assertNoError();
	}

	@Test
	public void testAddJarsFailure() {
		acceptJarFiles = false;
		model.addJarPaths("foo.jar", "bar.jar");
		assertJarFiles(new String[0], listener.jarPathsReceived);
		assertError();
		model.commit();
		assertTrue(jarFilesCommitted.length == 0);
	}

	@Test
	public void testAddJarsFailureRetainsLastState() {
		model.addJarPaths("foo.jar");
		assertNoError();
		acceptJarFiles = false;
		model.addJarPaths("bar.jar");
		String[] jarPaths = new String[] { "foo.jar" };
		assertJarFiles(jarPaths, listener.jarPathsReceived);
		assertError();
		model.commit();
		assertJarFiles(jarPaths, jarFilesCommitted);
	}

	@Test
	public void testRemoveJars() {
		model.addJarPaths("foo.jar", "bar.jar");
		model.removeJarPaths("bar.jar");
		String[] jarPaths = new String[] { "foo.jar" };
		assertJarFiles(jarPaths, listener.jarPathsReceived);
		model.commit();
		assertJarFiles(jarPaths, jarFilesCommitted);
		assertNoError();
	}

	@Test
	public void testMultipleRemoveJars() {
		model.addJarPaths("foo.jar", "bar.jar");
		model.removeJarPaths("bar.jar");
		assertJarFiles(new String[] { "foo.jar" }, listener.jarPathsReceived);
		model.removeJarPaths("foo.jar");
		assertJarFiles(new String[0], listener.jarPathsReceived);
		model.commit();
		assertJarFiles(new String[0], jarFilesCommitted);
		assertNoError();
	}

	@Test
	public void testConfigClassNamePropagationOnAdd() {
		String[] jarFiles = { "foo.jar" };
		String[] configNames = { "TestConfigurator" };
		presetConfigNames(configNames);
		model.addJarPaths(jarFiles);
		assertReceivedAndCommit(jarFiles, configNames);
		assertNoError();
	}

	@Test
	public void testConfigClassNamePropagationOnMultipleAdd() {
		presetConfigNames("TC1");
		model.addJarPaths("foo.jar");
		String[] jarFiles = { "foo.jar", "bar.jar" };
		String[] configNames = { "TC1", "TC2"  };
		presetConfigNames(configNames);
		model.addJarPaths("bar.jar");
		assertReceivedAndCommit(jarFiles, configNames);
		assertNoError();
	}

	@Test
	public void testConfigClassNameFailureOnAdd() {
		String[] jarFiles = {};
		String[] configNames = {};
		failExtraction = true;
		model.addJarPaths("foo.jar");
		assertError();
		assertReceivedAndCommit(jarFiles, configNames);
	}

	@Test
	public void testConfigClassNamePropagationOnRemove() {
		presetConfigNames("TC1", "TC2");
		model.addJarPaths("foo.jar", "bar.jar");
		String[] jarFiles = { "foo.jar" };
		String[] configNames = { "TC2"  };
		presetConfigNames(configNames);
		model.removeJarPaths("bar.jar");
		assertReceivedAndCommit(jarFiles, configNames);
		assertNoError();
	}

	@Test
	public void testConfigClassNamePropagationOnMultipleRemove() {
		presetConfigNames("TC1", "TC2");
		model.addJarPaths("foo.jar", "bar.jar");
		presetConfigNames("TC2");
		model.removeJarPaths("bar.jar");
		String[] jarFiles = {};
		String[] configNames = {};
		presetConfigNames(configNames);
		model.removeJarPaths("foo.jar");
		assertReceivedAndCommit(jarFiles, configNames);
		assertNoError();
	}

	@Test
	public void testConfigClassNameFailureOnRemove() {
		presetConfigNames("TC1");
		model.addJarPaths("foo.jar", "bar.jar");
		String[] jarFiles = { "foo.jar" };
		String[] configNames = {};
		failExtraction = true;
		model.removeJarPaths("bar.jar");
		assertError();
		assertReceivedAndCommit(jarFiles, configNames);
	}

	private void assertReceivedAndCommit(String[] jarFiles, String[] configNames) {
		assertJarFiles(jarFiles, listener.jarPathsReceived);
		assertArrayEquals(configNames, listener.configClassNamesReceived);
		model.commit();
		assertJarFiles(jarFiles, jarFilesCommitted);
		assertArrayEquals(configNames, configClassNamesCommitted);
	}

	private void assertJarFiles(String[] expected, String[] actual) {
		assertJarFiles(expected, toFiles(actual));
	}

	private void assertJarFiles(String[] expected, File[] actual) {
		ArrayList<File> expectedList = new ArrayList<File>(Arrays.asList(toFiles(expected)));
		Collections.sort(expectedList);
		assertArrayEquals(expectedList.toArray(new File[expectedList.size()]), actual);
	}

	private File[] toFiles(String[] paths) {
		File[] files = new File[paths.length];
		for (int fileIdx = 0; fileIdx < paths.length; fileIdx++) {
			try {
				files[fileIdx] = new File(paths[fileIdx]).getCanonicalFile();
			} 
			catch (IOException exc) {
				fail(exc.getMessage());
			}
		}
		return files;
	}
	
	private void assertNoError() {
		assertNull(exc);
		assertNull(errMsg);
	}

	private void assertError() {
		assertTrue(exc != null || errMsg != null);
	}

	private void presetConfigNames(String... configNames) {
		presetConfigNames= Arrays.asList(configNames);
	}

	private static class CapturingCustomListener implements CustomConfigListener {
		String[] jarPathsReceived;
		String[] configClassNamesReceived;

		@Override
		public void customConfig(String[] jarPaths, String[] configClassNames) {
			jarPathsReceived = jarPaths;
			configClassNamesReceived = configClassNames;
		}
	}
}
