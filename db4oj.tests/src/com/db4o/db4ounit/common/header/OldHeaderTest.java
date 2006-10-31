/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.header;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.db4ounit.util.*;

import db4ounit.Assert;
import db4ounit.TestCase;

public class OldHeaderTest implements TestCase {
    
    private static final String ORIGINAL_FILE = WorkspaceServices.workspacePath("db4oj.tests/test/db4oVersions/db4o_5.5.2");
    
    private static final String DB_FILE = WorkspaceServices.workspacePath("db4oj.tests/test/db4oVersions/db4o_5.5.2.yap");   
    
    public void test() {
        
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
