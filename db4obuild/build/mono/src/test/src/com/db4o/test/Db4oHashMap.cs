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

using com.db4o;
using com.db4o.ext;
using com.db4o.query;
using com.db4o.tools;
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
      
        public void storeOne() {
            i_map = Test.objectContainer().collections().newHashMap(10);
            setDefaultValues(i_map);
            i_helper = helper(10);
        }
      
        private static Db4oHashMapHelper helper(int a_depth) {
            if (a_depth > 0) {
                Db4oHashMapHelper helper1 = new Db4oHashMapHelper();
                helper1.i_childList = Test.objectContainer().collections().newLinkedList();
                helper1.i_childList.Add("hi");
                helper1.i_child = helper(a_depth - 1);
                return helper1;
            }
            return null;
        }
      
        private void setDefaultValues(IDictionary a_map) {
            for (int i1 = 0; i1 < DEFAULT.Length; i1++) {
                a_map[DEFAULT[i1]] = new Atom(DEFAULT[i1]);
            }
        }
      
        public void testOne() {
            checkHelper(i_helper);
            runElementTest(true);
            bool defrag1 = true;
            if (!Test.clientServer && defrag1) {
                long id1 = Test.objectContainer().getID(this);
                Test.close();
                new Defragment().run(AllTests.FILE_SOLO, true);
                Test.open();
                restoreMembers();
                checkHelper(i_helper);
                runElementTest(false);
            }
        }
      
        private void runElementTest(bool onOriginal) {
            IDictionary otherMap1 = new Hashtable();
            Atom atom1 = null;
            tDefaultValues();
            int itCount1 = 0;
            IEnumerator i1 = i_map.Keys.GetEnumerator();
            while (i1.MoveNext()) {
                String str2 = (String)i1.Current;
                itCount1++;
                atom1 = (Atom)i_map[str2];
                Test.ensure(atom1.name.Equals(str2));
                otherMap1[str2] = atom1;
            }
            Test.ensure(itCount1 == DEFAULT.Length);
            Test.ensure(i_map.Count == DEFAULT.Length);
            Test.objectContainer().deactivate(i_map, Int32.MaxValue);
            Test.ensure(((Atom)i_map["great"]).name.Equals("great"));
            Test.objectContainer().deactivate(i_map, Int32.MaxValue);
            if (onOriginal) {
                Query q1 = Test.query();
                Db4oHashMap template1 = new Db4oHashMap();
                template1.i_map = Test.objectContainer().collections().newHashMap(1);
                template1.i_map["cool"] = new Atom("cool");
                q1.constrain(template1);
                ObjectSet qResult1 = q1.execute();
                Test.ensure(qResult1.size() == 1);
                Test.ensure(qResult1.next() == this);
            }

            Object[] arr1 = new Object[i_map.Keys.Count];
            i_map.Keys.CopyTo(arr1, 0);
            tDefaultArray(arr1);
            String[] cmp1 = new String[DEFAULT.Length];
            j4o.lang.JavaSystem.arraycopy(DEFAULT, 0, cmp1, 0, DEFAULT.Length);
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
                Test.ensure(found1);
            }
            for (int j2 = 0; j2 < cmp1.Length; j2++) {
                Test.ensure(cmp1[j2] == null);
            }
            Test.objectContainer().deactivate(i_map, Int32.MaxValue);
            Test.ensure(i_map.Count > 0);
            Test.objectContainer().deactivate(i_map, Int32.MaxValue);
            i_map["yup"] = new Atom("yup");
            Test.ensure(i_map.Count == 4);
            atom1 = (Atom)i_map["yup"];
            Test.ensure(atom1.name.Equals("yup"));
            Atom removed1 = (Atom)i_map["great"];
            i_map.Remove("great");
            Test.ensure(removed1.name.Equals("great"));
            Test.objectContainer().deactivate(i_map, Int32.MaxValue);
            Test.ensure(i_map.Count == 3);
            IEnumerator en = otherMap1.Keys.GetEnumerator();
            while(en.MoveNext()){
                i_map.Remove(en.Current);
            }
            Test.objectContainer().deactivate(i_map, Int32.MaxValue);
            Test.ensure(i_map.Count == 1);
            i1 = i_map.Keys.GetEnumerator();
            i1.MoveNext();
            String str1 = (String)i1.Current;
            Test.ensure(str1.Equals("yup"));
            Test.ensure(!i1.MoveNext());
            i_map.Clear();
            Test.ensure(i_map.Count == 0);
            setDefaultValues(i_map);
            String[] strArr1 = new String[i_map.Count];
            i_map.Keys.CopyTo(strArr1, 0);
            tDefaultArray(strArr1);
            i_map.Clear();
            i_map["zero"] = "zero";
            long start1 = j4o.lang.JavaSystem.currentTimeMillis();
            for (int j2 = 0; j2 < COUNT; j2++) {
                i_map[MORE + j2] = new Atom(MORE + j2);
            }
            long stop1 = j4o.lang.JavaSystem.currentTimeMillis();
            Test.ensure(i_map.Count == COUNT + 1);
            lookupLast();
            Test.objectContainer().deactivate(i_map, Int32.MaxValue);
            lookupLast();
            lookupLast();
            Test.reOpen();
            restoreMembers();
            lookupLast();
            atom1 = new Atom("double");
            i_map["double"] = atom1;
            int previousSize1 = i_map.Count;
            Test.objectContainer().deactivate(i_map, Int32.MaxValue);

            Atom doubleAtom1 = (Atom)i_map["double"];
            i_map["double"] = new Atom("double");

            Test.ensure(atom1 == doubleAtom1);
            Test.ensure(i_map.Count == previousSize1);
            i_map["double"] = doubleAtom1;
            Test.commit();
            i_map["rollBack"] = "rollBack";
            i_map["double"] = new Atom("nono");
            Test.rollBack();
            Test.ensure(i_map["rollBack"] == null);
            Test.ensure(i_map.Count == previousSize1);
            atom1 = (Atom)i_map["double"];
            Test.ensure(atom1 == doubleAtom1);
            Test.ensure(i_map["double"] != null);
            Test.ensure(i_map["rollBack"] == null);
            otherMap1.Clear();
            otherMap1["other1"] = doubleAtom1;
            otherMap1["other2"] = doubleAtom1;
            IDictionaryEnumerator de = otherMap1.GetEnumerator();
            while(de.MoveNext()){
                i_map[de.Key] = de.Value;
            }
            Test.objectContainer().deactivate(i_map, Int32.MaxValue);
            Test.ensure(i_map["other1"] == doubleAtom1);
            Test.ensure(i_map["other2"] == doubleAtom1);
            i_map.Clear();
            Test.ensure(i_map.Count == 0);
            setDefaultValues(i_map);
            int j1 = 0;
            i1 = i_map.Keys.GetEnumerator();
            while (i1.MoveNext()) {
                String key1 = (String)i1.Current;
                if (key1.Equals("cool")) {
                    i_map.Remove("cool");
                }
                j1++;
            }
            Test.ensure(i_map.Count == 2);
            Test.ensure(i_map["cool"] == null);
            Test.ensure(j1 == 3);
            i_map["double"] = doubleAtom1;
            ((Db4oMap)i_map).deleteRemoved(true);
            i_map.Remove("double");
            Test.ensure(!Test.objectContainer().isStored(doubleAtom1));
            ((Db4oMap)i_map).deleteRemoved(false);
            i_map.Clear();
            Test.ensure(i_map.Count == 0);
            setDefaultValues(i_map);
        }
      
        private void tDefaultValues() {
            for (int i1 = 0; i1 < DEFAULT.Length; i1++) {
                Atom atom1 = (Atom)i_map[DEFAULT[i1]];
                Test.ensure(atom1.name.Equals(DEFAULT[i1]));
            }
        }
      
        private void tDefaultArray(Object[] arr) {
            Test.ensure(arr.Length == DEFAULT.Length);
            String[] str1 = new String[DEFAULT.Length];
            j4o.lang.JavaSystem.arraycopy(DEFAULT, 0, str1, 0, DEFAULT.Length);
            for (int i1 = 0; i1 < arr.Length; i1++) {
                bool found1 = false;
                for (int j1 = 0; j1 < str1.Length; j1++) {
                    if (arr[i1].Equals(str1[j1])) {
                        str1[j1] = null;
                        found1 = true;
                    }
                }
                Test.ensure(found1);
            }
            for (int j1 = 0; j1 < str1.Length; j1++) {
                Test.ensure(str1[j1] == null);
            }
        }
      
        private void restoreMembers() {
            Query q1 = Test.query();
            q1.constrain(j4o.lang.Class.getClassForObject(this));
            ObjectSet objectSet1 = q1.execute();
            Db4oHashMap dll1 = (Db4oHashMap)objectSet1.next();
            i_map = dll1.i_map;
            i_helper = dll1.i_helper;
        }
      
        private void lookupLast() {
            long start1 = j4o.lang.JavaSystem.currentTimeMillis();
            Atom atom1 = (Atom)i_map[MORE + (COUNT - 1)];
            long stop1 = j4o.lang.JavaSystem.currentTimeMillis();
            Test.ensure(atom1.name.Equals(MORE + (COUNT - 1)));
        }
      
        internal void checkHelper(Db4oHashMapHelper helper) {
            ExtObjectContainer con1 = Test.objectContainer();
            if (con1.isActive(helper)) {
                Test.ensure(helper.i_childList[0].Equals("hi"));
                checkHelper(helper.i_child);
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