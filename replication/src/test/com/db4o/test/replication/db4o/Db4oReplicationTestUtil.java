/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.db4o;

import java.io.*;

import com.db4o.*;
import com.db4o.inside.replication.*;
import com.db4o.replication.db4o.*;
import com.db4o.test.replication.*;

public class Db4oReplicationTestUtil {
    
    private static ObjectContainer _objectcontainer;
    
    public static final String PROVIDER_B_FILE = "providerB.yap";
    
    public static void configure(){
        Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
        Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
        new File(PROVIDER_B_FILE).delete();
    }
    
    public static TestableReplicationProvider providerB(){
//        close();
//        new File(PROVIDER_B_FILE).delete();
//        
        if(_objectcontainer == null){
            _objectcontainer = Db4o.openFile(PROVIDER_B_FILE);
        }
        return new Db4oReplicationProvider(_objectcontainer); 
    }
    
    public static void close(){
        if(_objectcontainer != null){
            _objectcontainer.close();
            _objectcontainer = null;
        }
    }

}
