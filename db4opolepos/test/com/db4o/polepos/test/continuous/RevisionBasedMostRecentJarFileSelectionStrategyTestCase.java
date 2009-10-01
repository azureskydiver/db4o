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
				strategy(new int[]{});
			}
		});
		Assert.expect(IllegalArgumentException.class, new CodeBlock() {
			public void run() throws Throwable {
				strategy(0, 1);
			}
		});
		Assert.expect(IllegalArgumentException.class, new CodeBlock() {
			public void run() throws Throwable {
				strategy(1, -1);
			}
		});
	}

	public void testTooFewFiles() {
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
			File[] db4oJars = db4oJarSequence(INITIAL_REVISION, INITIAL_REVISION + numInputFiles);
			for(int numSelected = 2; numSelected <= numInputFiles; numSelected++) {
				Db4oJarCollection jarCollection = strategy(createAllIndices(numSelected - 1)).select(db4oJars);
				Assert.areEqual(db4oJarName(INITIAL_REVISION + numInputFiles - 1), jarCollection.currentJar().getName());
				List<File> expectedOthers = Arrays.asList(db4oJarSequence(INITIAL_REVISION + numInputFiles - numSelected, INITIAL_REVISION + numInputFiles - 1));
				Set<File> actualOthers = jarCollection.otherJars();
				IteratorAssert.sameContent(expectedOthers, actualOthers);
			}
		}
	}

	public void testSelectsFirstAndLast() {
		for(int numInputFiles = 2; numInputFiles < 10; numInputFiles++) {
			File[] db4oJars = db4oJarSequence(INITIAL_REVISION, INITIAL_REVISION + numInputFiles);
			for(int numSelected = 2; numSelected <= numInputFiles; numSelected++) {
				Db4oJarCollection jarCollection = strategy(numSelected - 1).select(db4oJars);
				Assert.areEqual(db4oJarName(INITIAL_REVISION + numInputFiles - 1), jarCollection.currentJar().getName());
				List<File> expectedOthers = Arrays.asList(db4oJar(INITIAL_REVISION + numInputFiles - numSelected));
				Set<File> actualOthers = jarCollection.otherJars();
				IteratorAssert.sameContent(expectedOthers, actualOthers);
			}
		}
	}

	public void testSelectsSingleIntermediateJar() {
		File[] inputFiles = files("foo", db4oJarName(42), "bar", db4oJarName(43), "baz", db4oJarName(44));
		Db4oJarCollection jarCollection = strategy(1).select(inputFiles);
		Assert.areEqual(db4oJarName(44), jarCollection.currentJar().getName());
		IteratorAssert.sameContent(Arrays.asList(db4oJar(43)), jarCollection.otherJars());
	}
	
	public void testSelectsAvailableSubsetAndOldestForOneMissing() {
		File[] inputFiles = files("foo", db4oJarName(42), "bar", db4oJarName(43), "baz", db4oJarName(44), db4oJarName(45));
		Db4oJarCollection jarCollection = strategy(2, 5).select(inputFiles);
		Assert.areEqual(db4oJarName(45), jarCollection.currentJar().getName());
		IteratorAssert.sameContent(Arrays.asList(db4oJars(43, 42)), jarCollection.otherJars());
	}

	public void testSelectsAvailableSubsetAndOldestForTwoMissing() {
		File[] inputFiles = files("foo", db4oJarName(42), "bar", db4oJarName(43), "baz", db4oJarName(44), db4oJarName(45));
		Db4oJarCollection jarCollection = strategy(2, 5, 10).select(inputFiles);
		Assert.areEqual(db4oJarName(45), jarCollection.currentJar().getName());
		IteratorAssert.sameContent(Arrays.asList(db4oJars(43, 42)), jarCollection.otherJars());
	}

	private int[] createAllIndices(int length) {
		int[] indices = new int[length];
		for (int idx = 0; idx < length; idx++) {
			indices[idx] = idx + 1;
		}
		return indices;
	}
	
	private JarFileSelectionStrategy strategy(int... selectedIndices) {
		return new RevisionBasedMostRecentJarFileSelectionStrategy(selectedIndices);
	}
	
	private String db4oJarName(int revision) {
		return "db4o-7.10.103." + revision + "-all-java5.jar";
	}

	private File[] db4oJar(int rev) {
		return files(db4oJarNameSequence(rev, rev + 1));
	}

	private String[] db4oJarNames(int... revisions) {
		String[] names = new String[revisions.length];
		for (int fileIdx = 0; fileIdx < names.length; fileIdx++) {
			names[fileIdx] = db4oJarName(revisions[fileIdx]);
		}
		return names;
	}

	private File[] db4oJars(int... revisions) {
		return files(db4oJarNames(revisions));
	}

	private File[] db4oJarSequence(int from, int to) {
		return files(db4oJarNameSequence(from, to));
	}

	private String[] db4oJarNameSequence(int from, int to) {
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
