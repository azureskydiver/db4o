/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.util;

import com.db4o.foundation.io.Path4;

/**
 * @sharpen.ignore
 */
public class WorkspaceServices {
	
	public static String workspacePath(String fname) {
		return "../" + fname;
	}
	
	public static String workspaceTestFilePath(String fname) {
		return Path4.combine(WorkspaceLocations.TEST_FOLDER, fname);
	}

}
