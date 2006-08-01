/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.header;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.test.*;

import db4ounit.*;
import db4ounit.db4o.*;


public class IdentityTestCase extends Db4oTestCase{
    
    
    public void testIdentityPreserved() throws Exception{
        
        Db4oDatabase ident = db().identity();
        
        reopen();
        
        Db4oDatabase ident2 = db().identity();
        
        Assert.isNotNull(ident);
        Assert.areEqual(ident, ident2);
        
    }
    
    public void testGenerateIdentity() throws Exception{
        
        Db4oDatabase db = db().identity();
        byte[] oldSignature = db.getSignature();

        ((YapFile)db()).generateNewIdentity();
        
        reopen();
        
        db = db().identity();
        byte[] newSignature = db.getSignature();
        
        boolean same = true;
        
        for (int i = 0; i < oldSignature.length; i++) {
            if(oldSignature[i] != newSignature[i]){
                same =false;
            }
        }
        Assert.isTrue(! same);
    }


}
