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
using System.Collections.Specialized;

using com.db4o.ext;
using com.db4o.types;


namespace com.db4o.test
{

    public class StringInLists 
    {

        public IList arrayList;

        // uncommented for CompactFramework compatibility
        // public StringCollection stringCollection;
        
        public IList db4oLinkedList;

        public IDictionary  hashMap;
        public IDictionary db4oHashMap;

        public void storeOne() 
        {

            ExtObjectContainer oc = Test.objectContainer();
            Db4oCollections col = oc.collections();

            arrayList = new ArrayList();
            fillList(arrayList);

//            stringCollection = new StringCollection();
//            fillList(stringCollection);
        
            db4oLinkedList = col.newLinkedList();
            fillList(db4oLinkedList);
        
            hashMap = new Hashtable();
            fillMap(hashMap);
        
            db4oHashMap = col.newHashMap(1);
            fillMap(db4oHashMap);
        }

        public void testOne() 
        {
            checkList(arrayList);
//            checkList(stringCollection);
            checkList(db4oLinkedList);
            checkMap(hashMap);
            checkMap(db4oHashMap);
        }

        private void fillList(IList list) 
        {
            list.Add("One");
            list.Add("Two");
            list.Add("Three");
        }

        private void fillMap(IDictionary map) 
        {
            map["One"] = "One";
            map["Two"] = "Two";
            map["Three"] = "Three";
        }

        private void checkList(IList list) 
        {
            Test.ensure(list.Count == 3);
            Test.ensure(list[0].Equals("One"));
            Test.ensure(list[1].Equals("Two"));
            Test.ensure(list[2].Equals("Three"));
        }
    
        private void checkMap(IDictionary map)
        {
            Test.ensure(map.Count == 3);
            Test.ensure(map["One"].Equals("One"));
            Test.ensure(map["Two"].Equals("Two"));
            Test.ensure(map["Three"].Equals("Three"));
        }
    }
}
