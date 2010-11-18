/* Copyright (C) 2008  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.foundation;

import java.io.*;
import java.util.*;

import com.db4o.foundation.*;
import com.db4o.foundation.io.*;

import db4ounit.*;

/**
 * @exclude
 */
public class Path4TestCase implements TestCase{
	
	/**
	 * @sharpen.if !SILVERLIGHT
	 */
	public void testGetTempFileName(){
		String tempFileName = Path4.getTempFileName();
		Assert.isTrue(File4.exists(tempFileName));
		File4.delete(tempFileName);
	}
	
	/**
	 * @sharpen.if !SILVERLIGHT
	 */
	public void testForEachFile() throws FileNotFoundException {
		
		String tempFileName = Path4.getTempFileName();
		File root = new File(tempFileName);
		root.delete();
		root.mkdirs();
		createDirAndFile(createDirAndFile(createDirAndFile(root)));
		
		final Set<String> canonicalFilePaths = new HashSet<String>();
		
		Path4.forEachFile(root.getAbsolutePath(), new Procedure4<Pair<String,File>>() {
			public void apply(Pair<String, File> value) {
				if (value.second.isFile()) {
					canonicalFilePaths.add(value.first);
				}
			}
		});
		Assert.isTrue(canonicalFilePaths.remove("dir/file.txt"));
		Assert.isTrue(canonicalFilePaths.remove("dir/dir/file.txt"));
		Assert.isTrue(canonicalFilePaths.remove("dir/dir/dir/file.txt"));
		Assert.isTrue(canonicalFilePaths.isEmpty());
		
		Path4.forEachFile(root.getAbsolutePath(), new Procedure4<Pair<String,File>>() {
			public void apply(Pair<String, File> value) {
				value.second.delete();
			}
		});
		
		root.delete();
	}

	private File createDirAndFile(File root) throws FileNotFoundException {
		File dir = new File(root, "dir");
		dir.mkdir();
		File f = new File(dir, "file.txt");
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dir;
	}
	
}
