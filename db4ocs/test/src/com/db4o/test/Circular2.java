/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.Hashtable;

import com.db4o.ext.ExtObjectContainer;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;
import db4ounit.extensions.Db4oUtil;

public class Circular2 extends ClientServerTestCase {
    
    public Hashtable ht;
    
    private String name;
    
    protected void configure(ExtObjectContainer oc) {
        oc.configure().updateDepth(Integer.MAX_VALUE);
    }
    public void store(ExtObjectContainer oc){
        ht = new Hashtable();
        name = "parent";
        C2C c2c = new C2C();
        c2c.parent = this;
        ht.put("test", c2c);
        oc.set(ht);
    }
    
    public void conc(ExtObjectContainer oc){
    	ht = (Hashtable) Db4oUtil.getOne(oc, Hashtable.class);
        C2C c2c = (C2C)ht.get("test");
        Assert.areEqual("parent",c2c.parent.name);
    }
    
    public static class C2C{
        public Circular2 parent;
    }
}
