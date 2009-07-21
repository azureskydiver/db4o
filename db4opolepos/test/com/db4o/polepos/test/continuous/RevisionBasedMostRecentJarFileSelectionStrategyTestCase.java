/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.polepos.test.continuous;

import java.io.*;
import java.util.*;

import com.db4o.polepos.continuous.*;

import db4ounit.*;

public class RevisionBasedMostRecentJarFileSelectionStrategyTestCase implements TestCase {
	
	private static final int INITIAL_REVISION = 42;

	public void testIllegalConstructorArgs() {
		Assert.expect(IllegalArgumentException.class, new CodeBlock() {
			public void run() throws Throwable {
				strategy(0);
			}
		});
		Assert.expect(IllegalArgumentException.class, new CodeBlock() {
			public void run() throws Throwable {
				strategy(-1);
			}
		});
	}

	public void testTooFewFiles() {
		Assert.expect(IllegalArgumentException.class, new CodeBlock() {
			public void run() throws Throwable {
				strategy(1).select(files());
			}
		});
		Assert.expect(IllegalArgumentException.class, new CodeBlock() {
			public void run() throws Throwable {
				strategy(1).select(files(db4oJarName(INITIAL_REVISION)));
			}
		});
	}

	public void testTooFewDb4oCompliantFiles() {
		Assert.expect(IllegalArgumentException.class, new CodeBlock() {
			public void run() throws Throwable {
				strategy(1).select(files("foo", db4oJarName(INITIAL_REVISION)));
			}
		});
	}

	public void testSelectsAll() {
		for(int numInputFiles = 2; numInputFiles < 10; numInputFiles++) {
			for(int numOthers = 1; numOthers < numInputFiles; numOthers++) {
				File[] db4oJars = db4oJars(INITIAL_REVISION, INITIAL_REVISION + numInputFiles);
				Db4oJarCollection jarCollection = strategy(numOthers).select(db4oJars);
				Assert.areEqual(db4oJarName(INITIAL_REVISION + numInputFiles - 1), jarCollection.currentJar().getName());
				List<File> expectedOthers = Arrays.asList(db4oJars(INITIAL_REVISION + numInputFiles - numOthers - 1, INITIAL_REVISION + numInputFiles - 1));
				IteratorAssert.sameContent(expectedOthers, jarCollection.otherJars());
			}
		}
	}
	
	public void testSelectSubset() {
		File[] inputFiles = files("foo", db4oJarName(42), "bar", db4oJarName(43), "baz", db4oJarName(44));
		Db4oJarCollection jarCollection = strategy(1).select(inputFiles);
		Assert.areEqual(db4oJarName(44), jarCollection.currentJar().getName());
		IteratorAssert.sameContent(Arrays.asList(db4oJars(43,44)), jarCollection.otherJars());
	}

	private JarFileSelectionStrategy strategy(int numOthers) {
		return new RevisionBasedMostRecentJarFileSelectionStrategy(numOthers);
	}
	
	private String db4oJarName(int revision) {
		return "db4o-7.10.103." + revision + "-all-java5.jar";
	}

	private File[] db4oJars(int from, int to) {
		return files(db4oJarNames(from, to));
	}

	private String[] db4oJarNames(int from, int to) {
		String[] names = new String[to - from];
		for (int fileIdx = 0; fileIdx < names.length; fileIdx++) {
			names[fileIdx] = db4oJarName(fileIdx + from);
		}
		return names;
	}
	
	private File[] files(String...  paths) {
		File[] files = new File[paths.length];
		for (int fileIdx = 0; fileIdx < paths.length; fileIdx++) {
			files[fileIdx] = new File(paths[fileIdx]);
		}
		return files;
	}

}
