/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import java.util.*;

public class RefreshList {
    
    private List list;
    
    public void storeOne(){
        list = new ArrayList();
        list.add("Hi");
    }
    
    public void testOne(){
        list.remove(0);
        Test.ensure(list.size() == 0);
        Test.objectContainer().refresh(list, Integer.MAX_VALUE);
        Test.ensure(list.size() == 1);
    }
}
