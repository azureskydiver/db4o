/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using com.db4o.ext;

namespace com.db4o.test
{

    public class CollectionActivation
    {
        IList myList;
        static long id;

        public void storeOne()
        {
            ExtObjectContainer objectContainer = Test.objectContainer();
            myList = objectContainer.collections().newLinkedList();
            CollectionActivationElement cae = new CollectionActivationElement("test");
            objectContainer.set(cae);
            id = objectContainer.getID(cae);
            myList.Add(cae);
        }

        public void testOne()
        {
            ExtObjectContainer objectContainer = Test.objectContainer();
            objectContainer.activate(this, int.MaxValue);
            CollectionActivationElement cae = (CollectionActivationElement)objectContainer.getByID(id);
            Test.ensure(cae.name == null);
            cae = (CollectionActivationElement)myList[0];
            Test.ensure(cae.name != null);
            Test.ensure(cae.name == "test");
        }
    }

    public class CollectionActivationElement
    {
        public String name;

        public CollectionActivationElement(){}

        public CollectionActivationElement(String name){
            this.name = name;
        }
    }
}
