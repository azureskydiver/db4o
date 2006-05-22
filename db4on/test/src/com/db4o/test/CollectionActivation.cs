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

        public void StoreOne()
        {
            ExtObjectContainer objectContainer = Tester.ObjectContainer();
            myList = objectContainer.Collections().NewLinkedList();
            CollectionActivationElement cae = new CollectionActivationElement("test");
			cae.value = 42;
            objectContainer.Set(cae);
            id = objectContainer.GetID(cae);
            myList.Add(cae);
        }

        public void TestOne()
        {   
			CollectionActivationElement cae = null;

			ExtObjectContainer objectContainer = Tester.ObjectContainer();
			objectContainer.Activate(this, int.MaxValue);

            cae = (CollectionActivationElement)objectContainer.GetByID(id);
            Tester.Ensure("objects got by id should not be activated", cae.name == null);

            cae = (CollectionActivationElement)myList[0];
            Tester.Ensure(cae.name != null && cae.name == "test");
			Tester.Ensure(42 == cae.value);
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
