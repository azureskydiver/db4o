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

        public void store(){
            Tester.deleteAllInstances(this);
            CsCollections csc = new CsCollections();
            csc.fill();
            Tester.store(csc);
            Tester.commit();
        }

        public void test(){
            CsCollections csc = (CsCollections)Tester.getOne(this);
            csc.check();
        }

        private void fill(){
            
            arrayList = new ArrayList();
            fill(arrayList);
            
            hashTable = new Hashtable();
            fill(hashTable);

            queue = new Queue();
            queue.Enqueue(1);
            queue.Enqueue("hi");
            queue.Enqueue(new Atom("foo"));

            stack = new Stack();
            stack.Push(1);
            stack.Push("hi");
            stack.Push(new Atom("foo"));
        }

        private void check(){
            check(arrayList);
            check(hashTable);

            Tester.ensure(queue.Dequeue().Equals(1));
            Tester.ensure(queue.Dequeue().Equals("hi"));
            Tester.ensure(queue.Dequeue().Equals(new Atom("foo")));

            Tester.ensure(stack.Pop().Equals(new Atom("foo")));
            Tester.ensure(stack.Pop().Equals("hi"));
            Tester.ensure(stack.Pop().Equals(1));
        }

        private void fill(IList list){
            list.Add(1);
            list.Add(null);
            list.Add(new Atom("foo"));
            list.Add("foo");
        }


        private void check(IList list){
            Tester.ensure(list[0].Equals(1));
            Tester.ensure(list[1] == null);
            Tester.ensure(list[2].Equals(new Atom("foo")));
            Tester.ensure(list[3].Equals("foo"));
        }

        private void fill(IDictionary dict){
            dict[1] = 1;
            dict["hey"] = "ho";
            dict[new Atom("foo")] = new Atom("bar");
            dict[4] = "Yoman";
        }

        private void check(IDictionary dict){
            Tester.ensure(dict[1].Equals(1));
            Tester.ensure(dict["hey"].Equals("ho"));
            Tester.ensure(dict[new Atom("foo")].Equals(new Atom("bar")));
            Tester.ensure(dict[4].Equals("Yoman"));
        }

    }
}
