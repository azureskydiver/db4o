/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using j4o.lang;
using j4o.util;
using com.db4o;
using com.db4o.ext;
using com.db4o.query;
using com.db4o.tools;
namespace com.db4o.test {

    /**
     * 
     */
    public class Db4oLinkedList {
      
        public Db4oLinkedList() : base() {
        }
      
        static internal int COUNT = 10;
        internal IList i_list;
        internal Db4oLinkedListHelper i_helper;
        internal IList i_subList;
      
        public void storeOne() {
            i_list = Tester.objectContainer().collections().newLinkedList();
            setDefaultValues();
            i_helper = helper(10);
        }
      
        private static Db4oLinkedListHelper helper(int a_depth) {
            if (a_depth > 0) {
                Db4oLinkedListHelper helper1 = new Db4oLinkedListHelper();
                helper1.i_childList = Tester.objectContainer().collections().newLinkedList();
                helper1.i_childList.Add("hi");
                helper1.i_child = helper(a_depth - 1);
                return helper1;
            }
            return null;
        }
      
        private void setDefaultValues() {
            i_list.Add(new Atom("wow"));
            i_list.Add(new Atom("cool"));
            i_list.Add(new Atom("great"));
        }
      
        public void testOne() {
            checkHelper(i_helper);
            runElementTest(true);
            bool defrag1 = true;
            if (!Tester.clientServer && defrag1) {
                long id1 = Tester.objectContainer().getID(this);
                Tester.close();
                new Defragment().run(AllTests.FILE_SOLO, true);
                Tester.open();
                restoreMembers();
                checkHelper(i_helper);
                runElementTest(false);
            }
        }
      
