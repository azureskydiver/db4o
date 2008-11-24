/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.header;

import java.io.*;

import com.db4o.*;
import com.db4o.db4ounit.util.*;
import com.db4o.foundation.io.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

public class OldHeaderTest implements TestCase, OptOutNoFileSystemData {
    
    public void test() throws IOException {
    	final String originalFilePath = originalFilePath();
    	final String dbFilePath = dbFilePath();
    	if(! File4.exists(originalFilePath)){
    		TestPlatform.emitWarning(originalFilePath + " does not exist. Can not run " + getClass().getName());
    		return;
    	}
        
    	File4.copy(originalFilePath, dbFilePath);
        
    	Db4o.configure().allowVersionUpdates(true);    	
    	Db4o.configure().exceptionsOnNotStorable(false);    	
        ObjectContainer oc = Db4o.openFile(dbFilePath);
        try {
        	Assert.isNotNull(oc);
        } finally {
        	oc.close();
        	Db4o.configure().exceptionsOnNotStorable(true);    	
        	Db4o.configure().allowVersionUpdates(false);
        }
    }
    
    private static String originalFilePath() {
    	return WorkspaceServices.workspaceTestFilePath("db4oVersions/db4o_5.5.2");
    }

    private static String dbFilePath() {
    	return WorkspaceServices.workspaceTestFilePath("db4oVersions/db4o_5.5.2.yap");
    }
}
