/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.query.*;

public class FileSizeOnReopen {
    
    public String foo;
    
    public void storeOne(){
        foo = "foo";
    }
    
    public void testOne(){
        if(Test.canCheckFileSize()){
            for (int i = 0; i < 5; i++) {
                tLength();
            }
        }
    }
    
    public void tLength(){
        int fileLength = Test.fileLength();
        Test.reOpen();
        Query q = Test.query();
        q.constrain(this.getClass());
        FileSizeOnReopen fsor =  (FileSizeOnReopen)q.execute().next();
        Test.ensure(fsor.foo.equals("foo"));
        Test.reOpen();
        Test.ensureEquals(fileLength,Test.fileLength());
    }
}
