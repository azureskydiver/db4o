/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.old;

import com.db4o.test.*;


public class MigrateFromNull {
    
    public void test(){
        try{
            Test.objectContainer().migrateFrom(null);
        }catch(Exception e){
            Test.error();
        }
    }

}
