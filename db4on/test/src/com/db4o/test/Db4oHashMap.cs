/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;

using Db4o.Tools;

using com.db4o;
using com.db4o.ext;
using com.db4o.query;
using com.db4o.types;

namespace com.db4o.test {

    /**
     * 
     */
    public class Db4oHashMap {
      
        public Db4oHashMap() : base() {
        }
      
        static internal int COUNT = 10;
        static internal String[] DEFAULT = {
                                               "wow",
                                               "cool",
                                               "great"      };

        static internal String MORE = "more and more ";
        
        internal IDictionary i_map;
        internal Db4oHashMapHelper i_helper;
      
        public void StoreOne() {
            i_map = Tester.ObjectContainer().Collections().NewHashMap(10);
            SetDefaultValues(i_map);
            i_helper = Helper(10);
        }
      
        private static Db4oHashMapHelper Helper(int a_depth) {
            if (a_depth > 0) {
                Db4oHashMapHelper helper1 = new Db4oHashMapHelper();
                helper1.i_childList = Tester.ObjectContainer().Collections().NewLinkedList();
                helper1.i_childList.Add("hi");
                helper1.i_child = Helper(a_depth - 1);
                return helper1;
            }
            return null;
        }
      
        private void SetDefaultValues(IDictionary a_map) {
            for (int i1 = 0; i1 < DEFAULT.Length; i1++) {
                a_map[DEFAULT[i1]] = new Atom(DEFAULT[i1]);
            }
        }
      
        public void TestOne() {
            CheckHelper(i_helper);
            RunElementTest(true);
            bool defrag1 = true;
            if (!Tester.clientServer && defrag1) {
                long id1 = Tester.ObjectContainer().GetID(this);
                Tester.Close();
                new Defragment().Run(AllTests.FILE_SOLO, true);
                Tester.Open();
                RestoreMembers();
                CheckHelper(i_helper);
                RunElementTest(false);
            }
        }
      
        private void RunElementTest(bool onOriginal) {
            IDictionary otherMap1 = new Hashtable();
            Atom atom1 = null;
            TDefaultValues();
            int itCount1 = 0;
            IEnumerator i1 = i_map.Keys.GetEnumerator();
            while (i1.MoveNext()) {
                String str2 = (String)i1.Current;
                itCount1++;
                atom1 = (Atom)i_map[str2];
                Tester.Ensure(atom1.name.Equals(str2));
                otherMap1[str2] = atom1;
            }
            Tester.Ensure(itCount1 == DEFAULT.Length);
            Tester.Ensure(i_map.Count == DEFAULT.Length);
            Tester.ObjectContainer().Deactivate(i_map, Int32.MaxValue);
            Tester.Ensure(((Atom)i_map["great"]).name.Equals("great"));
            Tester.ObjectContainer().Deactivate(i_map, Int32.MaxValue);
            if (onOriginal) {
                Query q1 = Tester.Query();
                Db4oHashMap template1 = new Db4oHashMap();
                template1.i_map = Tester.ObjectContainer().Collections().NewHashMap(1);
                template1.i_map["cool"] = new Atom("cool");
                q1.Constrain(template1);
                ObjectSet qResult1 = q1.Execute();
                Tester.Ensure(qResult1.Size() == 1);
                Tester.Ensure(qResult1.Next() == this);
            }

            Object[] arr1 = new Object[i_map.Keys.Count];
            i_map.Keys.CopyTo(arr1, 0);
            TDefaultArray(arr1);
            String[] cmp1 = new String[DEFAULT.Length];
			System.Array.Copy(DEFAULT, 0, cmp1, 0, DEFAULT.Length);
            i1 = i_map.Keys.GetEnumerator();
            while (i1.MoveNext()) {
                String str2 = (String)i1.Current;
                bool found1 = false;
                for (int j2 = 0; j2 < cmp1.Length; j2++) {
                    if (str2.Equals(cmp1[j2])) {
                        cmp1[j2] = null;
                        found1 = true;
                    }
                }
                Tester.Ensure(found1);
            }
            for (int j2 = 0; j2 < cmp1.Length; j2++) {
                Tester.Ensure(cmp1[j2] == null);
            }
            Tester.ObjectContainer().Deactivate(i_map, Int32.MaxValue);
            Tester.Ensure(i_map.Count > 0);
            Tester.ObjectContainer().Deactivate(i_map, Int32.MaxValue);
            i_map["yup"] = new Atom("yup");
            Tester.Ensure(i_map.Count == 4);
            atom1 = (Atom)i_map["yup"];
            Tester.Ensure(atom1.name.Equals("yup"));
            Atom removed1 = (Atom)i_map["great"];
            i_map.Remove("great");
            Tester.Ensure(removed1.name.Equals("great"));
            Tester.ObjectContainer().Deactivate(i_map, Int32.MaxValue);
            Tester.Ensure(i_map.Count == 3);
            IEnumerator en = otherMap1.Keys.GetEnumerator();
            while(en.MoveNext()){
                i_map.Remove(en.Current);
            }
            Tester.ObjectContainer().Deactivate(i_map, Int32.MaxValue);
            Tester.Ensure(i_map.Count == 1);
            i1 = i_map.Keys.GetEnumerator();
            i1.MoveNext();
            String str1 = (String)i1.Current;
            Tester.Ensure(str1.Equals("yup"));
            Tester.Ensure(!i1.MoveNext());
            i_map.Clear();
            Tester.Ensure(i_map.Count == 0);
            SetDefaultValues(i_map);
            String[] strArr1 = new String[i_map.Count];
            i_map.Keys.CopyTo(strArr1, 0);
            TDefaultArray(strArr1);
            i_map.Clear();
            i_map["zero"] = "zero";
            long start1 = j4o.lang.JavaSystem.CurrentTimeMillis();
            for (int j2 = 0; j2 < COUNT; j2++) {
                i_map[MORE + j2] = new Atom(MORE + j2);
            }
            long stop1 = j4o.lang.JavaSystem.CurrentTimeMillis();
            Tester.Ensure(i_map.Count == COUNT + 1);
            LookupLast();
            Tester.ObjectContainer().Deactivate(i_map, Int32.MaxValue);
            LookupLast();
            LookupLast();
            Tester.ReOpen();
            RestoreMembers();
            LookupLast();
            atom1 = new Atom("double");
            i_map["double"] = atom1;
            int previousSize1 = i_map.Count;
            Tester.ObjectContainer().Deactivate(i_map, Int32.MaxValue);

            Atom doubleAtom1 = (Atom)i_map["double"];
            i_map["double"] = new Atom("double");

            Tester.Ensure(atom1 == doubleAtom1);
            Tester.Ensure(i_map.Count == previousSize1);
            i_map["double"] = doubleAtom1;
            Tester.Commit();
            i_map["rollBack"] = "rollBack";
            i_map["double"] = new Atom("nono");
            Tester.RollBack();
            Tester.Ensure(i_map["rollBack"] == null);
            Tester.Ensure(i_map.Count == previousSize1);
            atom1 = (Atom)i_map["double"];
            Tester.Ensure(atom1 == doubleAtom1);
            Tester.Ensure(i_map["double"] != null);
            Tester.Ensure(i_map["rollBack"] == null);
            otherMap1.Clear();
            otherMap1["other1"] = doubleAtom1;
            otherMap1["other2"] = doubleAtom1;
            IDictionaryEnumerator de = otherMap1.GetEnumerator();
            while(de.MoveNext()){
                i_map[de.Key] = de.Value;
            }
            Tester.ObjectContainer().Deactivate(i_map, Int32.MaxValue);
            Tester.Ensure(i_map["other1"] == doubleAtom1);
            Tester.Ensure(i_map["other2"] == doubleAtom1);
            i_map.Clear();
            Tester.Ensure(i_map.Count == 0);
            SetDefaultValues(i_map);
            int j1 = 0;
            i1 = i_map.Keys.GetEnumerator();
            while (i1.MoveNext()) {
                String key1 = (String)i1.Current;
                if (key1.Equals("cool")) {
                    i_map.Remove("cool");
                }
                j1++;
            }
            Tester.Ensure(i_map.Count == 2);
            Tester.Ensure(i_map["cool"] == null);
            Tester.Ensure(j1 == 3);
            i_map["double"] = doubleAtom1;
            ((Db4oMap)i_map).DeleteRemoved(true);
            i_map.Remove("double");
            Tester.Ensure(!Tester.ObjectContainer().IsStored(doubleAtom1));
            ((Db4oMap)i_map).DeleteRemoved(false);
            i_map.Clear();
            Tester.Ensure(i_map.Count == 0);
            SetDefaultValues(i_map);
        }
      
