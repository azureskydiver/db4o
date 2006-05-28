/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;

using Db4oTools;

using j4o.lang;
using j4o.util;

using com.db4o;
using com.db4o.ext;
using com.db4o.query;

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
      
        public void StoreOne() {
            i_list = Tester.ObjectContainer().Collections().NewLinkedList();
            SetDefaultValues();
            i_helper = Helper(10);
        }
      
        private static Db4oLinkedListUntypedHelper Helper(int a_depth) {
            if (a_depth > 0) {
                Db4oLinkedListUntypedHelper helper1 = new Db4oLinkedListUntypedHelper();
                helper1.i_childList = Tester.ObjectContainer().Collections().NewLinkedList();
                helper1.i_childList.Add("hi");
                helper1.i_child = Helper(a_depth - 1);
                return helper1;
            }
            return null;
        }
      
        private void SetDefaultValues() {
            ((IList)i_list).Add(new Atom("wow"));
            ((IList)i_list).Add(new Atom("cool"));
            ((IList)i_list).Add(new Atom("great"));
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
            IList otherList1 = new ArrayList();
            IEnumerator i1 = ((IList)i_list).GetEnumerator();
            i1.MoveNext();
            Atom atom1 = (Atom)i1.Current;
            Tester.Ensure(atom1.name.Equals("wow"));
            otherList1.Add(atom1);
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.Ensure(atom1.name.Equals("cool"));
            otherList1.Add(atom1);
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.Ensure(atom1.name.Equals("great"));
            otherList1.Add(atom1);
            Tester.Ensure(((IList)i_list).Count == 3);
            Tester.ObjectContainer().Deactivate(((IList)i_list), Int32.MaxValue);
            Tester.Ensure(((Atom)((IList)i_list)[((IList)i_list).Count - 1]).name.Equals("great"));
            Tester.ObjectContainer().Deactivate(((IList)i_list), Int32.MaxValue);
            if (onOriginal) {
                Query q1 = Tester.Query();

                Db4oLinkedListUntyped template1 = new Db4oLinkedListUntyped();
                template1.i_list = Tester.ObjectContainer().Collections().NewLinkedList();
                ((IList)template1.i_list).Add(new Atom("cool"));
                q1.Constrain(template1);

                ObjectSet qResult1 = q1.Execute();
                Tester.Ensure(qResult1.Size() == 1);
                Tester.Ensure(qResult1.Next() == this);
            }
            otherList1.Clear();
            Object[] arr1 = new Object[((IList)i_list).Count];
            ((IList)i_list).CopyTo(arr1, 0);
            Tester.Ensure(arr1.Length == 3);
            atom1 = (Atom)arr1[0];
            Tester.Ensure(atom1.name.Equals("wow"));
            atom1 = (Atom)arr1[1];
            Tester.Ensure(atom1.name.Equals("cool"));
            atom1 = (Atom)arr1[2];
            Tester.Ensure(atom1.name.Equals("great"));
            i1 = ((IList)i_list).GetEnumerator();
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.Ensure(atom1.name.Equals("wow"));
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.Ensure(atom1.name.Equals("cool"));
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.Ensure(atom1.name.Equals("great"));
            Tester.Ensure(((IList)i_list).Count == 3);
            Tester.Ensure(i1.MoveNext() == false );
            Tester.ObjectContainer().Deactivate(i_list, Int32.MaxValue);
            Tester.ObjectContainer().Deactivate(i_list, Int32.MaxValue);
            ((IList)i_list).Add(new Atom("yup"));
            Tester.Ensure(((IList)i_list).Count == 4);
            i1 = ((IList)i_list).GetEnumerator();
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.Ensure(atom1.name.Equals("wow"));
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.Ensure(atom1.name.Equals("cool"));
            i1.MoveNext();
            Atom toRemove1 = (Atom)i1.Current;
            Tester.Ensure(toRemove1.name.Equals("great"));
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.Ensure(atom1.name.Equals("yup"));
            Tester.Ensure(!i1.MoveNext());
            ((IList)i_list).Remove(toRemove1);
            Tester.ObjectContainer().Deactivate(i_list, Int32.MaxValue);
            i1 = ((IList)i_list).GetEnumerator();
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.Ensure(atom1.name.Equals("wow"));
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.Ensure(atom1.name.Equals("cool"));
            otherList1.Add(atom1);
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.Ensure(atom1.name.Equals("yup"));
            otherList1.Add(atom1);
            Tester.Ensure(((IList)i_list).Count == 3);
            IEnumerator e = otherList1.GetEnumerator();
            while(e.MoveNext()){
                ((IList)i_list).Remove(e.Current);
            }
            Tester.Ensure(((IList)i_list).Count == 1);
            i1 = ((IList)i_list).GetEnumerator();
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.Ensure(atom1.name.Equals("wow"));
            Tester.Ensure(!i1.MoveNext());
            e = otherList1.GetEnumerator();
            while(e.MoveNext()){
                ((IList)i_list).Add(e.Current);
            }
            Tester.Ensure(((IList)i_list).Count == 3);
            i1 = ((IList)i_list).GetEnumerator();
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.Ensure(atom1.name.Equals("wow"));
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.Ensure(atom1.name.Equals("cool"));
            i1.MoveNext();
            atom1 = (Atom)i1.Current;
            Tester.Ensure(atom1.name.Equals("yup"));
            Tester.Ensure(!i1.MoveNext());
            Atom[] atarr1 = new Atom[((IList)i_list).Count];
            ((IList)i_list).CopyTo(atarr1, 0);
            Tester.Ensure(atarr1[0].name.Equals("wow"));
            Tester.Ensure(atarr1[1].name.Equals("cool"));
            Tester.Ensure(atarr1[2].name.Equals("yup"));
            ((IList)i_list).Clear();
            ((IList)i_list).Add(new Atom("wow"));
            Tester.Ensure(((IList)i_list).Count == 1);
            long start1 = j4o.lang.JavaSystem.CurrentTimeMillis();
            for (int j1 = 0; j1 < COUNT; j1++) {
                ((IList)i_list).Add("more and more " + j1);
            }
            long stop1 = j4o.lang.JavaSystem.CurrentTimeMillis();
            Tester.Ensure(((IList)i_list).Count == COUNT + 1);
            LookupLast();
            Tester.ObjectContainer().Deactivate(i_list, Int32.MaxValue);
            LookupLast();
            Tester.ReOpen();
            RestoreMembers();
            LookupLast();
            String str1 = (String)((IList)i_list)[10];
            ((IList)i_list)[10] = new Atom("yo");
            Tester.Ensure(str1.Equals("more and more 9"));
            atom1 = (Atom)((IList)i_list)[10];
            ((IList)i_list).RemoveAt(10);
            Tester.Ensure(atom1.name.Equals("yo"));
            ((IList)i_list).Insert(5, new Atom("sure"));
            Tester.Ensure(((IList)i_list).Count == COUNT + 1);
            atom1 = (Atom)((IList)i_list)[5];
            ((IList)i_list).RemoveAt(5);
            Tester.Ensure(atom1.name.Equals("sure"));
            ((IList)i_list).Insert(0, new Atom("sure"));
            Tester.Ensure(((Atom)((IList)i_list)[0]).name.Equals("sure"));
            Tester.Ensure(((IList)i_list).Count == COUNT + 1);
            ((IList)i_list).Insert(((IList)i_list).Count, new Atom("sure"));
            Tester.Ensure(((IList)i_list).Count == COUNT + 2);
            Tester.Ensure(((Atom)((IList)i_list)[((IList)i_list).Count - 1]).name.Equals("sure"));
            atom1 = (Atom)((IList)i_list)[0];
            ((IList)i_list)[0] = "huh";
            Tester.Ensure(atom1.name.Equals("sure"));
            Tester.Ensure(((IList)i_list).Count == COUNT + 2);
            ((IList)i_list).Clear();
            Tester.Ensure(((IList)i_list).Count == 0);
            SetDefaultValues();
        }
      
        private void RestoreMembers() {
            Query q1 = Tester.Query();
            q1.Constrain(j4o.lang.Class.GetClassForObject(this));
            ObjectSet objectSet1 = q1.Execute();
            Db4oLinkedListUntyped dll1 = (Db4oLinkedListUntyped)objectSet1.Next();
            i_list = dll1.i_list;
            i_helper = dll1.i_helper;
        }
      
        private void LookupLast() {
            long start1 = j4o.lang.JavaSystem.CurrentTimeMillis();
            String str1 = (String)((IList)i_list)[COUNT];
            long stop1 = j4o.lang.JavaSystem.CurrentTimeMillis();
            Tester.Ensure(str1.Equals("more and more " + (COUNT - 1)));
        }
      
        internal void CheckHelper(Db4oLinkedListUntypedHelper helper) {
            ExtObjectContainer con1 = Tester.ObjectContainer();
            if (con1.IsActive(helper)) {
                Tester.Ensure(helper.i_childList[0].Equals("hi"));
                CheckHelper(helper.i_child);
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