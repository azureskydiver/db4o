/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class ChangeIdentity extends AbstractDb4oTestCase implements OptOutCS {
    
    public void test() throws Exception {

        byte[] oldSignature = db().identity().getSignature();

        ((LocalObjectContainer)db()).generateNewIdentity();
        
        reopen();
        
        ArrayAssert.areNotEqual(oldSignature, db().identity().getSignature());
    }
}