        private void runElementTest(bool onOriginal) {
            IList otherList1 = new ArrayList();
            IEnumerator i1 = i_list.GetEnumerator();
            i1.MoveNext();
            Atom atom1 = (Atom)i1.Current;
            Tester.ensure(atom1.name.Equals("wow"));
            otherList1.Add(atom1);
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.ensure(atom1.name.Equals("cool"));
            otherList1.Add(atom1);
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.ensure(atom1.name.Equals("great"));
            otherList1.Add(atom1);
            Tester.ensure(i_list.Count == 3);
            Tester.objectContainer().deactivate(i_list, Int32.MaxValue);
            Tester.ensure(((Atom)i_list[i_list.Count - 1]).name.Equals("great"));
            Tester.objectContainer().deactivate(i_list, Int32.MaxValue);
            if (onOriginal) {
                Query q1 = Tester.query();
                Db4oLinkedList template1 = new Db4oLinkedList();
                template1.i_list = Tester.objectContainer().collections().newLinkedList();
                template1.i_list.Add(new Atom("cool"));
                q1.constrain(template1);
                ObjectSet qResult1 = q1.execute();
                Tester.ensure(qResult1.size() == 1);
                Tester.ensure(qResult1.next() == this);
            }
            otherList1.Clear();
            Object[] arr1 = new Object[i_list.Count];
            i_list.CopyTo(arr1, 0);
            Tester.ensure(arr1.Length == 3);
            atom1 = (Atom)arr1[0];
            Tester.ensure(atom1.name.Equals("wow"));
            atom1 = (Atom)arr1[1];
            Tester.ensure(atom1.name.Equals("cool"));
            atom1 = (Atom)arr1[2];
            Tester.ensure(atom1.name.Equals("great"));
            i1 = i_list.GetEnumerator();
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.ensure(atom1.name.Equals("wow"));
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.ensure(atom1.name.Equals("cool"));
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.ensure(atom1.name.Equals("great"));
            Tester.ensure(i_list.Count == 3);
            Tester.ensure(i1.MoveNext() == false );
            Tester.objectContainer().deactivate(i_list, Int32.MaxValue);
            Tester.objectContainer().deactivate(i_list, Int32.MaxValue);
            i_list.Add(new Atom("yup"));
            Tester.ensure(i_list.Count == 4);
            i1 = i_list.GetEnumerator();
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.ensure(atom1.name.Equals("wow"));
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.ensure(atom1.name.Equals("cool"));
            i1.MoveNext();
            Atom toRemove1 = (Atom)i1.Current;
            Tester.ensure(toRemove1.name.Equals("great"));
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.ensure(atom1.name.Equals("yup"));
            Tester.ensure(!i1.MoveNext());
            i_list.Remove(toRemove1);
            Tester.objectContainer().deactivate(i_list, Int32.MaxValue);
            i1 = i_list.GetEnumerator();
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.ensure(atom1.name.Equals("wow"));
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.ensure(atom1.name.Equals("cool"));
            otherList1.Add(atom1);
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.ensure(atom1.name.Equals("yup"));
            otherList1.Add(atom1);
            Tester.ensure(i_list.Count == 3);
            IEnumerator e = otherList1.GetEnumerator();
            while(e.MoveNext()){
                i_list.Remove(e.Current);
            }
            Tester.ensure(i_list.Count == 1);
            i1 = i_list.GetEnumerator();
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.ensure(atom1.name.Equals("wow"));
            Tester.ensure(!i1.MoveNext());
            e = otherList1.GetEnumerator();
            while(e.MoveNext()){
                i_list.Add(e.Current);
            }
            Tester.ensure(i_list.Count == 3);
            i1 = i_list.GetEnumerator();
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.ensure(atom1.name.Equals("wow"));
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.ensure(atom1.name.Equals("cool"));
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.ensure(atom1.name.Equals("yup"));
            Tester.ensure(!i1.MoveNext());
            Atom[] atarr1 = new Atom[i_list.Count];
            i_list.CopyTo(atarr1, 0);
            Tester.ensure(atarr1[0].name.Equals("wow"));
            Tester.ensure(atarr1[1].name.Equals("cool"));
            Tester.ensure(atarr1[2].name.Equals("yup"));
            i_list.Clear();
            i_list.Add(new Atom("wow"));
            Tester.ensure(i_list.Count == 1);
            long start1 = j4o.lang.JavaSystem.currentTimeMillis();
            for (int j1 = 0; j1 < COUNT; j1++) {
                i_list.Add("more and more " + j1);
            }
            long stop1 = j4o.lang.JavaSystem.currentTimeMillis();
            Tester.ensure(i_list.Count == COUNT + 1);
            lookupLast();
            Tester.objectContainer().deactivate(i_list, Int32.MaxValue);
            lookupLast();
            Tester.reOpen();
            restoreMembers();
            lookupLast();
            String str1 = (String)i_list[10];
            i_list[10] = new Atom("yo");
            Tester.ensure(str1.Equals("more and more 9"));
            atom1 = (Atom)i_list[10];
            i_list.RemoveAt(10);
            Tester.ensure(atom1.name.Equals("yo"));
            i_list.Insert(5, new Atom("sure"));
            Tester.ensure(i_list.Count == COUNT + 1);
            atom1 = (Atom)i_list[5];
            i_list.RemoveAt(5);
            Tester.ensure(atom1.name.Equals("sure"));
            i_list.Insert(0, new Atom("sure"));
            Tester.ensure(((Atom)i_list[0]).name.Equals("sure"));
            Tester.ensure(i_list.Count == COUNT + 1);
            i_list.Insert(i_list.Count, new Atom("sure"));
            Tester.ensure(i_list.Count == COUNT + 2);
            Tester.ensure(((Atom)i_list[i_list.Count - 1]).name.Equals("sure"));
            atom1 = (Atom)i_list[0];
            i_list[0] = "huh";
            Tester.ensure(atom1.name.Equals("sure"));
            Tester.ensure(i_list.Count == COUNT + 2);
            i_list.Clear();
            Tester.ensure(i_list.Count == 0);
            setDefaultValues();
        }
      
        private void restoreMembers() {
            Query q1 = Tester.query();
            q1.constrain(j4o.lang.Class.getClassForObject(this));
            ObjectSet objectSet1 = q1.execute();
            Db4oLinkedList dll1 = (Db4oLinkedList)objectSet1.next();
            i_list = dll1.i_list;
            i_helper = dll1.i_helper;
        }
      
        private void lookupLast() {
            long start1 = j4o.lang.JavaSystem.currentTimeMillis();
            String str1 = (String)i_list[COUNT];
            long stop1 = j4o.lang.JavaSystem.currentTimeMillis();
            Tester.ensure(str1.Equals("more and more " + (COUNT - 1)));
        }
      
        internal void checkHelper(Db4oLinkedListHelper helper) {
            ExtObjectContainer con1 = Tester.objectContainer();
            if (con1.isActive(helper)) {
                Tester.ensure(helper.i_childList[0].Equals("hi"));
                checkHelper(helper.i_child);
            }
        }
    }

    public class Db4oLinkedListHelper {
         
        public Db4oLinkedListHelper() : base() {
        }
        public Db4oLinkedListHelper i_child;
        public IList i_childList;
    }

}