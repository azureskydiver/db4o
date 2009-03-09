/* Copyright (C) 2009  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.io.*;

import db4ounit.*;

public class DbPathDoesNotExistTestCase implements TestCase{
	
	public void test(){
		String tempPath = Path4.getTempPath();
		final String nonExistantPath = Path4.combine(tempPath, "/folderdoesnotexistneverever/filedoesnotexist.db4o");
		Assert.expect(Db4oIOException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(),nonExistantPath );
			}
		});
		
	}

}
