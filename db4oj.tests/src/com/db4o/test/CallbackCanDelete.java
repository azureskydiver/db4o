/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;


public class CallbackCanDelete {
    
    public String _name;
    
    public CallbackCanDelete _next;
    
    public CallbackCanDelete() {
    
    }

    public CallbackCanDelete(String _name, CallbackCanDelete _next) {
        this._name = _name;
        this._next = _next;
    }
    
    public void storeOne(){
        Test.deleteAllInstances(this);
        _name = "p1";
        _next = new CallbackCanDelete("c1", null);
    }
    
    public void test(){
        ObjectContainer oc = Test.objectContainer();
        ObjectSet objectSet = oc.get(new CallbackCanDelete("p1", null));
        CallbackCanDelete ccd = (CallbackCanDelete) objectSet.next();
        oc.deactivate(ccd, Integer.MAX_VALUE);
        oc.delete(ccd);
    }
    
    
    public boolean objectCanDelete(ObjectContainer container){
        container.activate(this, Integer.MAX_VALUE);
        Test.ensure(_name.equals("p1"));
        Test.ensure(_next != null);
        return true;
    }
    
}
