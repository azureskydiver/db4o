/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.io.*;

import com.db4o.*;
import com.db4o.query.*;


/**
 * 
 */
public class MigrationFromEarlierVersion {
    
    static final String FILE = "test/TwoNine.yap";
    
//    public void configure(){
//        Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
//        Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
//    }
    
    public void test(){
        if(! Test.isClientServer()){
            if(new File(FILE).exists()){
                Db4o.configure().readOnly(true);
                ObjectContainer con = Db4o.openFile(FILE);
                Query q = con.query();
                q.constrain(SimplestPossible.class);
                ObjectSet objectSet = q.execute();
                SimplestPossible sp = (SimplestPossible)objectSet.next();
                Test.ensure("sp".equals(sp.name));
                con.close();
                Db4o.configure().readOnly(false);
            }
        }
    }
}