        private void TDefaultValues() {
            for (int i1 = 0; i1 < DEFAULT.Length; i1++) {
                Atom atom1 = (Atom)i_map[DEFAULT[i1]];
                Tester.Ensure(atom1.name.Equals(DEFAULT[i1]));
            }
        }
      
        private void TDefaultArray(Object[] arr) {
            Tester.Ensure(arr.Length == DEFAULT.Length);
            String[] str1 = new String[DEFAULT.Length];
			System.Array.Copy(DEFAULT, 0, str1, 0, DEFAULT.Length);
            for (int i1 = 0; i1 < arr.Length; i1++) {
                bool found1 = false;
                for (int j1 = 0; j1 < str1.Length; j1++) {
                    if (arr[i1].Equals(str1[j1])) {
                        str1[j1] = null;
                        found1 = true;
                    }
                }
                Tester.Ensure(found1);
            }
            for (int j1 = 0; j1 < str1.Length; j1++) {
                Tester.Ensure(str1[j1] == null);
            }
        }
      
        private void RestoreMembers() {
            Query q1 = Tester.Query();
            q1.Constrain(j4o.lang.Class.GetClassForObject(this));
            ObjectSet objectSet1 = q1.Execute();
            Db4oHashMap dll1 = (Db4oHashMap)objectSet1.Next();
            i_map = dll1.i_map;
            i_helper = dll1.i_helper;
        }
      
        private void LookupLast() {
            long start1 = j4o.lang.JavaSystem.CurrentTimeMillis();
            Atom atom1 = (Atom)i_map[MORE + (COUNT - 1)];
            long stop1 = j4o.lang.JavaSystem.CurrentTimeMillis();
            Tester.Ensure(atom1.name.Equals(MORE + (COUNT - 1)));
        }
      
        internal void CheckHelper(Db4oHashMapHelper helper) {
            ExtObjectContainer con1 = Tester.ObjectContainer();
            if (con1.IsActive(helper)) {
                Tester.Ensure(helper.i_childList[0].Equals("hi"));
                CheckHelper(helper.i_child);
            }
        }
    }

    public class Db4oHashMapHelper {
         
        public Db4oHashMapHelper() : base() {
        }
        public Db4oHashMapHelper i_child;
        public IList i_childList;
    }

}