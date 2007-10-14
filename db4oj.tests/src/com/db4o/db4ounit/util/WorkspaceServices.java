/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.util;

import java.io.*;
import java.net.*;

import com.db4o.foundation.io.*;

import db4ounit.*;

/**
 * @sharpen.ignore
 */
public class WorkspaceServices {
	
	public static String workspacePath(String fname) {
		return Path4.combine(workspaceRoot(), fname);
	}
	
	public static String workspaceTestFilePath(String fname) {
		return Path4.combine(WorkspaceLocations.TEST_FOLDER, fname);
	}
	
	/**
	 * @sharpen.property
	 */
	public static String workspaceRoot() {
       String property = System.getProperty("dir.workspace");
        if(property != null){
            return property;
        }
		return findFolderWithChild(pathToClass(WorkspaceServices.class), "db4oj.tests");
	}
	
	static String pathToClass(Class clazz) {
		final URL resource = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class");
		return new File(resource.getFile()).getParent();
	}
	
	static String findFolderWithChild(String baseFolder, String folderChild) {
		
		File test = new File(baseFolder, folderChild);		
		if (test.exists()) return test.getParent(); 
		
		if (getParentFile(test) == null) return null;
		
		// we should test against root folder... :)		
		return findFolderWithChild(getParentFile(test).getParent(), folderChild);
	}
	
	private static File getParentFile(File file){
        String path = file.getParent();
        if (path == null){
            return null;
        }
        return new File(path);
	}

	public static File configurableWorkspacePath(String configurableProperty, String defaultWorkspacePath) {
		final String path = System.getProperty(configurableProperty, workspacePath(defaultWorkspacePath));
		final File file = new File(IOServices.safeCanonicalPath(path));
		Assert.isTrue(file.exists(), path); 
		return file;
	}

}
