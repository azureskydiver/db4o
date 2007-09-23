/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.migration;

import java.io.*;

import com.db4o.db4ounit.util.*;

/**
 * @sharpen.ignore
 */
public class Db4oLibrarian {
	
	private Db4oLibraryEnvironmentProvider _provider;

	public Db4oLibrarian(Db4oLibraryEnvironmentProvider provider) {
		_provider = provider;
	}

	public Db4oLibrary[] libraries() throws Exception {
		final File[] libFiles = libFiles(archivesPath());
		Db4oLibrary[] libraries = new Db4oLibrary[libFiles.length];
		for (int i = 0; i < libFiles.length; i++) {
			final File file = libFiles[i];
			libraries[i] = new Db4oLibrary(file.getCanonicalPath(), environmentFor(file));
		}
		return libraries;
	}

	private Db4oLibraryEnvironment environmentFor(File libFile)
			throws IOException {
		return _provider.environmentFor(libFile.getCanonicalPath());
	}
	
	private File[] libFiles(String libDir) {
        return new File(libDir).listFiles(new FilenameFilter() {
            public boolean accept(File file, String name) {
                return name.endsWith(".jar");
            }
        });
    }
	
	private String archivesPath() {
		return IOServices.safeCanonicalPath(System.getProperty("db4o.archives.path", "../db4o.archives/java1.2/"));
	}
}
