/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.header;

import com.db4o.*;
import com.db4o.test.lib.*;

import db4ounit.*;
import db4ounit.db4o.*;


public class OldHeaderTest extends Db4oTestCase{
    
    private static final String ORIGINAL_FILE = "test/db4oVersions/db4o_5.5.2";
    
    private static final String DB_FILE = "test/db4oVersions/db4o_5.5.2.yap";
    
    
    public void setUp() throws Exception {
        super.setUp();
        
    }
    
    public void test(){
        
        
        new File4(ORIGINAL_FILE).copy(DB_FILE);
        
        ObjectContainer oc = Db4o.openFile(DB_FILE);
        
        Assert.isNotNull(oc);
        
        oc.close();
    }
    
    

}
