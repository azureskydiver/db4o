/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

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
            Test.ensure(cae.name.Equals("test"));
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
