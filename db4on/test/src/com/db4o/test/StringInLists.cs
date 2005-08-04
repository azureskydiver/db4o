/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

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

            ExtObjectContainer oc = Tester.objectContainer();
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
            Tester.ensure(list.Count == 3);
            Tester.ensure(list[0].Equals("One"));
            Tester.ensure(list[1].Equals("Two"));
            Tester.ensure(list[2].Equals("Three"));
        }
    
        private void checkMap(IDictionary map)
        {
            Tester.ensure(map.Count == 3);
            Tester.ensure(map["One"].Equals("One"));
            Tester.ensure(map["Two"].Equals("Two"));
            Tester.ensure(map["Three"].Equals("Three"));
        }
    }
}
