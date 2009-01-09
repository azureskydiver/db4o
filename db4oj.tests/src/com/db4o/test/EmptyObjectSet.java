/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.internal.*;



/**
 */
@decaf.Ignore
public class EmptyObjectSet {
    
    public void test(){
        ObjectSet objectSet = Test.objectContainer().queryByExample(ObjectContainerBase.class);
        Test.ensure(objectSet.size() == 0);
    }

}
