/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.ext.*;


public class ChangeIdentity {
    
    public void test(){
        
        if(Test.isClientServer()){
            return;
        }
        
        ExtObjectContainer oc = Test.objectContainer();
        oc.identity();
        
        Db4oDatabase db = oc.identity();
        byte[] oldSignature = db.getSignature();

        ((YapFile)oc).generateNewIdentity();
        
        Test.reOpen();
        
        oc = Test.objectContainer();
        
        db = oc.identity();
        byte[] newSignature = db.getSignature();
        
        boolean same = true;
        
        for (int i = 0; i < oldSignature.length; i++) {
            if(oldSignature[i] != newSignature[i]){
                same =false;
            }
        }
        
        Test.ensure( ! same);
    }

}
