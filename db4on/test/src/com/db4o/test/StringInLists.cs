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

        public void StoreOne() 
        {

            ExtObjectContainer oc = Tester.ObjectContainer();
            Db4oCollections col = oc.Collections();

            arrayList = new ArrayList();
            FillList(arrayList);

//            stringCollection = new StringCollection();
//            FillList(stringCollection);
        
            db4oLinkedList = col.NewLinkedList();
            FillList(db4oLinkedList);
        
            hashMap = new Hashtable();
            FillMap(hashMap);
        
            db4oHashMap = col.NewHashMap(1);
            FillMap(db4oHashMap);
        }

        public void TestOne() 
        {
            CheckList(arrayList);
//            CheckList(stringCollection);
            CheckList(db4oLinkedList);
            CheckMap(hashMap);
            CheckMap(db4oHashMap);
        }

        private void FillList(IList list) 
        {
            list.Add("One");
            list.Add("Two");
            list.Add("Three");
        }

        private void FillMap(IDictionary map) 
        {
            map["One"] = "One";
            map["Two"] = "Two";
            map["Three"] = "Three";
        }

        private void CheckList(IList list) 
        {
            Tester.Ensure(list.Count == 3);
            Tester.Ensure(list[0].Equals("One"));
            Tester.Ensure(list[1].Equals("Two"));
            Tester.Ensure(list[2].Equals("Three"));
        }
    
        private void CheckMap(IDictionary map)
        {
            Tester.Ensure(map.Count == 3);
            Tester.Ensure(map["One"].Equals("One"));
            Tester.Ensure(map["Two"].Equals("Two"));
            Tester.Ensure(map["Three"].Equals("Three"));
        }
    }
}
