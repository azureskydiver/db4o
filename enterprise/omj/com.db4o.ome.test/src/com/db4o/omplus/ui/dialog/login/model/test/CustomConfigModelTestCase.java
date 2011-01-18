/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */

package com.db4o.omplus.ui.dialog.login.model.test;

import static org.easymock.EasyMock.*;

import java.io.*;
import java.util.*;

import org.easymock.*;
import org.junit.*;

import com.db4o.omplus.*;
import com.db4o.omplus.connection.*;
import com.db4o.omplus.ui.dialog.login.model.*;
import com.db4o.omplus.ui.dialog.login.model.CustomConfigModel.CustomConfigListener;


public class CustomConfigModelTestCase {

	private CustomConfigModel model;

	private CustomConfigListener listener;
	private CustomConfigSink sink;
	private ConfiguratorExtractor extractor;
	private ErrorMessageSink errSink;
		
	@Before
	public void setUp() {

		sink = createMock(CustomConfigSink.class);
		listener = createMock(CustomConfigListener.class);
		extractor = createMock(ConfiguratorExtractor.class);
		errSink = createMock(ErrorMessageSink.class);
		model = new CustomConfigModel(sink , extractor , new ErrorMessageHandler(errSink));		
		model.addListener(listener);
	}
	
	@Test
	public void testAddJars() throws Exception {
		final String[] jarPaths = { "foo.jar", "bar.jar" };
		final String[] configNames = { "FooConfig", "BarConfig" };
		assertAddJars(jarPaths, configNames, new String[0], new String[0]);	
		assertCommit(jarPaths, configNames);
	}
	
	@Test
	public void testMultipleAddJars() throws Exception {
		final String firstJar = "foo.jar";
		final String firstConfigName = "FooConfig";
		assertAddJars(arr(firstJar), arr(firstConfigName), new String[0], new String[0]);
		
		final String secondJar = "bar.jar";
		final String secondConfigName = "BarConfig";
		assertAddJars(arr(secondJar), arr(secondConfigName), arr(firstJar), arr(firstConfigName));
		assertCommit(arr(firstJar, secondJar), arr(firstConfigName, secondConfigName));
	}

	@Test
	public void testAddJarsFailure() throws Exception {
		assertAddJarsFailure(new String[] { "foo.jar", "bar.jar" }, 1);
		assertCommit(new String[0], new String[0]);
	}

	@Test
	public void testAddJarsFailureRetainsLastState() throws Exception {
		final String firstJar = "foo.jar";
		final String firstConfigName = "FooConfig";
		assertAddJars(arr(firstJar), arr(firstConfigName), new String[0], new String[0]);
		assertAddJarsFailure(arr("bar.jar"), 0);
		assertCommit(arr(firstJar), arr(firstConfigName));
	}

	@Test
	public void testRemoveJars() throws Exception {
		final String retainedJar = "foo.jar";
		final String removedJar = "bar.jar";
		final String retainedConfigName = "FooConfig";
		final String removedConfigName = "BarConfig";
		assertAddJars(arr(retainedJar, removedJar), arr(retainedConfigName, removedConfigName), new String[0], new String[0]);
		assertRemoveJars(arr(removedJar), arr(retainedJar), arr(retainedConfigName));
		assertCommit(arr(retainedJar), arr(retainedConfigName));
	}

	@Test
	public void testMultipleRemoveJars() throws Exception {
		final String retainedJar = "foo.jar";
		final String removedJar = "bar.jar";
		final String retainedConfigName = "FooConfig";
		final String removedConfigName = "BarConfig";
		assertAddJars(arr(retainedJar, removedJar), arr(retainedConfigName, removedConfigName), new String[0], new String[0]);
		assertRemoveJars(arr(removedJar), arr(retainedJar), arr(retainedConfigName));
		assertRemoveJars(arr(retainedJar), new String[0], new String[0]);
		assertCommit(new String[0], new String[0]);
	}

	@Test
	public void testConfigClassNameFailureOnAdd() throws Exception {
		final String jarPath = "foo.jar";
		expect(extractor.acceptJarFile(file(jarPath))).andReturn(true);
		expect(extractor.configuratorClassNames(files(jarPath))).andThrow(new DBConnectException(""));
		checkOrder(errSink, false);
		errSink.showError(EasyMock.<String>anyObject());
		errSink.logExc(eqExc(DBConnectException.class));
		replayMocks();
		model.addJarPaths("foo.jar");
		verifyMocks();
		resetMocks();
		
		assertCommit(new String[0], new String[0]);
	}

