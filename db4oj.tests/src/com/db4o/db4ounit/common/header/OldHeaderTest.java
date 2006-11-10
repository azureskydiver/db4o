/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.header;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.db4ounit.util.*;
import com.db4o.foundation.io.*;

import db4ounit.*;

public class OldHeaderTest implements TestCase {
    
    private static final String ORIGINAL_FILE = WorkspaceServices.workspacePath("db4oj.tests/test/db4oVersions/db4o_5.5.2");
    
    private static final String DB_FILE = WorkspaceServices.workspacePath("db4oj.tests/test/db4oVersions/db4o_5.5.2.yap");   
    
    public void test() {
    	
    	if(! File4.exists(ORIGINAL_FILE)){
    		TestPlatform.emitWarning(ORIGINAL_FILE + " does not exist. Can not run " + getClass().getName());
    		return;
    	}
        
    	File4.copy(ORIGINAL_FILE, DB_FILE);
        
    	Db4o.configure().allowVersionUpdates(true);    	
        ObjectContainer oc = Db4o.openFile(DB_FILE);
        try {
        	Assert.isNotNull(oc);
        } finally {
        	oc.close();
        	Db4o.configure().allowVersionUpdates(false);
        }
    }
}
