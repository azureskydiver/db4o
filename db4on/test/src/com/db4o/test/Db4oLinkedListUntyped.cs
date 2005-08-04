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
    public class Db4oLinkedListUntyped {
      
        public Db4oLinkedListUntyped() : base() {
        }
      
        static internal int COUNT = 10;
        internal Object i_list;
        internal Db4oLinkedListUntypedHelper i_helper;
        internal IList i_subList;
      
        public void storeOne() {
            i_list = Tester.objectContainer().collections().newLinkedList();
            setDefaultValues();
            i_helper = helper(10);
        }
      
        private static Db4oLinkedListUntypedHelper helper(int a_depth) {
            if (a_depth > 0) {
                Db4oLinkedListUntypedHelper helper1 = new Db4oLinkedListUntypedHelper();
                helper1.i_childList = Tester.objectContainer().collections().newLinkedList();
                helper1.i_childList.Add("hi");
                helper1.i_child = helper(a_depth - 1);
                return helper1;
            }
            return null;
        }
      
        private void setDefaultValues() {
            ((IList)i_list).Add(new Atom("wow"));
            ((IList)i_list).Add(new Atom("cool"));
            ((IList)i_list).Add(new Atom("great"));
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
            IEnumerator i1 = ((IList)i_list).GetEnumerator();
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
            Tester.ensure(((IList)i_list).Count == 3);
            Tester.objectContainer().deactivate(((IList)i_list), Int32.MaxValue);
            Tester.ensure(((Atom)((IList)i_list)[((IList)i_list).Count - 1]).name.Equals("great"));
            Tester.objectContainer().deactivate(((IList)i_list), Int32.MaxValue);
            if (onOriginal) {
                Query q1 = Tester.query();

                Db4oLinkedListUntyped template1 = new Db4oLinkedListUntyped();
                template1.i_list = Tester.objectContainer().collections().newLinkedList();
                ((IList)template1.i_list).Add(new Atom("cool"));
                q1.constrain(template1);

                ObjectSet qResult1 = q1.execute();
                Tester.ensure(qResult1.size() == 1);
                Tester.ensure(qResult1.next() == this);
            }
            otherList1.Clear();
            Object[] arr1 = new Object[((IList)i_list).Count];
            ((IList)i_list).CopyTo(arr1, 0);
            Tester.ensure(arr1.Length == 3);
            atom1 = (Atom)arr1[0];
            Tester.ensure(atom1.name.Equals("wow"));
            atom1 = (Atom)arr1[1];
            Tester.ensure(atom1.name.Equals("cool"));
            atom1 = (Atom)arr1[2];
            Tester.ensure(atom1.name.Equals("great"));
            i1 = ((IList)i_list).GetEnumerator();
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.ensure(atom1.name.Equals("wow"));
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.ensure(atom1.name.Equals("cool"));
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.ensure(atom1.name.Equals("great"));
            Tester.ensure(((IList)i_list).Count == 3);
            Tester.ensure(i1.MoveNext() == false );
            Tester.objectContainer().deactivate(i_list, Int32.MaxValue);
            Tester.objectContainer().deactivate(i_list, Int32.MaxValue);
            ((IList)i_list).Add(new Atom("yup"));
            Tester.ensure(((IList)i_list).Count == 4);
            i1 = ((IList)i_list).GetEnumerator();
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
            ((IList)i_list).Remove(toRemove1);
            Tester.objectContainer().deactivate(i_list, Int32.MaxValue);
            i1 = ((IList)i_list).GetEnumerator();
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
            Tester.ensure(((IList)i_list).Count == 3);
            IEnumerator e = otherList1.GetEnumerator();
            while(e.MoveNext()){
                ((IList)i_list).Remove(e.Current);
            }
            Tester.ensure(((IList)i_list).Count == 1);
            i1 = ((IList)i_list).GetEnumerator();
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.ensure(atom1.name.Equals("wow"));
            Tester.ensure(!i1.MoveNext());
            e = otherList1.GetEnumerator();
            while(e.MoveNext()){
                ((IList)i_list).Add(e.Current);
            }
            Tester.ensure(((IList)i_list).Count == 3);
            i1 = ((IList)i_list).GetEnumerator();
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
            Atom[] atarr1 = new Atom[((IList)i_list).Count];
            ((IList)i_list).CopyTo(atarr1, 0);
            Tester.ensure(atarr1[0].name.Equals("wow"));
            Tester.ensure(atarr1[1].name.Equals("cool"));
            Tester.ensure(atarr1[2].name.Equals("yup"));
            ((IList)i_list).Clear();
            ((IList)i_list).Add(new Atom("wow"));
            Tester.ensure(((IList)i_list).Count == 1);
            long start1 = j4o.lang.JavaSystem.currentTimeMillis();
            for (int j1 = 0; j1 < COUNT; j1++) {
                ((IList)i_list).Add("more and more " + j1);
            }
            long stop1 = j4o.lang.JavaSystem.currentTimeMillis();
            Tester.ensure(((IList)i_list).Count == COUNT + 1);
            lookupLast();
            Tester.objectContainer().deactivate(i_list, Int32.MaxValue);
            lookupLast();
            Tester.reOpen();
            restoreMembers();
            lookupLast();
            String str1 = (String)((IList)i_list)[10];
            ((IList)i_list)[10] = new Atom("yo");
            Tester.ensure(str1.Equals("more and more 9"));
            atom1 = (Atom)((IList)i_list)[10];
            ((IList)i_list).RemoveAt(10);
            Tester.ensure(atom1.name.Equals("yo"));
            ((IList)i_list).Insert(5, new Atom("sure"));
            Tester.ensure(((IList)i_list).Count == COUNT + 1);
            atom1 = (Atom)((IList)i_list)[5];
            ((IList)i_list).RemoveAt(5);
            Tester.ensure(atom1.name.Equals("sure"));
            ((IList)i_list).Insert(0, new Atom("sure"));
            Tester.ensure(((Atom)((IList)i_list)[0]).name.Equals("sure"));
            Tester.ensure(((IList)i_list).Count == COUNT + 1);
            ((IList)i_list).Insert(((IList)i_list).Count, new Atom("sure"));
            Tester.ensure(((IList)i_list).Count == COUNT + 2);
            Tester.ensure(((Atom)((IList)i_list)[((IList)i_list).Count - 1]).name.Equals("sure"));
            atom1 = (Atom)((IList)i_list)[0];
            ((IList)i_list)[0] = "huh";
            Tester.ensure(atom1.name.Equals("sure"));
            Tester.ensure(((IList)i_list).Count == COUNT + 2);
            ((IList)i_list).Clear();
            Tester.ensure(((IList)i_list).Count == 0);
            setDefaultValues();
        }
      
        private void restoreMembers() {
            Query q1 = Tester.query();
            q1.constrain(j4o.lang.Class.getClassForObject(this));
            ObjectSet objectSet1 = q1.execute();
            Db4oLinkedListUntyped dll1 = (Db4oLinkedListUntyped)objectSet1.next();
            i_list = dll1.i_list;
            i_helper = dll1.i_helper;
        }
      
        private void lookupLast() {
            long start1 = j4o.lang.JavaSystem.currentTimeMillis();
            String str1 = (String)((IList)i_list)[COUNT];
            long stop1 = j4o.lang.JavaSystem.currentTimeMillis();
            Tester.ensure(str1.Equals("more and more " + (COUNT - 1)));
        }
      
        internal void checkHelper(Db4oLinkedListUntypedHelper helper) {
            ExtObjectContainer con1 = Tester.objectContainer();
            if (con1.isActive(helper)) {
                Tester.ensure(helper.i_childList[0].Equals("hi"));
                checkHelper(helper.i_child);
            }
        }
    }

    public class Db4oLinkedListUntypedHelper {
         
        public Db4oLinkedListUntypedHelper() : base() {
        }
        public Db4oLinkedListUntypedHelper i_child;
        public IList i_childList;
    }

}