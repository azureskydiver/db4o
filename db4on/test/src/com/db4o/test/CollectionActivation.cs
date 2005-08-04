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
            ExtObjectContainer objectContainer = Tester.objectContainer();
            myList = objectContainer.collections().newLinkedList();
            CollectionActivationElement cae = new CollectionActivationElement("test");
			cae.value = 42;
            objectContainer.set(cae);
            id = objectContainer.getID(cae);
            myList.Add(cae);
        }

        public void testOne()
        {   
			CollectionActivationElement cae = null;

			ExtObjectContainer objectContainer = Tester.objectContainer();
			objectContainer.activate(this, int.MaxValue);

            cae = (CollectionActivationElement)objectContainer.getByID(id);
            Tester.ensure("objects got by id should not be activated", cae.name == null);

            cae = (CollectionActivationElement)myList[0];
            Tester.ensure(cae.name != null && cae.name == "test");
			Tester.ensure(42 == cae.value);
        }
    }

    public class CollectionActivationElement
    {
        public String name;

		public int value;

        public CollectionActivationElement(){}

        public CollectionActivationElement(String name){
            this.name = name;
        }
    }
}
