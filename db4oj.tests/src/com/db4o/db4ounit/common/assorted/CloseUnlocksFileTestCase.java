/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.*;
import com.db4o.test.lib.*;

import db4ounit.*;


public class CloseUnlocksFileTestCase implements TestCase {
    
    private static final String FILE = "unlocked.db4o";
    
    public void test(){
        File4.delete(FILE);
        Assert.isFalse(File4.exists(FILE));
        ObjectContainer oc = Db4o.openFile(FILE);
        oc.close();
        File4.delete(FILE);
        Assert.isFalse(File4.exists(FILE));
    }

}
