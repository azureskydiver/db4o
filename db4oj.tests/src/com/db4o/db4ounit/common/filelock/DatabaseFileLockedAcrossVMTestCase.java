/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */

package com.db4o.db4ounit.common.filelock;

import java.io.*;

import com.db4o.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.db4ounit.util.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.extensions.util.IOServices.*;

@decaf.Remove
public class DatabaseFileLockedAcrossVMTestCase
	extends TestWithTempFile
	implements OptOutInMemory, OptOutWorkspaceIssue {
	
	
	public void testLockedFile() throws IOException{
		ProcessRunner externalVM = JavaServices.startJava(AcquireNativeLock.class.getName(), new String[]{ tempFile() });
		externalVM.checkIfStarted("ready", 3000);
		try {
			Assert.expect(DatabaseFileLockedException.class, new CodeBlock() {
				public void run() throws Throwable {
					EmbeddedObjectContainer objectContainer = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), tempFile());
				}
			});
		} finally {
			externalVM.write("");
			try {
				externalVM.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	

	public static void main(String[] args) {
		new ConsoleTestRunner(DatabaseFileLockedAcrossVMTestCase.class).run();
	}
}
