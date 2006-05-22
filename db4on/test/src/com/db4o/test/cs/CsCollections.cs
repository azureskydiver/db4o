/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using j4o.lang;

namespace com.db4o.test.cs {

    public class CsCollections {
        ArrayList arrayList;
        Hashtable hashTable;
        Queue queue;
        Stack stack;

        public CsCollections(){
        }

        public void Store(){
            Tester.DeleteAllInstances(this);
            CsCollections csc = new CsCollections();
            csc.Fill();
            Tester.Store(csc);
            Tester.Commit();
        }

        public void Test(){
            CsCollections csc = (CsCollections)Tester.GetOne(this);
            csc.Check();
        }

        private void Fill(){
            
            arrayList = new ArrayList();
            Fill(arrayList);
            
            hashTable = new Hashtable();
            Fill(hashTable);

            queue = new Queue();
            queue.Enqueue(1);
            queue.Enqueue("hi");
            queue.Enqueue(new Atom("foo"));

            stack = new Stack();
            stack.Push(1);
            stack.Push("hi");
            stack.Push(new Atom("foo"));
        }

        private void Check(){
            Check(arrayList);
            Check(hashTable);

            Tester.Ensure(queue.Dequeue().Equals(1));
            Tester.Ensure(queue.Dequeue().Equals("hi"));
            Tester.Ensure(queue.Dequeue().Equals(new Atom("foo")));

            Tester.Ensure(stack.Pop().Equals(new Atom("foo")));
            Tester.Ensure(stack.Pop().Equals("hi"));
            Tester.Ensure(stack.Pop().Equals(1));
        }

        private void Fill(IList list){
            list.Add(1);
            list.Add(null);
            list.Add(new Atom("foo"));
            list.Add("foo");
        }


        private void Check(IList list){
            Tester.Ensure(list[0].Equals(1));
            Tester.Ensure(list[1] == null);
            Tester.Ensure(list[2].Equals(new Atom("foo")));
            Tester.Ensure(list[3].Equals("foo"));
        }

        private void Fill(IDictionary dict){
            dict[1] = 1;
            dict["hey"] = "ho";
            dict[new Atom("foo")] = new Atom("bar");
            dict[4] = "Yoman";
        }

        private void Check(IDictionary dict){
            Tester.Ensure(dict[1].Equals(1));
            Tester.Ensure(dict["hey"].Equals("ho"));
            Tester.Ensure(dict[new Atom("foo")].Equals(new Atom("bar")));
            Tester.Ensure(dict[4].Equals("Yoman"));
        }

    }
}
