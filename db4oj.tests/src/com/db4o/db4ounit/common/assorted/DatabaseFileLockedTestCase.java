/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.ext.*;
import com.db4o.internal.*;

import db4ounit.*;

@decaf.Remove
public class DatabaseFileLockedTestCase extends TestWithTempFile{
	
	public void testLockedFile() throws IOException{
		RandomAccessFile raf = null;
		try{
			raf = new RandomAccessFile(tempFile(), "rw");
			Object channel = Reflection4.invoke(raf, "getChannel");
			try {
				Reflection4.invoke(channel, "tryLock");
			}catch(ReflectException rex){
				Assert.fail("File shouldn't be locked already.");
				rex.printStackTrace();
			}
	
			Assert.expect(DatabaseFileLockedException.class, new CodeBlock() {
				public void run() throws Throwable {
					EmbeddedObjectContainer objectContainer = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), tempFile());
				}
			});
		}finally{
			raf.close();
		}
	}
	
	public void test(){
		EmbeddedObjectContainer objectContainer1 = null;
		try{
			EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
			objectContainer1 = Db4oEmbedded.openFile(config, tempFile());
			Assert.expect(DatabaseFileLockedException.class, new CodeBlock() {
				public void run() throws Throwable {
					EmbeddedObjectContainer objectContainer2 = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), tempFile());
				}
			});
		} finally {
			objectContainer1.close();
		}
	}

}
