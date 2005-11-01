/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.nativequery.cats;

import com.db4o.*;
import com.db4o.query.*;


public abstract class SodaCatPredicate extends Predicate {
    
    public void sodaQuery(ObjectContainer oc){
        Query q = oc.query();
        q.constrain(Cat.class);
        constrain(q);
        q.execute();
    }
    
    public abstract void constrain(Query q);
    

}
