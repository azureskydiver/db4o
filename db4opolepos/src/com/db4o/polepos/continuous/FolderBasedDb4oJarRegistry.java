/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.polepos.continuous;

import java.io.*;
import java.util.*;

public class FolderBasedDb4oJarRegistry implements Db4oJarRegistry {

	private final File[] _baseDirs;
	private final JarFileSelectionStrategy _strategy;

	public FolderBasedDb4oJarRegistry(File baseDir, JarFileSelectionStrategy strategy) {
		this(new File[] { baseDir }, strategy);
	}

	public FolderBasedDb4oJarRegistry(File[] baseDirs, JarFileSelectionStrategy strategy) {
		for (int dirIdx = 0; dirIdx < baseDirs.length; dirIdx++) {
			if(!baseDirs[dirIdx].isDirectory()) {
				throw new IllegalArgumentException("Not a directory: " + baseDirs[dirIdx].getAbsolutePath());
			}
		}
		_baseDirs = baseDirs;
		_strategy = strategy;
	}
	
	public Db4oJarCollection jarCollection() {
		return _strategy.select(collectFiles());
	}

	private File[] collectFiles() {
		Set<File> files = new HashSet<File>();
		for (File dir : _baseDirs) {
			files.addAll(Arrays.asList(dir.listFiles()));
		}
		return files.toArray(new File[files.size()]);
	}
	
	public static void main(String[] args) {
		FolderBasedDb4oJarRegistry registry = new FolderBasedDb4oJarRegistry(new File(args[0]), new RevisionBasedMostRecentJarFileSelectionStrategy(3));
		Db4oJarCollection jars = registry.jarCollection();
		System.out.println("CURRENT:\n" + jars.currentJar().getAbsolutePath() + "\nOTHERS:");
		for (File otherJar : jars.otherJars()) {
			System.out.println(otherJar.getAbsolutePath());
		}
	}
}
