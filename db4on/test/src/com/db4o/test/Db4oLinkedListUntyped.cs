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
            i_list = Test.objectContainer().collections().newLinkedList();
            setDefaultValues();
            i_helper = helper(10);
        }
      
        private static Db4oLinkedListUntypedHelper helper(int a_depth) {
            if (a_depth > 0) {
                Db4oLinkedListUntypedHelper helper1 = new Db4oLinkedListUntypedHelper();
                helper1.i_childList = Test.objectContainer().collections().newLinkedList();
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
            IList otherList1 = new ArrayList();
            IEnumerator i1 = ((IList)i_list).GetEnumerator();
            i1.MoveNext();
            Atom atom1 = (Atom)i1.Current;
            Test.ensure(atom1.name == "wow");
            otherList1.Add(atom1);
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Test.ensure(atom1.name == "cool");
            otherList1.Add(atom1);
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Test.ensure(atom1.name == "great");
            otherList1.Add(atom1);
            Test.ensure(((IList)i_list).Count == 3);
            Test.objectContainer().deactivate(((IList)i_list), Int32.MaxValue);
            Test.ensure(((Atom)((IList)i_list)[((IList)i_list).Count - 1]).name == "great");
            Test.objectContainer().deactivate(((IList)i_list), Int32.MaxValue);
            if (onOriginal) {
                Query q1 = Test.query();
                Db4oLinkedListUntyped template1 = new Db4oLinkedListUntyped();
                template1.i_list = Test.objectContainer().collections().newLinkedList();
                ((IList)template1.i_list).Add(new Atom("cool"));
                q1.constrain(template1);
                ObjectSet qResult1 = q1.execute();
                Test.ensure(qResult1.size() == 1);
                Test.ensure(qResult1.next() == this);
            }
            otherList1.Clear();
            Object[] arr1 = new Object[((IList)i_list).Count];
            ((IList)i_list).CopyTo(arr1, 0);
            Test.ensure(arr1.Length == 3);
            atom1 = (Atom)arr1[0];
            Test.ensure(atom1.name == "wow");
            atom1 = (Atom)arr1[1];
            Test.ensure(atom1.name == "cool");
            atom1 = (Atom)arr1[2];
            Test.ensure(atom1.name == "great");
            i1 = ((IList)i_list).GetEnumerator();
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Test.ensure(atom1.name == "wow");
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Test.ensure(atom1.name == "cool");
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Test.ensure(atom1.name == "great");
            Test.ensure(((IList)i_list).Count == 3);
            Test.ensure(i1.MoveNext() == false );
            Test.objectContainer().deactivate(i_list, Int32.MaxValue);
            Test.objectContainer().deactivate(i_list, Int32.MaxValue);
            ((IList)i_list).Add(new Atom("yup"));
            Test.ensure(((IList)i_list).Count == 4);
            i1 = ((IList)i_list).GetEnumerator();
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Test.ensure(atom1.name == "wow");
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Test.ensure(atom1.name == "cool");
            i1.MoveNext();
            Atom toRemove1 = (Atom)i1.Current;
            Test.ensure(toRemove1.name == "great");
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Test.ensure(atom1.name == "yup");
            Test.ensure(!i1.MoveNext());
            ((IList)i_list).Remove(toRemove1);
            Test.objectContainer().deactivate(i_list, Int32.MaxValue);
            i1 = ((IList)i_list).GetEnumerator();
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Test.ensure(atom1.name == "wow");
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Test.ensure(atom1.name == "cool");
            otherList1.Add(atom1);
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Test.ensure(atom1.name == "yup");
            otherList1.Add(atom1);
            Test.ensure(((IList)i_list).Count == 3);
            IEnumerator e = otherList1.GetEnumerator();
            while(e.MoveNext()){
                ((IList)i_list).Remove(e.Current);
            }
            Test.ensure(((IList)i_list).Count == 1);
            i1 = ((IList)i_list).GetEnumerator();
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Test.ensure(atom1.name == "wow");
            Test.ensure(!i1.MoveNext());
            e = otherList1.GetEnumerator();
            while(e.MoveNext()){
                ((IList)i_list).Add(e.Current);
            }
            Test.ensure(((IList)i_list).Count == 3);
            i1 = ((IList)i_list).GetEnumerator();
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Test.ensure(atom1.name == "wow");
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Test.ensure(atom1.name == "cool");
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Test.ensure(atom1.name == "yup");
            Test.ensure(!i1.MoveNext());
            Atom[] atarr1 = new Atom[((IList)i_list).Count];
            ((IList)i_list).CopyTo(atarr1, 0);
            Test.ensure(atarr1[0].name == "wow");
            Test.ensure(atarr1[1].name == "cool");
            Test.ensure(atarr1[2].name == "yup");
            ((IList)i_list).Clear();
            ((IList)i_list).Add(new Atom("wow"));
            Test.ensure(((IList)i_list).Count == 1);
            long start1 = j4o.lang.JavaSystem.currentTimeMillis();
            for (int j1 = 0; j1 < COUNT; j1++) {
                ((IList)i_list).Add("more and more " + j1);
            }
            long stop1 = j4o.lang.JavaSystem.currentTimeMillis();
            Test.ensure(((IList)i_list).Count == COUNT + 1);
            lookupLast();
            Test.objectContainer().deactivate(i_list, Int32.MaxValue);
            lookupLast();
            Test.reOpen();
            restoreMembers();
            lookupLast();
            String str1 = (String)((IList)i_list)[10];
            ((IList)i_list)[10] = new Atom("yo");
            Test.ensure(str1.Equals("more and more 9"));
            atom1 = (Atom)((IList)i_list)[10];
            ((IList)i_list).RemoveAt(10);
            Test.ensure(atom1.name.Equals("yo"));
            ((IList)i_list).Insert(5, new Atom("sure"));
            Test.ensure(((IList)i_list).Count == COUNT + 1);
            atom1 = (Atom)((IList)i_list)[5];
            ((IList)i_list).RemoveAt(5);
            Test.ensure(atom1.name.Equals("sure"));
            ((IList)i_list).Insert(0, new Atom("sure"));
            Test.ensure(((Atom)((IList)i_list)[0]).name.Equals("sure"));
            Test.ensure(((IList)i_list).Count == COUNT + 1);
            ((IList)i_list).Insert(((IList)i_list).Count, new Atom("sure"));
            Test.ensure(((IList)i_list).Count == COUNT + 2);
            Test.ensure(((Atom)((IList)i_list)[((IList)i_list).Count - 1]).name.Equals("sure"));
            atom1 = (Atom)((IList)i_list)[0];
            ((IList)i_list)[0] = "huh";
            Test.ensure(atom1.name.Equals("sure"));
            Test.ensure(((IList)i_list).Count == COUNT + 2);
            ((IList)i_list).Clear();
            Test.ensure(((IList)i_list).Count == 0);
            setDefaultValues();
        }
      
        private void restoreMembers() {
            Query q1 = Test.query();
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
            Test.ensure(str1.Equals("more and more " + (COUNT - 1)));
        }
      
        internal void checkHelper(Db4oLinkedListUntypedHelper helper) {
            ExtObjectContainer con1 = Test.objectContainer();
            if (con1.isActive(helper)) {
                Test.ensure(helper.i_childList[0].Equals("hi"));
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