	@Test
	public void testConfigClassNameFailureOnRemove() throws Exception {
		final String retainedJar = "foo.jar";
		final String retainedConfigName = "FooConfig";
		final String removedJar = "bar.jar";
		final String removedConfigName = "BarConfig";
		assertAddJars(canonicalPaths(retainedJar, removedJar), arr(retainedConfigName, removedConfigName), new String[0], new String[0]);
		expect(extractor.configuratorClassNames(files(retainedJar))).andThrow(new DBConnectException(""));
		checkOrder(errSink, false);
		errSink.showError(EasyMock.<String>anyObject());
		errSink.logExc(eqExc(DBConnectException.class));
		listener.customConfig(aryEq(new String[0]), aryEq(new String[0]));
		replayMocks();
		model.removeJarPaths(removedJar);
		verifyMocks();
		resetMocks();
		assertCommit(new String[0], new String[0]);
	}
	
	private static class ExceptionMatcher<T extends Throwable> implements IArgumentMatcher {
		
		private Class<T> expected;
		
		public ExceptionMatcher(Class<T> expected) {
			this.expected = expected;
		}
		
		@Override
		public void appendTo(StringBuffer str) {
			str.append("eqExc(" + expected.getName() + ")");
		}

		@Override
		public boolean matches(Object other) {
			return other != null && expected.isAssignableFrom(other.getClass());
		}
	}
	
	public static <T extends Throwable> T eqExc(Class<T> expected) {
	    reportMatcher(new ExceptionMatcher<T>(expected));
	    return null;
	}

	private void assertCommit(String[] jarPaths, String[] configNames) throws IOException {
		sink.customConfig(aryEq(canonicalPaths(jarPaths)), aryEq(sort(configNames)));
		replayMocks();
		model.commit();
		verifyMocks();
	}
	
	private void assertAddJars(String[] jarPaths, String[] configNames, String[] existingJarPaths, String[] existingConfigNames) throws Exception {
		final List<String> allJarsList = concat(canonicalPaths(jarPaths), canonicalPaths(existingJarPaths));
		final List<String> allConfigNamesList = concat(configNames, existingConfigNames);
		final String[] allJars = allJarsList.toArray(new String[allJarsList.size()]);
		final String[] allConfigNames = allConfigNamesList.toArray(new String[allConfigNamesList.size()]);
		
		listener.customConfig(aryEq(allJars), aryEq(allConfigNames));
		for (String jarName : jarPaths) {
			expectAcceptJarFileInvocation(jarName, true);
		}
		expect(extractor.configuratorClassNames(eq(files(allJars)))).andReturn(allConfigNamesList);
		replayMocks();
		model.addJarPaths(jarPaths);
		verifyMocks();
		resetMocks();
	}

	private void assertAddJarsFailure(String[] jarPaths, int failureIndex) throws IOException {
		for (int jarIdx = 0; jarIdx < failureIndex; jarIdx++) {
			expectAcceptJarFileInvocation(jarPaths[jarIdx], true);
		}
		expectAcceptJarFileInvocation(jarPaths[failureIndex], false);
		errSink.showError(EasyMock.<String>anyObject());
		replayMocks();
		model.addJarPaths(jarPaths);
		verifyMocks();
		resetMocks();		
	}

	private void assertRemoveJars(String[] removedJars, String[] retainedJars, String[] retainedConfigNames) throws Exception {
		expect(extractor.configuratorClassNames(files(retainedJars))).andReturn(Arrays.asList(retainedConfigNames));
		listener.customConfig(aryEq(canonicalPaths(retainedJars)), aryEq(retainedConfigNames));
		replayMocks();
		
		model.removeJarPaths(removedJars);
		verifyMocks();
		resetMocks();
	}
	
	private void expectAcceptJarFileInvocation(final String jarPath, final boolean retVal) throws IOException {
		expect(extractor.acceptJarFile(file(jarPath))).andReturn(retVal);
	}

	private void replayMocks() {
		replay(listener, sink, extractor, errSink);
	}
	
	private void verifyMocks() {
		verify(listener, sink, extractor, errSink);
	}

	private void resetMocks() {
		reset(listener, sink, extractor, errSink);
	}

	private List<File> files(String... paths) throws IOException {
		List<File> files = new ArrayList<File>(paths.length);
		for (String path : paths) {
			files.add(file(path));
		}
		Collections.sort(files);
		return files;
	}
	
	private File file(final String path) throws IOException {
		return new File(path).getCanonicalFile();
	}

	private String[] canonicalPaths(String... paths) throws IOException {
		final List<File> files = files(paths);
		String[] canonicalPaths = new String[files.size()];
		for (int pathIdx = 0; pathIdx < files.size(); pathIdx++) {
			canonicalPaths[pathIdx] = files.get(pathIdx).getAbsolutePath();
		}
		return canonicalPaths;
	}

	private <T extends Comparable<T>> T[] sort(T... arr) {
		final T[] copy = Arrays.copyOf(arr, arr.length);
		Arrays.sort(copy);
		return copy;
	}
	
	private <T> T[] arr(T... arr) {
		return arr;
	}
	
	private <T extends Comparable<T>> List<T> concat(T[] first, T[] second) {
		Set<T> set = new HashSet<T>();
		set.addAll(Arrays.asList(first));
		set.addAll(Arrays.asList(second));
		List<T> list = new ArrayList<T>(set);
		Collections.sort(list);
		return list;
	}
}
