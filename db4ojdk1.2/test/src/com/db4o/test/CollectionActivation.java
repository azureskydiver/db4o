/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.*;

import com.db4o.ext.*;


public class CollectionActivation {
    
    List myList;
    static long id;

    public void storeOne()
    {
        ExtObjectContainer objectContainer = Test.objectContainer();
        myList = objectContainer.collections().newLinkedList();
        CollectionActivationElement cae = new CollectionActivationElement("test");
        objectContainer.set(cae);
        id = objectContainer.getID(cae);
        myList.add(cae);
    }

    public void testOne()
    {
        ExtObjectContainer objectContainer = Test.objectContainer();
        objectContainer.activate(this, Integer.MAX_VALUE);
        CollectionActivationElement cae = (CollectionActivationElement)objectContainer.getByID(id);
        Test.ensure(cae.name == null);
        cae = (CollectionActivationElement)myList.get(0);
        Test.ensure(cae.name.equals("test"));
    }
    
    public static class CollectionActivationElement
    {
        public String name;

        public CollectionActivationElement(){}

        public CollectionActivationElement(String name){
            this.name = name;
        }
    }

}
