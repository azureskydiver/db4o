/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.internal;

import com.db4o.db4ounit.common.internal.EmbeddedClientObjectContainerTestCase.*;
import com.db4o.query.*;


public class ItemPredicate extends Predicate<Item>{
    public boolean match(Item item){
        return true;
    }
}